package egremyb.client;

import egremyb.common.Protocol;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.*;

public class ClientTest {
    private static ServerSocket   server;
    private static Socket         clientSocket;
    private static PrintWriter    clientOut;
    private static BufferedReader clientIn;

    @BeforeClass
    public static void setup() throws IOException {
        // create a server socket
        server = new ServerSocket(Protocol.DEFAULT_PORT);
        // run a thread to accept and welcome any client
        new Thread(() -> {
            while (true) {
                try {
                    clientSocket = server.accept();
                    clientOut    = new PrintWriter(clientSocket.getOutputStream(), true);
                    clientIn     = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    clientOut.println(Protocol.CMD_WELCOME);
                } catch (IOException ignored) {

                }
            }
        }).start();
    }

    @Test
    public void shouldBeAbleToOpenAConnectionToServer() throws IOException {
        Client client = new Client();
        // open connection
        assertEquals(0, client.openConnection());
        assertEquals(Protocol.CMD_HELLO,  clientIn.readLine());
        // close connection
        client.closeConnection();
        assertEquals(Protocol.CMD_BYE,  clientIn.readLine());
    }

    @Test
    public void shouldSendTheEquationCorrectlyAndReceiveSolution() throws IOException {
        final Double OPERAND1 = 10d;
        final Double OPERAND2 = 4d;
        final String OPERATOR = Protocol.ADD_OPERATOR;
        final String RESULT   = "14.00";
        Double result;

        // connect to server
        Client client = new Client();
        assertEquals(0, client.openConnection());
        assertEquals(Protocol.CMD_HELLO,  clientIn.readLine());

        // server send result first for client to read it
        // client send equation
        clientOut.println(RESULT);
        result = client.sendCalculationToCompute(OPERAND1, OPERATOR, OPERAND2);

        // check what each ends received
        assertEquals(OPERAND1 + Protocol.SEPARATOR + OPERATOR + Protocol.SEPARATOR + OPERAND2, clientIn.readLine());
        Double expected = OPERAND1 + OPERAND2;
        assertEquals(expected, result);

        // close client
        client.closeConnection();
        assertEquals(Protocol.CMD_BYE,  clientIn.readLine());
    }

    @Test
    public void shouldReturnNullWhenSendingBadlyWrittenEquation() throws IOException {
        final Double OPERAND1 = 10d;
        final Double OPERAND2 = 4d;
        final String OPERATOR = "ADD";
        final String RESULT   = Protocol.CMD_WRONG;
        Double result;

        // connect to server
        Client client = new Client();
        assertEquals(0, client.openConnection());
        assertEquals(Protocol.CMD_HELLO,  clientIn.readLine());

        // server send result first for client to read it
        // client send equation
        clientOut.println(RESULT);
        result = client.sendCalculationToCompute(OPERAND1, OPERATOR, OPERAND2);

        // check what each ends received
        assertEquals(OPERAND1 + Protocol.SEPARATOR + OPERATOR + Protocol.SEPARATOR + OPERAND2, clientIn.readLine());
        assertNull(result);

        // close client
        client.closeConnection();
        assertEquals(Protocol.CMD_BYE,  clientIn.readLine());
    }

    @AfterClass
    public static void tearDown() throws IOException {
        server.close();
    }

}
