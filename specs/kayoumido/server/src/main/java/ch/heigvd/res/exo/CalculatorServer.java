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

    /**
     * This class has the job to accept any incoming connection request, by creating a new server socket and accept the request.
     * For every client, a new instance of the `Worker` is created and passed to the `Thread` and start the newly created thread.
     * And then it returns in a waiting state for a new client.
     */
    private class Receptionist implements Runnable {

        @Override
        public void run() {
            ServerSocket server;

            try {
                server = new ServerSocket(LISTEN_PORT);
            } catch(IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
                return;
            }

            while(true) {
                LOG.info("Waiting (blocking) for a new client...");
                try {
                    // accept the new client and start a new thread to handle it
                    Socket client = server.accept();
                    new Thread(new Worker(client)).start();

                } catch(IOException e) {
                    // something bad happened
                    LOG.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }

    /**
     * This class will take care of a single client.
     * Uses the input and output streams connected to the socket to receive the calculation asked by the client
     * then return the result, then goes back to a waiting state.
     */
    private class Worker implements Runnable {

        Socket client;
        BufferedReader reader;
        PrintWriter writer;

        public Worker(Socket client) {
            try {
                this.client = client;
                this.reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                this.writer = new PrintWriter(client.getOutputStream());
            } catch(IOException e) {
                LOG.log(Level.SEVERE, e.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                Calculator calculator = new Calculator();

                // Reading client's input
                String cmd;
                while((cmd = this.reader.readLine()) != null) {

                    // Splits the input and performs the calculation
                    String[] operation = cmd.split(" ");

                    // Check the well-formedness of the calculation recieved from the client
                    if(operation[0].length() != 3 || operation.length != 3) {
                        LOG.log(Level.SEVERE, "Badly formatted calculation");
                        this.writer.println("Badly formatted calculation, please try again");
                        this.writer.flush();
                        continue;
                    }
                    Double res;
                    try {
                        res = calculator.calculate(operation[0], operation[1], operation[2]);
                    } catch(Exception e) { // Catches unknown parameter exception
                        LOG.log(Level.SEVERE, e.getMessage());
                        this.writer.println("Unknown operator, please try again");
                        this.writer.flush();
                        continue;
                    }
                    this.writer.println("> " + cmd.toUpperCase() + " = " + res);
                    this.writer.flush();
                }
            } catch(IOException e) {
                LOG.log(Level.SEVERE, e.getMessage());
            } finally {
                // current client is finished,
                // we can cleanup the resources
                try {
                    this.reader.close();
                } catch(IOException e) {
                    Logger.getLogger(CalculatorServer.class.getName()).log(Level.SEVERE, null, e);
                }

                this.writer.close();

                try {
                    this.client.close();
                } catch(IOException e) {
                    Logger.getLogger(CalculatorServer.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }

    /**
     * @brief Start the Server so clients can connect
     */
    private void start() {
        LOG.info("Starting server...");
        new Thread(new Receptionist()).start();
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
