package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private int port;
    private InetAddress hostname;
    private final String STOP_CLIENT = "kthx";
    private final String HELP_MESSAGE = "help";

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

            String userInput;
            do {
                System.out.println("Tape ton instruction :");
                userInput = inputReader.readLine();

                if (userInput.equals(HELP_MESSAGE)) {
                    helpMessage();
                    continue;
                }

                out.println(userInput);
                out.flush();

                if (!userInput.equals(STOP_CLIENT))
                    System.out.println("steuplé, tu peux m'faire ça ?");

                String serverAnswer = in.readLine();
                // LOG.log(Level.INFO, String.format("Réponse du serveur : %s", serverAnswer));
                System.out.println("Réponse du serveur : " + serverAnswer + "\n");
            } while (!userInput.equals(STOP_CLIENT));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            try {
                assert in != null;
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

    private void helpMessage() {
        System.out.println();
        System.out.println("Message d'aide");
        System.out.println();
        System.out.println("Pour envoyer une commande au serveur, taper");
        System.out.println("`plsdodat <opérande 1> <opérande 2> <opétateur>`");
        System.out.println("où <opérateur> est soit : +, -, * ou /");
        System.out.println();
        System.out.println("Pour quitter le client, taper");
        System.out.println("`kthx`");
        System.out.println();

    }
}
