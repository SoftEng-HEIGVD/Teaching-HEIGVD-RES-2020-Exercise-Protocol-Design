package egremyb.server;

import egremyb.common.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
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
                    LOG.log(Level.INFO,"A new client has arrived. Starting a new thread and delegating work to a new servant...");
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
                boolean saidHello = false;
                boolean sendWrong = false;

                try {
                    LOG.log(Level.INFO, "Reading until client sends {0} or closes the connection...", Protocol.CMD_BYE);
                    while ((shouldRun) && (clientMsg = in.readLine()) != null) {
                        sendWrong = false;
                        switch(clientMsg){
                            case Protocol.CMD_HELLO:
                                if(!saidHello){
                                    saidHello = true;
                                    // Client wants help to compute calculus
                                    LOG.log(Level.INFO, "Received {0} from client.\n Replying {1}...", new Object[]{Protocol.CMD_HELLO, Protocol.CMD_WELCOME});
                                    // Greeting client
                                    sendMessage(Protocol.CMD_WELCOME);
                                } else {
                                    sendWrong = true;
                                }
                                break;
                            case Protocol.CMD_BYE:
                                if(saidHello){
                                    // Client wants to end communication
                                    LOG.log(Level.INFO, "Received {0} from client.\n Ending connection...", Protocol.CMD_BYE);
                                    // End communication
                                    shouldRun = false;
                                } else{
                                    sendWrong = true;
                                }
                                break;
                            default :
                                if(saidHello){
                                    // Client might want the server to compute an equation
                                    sendMessage(processClientMsg(clientMsg));
                                } else {
                                    sendWrong = true;
                                }
                                break;
                        }

                        if(sendWrong){
                            sendMessage(Protocol.CMD_WRONG);
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
                        double firstOperand  = Double.parseDouble(tokens[0]);
                        double secondOperand = Double.parseDouble(tokens[2]);
                        double solution;
                        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                        symbols.setDecimalSeparator('.');
                        NumberFormat formater = new DecimalFormat(Protocol.NUMBER_FORMAT, symbols);

                        switch (tokens[1]){
                            case Protocol.ADD_OPERATOR:
                                solution = firstOperand + secondOperand;
                                LOG.log(Level.INFO, "Processing : {0} {1} {2} = {3}", new Object[]{firstOperand, Protocol.ADD_OPERATOR,secondOperand, solution});
                                String tmp = formater.format(solution);
                                return tmp;
                            case Protocol.SUB_OPERATOR:
                                solution = firstOperand - secondOperand;
                                LOG.log(Level.INFO, "Processing : {0} {1} {2} = {3}", new Object[]{firstOperand, Protocol.SUB_OPERATOR,secondOperand, solution});
                                return formater.format(solution);
                            case Protocol.MUL_OPERATOR:
                                solution = firstOperand * secondOperand;
                                LOG.log(Level.INFO, "Processing : {0} {1} {2} = {3}", new Object[]{firstOperand, Protocol.MUL_OPERATOR,secondOperand, solution});
                                return formater.format(solution);
                            case Protocol.DIV_OPERATOR:
                                solution = firstOperand / secondOperand;
                                LOG.log(Level.INFO, "Processing : {0} {1} {2} = {3}", new Object[]{firstOperand, Protocol.DIV_OPERATOR,secondOperand, solution});
                                return formater.format(solution);
                            case Protocol.POW_OPERATOR:
                                solution = Math.pow(firstOperand, secondOperand);
                                LOG.log(Level.INFO, "Processing : {0} {1} {2} = {3}", new Object[]{firstOperand, Protocol.POW_OPERATOR,secondOperand, solution});
                                return formater.format(solution);
                            default:
                                LOG.log(Level.INFO, "Client sent bad request");
                                return Protocol.CMD_WRONG;
                        }
                    }
                    catch(Exception e){
                        // Double.parseDouble() return two exceptions :
                        // - NullPointerException– when the string parsed is null
                        // - NumberFormatException– when the string parsed does not contain a parsable float
                        LOG.log(Level.INFO, "Client sent bad request");
                        return Protocol.CMD_WRONG;
                    }
                } else{
                    LOG.log(Level.INFO, "Client sent bad request");
                    return Protocol.CMD_WRONG;
                }

            }
        }
    }
}