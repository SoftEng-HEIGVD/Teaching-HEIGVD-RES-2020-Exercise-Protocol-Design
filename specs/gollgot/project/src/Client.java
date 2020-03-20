import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * This class allows the user to send simple calculation requests to a server.
 *
 * @author Robin Demarta
 */
public class Client {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean connected = false;

    public final static String DEFAULT_SERVER_ADDRESS = "localhost";
    public final static int DEFAULT_PORT = 12500;

    private final static String INIT_MSG = "HELLO";
    private final static String SERVER_READY_MSG = "READY";
    private final static String SERVER_ERROR_MSG = "ERROR";
    private final static String EXIT_MSG = "BYE";

    /**
     * Establish connection with server
     * @param serverAddress
     * @param port
     */
    public void connect(String serverAddress, int port) {
        try {
            clientSocket = new Socket(serverAddress, port);
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));

            // Initiate connection

            out.println(INIT_MSG);
            out.flush();

            String message = in.readLine();
            if (!message.equalsIgnoreCase(SERVER_READY_MSG)) {
                System.out.println("Server is busy. Try again. " + message);
            } else {
                connected = true;
            }
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Asks server to calculate something
     * @param calculation
     */
    public void calculate(String calculation) {
        try{
            if(!connected) {
                System.out.println("Not connected to server.");
                return;
            }

            out.println(calculation);
            out.flush();

            String message = in.readLine();
            if(message.equalsIgnoreCase(SERVER_ERROR_MSG)) {
                System.out.println("Server responded with error.");
            } else {
                System.out.println(message);
            }
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Exit app and tell server
     */
    public void exit() {
        out.println(EXIT_MSG);
        out.flush();
        cleanUp();
    }

    /**
     * Close connections
     */
    private void cleanUp() {
        try {
            out.close();
            in.close();
            clientSocket.close();
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
