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
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    BufferedReader in = null;
    PrintWriter out = null;
    boolean shouldRun;
    boolean connected= false;
    int three_way_handshake = 0;

    /**
     * This method initiates the process. The server creates a socket and binds
     * it to the previously specified port. It then waits for clients in a infinite
     * loop. When a client arrives, the server will read its input line by line
     * and send back the data converted to uppercase. This will continue until
     * the client sends the "FIN" command.
     */

    /**
     * This method does the entire processing.
     */
    public void start() {
        LOG.info("Starting server...");

            LOG.log(Level.INFO, "Creating a server socket and binding it on any of the available network interfaces and on port {8080}", new Object[]{Integer.toString(Protocol.CALCULATOR_DEFAULT_PORT)});
        try {
            serverSocket = new ServerSocket(Protocol.CALCULATOR_DEFAULT_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

            while (true) {
                try {
                    LOG.log(Level.INFO, "Waiting (blocking) for a new client on port {8080}", Protocol.CALCULATOR_DEFAULT_PORT);
                    clientSocket = serverSocket.accept();
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new PrintWriter(clientSocket.getOutputStream());
                    String line;

                    shouldRun = true;

                    LOG.info("Reading until client sends FIN or closes the connection...\nAll the request made by the client are going to be printed below...\n");
                    while (shouldRun) {
                        //Reading the message sent by the client via the socket
                        line = in.readLine();
                        System.out.println(line);

                        //Answering the message appropriately to the server depending on which type of request the client is asking for
                        String[] tokens = line.split(" ");
                        switch (tokens[0].toUpperCase()) {
                            case (Protocol.CMD_SYN):
                                if(three_way_handshake == 0 && !connected) {
                                    sendNotification(Protocol.CMD_SYN_ACK);
                                    three_way_handshake ++;
                                }
                                else if (connected) {
                                    sendNotification("You already handshaked...");
                                }
                                else {
                                    handshakeError();
                                }
                                break;

                            case (Protocol.CMD_FIN):
                                    sendNotification(Protocol.CMD_ACK);
                                    connected = false;
                                    shouldRun = false;
                                    LOG.info("Disconnecting the client...");
                                break;

                            case (Protocol.CMD_ADD):
                                if(connected) {
                                    if (tokens.length == 3) {
                                        int result = Integer.parseInt(tokens[1]) + Integer.parseInt(tokens[2]);
                                        sendNotification(Integer.toString(result));
                                    } else {
                                        sendNotification("Invalid operation");
                                    }
                                }else{
                                    handshakeError();
                                }
                                break;
                            case (Protocol.CMD_SUB):
                                if(connected) {
                                    if (tokens.length == 3) {
                                        int result = Integer.parseInt(tokens[1]) - Integer.parseInt(tokens[2]);
                                        sendNotification(Integer.toString(result));
                                    } else {
                                        sendNotification("Invalid operation...");
                                    }
                                }else{
                                    handshakeError();
                                }
                                break;
                            case (Protocol.CMD_MULT):
                                if(connected) {
                                    if (tokens.length == 3) {
                                        int result = Integer.parseInt(tokens[1]) * Integer.parseInt(tokens[2]);
                                        sendNotification(Integer.toString(result));
                                    } else {
                                        sendNotification("Invalid operation...");
                                    }
                                }else{
                                    handshakeError();
                                }
                                break;
                            case (Protocol.CMD_DIV):
                                if(connected) {
                                    if (tokens.length == 3) {
                                        int result = Integer.parseInt(tokens[1]) / Integer.parseInt(tokens[2]);
                                        sendNotification(Integer.toString(result));
                                    } else {
                                        sendNotification("Invalid operation...");
                                    }
                                }else{
                                    handshakeError();
                                }
                                break;

                            case (Protocol.CMD_ACK):
                                if(three_way_handshake == 1 &&!connected) {
                                    sendNotification("You are now connected to the server...");
                                    connected = true;
                                }
                                else if (connected){
                                    sendNotification("You are already connected to the server...");
                                }
                                else{
                                    handshakeError();
                                }
                                break;

                            default:
                                if(connected)
                                    sendNotification("What? I only understand SYN, ACK, FIN, ADD, SUB, MULT, DIV...");
                                else{
                                    handshakeError();
                                }
                                break;
                        }

                    }

                    LOG.info("Cleaning up resources...");
                    three_way_handshake = 0;
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

    /**
     * This method prints a handshake error message.
     */
    private void handshakeError(){
        sendNotification("Please handshake before using this application...");
        three_way_handshake = 0;
    }


    /**
     * This method sends notifications to the client.
     */
    private void sendNotification(String message) {
        out.println(message);
        out.flush();
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");
        Server server = new Server();
        server.start();
    }

}