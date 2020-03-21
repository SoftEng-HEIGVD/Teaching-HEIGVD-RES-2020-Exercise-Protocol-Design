package calculatorClient;

import java.io.*;
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


        try{
            cliSocket = new Socket(hostname, port);
            out = new PrintWriter(cliSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(cliSocket.getInputStream()));

            out.println("CALC 1 + 2");
            System.out.println("sent message");
            String answer = in.readLine();
            System.out.println("Answer : " + answer);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
