import client.Client;
import server.Server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {


        //argument 0 --> @ip ou DNS name --> client
        if(args.length > 0){
            try {
                Client c = new Client(args[0]);
                c.sendRequest("23*");
                c.verifyResponse();
                c.terminate();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                Server srv = new Server();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
