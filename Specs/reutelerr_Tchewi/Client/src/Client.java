import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A very simple example of TCP client
 *
 * @author Olivier Liechti (Modified by Quentin Le Ray)
 */
public class Client {

    static final Logger LOG = Logger.getLogger(Client.class.getName());

    //Change ip adress here
    private final String REQUEST_HOST = "127.0.0.1";
    private final int REQUEST_PORT = 2000;

    /**
     * This method does the entire processing.
     */
    public void start() {
        LOG.info("Starting client...");

        Socket serverSocket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;
        String operation;
        String result;

        try {
            LOG.log(Level.INFO, "Creating a server socket and binding it on any of the available network interfaces and on port {0}", new Object[]{Integer.toString(REQUEST_PORT)});
            serverSocket = new Socket(REQUEST_HOST, REQUEST_PORT);
            logSocketAddress(serverSocket);

            //Quand on active il sort quand-même du while mais en plein milieu de la requête, ça n'a aucun sens
            //while (operation.compareTo("none")) {

                System.out.println("Quelle opération voulez-vous effectuer ? (DoOp x op y)");
                reader = new BufferedReader(new InputStreamReader(System.in));
                operation = reader.readLine();
                reader.close();

                LOG.log(Level.INFO, "Getting a Writer and a Reader connected to the client socket...");

                writer = new PrintWriter(serverSocket.getOutputStream());
                reader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

                LOG.log(Level.INFO, "Writing the operation into the stream");

                //Test pour que ça fonctionne avec l'un ou l'autre
                //writer.println(operation);
                writer.write(operation);
                writer.flush();

                LOG.log(Level.INFO, "Waiting the response from server ...");

                result = reader.readLine();

                System.out.println("Résultat : " + result);

                //Pas besoin puisque le finally dans le try catch le fait tout le temps et que le wile marche po
                //reader.close();
                //writer.close();
                //serverSocket.close();


            //}

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        } finally {
            LOG.log(Level.INFO, "We are done. Cleaning up resources, closing streams and sockets...");
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            writer.close();
            try {
                serverSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                serverSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

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

        Client client = new Client();
        client.start();
    }

}
