package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    static final Logger LOG = Logger.getLogger(Server.class.getName());

    private final int LISTEN_PORT;
    private final String STOP_CONNECTION = "kthx";

    public Server (int port) {
        LISTEN_PORT = port;
    }

    public void start() {
        LOG.info("Starting server…");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            LOG.log(Level.INFO, "Creating a server socket and binding it on any of the available network interfaces and on port {0}", new Object[]{Integer.toString(LISTEN_PORT)});
            serverSocket = new ServerSocket(LISTEN_PORT);
            logServerSocketAddress(serverSocket);

            clientSocket = waitingForNewConnection(serverSocket);

            while (true) {
                LOG.log(Level.INFO, "Getting a Reader and a Writer connected to the client socket...");
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream());

                String clientCommand = in.readLine();
                LOG.log(Level.INFO, "Getting client command : {0}", clientCommand);

                if (!clientCommand.equals(STOP_CONNECTION)) {
                    String[] command = Parser.splitCommand(clientCommand);

                    if (command.length == 4) {
                        if (!command[0].equals("plsdodat")) {
                            LOG.log(Level.INFO, "First argument didn't ask for the task, rejecting command…");
                            out.println("Ouais, écoute, ça va pas le faire…");
                            out.flush();
                        } else {
                            LOG.log(Level.INFO, "Parsing command…");
                            try {
                                double op1 = Parser.getNumber(command[1]);
                                LOG.log(Level.INFO, "Getting first operand : {0}", op1);
                                double op2 = Parser.getNumber(command[2]);
                                LOG.log(Level.INFO, "Getting second operand : {0}", op2);
                                char op = Parser.getOperator(command[3]);
                                LOG.log(Level.INFO, "Getting operator : {0}", op);
                                double result = Calculator.operation(op1, op2, op);
                                LOG.log(Level.INFO, "Sending answer : {0}", result);
                                out.println("Tiens, c'est tout bon : " + result);
                                out.flush();
                            } catch (InterruptedException ie) {
                                String errorMessage = ie.getMessage();
                                LOG.log(Level.SEVERE, "Parsing error… {0}", errorMessage);
                                out.println(errorMessage);
                                out.flush();
                            }
                        }
                    } else {
                        LOG.log(Level.INFO, "Getting invalind command…");
                        out.println("Désolé, mais c'est pas une commande valide.");
                        out.flush();
                    }
                } else {
                    LOG.log(Level.INFO, "Client asking to stop connection");
                    out.println("Ça joue ! Au revoir !");
                    out.flush();
                    clientSocket.close();

                    clientSocket = waitingForNewConnection(serverSocket);
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        } finally {
            LOG.log(Level.INFO, "We are done. Cleaning up resources, closing streams and sockets...");
            try {
                assert in != null;
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            assert out != null;
            out.close();
            try {
                clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                serverSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * A utility method to print server socket information
     *
     * @param serverSocket the socket that we want to log
     */
    private void logServerSocketAddress(ServerSocket serverSocket) {
        LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{serverSocket.getLocalSocketAddress()});
        LOG.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(serverSocket.getLocalPort())});
        LOG.log(Level.INFO, "               is bound: {0}", new Object[]{serverSocket.isBound()});
    }

    /**
     * A utility method to print socket information
     *
     * @param clientSocket the socket that we want to log
     */
    private void logSocketAddress(Socket clientSocket) {
        LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{clientSocket.getLocalAddress()});
        LOG.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(clientSocket.getLocalPort())});
        LOG.log(Level.INFO, "  Remote Socket address: {0}", new Object[]{clientSocket.getRemoteSocketAddress()});
        LOG.log(Level.INFO, "            Remote port: {0}", new Object[]{Integer.toString(clientSocket.getPort())});
    }

    /**
     * Waiting for the connection from a new client
     *
     * @param serverSocket the server socket
     * @return socket for the client socket
     * @throws IOException is needed but don't know yet why
     */
    private Socket waitingForNewConnection(ServerSocket serverSocket) throws IOException {
        LOG.log(Level.INFO, "Waiting (blocking) for a connection request on {0} : {1}", new Object[]{serverSocket.getInetAddress(), Integer.toString(serverSocket.getLocalPort())});
        Socket clientSocket = serverSocket.accept();
        LOG.log(Level.INFO, "A client has arrived. We now have a client socket with following attributes:");
        logSocketAddress(clientSocket);
        return clientSocket;
    }
}
