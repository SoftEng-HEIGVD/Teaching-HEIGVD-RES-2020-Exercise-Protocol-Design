import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Serveur de la calculatrice permettant de résoudres des équations simple comme les addtion, soustraction,
 * multiplication et division. Une fois réaliser il ferme la connexion.
 * Inspiré de : Olivier Liechti
 * Bouyiatiotis Stéphane, Lopes Gouveia Rui Philip
 */
public class Server {
    final static Logger LOG = Logger.getLogger(Server.class.getName());

    private final int LISTEN_PORT = 2205;
    private final String SEPARATOR = " ";

    int port;

    public Server(int port){
        this.port = port;
    }

    public void listenClient(){
        new Thread(new ReceptionistWorker()).start();
    }

    private class ReceptionistWorker implements Runnable {

        @Override
        public void run() {
            ServerSocket serverSocket;

            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException ex) {
                return;
            }

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new calculatorWorker(clientSocket)).start();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

    private class calculatorWorker implements Runnable {

        Socket clientSocket;
        BufferedReader reader = null;
        PrintWriter writer = null;

        public calculatorWorker(Socket clientSocket) {
            try {
                this.clientSocket = clientSocket;
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        @Override
        public void run() {
            String clientMessage;

            try {
                clientMessage = reader.readLine();
                sendMessage(calculator(clientMessage));

                clientSocket.close();
                reader.close();
                writer.close();

            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }

        }

        private void sendMessage(String msg){
            writer.println(msg);
            writer.flush();
        }


        private String calculator(String clientMessage){
            String[] operation = clientMessage.split(SEPARATOR);

            if(operation.length == 3){

                try {
                    double firstOperand = Double.parseDouble(operation[1]);
                    double secondOperand = Double.parseDouble(operation[2]);

                    if(operation[0].equals("ADD")){
                        return Double.toString(firstOperand + secondOperand);
                    } else if(operation[0].equals("SUB")){
                        return Double.toString(firstOperand - secondOperand);
                    } else if(operation[0].equals("MUL")){
                        return Double.toString(firstOperand * secondOperand);
                    }else if(operation[0].equals("DIV")){
                        return Double.toString(firstOperand / secondOperand);
                    } else {
                        return "Operation not reconized";
                    }

                } catch (Exception e){
                    return "Bad operand";
                }

            } else {
                return "Size probleme";
            }
        }
    }


}
