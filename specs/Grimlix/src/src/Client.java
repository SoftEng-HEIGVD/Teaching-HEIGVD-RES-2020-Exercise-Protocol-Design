package specs.Grimlix.src.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    final static Logger LOG = Logger.getLogger(Client.class.getName());

    private Socket clientSocket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;

    public void connect(String serverAdress, int serverPort){
        try {
            clientSocket = new Socket(serverAdress, serverPort);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            out = new PrintWriter(clientSocket.getOutputStream());
            LOG.log(Level.INFO, "Client was able to connect to the server.");
            this.run();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Unable to connect to server: ", e.getMessage());
            e.printStackTrace();
        }
    }

    private void run(){
        boolean connected = true;
        String line;
        try{
            while((connected && (line = in.readLine()) != null)){
                switch(line){
                    case Protocol.CMD_HELLO:
                        out.println(3);
                        out.flush();
                        LOG.log(Level.INFO, "I sent first operand (3)");
                        break;
                    case Protocol.OPERATOR:
                        out.println("+");
                        out.flush();
                        LOG.log(Level.INFO, "I sent operator (+)");
                        break;
                    case Protocol.SECOND_OPERAND:
                        out.println(4);
                        out.flush();
                        LOG.log(Level.INFO, "I sent second operand (4)");
                        break;
                }
            }
            LOG.log(Level.INFO, "I received the result");
            LOG.info("Cleaning up resources...");
            clientSocket.close();
            in.close();
            out.close();
        }catch (IOException ex) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            if (out != null) {
                out.close();
            }
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
