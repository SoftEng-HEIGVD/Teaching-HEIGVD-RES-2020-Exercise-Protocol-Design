package ch.heigvd.res.exercice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.logging.Level;
import java.util.logging.Logger;



public class Server {
    final static Logger LOG = Logger.getLogger(Server.class.getName());

    int port;

    public Server(int port){
        this.port = port;
    }
    public boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    public void connectClient() {
        ServerSocket serverSocket;
        Socket clientSocket = null;
        PrintWriter writer = null;
        BufferedReader reader = null;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return;
        }

        while (true) {
            try {
                LOG.log(Level.INFO, "Waiting for client");
                clientSocket = serverSocket.accept();
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream());
                boolean stop = false;

                writer.println(" Welcome to Calculator online: \n MUL num1 num2 : Multiplication" +
                        "\n DIV num1 num2 : Division \n ADD num1 num2 : Addition \n SUB num1 num2 : Substraction" +
                        "\n  STOP : Exit connexion");
                writer.flush();
                String line = reader.readLine();

                while ((!stop) && (line != null)) {
                    String tokens[] = line.split(" ");
                    String operation = tokens[0];

                    if (operation.compareTo(Protocol.OP_STOP) != 0 && tokens.length != 3) {
                        writer.println("Enter the correct number of arguments !");
                        writer.flush();
                        continue;
                    }
                    if ((operation.compareTo(Protocol.OP_STOP) != 0) && (isNumeric(tokens[1]) || isNumeric(tokens[2]))) {
                        writer.println("Enter numbers only");
                        writer.flush();
                        continue;
                    }
                    Double num1 = Double.parseDouble(tokens[1]);
                    Double num2 = Double.parseDouble(tokens[2]);

                    switch (operation) {
                        case (Protocol.OP_MUlTIPLICATION):
                            writer.println("The result of the multiplication is : " + (num1 * num2));
                            break;
                        case (Protocol.OP_DIVISION):
                            writer.println("The result of the division is : " + (num1 / num2));
                            break;
                        case (Protocol.OP_ADDITION):
                            writer.println("The result of the addition is : " + (num1 + num2));
                            break;
                        case (Protocol.OP_SUB):
                            writer.println("The result of the substraction is : " + (num1 - num2));
                            break;
                        case (Protocol.OP_STOP):
                            writer.println("Stopping the connexion");
                            break;
                        default:
                            writer.println("Error none of the operations");
                            break;
                    }
                    writer.flush();
                }
                clientSocket.close();
                reader.close();
                writer.close();

            } catch (IOException e) {
                LOG.log(Level.SEVERE,e.getMessage(), e);
            }

        }

    }
}
