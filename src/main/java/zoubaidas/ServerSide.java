package zoubaidas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

public class ServerSide {
    //Setting server logger
    final static Logger LOG = Logger.getLogger(ServerSide.class.getName());
    //Port we are going to use to communicate with client application.
    private final int LISTEN_PORT = 2342;


    public void start() {
        LOG.info("Starting server ... \n");

        try {
            LOG.log(Level.INFO, "Creating server socket and binding it on any if on port " + LISTEN_PORT);
            ServerSocket serverSocket = new ServerSocket(LISTEN_PORT);
            //Logging server socket infos
            logServerSocketAddress(serverSocket);


            while (true) {
                LOG.log(Level.INFO, "Waiting (blocking) for a connection request on {0} : {1}", new Object[]{serverSocket.getInetAddress(), Integer.toString(serverSocket.getLocalPort())});
                Socket clientSocket = serverSocket.accept();

                LOG.log(Level.INFO, "A client has arrived. We now have a client socket with following attributes:");
                logSocketAddress(clientSocket);

                LOG.log(INFO, "Serving client ... : a reader and a writer are being connected to client socket ");
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());

                String line;
                LOG.log(INFO, "Waiting for client request ... ");
                long i = 0;
                while ((line = reader.readLine()) != null && i++ != 1000000000000000000L) {
                    LOG.log(INFO, "Parsing client request ... ");
                    String response = parseClientRequest(line);
                    LOG.log(Level.FINEST, "Delevering response ... ");
                    writer.println(response);
                    writer.flush();
                }
                //closing reader and writer
                LOG.log(INFO, "Closing socket with client");
                clientSocket.close();
                reader.close();
                writer.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
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
     * A utility method to parse a client request. It must find an operator operand1 operand2
     *
     * @param request
     * @return
     */
    private String parseClientRequest(String request) {

        String response = "Answer is : ";
        String rqs = request;
        double result;
        String rqsArray[] = rqs.split(" ");
        if (rqsArray[0].contains("add") || rqsArray[0].contains("ADD")) {
            result = Double.parseDouble(rqsArray[1].trim()) + Double.parseDouble(rqsArray[2].trim());
            response += result;
        } else if (rqsArray[0].contains("mult") || rqsArray[0].contains("MULT")) {
            result = Double.parseDouble(rqsArray[1].trim()) * Double.parseDouble(rqsArray[2].trim());
            response += result;
        } else if (rqsArray[0].contains("sub") || rqsArray[0].contains("SUB")) {
            result = Double.parseDouble(rqsArray[1].trim()) - Double.parseDouble(rqsArray[2].trim());
            response += result;
        } else if (rqsArray[0].contains("div") || rqsArray[0].contains("DIV")) {
            if (Double.valueOf(rqsArray[2].trim()).equals((double) 0)) {
                response += "Error : Denominator should cannot be zero. (I dont know why ... but it's maths you know " +
                        "it got its own problems ...)";
            } else {
                result = Double.parseDouble(rqsArray[1]) / Double.parseDouble(rqsArray[2]);
                response += result;
            }
        }
        return response;

    }

    public static void main(String[] args) {
        ServerSide calc = new ServerSide();
        calc.start();
    }
}
