import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements Runnable{

    static final Logger LOG = Logger.getLogger(Server.class.getName());

    private final Charset encoding = StandardCharsets.UTF_8;
    private final int LISTEN_PORT = 2205;

    @Override
    public void run() {
        Socket clientSocket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;
        Scanner userInput;


        try {
            while(true){
                LOG.log(Level.INFO, "Client connect to the server");
                clientSocket = new Socket("localhost", LISTEN_PORT);


                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), encoding));
                writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), encoding));
                userInput = new Scanner(System.in);


                while(true){

                    System.out.println(reader.readLine());
                    String test = userInput.nextLine();
                    System.out.println(test);
                    writer.println(test);
                    writer.flush();
                }

            }

        }catch (IOException ex){
            LOG.log(Level.SEVERE, ex.getMessage());
        }
    }
    public static void main(String[] args){
        Client client = new Client();
        client.run();
    }
}
