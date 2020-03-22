package client;

import server.Server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Client {

    static final Logger LOG = Logger.getLogger(Server.class.getName());

    private final Charset encoding = StandardCharsets.UTF_8;
    private final int LISTEN_PORT = 2205;

    public void run() {
        Socket clientSocket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;
        Scanner userInput;
        boolean shouldRun = true;

        try {
            while(shouldRun){
                LOG.log(Level.INFO, "Client connect to the server");
                clientSocket = new Socket("localhost", LISTEN_PORT);


                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), encoding));
                writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), encoding));
                userInput = new Scanner(System.in);


                while(shouldRun){

                    System.out.println(reader.readLine());
                    String input = userInput.nextLine();
                    if(input.equals("exit")) shouldRun = false;
                    writer.write(input + "\n");
                    writer.flush();
                }

            }

        }catch (IOException ex){
            LOG.log(Level.SEVERE, ex.getMessage());
        }
    }
}
