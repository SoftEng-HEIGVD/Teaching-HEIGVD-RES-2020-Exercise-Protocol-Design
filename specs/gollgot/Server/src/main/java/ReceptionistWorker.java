import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This inner class implements the behaviour of the "Receptionist", whose responsibility is to
 * listen for incoming connection request. As soon as a new client has arrived, the receptionist
 * delegates the processing to "servant" who will execute on its own thread.
 */
public class ReceptionistWorker implements Runnable {

    private int port;

    public ReceptionistWorker(int port){
        this.port = port;
    }

    @Override
    public void run() {
        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            Server.LOG.warning("Error occurred on the ServerSocket initialisation : " + e.getMessage());
            return;
        } catch(IllegalArgumentException e){
            Server.LOG.warning("Error port value out of range");
            return;
        }

        while(true){
            Server.LOG.info("Waiting for a new client on port " + port);
            try {
                Socket clientSocket = serverSocket.accept();
                Server.LOG.info("A new client has arrived. Starting a new thread and delegating work to a new servant...");

                ServantWorker servantWorker = new ServantWorker(clientSocket);
                new Thread(servantWorker).start();

            } catch (IOException e) {
                Server.LOG.warning("Error occurred on the Socket Client initialisation (server accept) : " + e.getMessage());
            }
        }
    }
}
