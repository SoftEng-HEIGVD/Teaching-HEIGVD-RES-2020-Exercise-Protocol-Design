package ch.heig.res.calc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalcServer {

    static Logger LOG;
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$s] %5$s%n");
        LOG = Logger.getLogger(CalcServer.class.getName());
    }
    final static int PORT = 4452;

    private enum SupportedOperation {ADD, SUB, MULT, DIV};

    public static void main(String[] args) {
        CalcServer server = new CalcServer();
        server.serveClients();
    }


    public void serveClients() {
        LOG.info("Starting the Receptionist Worker on a new thread...");
        new Thread(new ReceptionistWorker()).start();
    }

    private class ReceptionistWorker implements Runnable {

        @Override
        public void run() {
            ServerSocket serverSocket;

            try {
                serverSocket = new ServerSocket(PORT);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
                return;
            }

            while (true) {
                LOG.log(Level.INFO, "Waiting (blocking) for a new client on port {0}", PORT);
                try {
                    Socket clientSocket = serverSocket.accept();
                    LOG.info("A new client has arrived. Starting a new thread and delegating work to a new servant...");
                    new Thread(new ServantWorker(clientSocket)).start();
                } catch (IOException ex) {
                    Logger.getLogger(CalcServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

        private class ServantWorker implements Runnable {

            Socket clientSocket;
            BufferedReader in = null;
            PrintWriter out = null;

            public ServantWorker(Socket clientSocket) {
                try {
                    this.clientSocket = clientSocket;
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new PrintWriter(clientSocket.getOutputStream());
                } catch (IOException ex) {
                    Logger.getLogger(CalcServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void run() {
                String line;
                boolean shouldRun = true;

                out.println("CALC SERVER (Ali v.0.2.1)");
                out.flush();
                StringBuilder supOp = new StringBuilder();
                for (SupportedOperation op : SupportedOperation.values()) {
                    supOp.append(op);
                    if(!op.equals(SupportedOperation.values()[SupportedOperation.values().length - 1])) {
                        supOp.append(" ");
                    }
                }
                out.println(supOp.toString());
                out.flush();
                try {
                    LOG.info("Waiting for operations or client closes the connection...");
                    while ((shouldRun) && (line = in.readLine()) != null) {
                        String[] parts = line.split(" ");

                        LOG.info("Parts: " + Arrays.toString(parts));

                        // TODO check parts
                        String operation = parts[0];
                        float operand1 = Float.parseFloat(parts[1]);
                        float operand2 = Float.parseFloat(parts[2]);

                        try {
                            float result = executeOperation(SupportedOperation.valueOf(operation), operand1, operand2);
                            out.print(String.format("%.2f%n", result));
                            out.flush();
                            LOG.info("Result: " + result);
                        } catch (Exception e) {
                            LOG.warning("Error in operation");
                        }
                    }

                    LOG.info("Cleaning up resources...");
                    clientSocket.close();
                    in.close();
                    out.close();

                } catch (IOException ex) {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex1) {
                            LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                        }
                    }
                    if (out != null) {
                        out.close();
                    }
                    if (clientSocket != null) {
                        try {
                            clientSocket.close();
                        } catch (IOException ex1) {
                            LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                        }
                    }
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }

            private float executeOperation(SupportedOperation operation, float operand1, float operand2) {
               switch (operation) {
                   case ADD:
                       return operand1 + operand2;
                   case SUB:
                       return operand1 - operand2;
                   case MULT:
                       return operand1 * operand2;
                   case DIV:
                       return operand1 / operand2;
                   default:
                       throw new RuntimeException("Operation not supported");
               }
            }
        }
    }

}
