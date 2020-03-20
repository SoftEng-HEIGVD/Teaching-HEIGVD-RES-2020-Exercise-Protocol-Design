import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This server is based on the single threaded server given by Olivier Liechti given in class.
 * We will adapt it so that our specifications defined on the protocol.md are respected.
 * This class implements a single-threaded TCP server. It is able to interact
 * with only one client at the time. If a client tries to connect when
 * the server is busy with another one, it will have to wait.
 *
 * @author Mathias Maillard
 * @author Rosy-Laure Wonjamouna
 */
public class Server {

    final static Logger LOG = Logger.getLogger(Server.class.getName());

    int port;

    /**
     * Constructor
     * @param port the port to listen on
     */
    public Server(int port) {
        this.port = port;
    }

    /**
     * This method initiates the process. The server creates a socket and binds
     * it to the previously specified port. It then waits for clients in a infinite
     * loop. When a client arrives, the server will read its input line by line
     * and send back the data converted to uppercase. This will continue until
     * the client sends the "FIN" command.
     */
    public void serveClients() {
        ServerSocket serverSocket;
        Socket clientSocket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        boolean shouldRun;
        boolean connected;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return;
        }

        while (true) {
            try {

                LOG.log(Level.INFO, "Waiting (blocking) for a new client on port {1000}", port);
                clientSocket = serverSocket.accept();
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream());
                String line;

                shouldRun = true;
                connected = false;

                out.println("Welcome to the Single-Threaded Server.\nSend me text lines and conclude with the FIN command.");
                out.flush();
                LOG.info("Reading until client sends FIN or closes the connection...");
                while ( (shouldRun) && (line = in.readLine()) != null ) {
                    String[] tokens = line.split(" ");
                    switch(tokens[0].toUpperCase()){
                        case (Protocol.CMD_SYN):
                            sendNotification(Protocol.CMD_SYN_ACK);
                            break;
                        case (Protocol.CMD_ACK):
                            if (!connected)
                                connected = true;
                            break;
                        case (Protocol.CMD_FIN):
                            if (connected) {
                                sendNotification(Protocol.CMD_ACK);
                                connected = false;
                            }
                            break;
                        case (Protocol.CMD_KILL):
                            sendNotification("KILL command received. Bringing server down...");
                            shutdown();
                            break;
                        case (Protocol.CMD_ADD):
                            if(tokens.length() == 3 && connected) {
                                int result = tokens[1] + tokens[2];
                                sendNotification(Integer.toString(result));
                            }
                            else
                                sendNotification("Invalid operation");
                            break;
                        case (Protocol.CMD_SUB):
                            if (tokens.length() == 3 && connected){
                                int result = tokens[1] - tokens[2];
                                sendNotification(Integer.toString(result));
                            }
                            else
                                sendNotification("Invalid operation");
                            break;
                    }

                }

                LOG.info("Cleaning up resources...");
                clientSocket.close();
                in.close();
                out.close();

            } catch (IOException ex) {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ex1) {
                        LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                    }
                }
                if (out != null) {
                    out.close();
                }
                if (clientSocket != null) {
                    try {
                        clientSocket.close();
                    } catch (IOException ex1) {
                        LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                    }
                }
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    public void sendNotification(String message) {
        out.println(message);
        out.flush();
    }

    private void shutdown() {
        LOG.info("Shutting down server...");
        shouldRun = false;
        connected = false;
        try {
            serverSocket.close();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}