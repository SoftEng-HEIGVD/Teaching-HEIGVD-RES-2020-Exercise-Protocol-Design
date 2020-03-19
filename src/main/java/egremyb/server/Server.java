package egremyb.server;

import egremyb.common.Protocol;
import jdk.nashorn.internal.objects.annotations.Constructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a multi-threaded TCP server. It is able to interact
 * with several clients at the time, as well as to continue listening for
 * connection requests.
 *
 * @author Olivier Liechti
 */
public class Server {

    final static Logger LOG = Logger.getLogger(Server.class.getName());

    int port;

    /**
     * Default constructor
     */
    public Server() {
        this.port = Protocol.DEFAULT_PORT;
    }

    /**
     * Constructor
     *
     * @param port the port to listen on
     */
    public Server(int port) {
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
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void run() {
                String clientMsg;
                boolean shouldRun = true;

                try {
                    LOG.info("Reading until client sends BYE or closes the connection...");
                    while ((shouldRun) && (clientMsg = in.readLine()) != null) {

                        switch(clientMsg){
                            case Protocol.CMD_HELLO:
                                // Client wants help to compute calculus
                                // Greeting client
                                sendMessage(Protocol.CMD_WELCOME);
                                break;
                            case Protocol.CMD_BYE:
                                // Client wants to end communication
                                // End communication
                                shouldRun = false;
                                break;
                            default :
                                // Client might want the server to compute an equation
                                sendMessage(processClientMsg(clientMsg));
                                break;
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

            private void sendMessage(String content){
                    out.println(content);
                    out.flush();
            }


            private String processClientMsg(String clientMsg){
                // Searching for pattern "<number> <operator> <number>"
                String[] tokens = clientMsg.split(Protocol.SEPARATOR);

                if(tokens.length == 3){
                    try{
                        double firstOperand = Double.parseDouble(tokens[0]);
                        double secondOperand = Double.parseDouble(tokens[2]);
                        double solution;

                        switch (tokens[1]){
                            case Protocol.ADD_OPERATOR:
                                solution = firstOperand + secondOperand;
                                return Double.toString(solution);
                            case Protocol.SUB_OPERATOR:
                                solution = firstOperand - secondOperand;
                                return Double.toString(solution);
                            case Protocol.MUL_OPERATOR:
                                solution = firstOperand * secondOperand;
                                return Double.toString(solution);
                            case Protocol.DIV_OPERATOR:
                                solution = firstOperand / secondOperand;
                                return Double.toString(solution);
                            case Protocol.POW_OPERATOR:
                                solution = Math.pow(firstOperand, secondOperand);
                                return Double.toString(solution);
                            default:
                                return Protocol.CMD_WRONG;
                        }
                    }
                    catch(Exception e){
                        // Double.parseDouble() return two exceptions :
                        // - NullPointerException– when the string parsed is null
                        // - NumberFormatException– when the string parsed does not contain a parsable float
                        return Protocol.CMD_WRONG;
                    }
                } else{
                    return Protocol.CMD_WRONG;
                }

            }
        }
    }
}