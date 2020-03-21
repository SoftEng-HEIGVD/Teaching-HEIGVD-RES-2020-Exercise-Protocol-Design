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
        if(!is.readLine().equals("ACK")){
            terminate();
        }
    }


    public void terminate() throws IOException {
        socket.close();
        is.close();
        os.close();
    }

    public void sendRequest(String request) throws IOException {
        os.write(request);
        os.flush();
    }

    public void verifyResponse() throws IOException {
        String r = is.readLine();
        if(r.equals("CLOSE")){
            terminate();
        }
        if(r.equals("INVALID FORMAT")){
            terminate();
            throw new IOException("Invalid request format");
        }
        if(r.matches("^[0-9]*$")){
            os.write("RECEIVED");
            System.out.println(r);
            return;
        }
        throw new IOException("Error while receiving from server");
    }
}
