package server.test.tiffanybonzon;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.impl.tiffanybonzon.Server;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class Tests {
    @BeforeAll
    static void initServ() {
        Server srv = new Server(2112);
        srv.serveClients();
    }

    /*****************************************
     * CONNECTION TESTS
     *****************************************/
    @Test
    void theServerShouldAcceptAConnection() {
        Socket client = null;

        try {
            client = new Socket("localhost", 2112);
            assertTrue(client.isConnected());

        } catch(IOException e) {
            fail();
        } finally {
            try {
                client.close();
            } catch(Exception e1) {
                System.out.println(e1.getMessage());
            }
        }
    }

    @Test
    void theServerShouldAcceptMultipleConnections() {
        int clientNum = 5;
        Socket[] clients = new Socket[clientNum];

        try {
            for(int i = 0; i < clientNum; ++i) {
                clients[i] = new Socket("localhost", 2112);
            }

            assertTrue(clients[0].isConnected() && clients[1].isConnected() && clients[2].isConnected() && clients[3].isConnected() && clients[4].isConnected());
        } catch(IOException e) {
            fail();
        } finally {
            try {
                for(int i = 0; i < clientNum; ++i) {
                    clients[i].close();
                }
            } catch(Exception e1) {
                System.out.println(e1.getMessage());
            }
        }
    }

    /*****************************************
     * INIT TESTS
     *****************************************/
    @Test
    void theServerShouldSendOPListWhenReceivingHello() {
        Socket client = null;
        String expectedResponse = "> Available operations: ADD SUB MUL DIV POW";
        BufferedWriter clientRequest = null;
        BufferedReader serverResponse = null;

        try {
            client = new Socket("localhost", 2112);
            clientRequest = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            serverResponse = new BufferedReader(new InputStreamReader(client.getInputStream()));

            clientRequest.write("HELLO\n");
            clientRequest.flush();

            assertEquals(expectedResponse, serverResponse.readLine());

        } catch(IOException e) {
            fail();
        } finally {
            try {
                clientRequest.close();
                serverResponse.close();
                client.close();
            } catch(Exception e1) {
                System.out.println(e1.getMessage());
            }
        }
    }

    @Test
    void theServerShouldSendOPListWhenReceivingMultipleHello() {
        Socket client = null;
        String expectedResponse = "> Available operations: ADD SUB MUL DIV POW";
        BufferedWriter clientRequest = null;
        BufferedReader serverResponse = null;

        try {
            client = new Socket("localhost", 2112);
            clientRequest = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            serverResponse = new BufferedReader(new InputStreamReader(client.getInputStream()));

            clientRequest.write("HELLO\n");
            clientRequest.flush();
            if(serverResponse.readLine().equals(expectedResponse)) {
                clientRequest.write("HELLO\n");
                clientRequest.flush();
                assertEquals(expectedResponse, serverResponse.readLine());
            } else {
                fail();
            }

        } catch(IOException e) {
            fail();
        } finally {
            try {
                clientRequest.close();
                serverResponse.close();
                client.close();
            } catch(Exception e1) {
                System.out.println(e1.getMessage());
            }
        }
    }
}
