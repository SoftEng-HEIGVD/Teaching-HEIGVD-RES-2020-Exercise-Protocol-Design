package KurohanaJuri;

import KurohanaJuri.client.Client;
import KurohanaJuri.server.CalculatorServer;

public class Main {
    private final static String HOSTNAME = "10.192.91.10";
    private final static String LOCALHOST = "127.0.0.1";
    private final static int PORT = 9999;

    public static void main(String[] args) {
        if(args[0].equals("Client")){
            new Client(LOCALHOST, PORT);
        } else if(args[0].equals("Server")){
            new CalculatorServer(PORT);
        } else {
            System.out.println("Command unknown");
        }
    }
}
