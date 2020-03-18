package specs.Grimlix.src.src;

import specs.Grimlix.src.src.Protocol;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

enum Turn {
    OPERAND,
    OPERATOR,
}

enum Operator{
    DIV{
        public double calculate(int firstOperand, int secondOperand){
            return firstOperand / secondOperand;
        }
    },
    MULT{
        public double calculate(int firstOperand, int secondOperand){
            return firstOperand * secondOperand;
        }
    },
    ADD{
        public double calculate(int firstOperand, int secondOperand){
            return firstOperand + secondOperand;
        }
    };

    public abstract double calculate(int firstOperand, int secondOperand);

}

public class Server {

    final static Logger LOG = Logger.getLogger(Server.class.getName());

    private ServerSocket serverSocket;
    private Socket clientSocket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;

    private Turn turn = Turn.OPERAND;
    private Operator operator = null;

    public void run(){
        //Binding serverSocket to the default port
        try {
            serverSocket = new ServerSocket(Protocol.DEFAULT_PORT);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return;
        }

        while (true) {
            try {
                LOG.log(Level.INFO, "Waiting a client to connect.", Protocol.DEFAULT_PORT);
                clientSocket = serverSocket.accept();

                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream());

                //Writing the hello message
                out.println(Protocol.CMD_HELLO);
                out.flush();

                String line = "";
                boolean shouldRun = true;
                int firstOperand = 0, secondOperand = 0;
                boolean firstOperandDone = false, error = false;

                while((shouldRun) && (line = in.readLine()) != null){
                    switch(turn){
                        case OPERAND:
                            //Checking if the value is a digit
                            for(int i = 0;  i < line.length(); i++){
                                if(!Character.isDigit(line.charAt(i))){
                                    error = true;
                                }
                            }
                            if(!error){
                                if(firstOperandDone){
                                    secondOperand = Integer.parseInt(line);
                                    shouldRun = false;
                                }else{
                                    firstOperand = Integer.parseInt(line);
                                    firstOperandDone = true;
                                    out.println(Protocol.OPERATOR);
                                    out.flush();
                                    turn = Turn.OPERATOR;
                                }
                            }else{
                                out.println(Protocol.OPERAND_ERROR);
                                out.flush();
                            }
                            error = false;
                            break;
                        case OPERATOR:
                            if(line.equals("/")){
                                operator = Operator.DIV;
                            }else if(line.equals("*")){
                                operator = Operator.MULT;
                            }else if(line.equals("+")){
                                operator = Operator.ADD;
                            }else{
                                out.println(Protocol.OPERATOR_ERROR);
                                out.flush();
                                break;
                            }
                            //we have the right operator, we ask for the second operand now.
                            out.println(Protocol.SECOND_OPERAND);
                            out.flush();
                            turn = Turn.OPERAND;
                            break;
                    }
                }

                double result = operator.calculate(firstOperand, secondOperand);

                out.write(Protocol.RESULT + result);
                out.flush();

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
