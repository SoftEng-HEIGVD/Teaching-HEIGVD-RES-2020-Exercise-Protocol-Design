import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    static Logger LOG = Logger.getLogger(Client.class.getName());

    private final int CONNECTION_PORT = 6666;
    private final String IP_ADDRESS = "localhost";


    public void run(){

        Socket socket = null;
        PrintWriter writer = null;
        BufferedReader reader = null;
        Scanner input = null;

        try{
            LOG.log(Level.INFO, "Creating connection socket");
            socket = new Socket(IP_ADDRESS, CONNECTION_PORT);

            LOG.log(Level.INFO, "Creating socket, reader and writer");
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            input = new Scanner(System.in);
            while(true){
                String s = "";
                while((s = reader.readLine()) != null && !s.equals("END")) {
                    System.out.println(s);
                }

                if(s == null)
                    break;

                String message = input.nextLine();
                writer.println(message);
                writer.flush();
                System.out.println(message);
            }


        }

        catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

}




