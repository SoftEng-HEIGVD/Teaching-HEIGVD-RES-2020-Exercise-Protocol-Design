package main.java.ch.heigvd.res;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * largely based on presence example form MrLiechti (especially on the runnuable interface and the worker internal class)
 * https://github.com/SoftEng-HEIGVD/Teaching-HEIGVD-RES-2020/blob/master/examples/06-PresenceApplication/PresenceApplication/src/main/java/ch/heigvd/res/examples/PresenceServer.java
 */
public class Server implements Runnable{

    final static Logger LOG = Logger.getLogger(Server.class.getName());
    private static Charset CHARSET = StandardCharsets.UTF_8;
    private ServerSocket serverSocket;
    private boolean shouldRun;
    final List<Worker> connectedWorkers;

    public Server() {
        this.shouldRun = true;
        this.connectedWorkers = Collections.synchronizedList(new LinkedList<Worker>());
    }

    private void registerWorker(Worker worker) {
        LOG.log(Level.INFO, ">> Waiting for lock before registring worker {0}", worker.userName);
        connectedWorkers.add(worker);
        LOG.log(Level.INFO, "<< Worker {0} registered.", worker.userName);
    }

    private void unregisterWorker(Worker worker) {
        LOG.log(Level.INFO, ">> Waiting for lock before unregistring worker {0}", worker.userName);
        connectedWorkers.remove(worker);
        LOG.log(Level.INFO, "<< Worker {0} unregistered.", worker.userName);
    }


    @Override
    public void run() {
        try {
            LOG.log(Level.INFO, "Starting Calc Server on port {0}", Protocol.CALC_DEFAULT_PORT);
            serverSocket = new ServerSocket(Protocol.CALC_DEFAULT_PORT);
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


    class Worker implements Runnable {

        Socket clientSocket;
        BufferedReader reader;
        BufferedWriter writer;
        boolean connected;
        String userName = "An anonymous user";

        private void sendCustomDouble(double toSend) throws IOException {
            LOG.info("sending " + toSend);
            if (Double.isNaN(toSend)) {
                sendNOK("calculation, rightHand and/or " +
                        "leftHand has not been initialised" +
                        "\n(or the calculation was mathematically impossible, don't do 0/0)");
            }
            writer.write(toSend + Protocol.EOL);
            writer.flush();
        }

        private void sendNOK(String errorMsg) throws IOException {
            LOG.info("sending NOK");
            writer.write(Protocol.NOK + Protocol.EOL);
            writer.flush();
            throw new IOException("failed " + errorMsg);
        }

        private void sendOK() throws IOException {
            LOG.info("sending OK");
            writer.write(Protocol.OK + Protocol.EOL);
            writer.flush();
        }

        private boolean opControl(String op) {
            for (String s : Protocol.OP) {
                if (op.equals(s)) {
                    return true;
                }
            }
            return false;
        }

        public Worker(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), CHARSET));
                writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), CHARSET));
                connected = true;
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        @Override
        public void run() {

            try {

                while (connected) {
                    //Get START instruction
                    String meeting = reader.readLine();
                    LOG.info("received "+meeting);
                    if (!meeting.equals(Protocol.START)) {
                        sendNOK("meeting");
                    }

                    sendOK();

                    //Get operator instruction
                    String operation = reader.readLine();
                    LOG.info("received "+ operation);
                    if (!opControl(operation)) {
                        sendNOK("operation");
                    }
                    sendOK();


                    //Get leftHand number
                    //why NaN? https://www.youtube.com/watch?v=5TFDG-y-EHs
                    double leftHand = Double.NaN;
                    try {
                        leftHand = Double.parseDouble(reader.readLine());
                        LOG.info("received "+leftHand);
                    } catch (Throwable e) {
                        sendNOK("lefthand: " + e);
                    }
                    sendOK();


                    //Get rightHand number
                    double rightHand = Double.NaN;
                    try {
                        rightHand = Double.parseDouble(reader.readLine());
                        LOG.info("received "+rightHand);
                    } catch (Throwable e) {
                        sendNOK("righthand: " + e);
                    }
                    sendOK();

                    //on calcul la réponse et on l'envoie
                    double answer;
                    switch (operation) {
                        case Protocol.ADD:
                            answer = leftHand + rightHand;
                            sendCustomDouble(answer);
                            break;
                        case Protocol.SUB:
                            answer = leftHand - rightHand;
                            sendCustomDouble(answer);
                            break;
                        case Protocol.MUL:
                            answer = leftHand * rightHand;
                            sendCustomDouble(answer);
                            break;
                        case Protocol.DIV:
                            answer = leftHand / rightHand;
                            sendCustomDouble(answer);
                            break;
                        default:
                            sendNOK("impossible operation (should be impossible to trigger)");
                            break;
                    }

                    //on verifie si on fini ou si on fait un nouveau calcul
                    String finished = reader.readLine();
                    if (finished.equals(Protocol.NOK)) {
                        connected = true;
                    } else if (finished.equals(Protocol.OK)) {
                        connected = false;
                    } else {
                        throw new IOException("veryfing if over or new calc failed");
                    }
                    LOG.info("ok for continuing "+connected);
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            } finally {
                unregisterWorker(this);
                try {
                    cleanup();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void cleanup() throws IOException {
            LOG.log(Level.INFO, "Cleaning up worker used by {0}", userName);

            LOG.log(Level.INFO, "Closing clientSocket used by {0}", userName);
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }

            LOG.log(Level.INFO, "Closing in used by {0}", userName);
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }

            LOG.log(Level.INFO, "Closing out used by {0}", userName);
            if (writer != null) {
                writer.close();
            }

            LOG.log(Level.INFO, "Clean up done for worker used by {0}", userName);
        }

        private void disconnect() throws IOException {
            LOG.log(Level.INFO, "Disconnecting worker used by {0}", userName);
            connected = false;
            cleanup();
        }

    }

}