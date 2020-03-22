package server.impl.tiffanybonzon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the multithreaded server.
 * ***Strongly**** inspired by the Multi-Threaded TCP server example
 *
 * @author Tiffany Bonzon
 */
public class Server {
    final static Logger LOG = Logger.getLogger(Server.class.getName());
    int port;

    /**
     * Server Constructor
     * @param port The port number on which the server listens
     */
    public Server(int port) {
        this.port = port;
    }

    /**
     * Initializes the process on a new thread and starts it
     */
    public void serveClients() {
        LOG.info("Starting the CalcServer Worker on a new thread...");
        new Thread(new CalcServerWorker()).start();
    }

    /**
     * Listens for incoming connections and starts a new thread once a client connects
     */
    private class CalcServerWorker implements Runnable {
        public void run() {
            ServerSocket ss;

            // Tries to create a socket binded to the specified port
            try {
                ss = new ServerSocket(port);
            } catch(IOException e) {
                LOG.log(Level.SEVERE, null, e);
                return;
            }

            while(true) {
                LOG.log(Level.INFO, "Waiting for a new client on port {0}", port);

                try {
                    Socket cs = ss.accept();
                    LOG.info("A new client has arrived. Starting a new thread and delegating work...");
                    new Thread(new ServantWorker(cs)).start();
                } catch(IOException e) {
                    LOG.log(Level.SEVERE, null, e);
                }
            }
        }

        /**
         * Takes care of clients once they have connected.
         * This is where we implement the application protocol logic
         */
        private class ServantWorker implements Runnable {
            Socket cs;
            BufferedReader in = null;
            PrintWriter out = null;

            public ServantWorker(Socket cs) {
                try {
                    this.cs = cs;
                    in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
                    out = new PrintWriter(cs.getOutputStream());
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, null, e);
                }
            }

            public void run() {
                String clientMessage;
                boolean clientSentEND = false;

                try {
                    LOG.info("Reading until the client sends END...");
                    while(!clientSentEND && (clientMessage = in.readLine()) != null) {
                        clientMessage = clientMessage.toUpperCase();

                        if(clientMessage.equals("HELLO")) {
                            out.println("> Available operations: ADD SUB MUL DIV POW");
                            out.flush();
                        } else if(clientMessage.equals("HELP")) {
                            out.println("> Syntax: <OP> <NUM1> <NUM2> Each element is separated by a space, decimal is specified with a dot. Connection can be closed by typing END");
                            out.flush();
                        } else if(clientMessage.startsWith("END")) {
                            clientSentEND = true;
                        } else if(clientMessage.startsWith("ADD") || clientMessage.startsWith("SUB") || clientMessage.startsWith("MUL") || clientMessage.startsWith("DIV") || clientMessage.startsWith("POW")) {
                            out.println("> " + processCalc(clientMessage));
                            out.flush();
                        } else {
                            out.println("> ERROR: Unknown command!");
                            out.flush();
                        }
                    }

                    LOG.info("Cleaning up resources...");
                    cs.close();
                    in.close();
                    out.close();

                } catch(IOException e) {
                    if(in != null) {
                        try {
                            in.close();
                        } catch(IOException ein) {
                            LOG.log(Level.SEVERE, ein.getMessage(), ein);
                        }
                    }

                    if(out != null) {
                        out.close();
                    }

                    if(cs != null) {
                        try {
                            cs.close();
                        } catch(IOException ecs) {
                            LOG.log(Level.SEVERE, ecs.getMessage(), ecs);
                        }
                    }
                }
            }

            /**
             * Calculates the result of the operation sent by the client
             * @param message The operation sent by the client
             * @return The result of the operation or the appropriate error message
             */
            private String processCalc(String message) {
                String[] op = message.split(" ");
                double operand1, operand2, result;
                String ret;

                if(op.length != 3) {
                    return "ERROR: Invalid syntax!";
                }

                try {
                    operand1 = Double.parseDouble(op[1]);
                    operand2 = Double.parseDouble(op[2]);
                } catch(NumberFormatException e) {
                    return "ERROR: Invalid operands!";
                }

                switch(op[0]) {
                    case "ADD":
                        result = operand1 + operand2;
                        break;
                    case "SUB":
                        result = operand1 - operand2;
                        break;
                    case "MUL":
                        result = operand1 * operand2;
                        break;
                    case "DIV":
                        if(operand2 == 0) {
                            return "ERROR: Can't divide by 0!";
                        }
                        result = operand1 / operand2;
                        break;
                    case "POW":
                        result = Math.pow(operand1, operand2);
                        break;
                    default:
                        return "ERROR: Unknown operation!";
                }

                return "Result: " + result;
            }
        }
    }
}
