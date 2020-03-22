package ch.heigvd.res.client;

import ch.heigvd.res.common.Operator;
import ch.heigvd.res.common.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    final static Logger LOG = Logger.getLogger(Client.class.getName());
    static int nbClient = 0;

    Socket clientSocket;
    BufferedReader in;
    PrintWriter out;
    boolean connected = false;
    private int idClient = ++nbClient;

    /**
     * Default constructor of Client
     */
    public Client() {
        // Ignore logs under WARNING level
        LOG.setLevel(Level.WARNING);
    }

    /**
     * This method is used to connect to the server on the default port.
     *
     * @param serverAddress the IP address used by the Presence Server
     */
    public void connect(String serverAddress) {
        connect(serverAddress, Protocol.DEFAULT_PORT);
    }

    /**
     * This method is used to connect to the server.
     *
     * @param serverAddress the IP address used by the Presence Server
     * @param serverPort the port used by the Presence Server
     */
    public void connect(String serverAddress, int serverPort) {
        LOG.log(Level.INFO, "Client {0} trying to connect", idClient);
        try {
            clientSocket = new Socket(serverAddress, serverPort);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),Protocol.CHARSET));
            out = new PrintWriter(clientSocket.getOutputStream());
            connected = true;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Unable to connect to server: {0}", e.getMessage());
            cleanup();
            return;
        }

        // Let us send the HELLO command to connect us to the server
        out.println(Protocol.CMD_HELLO);
        out.flush();
        try {
            //See what the server responded to us
            LOG.log(Level.INFO, String.format("Server response: %s", in.readLine()));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * Disconnect the client from the server
     */
    public void disconnect() {
        LOG.log(Level.INFO, "Attempting to disconnect");
        connected = false;
        //If the client didn't succeeded the connection out is null
        if(out != null) {
            out.println(Protocol.CMD_BYE);
            cleanup();
        }
        else{
            LOG.log(Level.INFO, "Client not connected impossible to disconnect");
        }
    }

    /**
     * Closes in, out and closes socket
     */
    private void cleanup() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        if (out != null) {
            out.close();
        }

        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * Sends a calculation to the server and returns the reply of the server.
     * @param a first operand
     * @param b second operand
     * @param op a compatible Operator
     * @return String reply of the server
     */
    public String getCalculationResult(int a, int b, Operator op) {
        LOG.log(Level.INFO, "Client {0} trying to calculate", idClient);
        if (!connected) {
            LOG.log(Level.SEVERE, "Attempting to run getCalculationResult when not connected");
            return "";
        }

        String str = String.format("%d %s %d", a, op, b);
        LOG.log(Level.INFO, "Sending to server: {0}", str);
        out.println(str);
        out.flush();
        String response = "";
        try {
            response = in.readLine();
            LOG.log(Level.INFO, String.format("Server response: %s", response));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return response;
    }
}
