package ch.heigvd.res.server;

import ch.heigvd.res.common.Protocol;
import ch.heigvd.res.common.Operator;
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
 * @modified Stéphane Teixeira Carvalho
 */
public class Server implements Runnable{

    final static Logger LOG = Logger.getLogger(Server.class.getName());

    boolean shouldRun;
    ServerSocket serverSocket;
    final List<Worker> connectedWorkers;
    int nbWorker = 0;

    /**
     * Default Constructor of the class Server
     */
    public Server() {
        this.shouldRun = true;
        this.connectedWorkers = Collections.synchronizedList(new LinkedList<Worker>());
    }

    /**
     * Function that add the new worker in the list of workers of the server
     * @param worker Object of type worker that is the new worker to add to the list
     */
    private void registerWorker(Worker worker) {
        LOG.log(Level.INFO, ">> Waiting for lock before registring a worker {0}", worker.idWorker);
        connectedWorkers.add(worker);
        LOG.log(Level.INFO, "<< Worker registered.");
    }

    /**
     * Function that remove a worker from the list of workers
     * @param worker Object of type worker that is the worker to remove from the list
     */
    private void unregisterWorker(Worker worker) {
        LOG.log(Level.INFO, ">> Waiting for lock before unregistring a worker {0}", worker.idWorker);
        connectedWorkers.remove(worker);
        LOG.log(Level.INFO, "<< Worker unregistered.");
    }

    /**
     * Function that disconnects all the workers present on the server
     */
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

    /**
     * Override of the function run because server implements the interface Runnable
     */
    @Override
    public void run() {
        try {
            LOG.log(Level.INFO, "Starting Presence Server on port {0}", Protocol.DEFAULT_PORT);
            serverSocket = new ServerSocket(Protocol.DEFAULT_PORT);
            while (shouldRun) {
                Socket clientSocket = serverSocket.accept();
                Worker newWorker = new Worker(clientSocket);
                registerWorker(newWorker);
                new Thread(newWorker).start();
            }
            serverSocket.close();
            LOG.info("shouldRun is false... server going down");
        } catch (IOException ex) {
            if(!shouldRun){
                LOG.info("socket closed server is going down");
            }else {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
                System.exit(-1);
            }
        }
    }

    /**
     * Function that shutdown the server.
     * This function is not used for the moment but we let this function because we thought that it could become interesting
     * to shutdown the server after x seconds without any client for example.
     */
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

    /**
     * Innerclass of Server that permits to define what a client of the server can do
     */
    class Worker implements Runnable {

        Socket clientSocket;
        BufferedReader in;
        PrintWriter out;
        boolean connected;
        int idWorker;

        /**
         * Constructor of the class Worker
         * @param clientSocket Socket that represents the socket that is used to communicate with the client
         */
        public Worker(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), Protocol.CHARSET));
                out = new PrintWriter(clientSocket.getOutputStream());
                connected = true;
                idWorker = ++nbWorker;
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        @Override
        public void run() {
            String commandLine;
            try {
                while (connected && ((commandLine = in.readLine()) != null)) {
                    String[] tokens = commandLine.split(" ");
                    if (tokens[0].toUpperCase().equals(Protocol.CMD_BYE)) {
                        connected = false;
                    } else if (tokens[0].toUpperCase().equals(Protocol.CMD_HELLO)) {
                        StringBuilder str = new StringBuilder("HELLO, GIVE CALCULATIONS(supported operators : ");
                        //Run trough all available operators to send to the client
                        for(Operator op : Operator.values()){
                            str.append(op.toString());
                            str.append(" ");
                        }
                        str.append(")");
                        sendNotification(str.toString());
                    } else {
                        //Creation of a new object calculator that permits to calculate an operation with a String array
                        Calculator calculator = new Calculator(tokens);
                        new Thread(calculator).start();
                    }
                }
            } catch (IOException ex) {
                if (!shouldRun){
                    LOG.info("socket closed server is going down");
                }else {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                    System.exit(-1);
                }
            } finally {
                unregisterWorker(this);
                cleanup();
            }
        }

        /**
         * Function that will close the different connections of a worker
         */
        private void cleanup() {
            LOG.log(Level.INFO, "Cleaning up worker used by worker {0}", idWorker);

            LOG.log(Level.INFO, "Closing clientSocket used by worker {0}", idWorker);
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }

            LOG.log(Level.INFO, "Closing in used by worker {0}", idWorker);
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }

            LOG.log(Level.INFO, "Closing out used by worker {0}", idWorker);
            if (out != null) {
                out.close();
            }

            LOG.log(Level.INFO, "Clean up done for worker {0}", idWorker);
        }

        /**
         * Function that will permit to send messages to the clients
         * @param message String containing the message that we want to send
         */
        public void sendNotification(String message) {
            out.println(message);
            out.flush();
        }

        /**
         * Function that will disconnect the worker from the server
         */
        private void disconnect() {
            LOG.log(Level.INFO, "Disconnecting worker {0}", idWorker);
            connected = false;
            cleanup();
        }

        /**
         * This class implements the calculation feature of the server.
         *
         * @author Stéphane Teixeira Carvalho
         */
        class Calculator implements Runnable {
            String[] tokens;
            double result;
            int operand1;
            int operand2;

            /**
             * Specified Constructor to initialize an object Calculator
             * @param tokens array of string that contains the operation that as to be done
             */
            public Calculator(String[] tokens) {
                this.tokens = tokens;
                result = 0.0;
                operand1 = 0;
                operand2 = 0;
            }

            /**
             * Override of the function because we implemented the runnable interface
             */
            @Override
            public void run() {
                //If tokens has more than 3 arguments error
                if (tokens.length > 3) {
                    sendNotification("Please send another calcul wrong number of arguments");
                } else {
                    //Try to convert operands in the tokens as int
                    try {
                        operand1 = Integer.parseInt(tokens[0]);
                        operand2 = Integer.parseInt(tokens[2]);
                    } catch (NumberFormatException e) {
                        sendNotification("Please send another calcul numbers for the operation incorrect");
                    } finally {
                        //Search in the operators if the one passed is in the list
                        Operator op = Operator.getOperator(tokens[1]);
                        if (op == null) {
                            sendNotification("Unknown operator");
                        } else {
                            result = op.eval(operand1,operand2);
                            sendNotification(operand1 + " " + op.toString() + " " + operand2 + " = " + result);
                        }
                    }
                }
            }
        }
    }


}