package doeni98;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        ClacServer clacServer = new ClacServer();
        clacServer.start();
    }
}

class ClacServer{
    static final Logger LOG = Logger.getLogger(ClacServer.class.getName());

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private int listenPort = 6942;
    private BufferedReader reader = null;
    private PrintWriter writer = null;
    public void start(){

        try {
            LOG.log(Level.INFO, "Starting Server Socket");
            serverSocket = new ServerSocket(listenPort);
            logServerSocketAddress(serverSocket);

            while (true) {
                LOG.log(Level.INFO, "Waiting on connection");
                clientSocket = serverSocket.accept();

                LOG.log(Level.INFO, "New Client:");
                logSocketAddress(clientSocket);

                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream());

                String request = reader.readLine();
                LOG.log(Level.INFO, "Got request: " + request);
                String answer;
                try {
                    Double result = calc(request);
                    answer = result.toString();
                } catch (Exception calcExc) {
                    answer = "ERROR: " + calcExc.getMessage();
                }
                LOG.log(Level.INFO, "Responding to request with:\n" + answer);
                writer.println(answer);
                writer.flush();
                reader.close();
                writer.close();
                clientSocket.close();

            }

        }catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        } finally {
            LOG.log(Level.INFO, "We are done. Cleaning up resources, closing streams and sockets...");
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(ClacServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            writer.close();
            try {
                clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(ClacServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                serverSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(ClacServer.class.getName()).log(Level.SEVERE, null, ex);
            }
    }


}

    private double calc(String request) throws Exception{
        double operand1, operand2;
        String requestSplit[];
        if((requestSplit = request.split(";")).length != 3){
            throw new Exception("Wrong formatted request");
        }
        operand1 = Double.parseDouble(requestSplit[0]); // can throw
        operand2 = Double.parseDouble(requestSplit[2]); // can throw
        switch (requestSplit[1]){
            case "+":
                return operand1 + operand2;
            case "-":
                return operand1 - operand2;
            case "*":
                return operand1 * operand2;
            case "/":
                return operand1 / operand2;
            default:
                throw new Exception("Unknown operator");
        }

    }
    /**
    * A utility method to print server socket information
    * Author: Liechti Olivier
    * @param serverSocket the socket that we want to log
    */
    private void logServerSocketAddress(ServerSocket serverSocket) {
        LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{serverSocket.getLocalSocketAddress()});
        LOG.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(serverSocket.getLocalPort())});
        LOG.log(Level.INFO, "               is bound: {0}", new Object[]{serverSocket.isBound()});
    }

    /**
     * A utility method to print socket information
     * Author: Liechti Olivier
     * @param clientSocket the socket that we want to log
     */
    private void logSocketAddress(Socket clientSocket) {
        LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{clientSocket.getLocalAddress()});
        LOG.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(clientSocket.getLocalPort())});
        LOG.log(Level.INFO, "  Remote Socket address: {0}", new Object[]{clientSocket.getRemoteSocketAddress()});
        LOG.log(Level.INFO, "            Remote port: {0}", new Object[]{Integer.toString(clientSocket.getPort())});
    }
}