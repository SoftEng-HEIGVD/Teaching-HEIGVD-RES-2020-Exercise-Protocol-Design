package egremyb.client;

import egremyb.common.Protocol;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    final static Logger LOG         = Logger.getLogger(Client.class.getName());
    final static int    BUFFER_SIZE = 1024;

    Socket       clientSocket;
    OutputStream os;
    InputStream  is;
    String       ip;
    int          port;

    /**
     * Set ip and port to default
     */
    public Client() {
        this("localhost");
    }

    /**
     * Set given ip and the default port
     * @param ip address of server
     */
    public Client(String ip) {
        this(ip, Protocol.DEFAULT_PORT);
    }

    /**
     * Set given ip and port
     * @param ip address of server
     * @param port port of server
     */
    public Client(String ip, int port) {
        clientSocket = null;
        os           = null;
        is           = null;
        this.ip      = ip;
        this.port    = port;
    }

    /**
     * Open a connection with to the server
     */
    public void openConnection() {
        try {
            // init the socket and streams
            clientSocket = new Socket(ip, port);
            os           = clientSocket.getOutputStream();
            is           = clientSocket.getInputStream();
            // send HELLO message
            os.write(Protocol.CMD_HELLO.getBytes());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            closeConnection();
        }
    }

    /**
     * Close the connection to the server
     */
    public void closeConnection() {
        // check if a connection was open
        if (clientSocket == null || clientSocket.isClosed()) {
            return;
        }
        // close Input Stream
        try {
            is.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        // close Output Stream
        try {
            os.write(Protocol.CMD_BYE.getBytes());
            os.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        // close Socket
        try {
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Send a calculation for the server to compute
     * @param operand1 First operand
     * @param operator Operator
     * @param operand2 Second operand
     * @return Result of calculation
     */
    public int sendCalculationToCompute(final int operand1, final char operator, final int operand2) {
        String request;
        byte[] buffer = new byte[BUFFER_SIZE];
        int    newBytes;

        try {
            // send request
            request = Integer.toString(operand1) + ' ' + operator + ' ' + Integer.toString(operand2);
            os.write(request.getBytes());

            // wait and read the response
            ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
            while ((newBytes = is.read(buffer)) != -1) {
                responseBuffer.write(buffer, 0, newBytes);
            }
            LOG.log(Level.INFO, "Response from server to \"" + request + "\" : ");
            LOG.log(Level.INFO, responseBuffer.toString());

            // return the response as an int
            return Integer.parseInt(responseBuffer.toString());
        } catch (IOException | NumberFormatException ex) {
            ex.printStackTrace();
        }

        return 0;
    }
}