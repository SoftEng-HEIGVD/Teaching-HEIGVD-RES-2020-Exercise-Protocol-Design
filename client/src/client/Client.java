package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private int port;
    private InetAddress hostname;
    boolean isReading = false;
    private String userInput;
    private String serverAnswer;
    static final Logger LOG = Logger.getLogger(Client.class.getName());

    public Client (int port, InetAddress hostname) {
        this.port = port;
        this.hostname = hostname;
    }

    public void phoneServer() {
        Socket clientSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            clientSocket = new Socket(hostname, port);
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

            // isReading = true;
            while (true) {
                System.out.println("Tape ton instruction (genre `plsdodat 1 1 +`) :");
                userInput = inputReader.readLine();

                out.println(userInput);
                out.flush();

                System.out.println("steuplé, tu peux m'faire ça ?");
                serverAnswer = in.readLine();
                System.out.println("Réponse : " + serverAnswer);
                // isReading = false;
            }

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            out.close();
            try {
                clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
