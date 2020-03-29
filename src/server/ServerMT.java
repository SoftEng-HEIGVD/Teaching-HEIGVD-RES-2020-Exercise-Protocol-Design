package server;

import protocol.Protocol;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a multi-threaded TCP server. It is able to interact
 * with several clients at the time, as well as to continue listening for
 * connection requests.
 *
 * @author Olivier Liechti
 */
public class ServerMT {

    final static Logger LOG = Logger.getLogger(ServerMT.class.getName());

    int port;

    /**
     * Constructor
     *
     * @param port the port to listen on
     */
    public ServerMT(int port) {
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
                    Logger.getLogger(ServerMT.class.getName()).log(Level.SEVERE, null, ex);
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
            BufferedReader reader = null;
            BufferedWriter writer = null;
            private String operator;
            private double firstOperand;
            private double secondOperand;
            private double result;

            public ServantWorker(Socket clientSocket) {
                try {
                    this.clientSocket = clientSocket;
                    reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                } catch (IOException ex) {
                    Logger.getLogger(ServerMT.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void run() {
                String line;
                boolean shouldRun = true;

                try {
                    LOG.log(Level.INFO, "Waiting for protocol initiation...");
                    line = reader.readLine();

                    if(!line.equals(Protocol.CLIENT_OPENING)){
                        LOG.log(Level.WARNING, String.format("Getting an incorrect initiation : %s instead of %s",line,Protocol.CLIENT_OPENING));
                        clientSocket.close();
                        writer.close();
                        reader.close();
                        return;
                    }

                    LOG.log(Level.INFO,"Client has started protocol initiation...");
                    writer.write(Protocol.SERVER_OPENING + '\n');
                    writer.flush();

                    LOG.log(Level.INFO,"Trying to read from client...");
                    while(true) {
                        line = reader.readLine();
                        LOG.log(Level.INFO, String.format("%s", line));

                        //Commande invalide
                        if (!getAndCheckUserInput(line)) {
                            LOG.log(Level.WARNING, "Invalid user input");
                            writer.write(Protocol.SERVER_ERROR + '\n');
                            writer.flush();
                            continue;
                        }

                        //Commande valide
                        if (operator.equals(Protocol.CLIENT_CLOSE_REQUEST)) { //Close request
                            LOG.log(Level.INFO, "Closing...");
                            clientSocket.close();
                            writer.close();
                            reader.close();
                            break;
                        } else {                                              //Résultat opération
                            LOG.log(Level.INFO, "Computing...");
                            writer.write(getOperationResult());
                            writer.flush();
                        }
                    }

                } catch (IOException ex) {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException ex1) {
                            LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                        }
                    }
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (IOException ex1) {
                            LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                        }
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

            private boolean checkCloseRequest(String message) {
                return message.equals(Protocol.CLIENT_CLOSE_REQUEST);
            }

            private boolean getAndCheckUserInput(String input) {
                boolean valid = false;
                String[] inputTokens = new String[3];

                StringTokenizer st = new StringTokenizer(input, " ", false);
                int numberOfTokens = 0;
                while(st.hasMoreTokens() && numberOfTokens < 3){
                    inputTokens[numberOfTokens++] = st.nextToken();
                }


                if(numberOfTokens == 3 && !st.hasMoreTokens()){
                    if(isNumeric(inputTokens[0]) && isNumeric(inputTokens[2]) && isOperator(inputTokens[1])){
                        operator = inputTokens[1];
                        firstOperand = Double.parseDouble(inputTokens[0]);
                        secondOperand = Double.parseDouble(inputTokens[2]);

                        if(!(secondOperand == 0 && operator.equals("/"))){
                            valid = true;
                        }
                    }
                }else if(numberOfTokens == 1){
                    if(checkCloseRequest(inputTokens[0])){
                        operator = inputTokens[0];
                        valid = true;
                    }
                }
                return valid;
            }

            private String getOperationResult()
            {
                if(operator.equals("+")){       //TODO sale
                    result = firstOperand + secondOperand;
                }else if(operator.equals("-")){
                    result = firstOperand - secondOperand;
                }else if(operator.equals("*")){
                    result = firstOperand * secondOperand;
                }else if(operator.equals("/")){
                    result = firstOperand / secondOperand;
                }

                return firstOperand + " " + operator + " " + secondOperand  + " = "
                        + Protocol.resultFormat.format(result) + "\n";
            }


            private boolean isNumeric(String line) {
                try{
                    Double.parseDouble(line);
                }catch(NumberFormatException nfe){
                    return false;
                }
                return true;
            }


            private boolean isOperator(String input) {
                for(String op : Protocol.OPERATORS)
                    if (input.equals(op)){
                        return true;
                    }
                return false;
            }
        }
    }
}