/**
 * @author : Kayoumi Doran
 */

package ch.heigvd.res.exo;

import ch.heigvd.res.exo.Calculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalculatorServer {
    private final int LISTEN_PORT = 49153;
    static final Logger LOG = Logger.getLogger(CalculatorServer.class.getName());
    static final String QUIT = "QUIT";

    /**
     * Method starting a server capable of performing small arithmetic operations based on a client input.
     * The server starts, waits for a client to connect and then
     * performs the calculation once asked and returns the result, then goes back to waiting state
     */
    private void start() {
        LOG.info("Starting server...");

        ServerSocket server = null;
        Socket client = null;
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            Calculator calculator = new Calculator();

            server = new ServerSocket(LISTEN_PORT);

            // Watining until a client tries to connect and establish streams to get and send data
            client = server.accept();
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new PrintWriter(client.getOutputStream());

            // Reading client's input
            String cmd;
            while((cmd = reader.readLine()) != null) {
                // TODO : no need for that, checked on the client side
                if(cmd.equalsIgnoreCase(QUIT))
                    break;

                // Splits the input and performs the calculation
                String[] operation = cmd.split(" ");

                int res = calculator.calculate(operation[0], operation[1], operation[2]);
                writer.println("> " + cmd.toUpperCase() + " = " + res);
                writer.flush();
            }
            // Closes streams
            writer.close();
            reader.close();
        } catch(IOException e) {
            LOG.log(Level.SEVERE, e.getMessage());
        } finally {
            LOG.log(Level.INFO, "We are done. Cleaning up resources, closing streams and sockets...");
            try {
                reader.close();
            } catch(IOException ex) {
                Logger.getLogger(CalculatorServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            writer.close();
            try {
                client.close();
            } catch(IOException ex) {
                Logger.getLogger(CalculatorServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                server.close();
            } catch(IOException ex) {
                Logger.getLogger(CalculatorServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Creates a new server and start it
        CalculatorServer server = new CalculatorServer();
        server.start();
    }
}
