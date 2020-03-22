import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A very simple example of TCP server. When the server starts, it binds a
 * server socket on any of the available network interfaces and on port 2000. It
 * then waits until one (only one!) client makes a connection request.
 *
 * @author Robin Reuteler (modified copy of the StreamingTimeServer by Olivier Lietchi)
 */
public class CalculatorServer {

    static final Logger LOG = Logger.getLogger(CalculatorServer.class.getName());

    private final int TEST_DURATION = 15000;
    private final int PAUSE_DURATION = 1000;
    private final int NUMBER_OF_ITERATIONS = TEST_DURATION / PAUSE_DURATION;
    private final int LISTEN_PORT = 2000;

    /**
     * This method does the entire processing.
     */
    public void start() {
        LOG.info("Starting server...");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            LOG.log(Level.INFO, "Creating a server socket and binding it on any of the available network interfaces and on port {0}", new Object[]{Integer.toString(LISTEN_PORT)});
            serverSocket = new ServerSocket(LISTEN_PORT);
            logServerSocketAddress(serverSocket);

            while (true) {
                LOG.log(Level.INFO, "Waiting (blocking) for a connection request on {0} : {1}", new Object[]{serverSocket.getInetAddress(), Integer.toString(serverSocket.getLocalPort())});
                clientSocket = serverSocket.accept();

                LOG.log(Level.INFO, "A client has arrived. We now have a client socket with following attributes:");
                logSocketAddress(clientSocket);

                LOG.log(Level.INFO, "Getting a Reader and a Writer connected to the client socket...");
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream());

                LOG.log(Level.INFO, "Reading the client's request");
                String request = reader.readLine();
                String[] params = request.split(" ");
                double result=0;
                if(params[0].equals("DoOp"))
                {
                    result = DoOp(params[2].charAt(0), Double.parseDouble(params[1]), Double.parseDouble(params[3]));
                }
                writer.write("Result " + String.valueOf(result));
                writer.flush();

                reader.close();
                writer.close();
                clientSocket.close();


            }

        } catch (IOException /*| InterruptedException*/ ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        } finally {
            LOG.log(Level.INFO, "We are done. Cleaning up resources, closing streams and sockets...");
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(CalculatorServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            writer.close();
            try {
                clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(CalculatorServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                serverSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(CalculatorServer.class.getName()).log(Level.SEVERE, null, ex);
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

    double DoOp(char op, double x, double y)
    {
        switch(op)
        {
            case '+' : return x + y;
            case '-' : return x - y;
            case '*' : return x * y;
            case '/' : return x / y;
            default : return 0;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

        CalculatorServer server = new CalculatorServer();
        server.start();
    }

}
