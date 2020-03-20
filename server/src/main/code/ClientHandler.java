package main.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable{

    static final Logger LOG = Logger.getLogger(ClientHandler.class.getName());
    private BufferedReader reader = null;
    private PrintWriter writer = null;
    private Worker wrk;
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        writer = new PrintWriter(clientSocket.getOutputStream());
        wrk = new Worker();
    }

    @Override
    public void run() {
        try{
            LOG.log(Level.INFO, "Client Handler thread started");
            String request = reader.readLine();
            LOG.log(Level.INFO, "Got request: " + request);
            String answer;
            try {
                Double result = wrk.calc(request);
                answer = result.toString();
            } catch (Exception calcExc) {
                answer = "ERROR: " + calcExc.getMessage();
            }
            LOG.log(Level.INFO, "Responding to request with:\n" + answer);
            writer.println(answer);
            writer.flush();
            reader.close();
            writer.close();
            clientSocket.close();
            LOG.log(Level.INFO, "Client Handler thread started");
        }catch (IOException ex){
            LOG.log(Level.SEVERE, ex.getMessage());
        }finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            writer.close();
            try {
                clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }

    public void close() throws IOException{
        if(!clientSocket.isClosed())
        clientSocket.close();
    }
}
