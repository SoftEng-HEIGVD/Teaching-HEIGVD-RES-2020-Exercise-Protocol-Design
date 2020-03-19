import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalculatorServer {

    static final Logger LOG = Logger.getLogger(CalculatorServer.class.getName());
    private final int LISTEN_PORT = 220;

    private void start() {
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

                String clientInput = reader.readLine();
                writer.println("Welcome to the Calculator Server!");
                writer.println("Press \"exit\" if you want to quit.");
                writer.print("Enter your operation : ");
                writer.flush();

                /*while(clientInput != "exit") {
                    writer.println("Good choice...");
                    writer.println("Another one ? : ");
                }*/

                reader.close();
                writer.close();
                clientSocket.close();
            }

        } catch (IOException ex) {
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


    public static void main(String[] args) {

        CalculatorServer server = new CalculatorServer();
        server.start();

    }

}
