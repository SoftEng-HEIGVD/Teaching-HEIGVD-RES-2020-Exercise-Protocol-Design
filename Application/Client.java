import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a simple client for our custom presence protocol.
 * When the client connects to a server, a thread is started to listen for
 * notifications sent by the server.
 *
 * @author Olivier Liechti
 */
public class Client {

    final static Logger LOG = Logger.getLogger(Client.class.getName());

    Socket clientSocket;
    BufferedReader in;
    PrintWriter out;
    boolean connected = true;
    String userName;
    String line;


      /**
     * This method is used to connect to the server and to inform the server that
     * the user "behind" the client has a name (in other words, the HELLO command
     * is issued after successful connection).
     *
     *
     */
    public void connect() {
        try {
            LOG.log(Level.INFO, "I am a new client on port {8080}", Protocol.CALCULATOR_DEFAULT_PORT);

            clientSocket = new Socket(Protocol.CALCULATOR_DEFAULT_ADDRESS, Protocol.CALCULATOR_DEFAULT_PORT);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream());
            connected = true;
            LOG.info("Enter something please...");

            while (connected) {
                //Writing a message
                BufferedReader msgInput = new BufferedReader(new InputStreamReader(System.in));
                String msg = msgInput.readLine();
                //Sending the message to the server via the client socket
                out.println(msg);
                out.flush();
                //Reading the response of the server
                line = in.readLine();
                System.out.println(line);
                //If the message sent by the client was FIN, disconnect the client
                if(msg.equals("FIN")) {
                    connected = false;
                    LOG.info("Disconnecting the client...");
                }
            }
            clientSocket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Unable to connect to server: {0}", e.getMessage());
            cleanup();
            return;
        }

    }


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
    private void sendNotification(String message) {
        out.println(message);
        out.flush();
    }
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

        Client client = new Client();
        client.connect();
    }

}