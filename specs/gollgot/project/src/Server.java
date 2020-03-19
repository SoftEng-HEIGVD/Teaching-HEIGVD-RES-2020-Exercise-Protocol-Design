import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private int port;

    public Server(int port){
        this.port = port;
    }

    /**
     * Initiates the process. Server creates a socket and binds it to the chosen port. Then waits for
     * clients inside an infinite loop. When client arrives, server will read it's input line.
     */
    public void serveClients(){
        System.out.println("Starting the Receptionist Worker on a new thread ...");
        new Thread(new ReceptionistWorker()).start();
    }


    /**
     * This inner class implements the behaviour of the "Receptionist", whose responsibility is to
     * listen for incoming connection request. As soon as a new client has arrived, the receptionist
     * delegates the processing to "servant" who will execute on its own thread.
     */
    private class ReceptionistWorker implements Runnable{

        @Override
        public void run() {
            ServerSocket serverSocket;

            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error occurred on the ServerSocket initialisation : " + e.getMessage());
                return;
            }

            while(true){
                System.out.println("Waiting for a new client on port " + port);
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("A new client has arrived. Starting a new thread and delegating work to a new servant...");
                    new Thread(new ServantWorker(clientSocket)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Error occurred on the Socket Client initialisation (server accept) : " + e.getMessage());
                }
            }

        }

    }

    private class ServantWorker implements Runnable {

        Socket clientSocket;
        BufferedReader in = null;
        PrintWriter out = null;

        public ServantWorker(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                this.out = new PrintWriter(clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error occurred on in / out buffer creation : " + e.getMessage());
            }
        }

        private int calculate(String line){
            return 42;
        }



        @Override
        public void run() {
            String line;
            boolean shouldRun = true;

            out.println("Welcome to the multi-threaded server.");
            out.flush();

            try {

                System.out.println("Reading until client sends BYE or close connection ...");
                while((shouldRun && (line = in.readLine()) != null)){

                    // Client ask for calculation
                    if(line.equalsIgnoreCase("calculation")){
                        out.println("Ready for calculation");
                        out.flush();
                        // Loop through all calculation the client want to do
                        while(shouldRun && (line = in.readLine()) != null){
                            // Client ask to quit
                            if(line.equalsIgnoreCase("bye")){
                                shouldRun = false;
                            }else{
                                int result = calculate(line);
                                out.println("> " + result);
                                out.flush();
                            }
                        }
                    }

                    // Client ask to quit
                    if(line.equalsIgnoreCase("bye")){
                        shouldRun = false;
                    }
                    out.println("> " + line.toUpperCase());
                    out.flush();
                }

                System.out.println("Cleaning up resources...");
                clientSocket.close();
                in.close();
                out.close();

            } catch (IOException e) {
                if(in != null){
                    try {
                        in.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                if(out != null){
                    out.close();
                }
                if(clientSocket != null) {
                    try {
                        clientSocket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                e.printStackTrace();
                System.out.println("Error occurred on read line : " + e.getMessage());
            }
        }
    }

}
