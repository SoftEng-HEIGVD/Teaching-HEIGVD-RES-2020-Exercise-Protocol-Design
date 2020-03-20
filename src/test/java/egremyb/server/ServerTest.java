package egremyb.server;

import egremyb.common.Protocol;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {
    @Test
    void shouldAcceptConnection() {
        Server server = new Server(Protocol.DEFAULT_PORT);
        Socket clientSocket = null;

        server.serveClients();
        try {
            clientSocket = new Socket("localhost", Protocol.DEFAULT_PORT);
            assertTrue(clientSocket.isConnected());
        } catch (IOException e) {
            e.getMessage();
        }
    }
}