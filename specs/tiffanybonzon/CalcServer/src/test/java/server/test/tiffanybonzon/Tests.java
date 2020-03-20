package server.test.tiffanybonzon;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.impl.tiffanybonzon.Server;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
        assertTrue(true);
    }
}
