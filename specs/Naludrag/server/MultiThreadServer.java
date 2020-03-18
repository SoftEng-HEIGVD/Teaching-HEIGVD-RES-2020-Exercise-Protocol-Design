
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a multi-threaded TCP server. It is able to interact
 * with several clients at the time, as well as to continue listening for
 * connection requests.
 *
 * @author Olivier Liechti
 */
public class MultiThreadServer implements Runnable{

    final static Logger LOG = Logger.getLogger(MultiThreadServer.class.getName());

    boolean shouldRun;
    ServerSocket serverSocket;
    final List<Worker> connectedWorkers;

    public MultiThreadServer() {
        this.shouldRun = true;
        this.connectedWorkers = Collections.synchronizedList(new LinkedList<Worker>());
    }

    private void registerWorker(Worker worker) {
        LOG.log(Level.INFO, ">> Waiting for lock before registring worker");
        connectedWorkers.add(worker);
        //Add message to the worker
        LOG.log(Level.INFO, "<< Worker registered.");
    }

    private void unregisterWorker(Worker worker) {
        LOG.log(Level.INFO, ">> Waiting for lock before unregistring worker");
        connectedWorkers.remove(worker);
        LOG.log(Level.INFO, "<< Worker unregistered.");
    }

    private void disconnectConnectedWorkers() {
        LOG.info(">> Waiting for lock before disconnecting workers");
        synchronized (connectedWorkers) {
            LOG.info("Disconnecting workers");
            for (Worker worker : connectedWorkers) {
                worker.disconnect();
            }
        }
        LOG.info("<< Workers disconnected");
    }

    @Override
    public void run() {
        try {
            LOG.log(Level.INFO, "Starting Presence Server on port {0}", Protocol.PRESENCE_DEFAULT_PORT);
            serverSocket = new ServerSocket(Protocol.PRESENCE_DEFAULT_PORT);
            while (shouldRun) {
                Socket clientSocket = serverSocket.accept();
                Worker newWorker = new Worker(clientSocket);
                registerWorker(newWorker);
                new Thread(newWorker).start();
            }
            serverSocket.close();
            LOG.info("shouldRun is false... server going down");
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            System.exit(-1);
        }
    }

    private void shutdown() {
        LOG.info("Shutting down server...");
        shouldRun = false;
        try {
            serverSocket.close();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        disconnectConnectedWorkers();
    }

    class Worker implements Runnable {

        Socket clientSocket;
        BufferedReader in;
        PrintWriter out;
        boolean connected;

        public Worker(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream());
                connected = true;
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        @Override
        public void run() {
            String commandLine;
            int result = 0;
            sendNotification("HELLO, GIVE CALCULATIONS(supported operators : + - * /)");
            try {
                while (connected && ((commandLine = in.readLine()) != null)) {
                    String[] tokens = commandLine.split(" ");
                    if (tokens[0].toUpperCase().equals(Protocol.CMD_BYE)) {
                        connected = false;
                    } else {
                        try {
                            if (tokens.length > 3) {
                                throw new NumberFormatException();
                            }
                            int operand1 = Integer.parseInt(tokens[0]);
                            int operand2 = Integer.parseInt(tokens[2]);
                            if (tokens[1].length() > 1 || tokens[1].equals("+") || tokens[1].equals("-") ||
                                    tokens[1].equals("*") || tokens[1].equals("/")) {
                                throw new NumberFormatException();
                            } else {
                                switch (tokens[1].charAt(0)) {
                                    case '+':
                                        result = operand1 + operand2;
                                        break;
                                    case '-':
                                        result = operand1 - operand2;
                                        break;
                                    case '*':
                                        result = operand1 * operand2;
                                        break;
                                    case '/':
                                        result = operand1 / operand2;
                                        break;
                                }
                            }
                            sendNotification(result + " = " + commandLine);
                        } catch (NumberFormatException e) {
                            System.out.println("The calcul passed is not the right format");
                            sendNotification("Please send another calcul wrong format");
                        }
                    }
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            } finally {
                unregisterWorker(this);
                cleanup();
            }
        }

        private void cleanup() {
            LOG.log(Level.INFO, "Cleaning up worker used by worker");

            LOG.log(Level.INFO, "Closing clientSocket used by worker");
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }

            LOG.log(Level.INFO, "Closing in used by worker");
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }

            LOG.log(Level.INFO, "Closing out used by a worker");
            if (out != null) {
                out.close();
            }

            LOG.log(Level.INFO, "Clean up done for worker");
        }

        public void sendNotification(String message) {
            out.println(message);
            out.flush();
        }

        private void disconnect() {
            LOG.log(Level.INFO, "Disconnecting worker");
            connected = false;
            cleanup();
        }

    }
}