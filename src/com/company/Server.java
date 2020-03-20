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

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(Protocol.DEFAULT_PORT);

            while (this.shouldRun) {

                Socket clientSocket = serverSocket.accept();

                Server.this.notifyConnectedWorkers("Someone has arrived...");

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
            sendNotification("Send an operation: [op1] [+,-,*,/] [op2]");


            try {

                while (connected && ((calcul = in.readLine()) != null)) {

                    String[] strOp = calcul.split(" ");

                    double op1 = Double.parseDouble(strOp[0]);
                    double op2 = Double.parseDouble(strOp[2]);

                    switch (strOp[1].toCharArray()[0]) {

                        case (Protocol.OP_ADD):
                            sendNotification("RES " + (op1 + op2));
                            break;
                        case (Protocol.OP_SUB):
                            sendNotification("RES " + (op1 - op2));
                            break;
                        case (Protocol.OP_MUL):
                            sendNotification("RES " + (op1 * op2));
                            break;
                        case (Protocol.OP_DIV):

                            if(op2 != 0){

                                sendNotification("RES " + (op1 / op2));
                            } else {

                                sendNotification("ERR cannot divide by 0");
                            }

                            break;
                        default:
                            sendNotification("ERR unknown : " + strOp[1] + ", use only : [+, -, *, /]");
                    }


                }

            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            } finally {
                unregisterWorker(this);
                Server.this.notifyConnectedWorkers("Someone left...");
                cleanup();
            }
        }

        private void cleanup() {

            LOG.log(Level.INFO, "Cleaning up worker");

            LOG.log(Level.INFO, "Closing clientSocked");
            try {
                clientSocket.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }

            LOG.log(Level.INFO, "Closing in");
            try {
                in.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }

            LOG.log(Level.INFO, "Closing out");
            if(out != null){
                out.close();
            }

            LOG.log(Level.INFO, "Clean up done");
        }

        public void sendNotification(String message) {
            out.println(message);
            out.flush();
        }

        private void disconnect() {
            LOG.log(Level.INFO , "Disconnecting worker");
            connected = false;
            cleanup();
        }
    }
}
