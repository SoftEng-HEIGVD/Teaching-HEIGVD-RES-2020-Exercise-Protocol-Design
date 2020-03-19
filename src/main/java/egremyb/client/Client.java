package egremyb.client;

import egremyb.common.Protocol;
import java.io.*;
import java.net.Socket;

public class Client {
    private final static int    BUFFER_SIZE = 1024;
    private final static String NO_CONNECTION_OPENED = "There is no connection opened to the server.\n";
    private final static String UNEXPECTED_RESPONSE  = "The response of the server was unexpected.\n";
    private final static String BAD_REQUEST          = "The operation must have been badly written.\n";

    private Socket       clientSocket;
    private OutputStream os;
    private InputStream  is;
    private String       ip;
    private int          port;
    private boolean      connected;

    /**
     * Send a String to the server
     * @param s String to send
     * @throws IOException if an error occurred while writing
     */
    private void sendRequest(String s) throws IOException {
        os.write((s + '\n').getBytes());
        os.flush();
    }

    /**
     * Get the response of the server
     * @return Response as a String
     * @throws IOException if an error occurred while reading
     */
    private String getResponse() throws IOException{
        return getResponse(null);
    }

    /**
     * Get the response of the server
     * @param expected Expected response of th server
     * @return Response as a String
     * @throws IOException if an error occurred while reading
     *                     or if the response wasn't the one expected
     */
    private String getResponse(String expected) throws IOException{
        byte[] buffer = new byte[BUFFER_SIZE];
        int    newBytes;
        ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
        String response;
        // read the response
        while ((newBytes = is.read(buffer)) != -1) {
            responseBuffer.write(buffer, 0, newBytes);
        }
        response = responseBuffer.toString();
        // return response
        if (expected == null || response.equals(expected)) {
            return response;
        } else {
            throw new IOException(UNEXPECTED_RESPONSE);
        }
    }

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
        clientSocket   = null;
        os             = null;
        is             = null;
        this.ip        = ip;
        this.port      = port;
        this.connected = false;
    }

    /**
     * Open a connection with to the server
     */
    public void openConnection() {
        // close open connection if there is one
        if (connected) {
            closeConnection();
        }
        // try to connect to the server
        try {
            // init the socket and streams
            clientSocket = new Socket(ip, port);
            os           = clientSocket.getOutputStream();
            is           = clientSocket.getInputStream();
            // send HELLO message
            sendRequest(Protocol.CMD_HELLO);
            // get response expected as a WELCOME message
            getResponse(Protocol.CMD_WELCOME);
            connected = true;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            closeConnection();
        }
    }

    /**
     * Close the connection to the server
     */
    public void closeConnection() {
        // check if a connection was open
        if (connected) {
            return;
        }
        // close Input Stream
        try {
            is.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        // close Output Stream
        try {
            sendRequest(Protocol.CMD_BYE);
            os.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        // close Socket
        try {
            clientSocket.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
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
        String response;
        if (connected) {
            try {
                // send request
                sendRequest(Integer.toString(operand1) + ' ' + operator + ' ' + operand2);
                // check the response of the server
                response = getResponse();
                if (response.equals(Protocol.CMD_WRONG)) {
                    System.out.println(BAD_REQUEST);
                    return 0;
                }
                // return the response as an int
                return Integer.parseInt(response);
            } catch (IOException | NumberFormatException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            System.out.println(NO_CONNECTION_OPENED);
        }

        return 0;
    }
}