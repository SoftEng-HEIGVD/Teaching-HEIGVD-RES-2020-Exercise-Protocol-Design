import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

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

    private final static Logger LOG = Logger.getLogger(Client.class.getName());

    /**
     * Establish connection with server
     * @param serverAddress
     * @param port
     */
    public Client(String serverAddress, int port) {
        try {
            clientSocket = new Socket(serverAddress, port);
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));

            // Initiate connection
            out.println(INIT_MSG);
            out.flush();

            String message = in.readLine();
            if (!message.equalsIgnoreCase(SERVER_READY_MSG)) {
                LOG.warning("Server is busy. Try again. " + message);
            } else {
                connected = true;
                LOG.info("Connected to server.");
            }
        } catch(IOException e) {
            LOG.warning(e.getMessage());
        }
    }

    /**
     * Asks server to calculate something
     * @param calculation
     */
    public void calculate(String calculation) {
        try{
            if(!connected) {
                LOG.severe("Not connected to server.");
                return;
            }

            out.println(calculation);
            out.flush();

            String message = in.readLine();
            if(message.equalsIgnoreCase(SERVER_ERROR_MSG)) {
                LOG.severe("Server responded with error.");
            } else { // Server did the calculation, display result
                LOG.info("Result: " + message + "\n");
            }
        } catch(IOException e) {
            LOG.warning(e.getMessage());
        }
    }

    /**
     * Exit app and tell server
     */
    public void close() {
        if(!connected) {
            LOG.severe("Not connected to server.");
            return;
        }

        out.println(EXIT_MSG);
        out.flush();
        cleanUp();
    }

    /**
     * Close connections
     */
    private void cleanUp() {
        try {
            if(out != null) out.close();
            if(in != null) in.close();
            if(clientSocket != null) clientSocket.close();
        } catch(IOException e) {
            LOG.info(e.getMessage());
        }
    }

    /**
     * Connect to server using args parameters and performs simple calculation tests.
     * @param args [0] : server address, [1] : port
     */
    public static void main(String[] args) {
        try {
            if(args.length < 2) {
                System.out.println("There must be two arguments.");
                return;
            }

            Client client = new Client(args[0], Integer.parseInt(args[1]));
            client.calculate("37 + 5"); // Correct calculation request
            client.calculate("Hey"); // Wrong message
            client.calculate("68 + 29"); // Another correct calculation request

            client.close();
        } catch(NumberFormatException e){
            LOG.warning("Wrong argument, the port must be an integer value ");
        }

    }

}
