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
public class PresenceClient {

    final static Logger LOG = Logger.getLogger(PresenceClient.class.getName());

    Socket clientSocket;
    BufferedReader in;
    PrintWriter out;
    boolean connected = false;
    String userName;

    /**
     * This inner class implements the Runnable interface, so that the run()
     * method can execute on its own thread. This method reads data sent from the
     * server, line by line, until the connection is closed or lost.
     */

    /**
     * This method is used to connect to the server and to inform the server that
     * the user "behind" the client has a name (in other words, the HELLO command
     * is issued after successful connection).
     *
     * @param serverAddress the IP address used by the Presence Server
     * @param serverPort the port used by the Presence Server
     * @param userName the name of the user, used as a parameter for the HELLO command
     */
    public void connect(String serverAddress, int serverPort) {
        try {
            clientSocket = new Socket(serverAddress, serverPort);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream());
            // Let us send the SYN command to inform the server about our intention to connect
            sendNotification(Protocol.CMD_SYN);
            while(!connected && line = in.readLine != null){
                String tokens = line.split(" ");
                switch(tokens[0].toUpperCase()){
                    case(Protocol.CMD_SYN_ACK):
                        connected = true;
                        sendNotification(Protocol.CMD_ACK);
                        break;
                    default:
                        sendNotification("You are not connected to the server yet");
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Unable to connect to server: {0}", e.getMessage());
            cleanup();
            return;
        }
        //we listen for server notifications. We read data sent from the
        //server, line by line, until the connection is closed or lost.
        String notification;
        try {
            while ((connected && (notification = in.readLine()) != null)) {
                LOG.log(Level.INFO, "Server notification for {1}: {0}", new Object[]{notification});
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Connection problem in client used by {1}: {0}", new Object[]{e.getMessage()});
            connected = false;
        } finally {
            cleanup();
        }

    }

    public void disconnect() {
        LOG.log(Level.INFO, "{0} has requested to be disconnected.", userName);
        connected = false;
        sendNotification(Protocol.CMD_FIN);
        cleanup();
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
    public void sendNotification(String message) {
        out.println(message);
        out.flush();
    }


}