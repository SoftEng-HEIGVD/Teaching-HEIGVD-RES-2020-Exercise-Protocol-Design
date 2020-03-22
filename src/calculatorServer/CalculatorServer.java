package calculatorServer;

import calculatorServer.Processing.Parser;
import calculatorServer.Processing.SyntaxChecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class CalculatorServer {
    int port;

    public CalculatorServer(int port){
        this.port = port;
    }

    public void serveClients(){
        ServerSocket srvSocket;
        Socket cliSocket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try{
            srvSocket = new ServerSocket(port);
        }
        catch(IOException e){
            e.printStackTrace();
            return;
        }

        while(true){
            try{
                //Débute la connexion
                cliSocket = srvSocket.accept();
                System.out.println("connected");
                in = new BufferedReader(new InputStreamReader(cliSocket.getInputStream()));
                out = new PrintWriter(cliSocket.getOutputStream(), true);

                //Attend de recevoir une requête de calcul valide
                String recievedLine;
                SyntaxChecker checker = new SyntaxChecker();
                do{
                    recievedLine = in.readLine();
                    System.out.println("read line : " + recievedLine);
                }while(!checker.checkCalculationRequest(recievedLine));

                Parser p = new Parser();
                double result = p.parseCalculationRequest(recievedLine);

                //Renvoie le résultat au client
                out.println(Double.toString(result));

            }catch(IOException e){
                //TODO
            }

        }
    }
}
