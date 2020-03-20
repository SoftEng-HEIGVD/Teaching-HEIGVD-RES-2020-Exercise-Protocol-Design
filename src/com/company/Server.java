package com.company;

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

public class Server implements Runnable {

    final static Logger LOG = Logger.getLogger(Server.class.getName());

    boolean shouldRun;
    ServerSocket serverSocket;
    final List<Worker> connectedWorkers;

    public Server() {
        this.shouldRun = true;
        this.connectedWorkers = Collections.synchronizedList(new LinkedList<Worker>());
    }

    private void registerWorker(Worker worker) {

        connectedWorkers.add(worker);
    }

    private void unregisterWorker(Worker worker) {

        connectedWorkers.remove(worker);
    }

    private void notifyConnectedWorkers(String message) {

        synchronized (connectedWorkers) {
            LOG.info("Notifying workers");
            for(Worker worker : connectedWorkers){
                worker.sendNotification(message);
            }
        }
        LOG.info("Workers notified");

    }

    private void disconnectConnectedWorkers() {

        synchronized (connectedWorkers) {
            LOG.info("Disconnecting workers");
            for(Worker worker : connectedWorkers) {
                worker.disconnect();
            }
        }
        LOG.info("Workers disconnected");
    }

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(Protocol.DEFAULT_PORT);

            while (this.shouldRun) {

                Socket clientSocket = serverSocket.accept();
                //NOTIFY
                Worker newWorker = new Worker(clientSocket);
                registerWorker(newWorker);
                new Thread(newWorker).start();
            }

            serverSocket.close();

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            System.exit(-1);
        }
    }

    private void shutdown() {
        this.shouldRun = false;

        try {

            serverSocket.close();

        } catch (IOException ex) {

            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
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

            String calcul;
            double result = 0;
            Server.this.notifyConnectedWorkers("Welcome !");
            Server.this.notifyConnectedWorkers("Give me your calc !");

            try {

                while (connected && ((calcul = in.readLine()) != null)) {

                    String strOp[] = calcul.split(" ");

                    double op1 = Double.parseDouble(strOp[0]);
                    double op2 = Double.parseDouble(strOp[2]);

                    switch (strOp[1].toCharArray()[0]) {

                        case (Protocol.OP_ADD):
                            result = op1 + op2;
                            break;
                        case (Protocol.OP_SUB):
                            result = op1 - op2;
                            break;
                        case (Protocol.OP_MUL):
                            result = op1 * op2;
                            break;
                        case (Protocol.OP_DIV):
                            result = op1 / op2;
                            break;
                        default:
                            sendNotification("I don't new this operation ! I only understand +, -, *, /");
                    }

                    sendNotification("Your resultats is " + result);
                }

            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        private void cleanup() {

        }
    }
}
