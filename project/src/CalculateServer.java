import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A very simple example of TCP server. When the server starts, it binds a
 * server socket on any of the available network interfaces and on port 2205. It
 * then waits until one (only one!) client makes a connection request. When the
 * client arrives, the server does not even check if the client sends data. It
 * simply writes the current time, every second, during 15 seconds.
 *
 * To test the server, simply open a terminal, do a "telnet localhost 2205" and
 * see what you get back. Use Wireshark to have a look at the transmitted TCP
 * segments.
 *
 * @author Olivier Liechti
 */
public class CalculateServer {

    static final Logger LOG = Logger.getLogger(CalculateServer.class.getName());

    private final int TEST_DURATION = 15000;
    private final int PAUSE_DURATION = 1000;
    private final int NUMBER_OF_ITERATIONS = TEST_DURATION / PAUSE_DURATION;
    private final int LISTEN_PORT = 2205;
    private final int NB_CHARS = 50;

    /**
     * This method does the entire processing.
     */
    public void start() {
        LOG.info("Starting CalculateServer...");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {

            serverSocket = new ServerSocket(LISTEN_PORT);
            logServerSocketAddress(serverSocket);

            while (true) {
                clientSocket = serverSocket.accept();

                logSocketAddress(clientSocket);

                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream());

                char[] buffer = new char[NB_CHARS];

                int length = reader.read(buffer, 0, NB_CHARS);

                String calculateAsked = new String(buffer, 0, length);

                calculateAsked = calculateAsked.replaceAll("(\\n|\\r)","");
                String[] calculSplited = calculateAsked.split(" ");

                Queue<Integer> numbers = new LinkedList<>();

                for(String current : calculSplited){

                    System.out.println(numbers);

                    if(current.matches("^([+*/\\-])$")) {
                        Integer fstOperand = numbers.poll();
                        Integer sndOperand = numbers.poll();
                        int tmp = 0;
                        switch (current.charAt(0)){
                            case '+':
                                tmp = fstOperand + sndOperand;
                                break;
                            case '-':
                                tmp = fstOperand - sndOperand;
                                break;
                            case '*':
                                tmp = fstOperand * sndOperand;
                                break;
                            case '/':
                                tmp = fstOperand / sndOperand;
                                break;
                            default:
                                break;
                        }
                        numbers.add(tmp);
                    }
                    else{
                        numbers.add(Integer.valueOf(current));
                    }
                }

                writer.println(String.format("Result : %d", numbers.poll()));
                writer.flush();

                clientSocket.close();


            }

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        } finally {
            LOG.log(Level.INFO, "We are done. Cleaning up resources, closing streams and sockets...");
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(CalculateServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            writer.close();
            try {
                clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(CalculateServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                serverSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(CalculateServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * A utility method to print server socket information
     *
     * @param serverSocket the socket that we want to log
     */
    private void logServerSocketAddress(ServerSocket serverSocket) {
        LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{serverSocket.getLocalSocketAddress()});
        LOG.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(serverSocket.getLocalPort())});
        LOG.log(Level.INFO, "               is bound: {0}", new Object[]{serverSocket.isBound()});
    }

    /**
     * A utility method to print socket information
     *
     * @param clientSocket the socket that we want to log
     */
    private void logSocketAddress(Socket clientSocket) {
        LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{clientSocket.getLocalAddress()});
        LOG.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(clientSocket.getLocalPort())});
        LOG.log(Level.INFO, "  Remote Socket address: {0}", new Object[]{clientSocket.getRemoteSocketAddress()});
        LOG.log(Level.INFO, "            Remote port: {0}", new Object[]{Integer.toString(clientSocket.getPort())});
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

        CalculateServer server = new CalculateServer();
        server.start();
    }

}