import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a single-threaded TCP server. It is able to interact
 * with only one client at the time. If a client tries to connect when
 * the server is busy with another one, it will have to wait.
 *
 * @author Olivier Liechti
 */
public class CalculatorSingleThread {

    final static Logger LOG = Logger.getLogger(CalculatorSingleThread.class.getName());

    int port;

    private final static String SERVER_IP_LOCAL = "127.0.0.1";

    /**
     * Constructor
     *
     * @param port the port to listen on
     */
    public CalculatorSingleThread(int port) {
        this.port = port;
    }

    /**
     * This method initiates the process. The server creates a socket and binds
     * it to the previously specified port. It then waits for clients in a infinite
     * loop. When a client arrives, the server will read its input line by line
     * and send back the data converted to uppercase. This will continue until
     * the client sends the "BYE" command.
     */
    public void serveClients() {
        ServerSocket serverSocket;
        Socket clientSocket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            InetAddress address = InetAddress.getByName(SERVER_IP_LOCAL);
            serverSocket = new ServerSocket(port, 1, address);
            LOG.log(Level.INFO, "Socket Address {0}", serverSocket.getLocalSocketAddress());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return;
        }

        while (true) {
            try {
                LOG.log(Level.INFO, "Waiting (blocking) for a new client on port {0}", port);
                clientSocket = serverSocket.accept();
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream());
                String line;
                boolean shouldRun = true;

                out.println(
                        "Welcome to the Single-Threaded Calculator Server.\nWhat operation would you like to do ? " +
                                "(quit with END line)");
                out.flush();
                line = in.readLine();
                while ((shouldRun) && line != null) {
                    if (line.equalsIgnoreCase("end")) {
                        LOG.log(Level.INFO, "Close Connection");
                        shouldRun = false;
                    }

                    LOG.log(Level.INFO, "Message receive {0}", line);

                    String[] inputArray = line.split(" ");

                    if (inputArray[0].equals("COMPUTE")) {
                        String[] subInputArray = Arrays.copyOfRange(inputArray, 1,4 );
                        if (CalculatorUtils.isInputValid(subInputArray)) {
                            double result = CalculatorUtils.computeInput(subInputArray);
                            out.println("= " + result);
                            out.flush();
                        } else {
                            out.println("Invalid input.");
                            out.flush();
                        }
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

}