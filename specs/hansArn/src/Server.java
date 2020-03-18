
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    int port;
    int timeOut;

    /**
     * Constructor
     * @param port the port to listen on
     * @param timeOutInSecond set timeout in seconds
     */
    public Server(int port, int timeOutInSecond) {
        this.port = port;
        this.timeOut = timeOutInSecond * 1000;
    }

    /**
     * Constructor
     * @param port the port to listen on
     */
    public Server(int port) {
        this.port = port;
        this.timeOut = 0;
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
                // configuration du client
                clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(timeOut);
                // configuration des IOs
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream());
                String line;
                // booléen d'arrêt
                boolean shouldRun = true;

                // message d'acceuil
                out.println("Welcome to calculator Server.\nSend me something to calculate with the form \" number sign number \" and conclude with the BYE command.\n" +
                        "4 operations are available (+, -, \\, *)");
                out.flush();

                while ( (shouldRun) && (line = in.readLine()) != null ) {
                    // si le client dit bye on ferme la connexion
                    if (line.equalsIgnoreCase("bye")) {
                        shouldRun = false;
                    }else{
                        out.print("> ");
                        String[] argumentArray = line.split(" ");
                        // si moins de 3 arguments on ferme la connexion
                        if (argumentArray.length != 3){
                            out.println("ERROR : Bad Use");
                            out.flush();
                            clientSocket.close();
                            in.close();
                            out.close();
                        }else {
                            try {
                                Calculator C = new Calculator(argumentArray[1], Float.parseFloat(argumentArray[0]), Float.parseFloat(argumentArray[2]));
                                out.println(C.Op());
                                out.flush();
                            }
                            // dans le cas ou la transformation en float échoue
                            catch (NumberFormatException e) {
                                out.println("ERROR : Bad Argument");
                                out.flush();
                                clientSocket.close();
                                in.close();
                                out.close();

                            }
                            // dans le cas d'un signe non implémenté
                            catch (IllegalArgumentException e) {
                                out.println("ERROR : Bad Argument");
                                out.flush();

                                clientSocket.close();
                                in.close();
                                out.close();
                            }
                        }
                    }
                }

                clientSocket.close();
                in.close();
                out.close();

            } catch (SocketTimeoutException e ){
                out.println("Timeout");
                out.flush();
                try {
                    clientSocket.close();
                    in.close();
                    out.close();
                }catch (IOException error){
                    System.out.println(error);
                }
            }catch (IOException ex) {
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
