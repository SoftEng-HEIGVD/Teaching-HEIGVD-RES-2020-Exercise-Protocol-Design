package ch.heigvd.res.exercice;

import com.sun.nio.sctp.SctpStandardSocketOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    final static Logger LOG = Logger.getLogger(Client.class.getName());

    Socket clientSocket;
    boolean connected = false;
    BufferedReader reader;
    PrintWriter writer;
    String entree;

    public void connect(String serverAddress, int serverPort){
        try {
            clientSocket = new Socket(serverAddress, serverPort);
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream());
            connected = true;
            this.entree = entree;

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void cleanChannel(){
        try {
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        }
        catch (IOException ex){
            LOG.log(Level.SEVERE, ex.getMessage(),ex);
        }

    }
    public void disconnect(){
        connected = false;
        cleanChannel();
    }



}
