package client;

import java.io.*;
import java.net.Socket;

public class Client
{
    private final int PORT = 50000;
    private BufferedReader is;
    private BufferedWriter os;
    private Socket socket;

    public Client(String host) throws IOException {
        socket = new Socket(host, PORT);
        is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        os = new BufferedWriter(new PrintWriter(socket.getOutputStream()));
        sendRequest("OPEN");
        System.out.println("OPEN sent");
        if(!is.readLine().equals("ACK")){
            System.out.println("Doit être ACK");
            terminate();
        }
    }


    public void terminate() throws IOException {
        socket.close();
        is.close();
        os.close();
    }

    public void sendRequest(String request) throws IOException {
        os.write(request + "\n");
        os.flush();
    }

    public void verifyResponse() throws IOException {
        String r = is.readLine();
        if(r.equals("CLOSE")){
            System.out.println("CLOSE received");
            terminate();
        }
        if(r.equals("INVALID FORMAT")){
            System.out.println("INVALID FORMAT SENT");
            terminate();
            throw new IOException("Invalid request format");
        }
        if(r.matches("^[0-9]*$")){
            sendRequest("RECEIVED");
            System.out.println(r);
            return;
        }
        throw new IOException("Error while receiving from server");
    }
}
