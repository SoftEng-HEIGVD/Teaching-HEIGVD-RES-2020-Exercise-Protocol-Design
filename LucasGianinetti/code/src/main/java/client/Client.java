package client;

import protocol.Protocol;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private enum OPERATORS {
        ADD, SUB, MUL, DIV
    }

    private double firstOperand;
    private double secondOperand;
    private OPERATORS operator;
    static final Logger LOG = Logger.getLogger(Client.class.getName());

    private Socket clientSocket = null;
    private BufferedReader reader = null;
    private BufferedWriter writer = null;

    public void start(){

        getUserInput();
        try{
            clientSocket = new Socket("localhost", Protocol.SERVER_PORT);
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));


            writer.write(Protocol.CLIENT_GREETINGS+'\n');
            writer.flush();
            checkServerMessage(Protocol.SERVER_GREETINGS);

            checkServerMessage(Protocol.SERVER_ASK_OPERATOR);
            writer.write(operator.toString() +'\n');
            writer.flush();

            checkServerMessage(Protocol.SERVER_ACK);
            checkServerMessage(Protocol.SERVER_ASK_FIRST_OPERAND);
            writer.write(Double.toString(firstOperand) +'\n');
            writer.flush();
            checkServerMessage(Protocol.SERVER_ACK);
            checkServerMessage(Protocol.SERVER_ASK_SECOND_OPERAND);
            writer.write(Double.toString(secondOperand) + '\n');
            writer.flush();
            checkServerMessage(Protocol.SERVER_ACK);
            checkServerMessage(Protocol.SERVER_RESULT);



            writer.write("Done\n");
            writer.flush();

            clientSocket.close();
            writer.close();
            reader.close();

        }catch (IOException ex){
            if(reader != null){
                try{
                    reader.close();
                }catch(IOException ex1){
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            if(writer != null){
                try{
                    writer.close();
                }catch (IOException ex1){
                    LOG.log(Level.SEVERE, ex1.getMessage(),ex1);
                }
            }
            if(clientSocket != null){
                try{
                    clientSocket.close();
                }catch (IOException ex1){
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            LOG.log(Level.SEVERE, ex.getMessage(),ex);
        }
    }


    /**
     * Get user operation. An operator and 2 operands are asked.
     */
    private void getUserInput() {
        String input;
        Scanner scan = new Scanner(System.in);

        do{
            System.out.println("Enter an operator.");
            input = scan.next();
        }while(!isLegalOperator(input));

        operator = OPERATORS.valueOf(input);

        do{
            System.out.println("Enter first operand.");
            input = scan.next();
        }while(!isCorrectOperand(input));

        firstOperand = Double.parseDouble(input);

        do{
            System.out.println("Enter second operand.");
            input = scan.next();
        }while(!isCorrectOperand(input));

        secondOperand = Double.parseDouble(input);
    }


    /**
     * Check if String contains valid Operand
     * @param input String on which we want to do the check
     * @return true if input is a Double, else false
     */
    private boolean isCorrectOperand(String input){
        if(input == null) {
            return false;
        }
        try{
            double d = Double.parseDouble(input);
        }catch(NumberFormatException nfe){
            return false;
        }
        return true;
    }

    /**
     * Check if String contains valid Operator
     * @param input String on which we want to do the check
     * @return true is line is a valid Operator
     */
    private boolean isLegalOperator(String input) {
        for(OPERATORS o : OPERATORS.values()){
            if(input.equals(o.name())){
                return true;
            }
        }
        return false;
    }

    /**
     * Check if we get the correct message from the server
     * @param message String that we expect the server send us as next line
     * @throws IOException
     */
    private void checkServerMessage(String message) throws IOException{
        String line = reader.readLine();
        if(!line.contains(message)){//contains instead of equals because SERVER_ACK is not the complete line we will get from the server
            throw new IOException("Error while interracting with the server.");
        }
        //Display the result of the operation
        if(message == Protocol.SERVER_RESULT){
            System.out.println(line);
        }
    }

}
