package server.ClarFleu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.System.exit;
import static java.lang.System.out;

/**
 * This class implements a multi-threaded TCP server. It is able to interact
 * with several clients at the time, as well as to continue listening for
 * connection requests.
 * It modelises the calculator server
 *
 * @authors Olivier Liechti
 *          Clarisse Fleurimont
 */
public class CalculatorServer {
    private final static int COMPONENTS = 3;
    final static Logger LOG = Logger.getLogger(CalculatorServer.class.getName());

    int port;

    /**
     * Constructor
     *
     * @param port the port to listen on
     */
    public CalculatorServer(int port) {
        this.port = port;
    }

    /**
     * This method initiates the process. The server creates a socket and binds it
     * to the previously specified port. It then waits for clients in a infinite
     * loop. When a client arrives, the server will read its input line by line
     * and send back the data converted to uppercase. This will continue until the
     * client sends the "BYE" command.
     */
    public void serveClients() {
        LOG.info("Starting the Receptionist Worker on a new thread...");
        new Thread(new ReceptionistWorker()).start();
    }

    /**
     * Method used to do a simple calculation
     * @param calc (String) the calculation to evaluate
     * @return reslut (int) the result of the calculation
     * @throws Exception if there are missing operator and/or componenent or if there is a division by 0
     */
    private double calculate(String calc) throws Exception {
        String components[] = new String[COMPONENTS];
        int index = 0;
        for (int i = 0; i < calc.length(); ++i) {
            char c = calc.charAt(i);
            if(c != ' ') {
                if(components[index] == null) {
                    components[index] = "" + c;
                } else {
                    components[index] += c;
                }
            } else {
                ++index;
            }
            if(index >= COMPONENTS) {
                throw new Exception("EXCES COMP");
            }
        }

        for (String comp : components) {
            if (comp == null)
                throw new Exception("COMP MISS");
        }

        switch (components[1]){
            case "+" : return Double.parseDouble(components[0]) + Double.parseDouble(components[2]);
            case "-" : return Double.parseDouble(components[0]) - Double.parseDouble(components[2]);
            case "÷" :
                if( Double.parseDouble(components[2]) != 0) {
                    return Double.parseDouble(components[0]) / Double.parseDouble(components[2]);
                } else {
                    throw new ArithmeticException("DIV 0");
                }
            case "x" : return Double.parseDouble(components[0]) * Double.parseDouble(components[2]);
            case "%" : return Double.parseDouble(components[0]) % Double.parseDouble(components[2]);
            default  : throw new Exception("UNK OP");
        }
    }

    /**
     * This inner class implements the behavior of the "receptionist", whose
     * responsibility is to listen for incoming connection requests. As soon as a
     * new client has arrived, the receptionist delegates the processing to a
     * "servant" who will execute on its own thread.
     */
    private class ReceptionistWorker implements Runnable {

        @Override
        public void run() {
            ServerSocket serverSocket;

            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
                return;
            }

            while (true) {
                LOG.log(Level.INFO, "Waiting (blocking) for a new client on port {0}", port);
                try {
                    Socket clientSocket = serverSocket.accept();
                    LOG.info("A new client has arrived. Starting a new thread and delegating work to a new servant...");
                    new Thread(new ServantWorker(clientSocket)).start();
                } catch (IOException ex) {
                    Logger.getLogger(CalculatorServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        /**
         * This inner class implements the behavior of the "servants", whose
         * responsibility is to take care of clients once they have connected. This
         * is where we implement the application protocol logic, i.e. where we read
         * data sent by the client and where we generate the responses.
         */
        private class ServantWorker implements Runnable {

            Socket clientSocket;
            BufferedReader in = null;
            PrintWriter out = null;

            public ServantWorker(Socket clientSocket) {
                try {
                    this.clientSocket = clientSocket;
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new PrintWriter(clientSocket.getOutputStream());
                } catch (IOException ex) {
                    Logger.getLogger(CalculatorServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void run() {
                String line;
                boolean shouldRun = false;

                try {
                    LOG.info("Reading until client sends HELLO to open the connection...");
                    while (!shouldRun && (line = in.readLine()) != null) {
                        if(line.equalsIgnoreCase("hello")){
                            shouldRun = true;
                            out.println("HELLO CALC");
                        }
                        out.flush();
                    }

                    LOG.info("Reading until client sends BYE or closes the connection...");
                    while ((shouldRun) && (line = in.readLine()) != null) {
                        if (line.equalsIgnoreCase("bye")) {
                            out.println("Goodbye !");
                            shouldRun = false;
                        } else {
                            try {
                                double result = calculate(line);
                                out.println(((result == (long) result) ? String.format("%d",(long)result) : result));
                            } catch (Exception e) {
                                out.println("ERROR : " + e.getMessage());
                            }

                        }
                        out.flush();
                    }

                    LOG.info("Cleaning up ressources...");
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
}
