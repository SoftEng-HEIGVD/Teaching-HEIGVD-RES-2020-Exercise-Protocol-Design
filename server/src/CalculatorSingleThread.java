import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

    private static final char OP_ADDITION = '+';
    private static final char OP_SUBSTRACTION = '-';
    private static final char OP_DIVISION = '/';
    private static final char OP_MULTIPLICATION = '*';

    int port;

    /**
     * Constructor
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
            serverSocket = new ServerSocket(port);
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

                out.println("Welcome to the Calculator Server.\nWhat operation would you like to do ? (quit with END line)");
                out.flush();
                LOG.info("Reading until client sends BYE or closes the connection...");
                while ( (shouldRun) && (line = in.readLine()) != null ) {
                    if (line.equalsIgnoreCase("bye")) {
                        shouldRun = false;
                    }

                    String[] inputArray = line.split(" ");

                    if(isInputValid(inputArray)) {
                        double result = computeInput(inputArray);
                        out.println("= " + result);
                        out.flush();
                    } else {
                        out.println("Invalid input.");
                        out.flush();
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

    private boolean isInputValid(String[] inputArray) {
        return inputArray.length == 3 && isNumeric(inputArray[0]) && isNumeric(inputArray[2]) && inputArray[1]
                .length() == 1 && isValidOperator(inputArray[1].charAt(0));
    }

    public static boolean isNumeric(String str) {

        if (str == null || str.length() == 0) {
            return false;
        }

        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;

    }

    public static boolean isValidOperator(char c) {
        return c == OP_ADDITION || c == OP_DIVISION ||
                c == OP_MULTIPLICATION || c == OP_SUBSTRACTION;
    }

    private double computeInput(String[] inputArray) {
        double result = 0;

        switch(inputArray[1].charAt(0)) {
            case OP_ADDITION:
                result = Double.parseDouble(inputArray[0]) + Double.parseDouble(inputArray[2]);
                break;
            case OP_SUBSTRACTION:
                result = Double.parseDouble(inputArray[0]) - Double.parseDouble(inputArray[2]);
                break;
            case OP_MULTIPLICATION:
                result = Double.parseDouble(inputArray[0]) * Double.parseDouble(inputArray[2]);
                break;
            case OP_DIVISION:
                result = Double.parseDouble(inputArray[0]) / Double.parseDouble(inputArray[2]);
                break;
        }

        return result;
    }
}