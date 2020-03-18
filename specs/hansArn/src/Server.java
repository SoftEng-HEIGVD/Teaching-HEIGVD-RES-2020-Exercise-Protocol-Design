
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    int port;

    /**
     * Constructor
     * @param port the port to listen on
     */
    public Server(int port) {
        this.port = port;
    }

    public void serveClients() {
        ServerSocket serverSocket;
        Socket clientSocket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        // test si le port est déjà utilisé
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            System.out.println(ex);
            return;
        }

        while (true) {
            try {
                clientSocket = serverSocket.accept();
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream());
                String line;
                boolean shouldRun = true;

                out.println("Welcome to calculator Server.\nSend me something to calculate with the form \" number sign number \" and conclude with the BYE command.\n" +
                        "4 operations are available (+, -, \\, *)");
                out.flush();
                while ( (shouldRun) && (line = in.readLine()) != null ) {
                    if (line.equalsIgnoreCase("bye")) {
                        shouldRun = false;
                    }
                    out.print("> ");
                    String[] argumentArray = line.split(" ");
                    if (argumentArray.length != 3){
                        out.println("ERROR : Bad Use");
                        out.flush();
                        clientSocket.close();
                        in.close();
                        out.close();
                    }else{
                        try {
                            Calculator C = new Calculator(argumentArray[1], Float.parseFloat(argumentArray[0]), Float.parseFloat(argumentArray[2]));
                        }catch (NumberFormatException e){
                            out.println("ERROR : Bad Argument");
                            out.flush();
                            clientSocket.close();
                            in.close();
                            out.close();
                        }
                    }
                }

                clientSocket.close();
                in.close();
                out.close();

            } catch (IOException ex) {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ex1) {
                        System.out.println(ex1);
                    }
                }
                if (out != null) {
                    out.close();
                }
                if (clientSocket != null) {
                    try {
                        clientSocket.close();
                    } catch (IOException ex1) {
                        System.out.println(ex1);
                    }
                }
            }
        }
    }
}
