package doeni98;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        Server clacServer = new Server();
        Thread trServer = new Thread(clacServer);
        trServer.start();

        try {
            Thread.sleep((1000 * 60 * 1));
            clacServer.quit();
            trServer.join();
        }catch (InterruptedException iExc){
            iExc.printStackTrace();
        }
    }
}


