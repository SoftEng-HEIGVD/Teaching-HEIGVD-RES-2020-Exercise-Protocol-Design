
import Processing.Parser;
import syntaxChecking.SyntaxChecker;

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
        boolean stayConnected;

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
                stayConnected = true;
                System.out.println("connected");
                in = new BufferedReader(new InputStreamReader(cliSocket.getInputStream()));
                out = new PrintWriter(cliSocket.getOutputStream(), true);

                //Attend de recevoir une requête de calcul valide
                String recievedLine;
                SyntaxChecker checker = new SyntaxChecker();

                while(stayConnected){
                    do{
                        recievedLine = in.readLine();
                        if(recievedLine.equals("CEND")) stayConnected = false;
                    }while(!checker.checkCalculationRequest(recievedLine) && stayConnected);

                    if(stayConnected){
                        Parser p = new Parser();
                        double result = p.parseCalculationRequest(recievedLine);
                        //Renvoie le résultat au client
                        out.println(Double.toString(result));
                    }
                }

                cliSocket.close();
                in.close();
                out.close();

            }catch(IOException e){
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                if (out != null) {
                    out.close();
                }
                if (cliSocket != null) {
                    try {
                        cliSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                e.printStackTrace();
            }

        }
    }
}
