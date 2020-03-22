import syntaxChecking.SyntaxChecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class CalculatorClient {
    int port;
    String hostname;

    public CalculatorClient(int port, String hostname){
        this.port = port;
        this.hostname = hostname;
    }


    public void requestServer(){
        Socket cliSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        boolean stayConnected;

        try{
            while(true) {
                //Create connection and streams
                cliSocket = new Socket(hostname, port);
                stayConnected = true;
                out = new PrintWriter(cliSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(cliSocket.getInputStream()));

                //Read input from user until the syntax is correct
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                String request = "";
                SyntaxChecker checker = new SyntaxChecker();
                do {
                    if (!request.equals("")) System.out.println("Syntax error. please retry");
                    request = consoleReader.readLine();
                    if(request.equals("CEND")){
                        out.println(request);
                        //cliSocket.close();
                        stayConnected = false;
                        break;
                    }
                } while (!checker.checkCalculationRequest(request));

                //Leave loop if user asked to quit
                if(!stayConnected) break;

                //Send request
                out.println(request);
                out.flush();

                //Wait for the answer
                String answer = in.readLine();
                System.out.println("Answer : " + answer);
            }

            cliSocket.close();
            in.close();
            out.close();

        } catch (IOException e) {

        }


    }

}
