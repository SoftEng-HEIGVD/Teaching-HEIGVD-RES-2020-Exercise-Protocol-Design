package egremyb.server;

import egremyb.common.Protocol;

import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import static org.junit.Assert.*;

public class ServerTest {

    @Test
    public void shouldAcceptConnection() {
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

    @Test
    public void shouldAcceptThreeSimultaneousConnections() {
        Server server = new Server(Protocol.DEFAULT_PORT);
        Socket clientSocket1 = null;
        Socket clientSocket2 = null;
        Socket clientSocket3 = null;

        server.serveClients();
        try {
            clientSocket1 = new Socket("localhost", Protocol.DEFAULT_PORT);
            clientSocket2 = new Socket("localhost", Protocol.DEFAULT_PORT);
            clientSocket3 = new Socket("localhost", Protocol.DEFAULT_PORT);

            assertTrue(clientSocket1.isConnected() && clientSocket2.isConnected() && clientSocket3.isConnected());
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Test
    public void shouldRespondsToHelloWithWelcome() {
        Server server = new Server(Protocol.DEFAULT_PORT);
        Socket clientSocket = null;
        BufferedWriter toServer = null;
        BufferedReader fromServer = null;

        server.serveClients();
        try {
            clientSocket = new Socket("localhost", Protocol.DEFAULT_PORT);

            if(!clientSocket.isConnected()) return;

            toServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            toServer.write(Protocol.CMD_HELLO + "\n");
            toServer.flush();

            assertEquals(Protocol.CMD_WELCOME, fromServer.readLine());

            toServer.close();
            fromServer.close();
            clientSocket.close();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Test
    public void shouldRespondsWrongIfSecondHello() {
        Server server = new Server(Protocol.DEFAULT_PORT);
        Socket clientSocket = null;
        BufferedWriter toServer = null;
        BufferedReader fromServer = null;

        server.serveClients();
        try {
            clientSocket = new Socket("localhost", Protocol.DEFAULT_PORT);

            if(!clientSocket.isConnected()) return;

            toServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            toServer.write(Protocol.CMD_HELLO + "\n");
            toServer.flush();

            if(fromServer.readLine() != Protocol.CMD_WELCOME) return;

            toServer.write(Protocol.CMD_HELLO + "\n");
            toServer.flush();

            assertEquals(Protocol.CMD_WRONG, fromServer.readLine());

            toServer.close();
            fromServer.close();
            clientSocket.close();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Test
    public void shouldRespondsWrongIfNoHelloBeforeBye() {
        Server server = new Server(Protocol.DEFAULT_PORT);
        Socket clientSocket = null;
        BufferedWriter toServer = null;
        BufferedReader fromServer = null;

        server.serveClients();
        try {
            clientSocket = new Socket("localhost", Protocol.DEFAULT_PORT);

            if(!clientSocket.isConnected()) return;

            toServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            toServer.write(Protocol.CMD_BYE + "\n");
            toServer.flush();

            assertEquals(Protocol.CMD_WRONG, fromServer.readLine());

            toServer.close();
            fromServer.close();
            clientSocket.close();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Test
    public void shouldRespondsWrongIfNoHelloBeforeEquation() {
        Server server = new Server(Protocol.DEFAULT_PORT);
        Socket clientSocket = null;
        BufferedWriter toServer = null;
        BufferedReader fromServer = null;

        server.serveClients();
        try {
            clientSocket = new Socket("localhost", Protocol.DEFAULT_PORT);

            if(!clientSocket.isConnected()) return;

            toServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            toServer.write("1.3" + Protocol.SEPARATOR + Protocol.SUB_OPERATOR + Protocol.SEPARATOR + "-2.3\n");
            toServer.flush();

            assertEquals(Protocol.CMD_WRONG, fromServer.readLine());

            toServer.close();
            fromServer.close();
            clientSocket.close();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Test
    public void shouldGiveSolutionToAdd() {
        Server server = new Server(Protocol.DEFAULT_PORT);
        Socket clientSocket = null;
        BufferedWriter toServer = null;
        BufferedReader fromServer = null;
        NumberFormat formater = new DecimalFormat(Protocol.NUMBER_FORMAT);

        server.serveClients();
        try {
            clientSocket = new Socket("localhost", Protocol.DEFAULT_PORT);

            if(!clientSocket.isConnected()) return;

            toServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            toServer.write(Protocol.CMD_HELLO + "\n");
            toServer.flush();

            if(!fromServer.readLine().equals(Protocol.CMD_WELCOME)) return;

            toServer.write("1.3"+ Protocol.SEPARATOR + Protocol.ADD_OPERATOR + Protocol.SEPARATOR + "-2.3\n");
            toServer.flush();

            assertEquals(formater.format(-1d), fromServer.readLine());

            toServer.close();
            fromServer.close();
            clientSocket.close();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Test
    public void shouldGiveSolutionToSub() {
        Server server = new Server(Protocol.DEFAULT_PORT);
        Socket clientSocket = null;
        BufferedWriter toServer = null;
        BufferedReader fromServer = null;
        NumberFormat formater = new DecimalFormat(Protocol.NUMBER_FORMAT);

        server.serveClients();
        try {
            clientSocket = new Socket("localhost", Protocol.DEFAULT_PORT);

            if(!clientSocket.isConnected()) return;

            toServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            toServer.write(Protocol.CMD_HELLO + "\n");
            toServer.flush();

            if(!fromServer.readLine().equals(Protocol.CMD_WELCOME)) return;

            toServer.write("1.3" + Protocol.SEPARATOR + Protocol.SUB_OPERATOR + Protocol.SEPARATOR + "-2.3\n");
            toServer.flush();

            assertEquals(formater.format(3.6), fromServer.readLine());

            toServer.close();
            fromServer.close();
            clientSocket.close();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Test
    public void shouldGiveSolutionToMul() {
        Server server = new Server(Protocol.DEFAULT_PORT);
        Socket clientSocket = null;
        BufferedWriter toServer = null;
        BufferedReader fromServer = null;
        NumberFormat formater = new DecimalFormat(Protocol.NUMBER_FORMAT);

        server.serveClients();
        try {
            clientSocket = new Socket("localhost", Protocol.DEFAULT_PORT);

            if(!clientSocket.isConnected()) return;

            toServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            toServer.write(Protocol.CMD_HELLO + "\n");
            toServer.flush();

            if(!fromServer.readLine().equals(Protocol.CMD_WELCOME)) return;

            toServer.write("1.3" + Protocol.SEPARATOR + Protocol.MUL_OPERATOR + Protocol.SEPARATOR + "-2.3\n");
            toServer.flush();

            assertEquals(formater.format(-2.99), fromServer.readLine());

            toServer.close();
            fromServer.close();
            clientSocket.close();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Test
    public void shouldGiveSolutionToDiv() {
        Server server = new Server(Protocol.DEFAULT_PORT);
        Socket clientSocket = null;
        BufferedWriter toServer = null;
        BufferedReader fromServer = null;
        NumberFormat formater = new DecimalFormat(Protocol.NUMBER_FORMAT);

        server.serveClients();
        try {
            clientSocket = new Socket("localhost", Protocol.DEFAULT_PORT);

            if(!clientSocket.isConnected()) return;

            toServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            toServer.write(Protocol.CMD_HELLO + "\n");
            toServer.flush();

            if(!fromServer.readLine().equals(Protocol.CMD_WELCOME)) return;

            toServer.write("1.3" + Protocol.SEPARATOR + Protocol.DIV_OPERATOR + Protocol.SEPARATOR + "-2.3\n");
            toServer.flush();

            assertEquals(formater.format(-0.5652173913), fromServer.readLine());

            toServer.close();
            fromServer.close();
            clientSocket.close();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Test
    public void shouldGiveSolutionToPow() {
        Server server = new Server(Protocol.DEFAULT_PORT);
        Socket clientSocket = null;
        BufferedWriter toServer = null;
        BufferedReader fromServer = null;
        NumberFormat formater = new DecimalFormat(Protocol.NUMBER_FORMAT);

        server.serveClients();
        try {
            clientSocket = new Socket("localhost", Protocol.DEFAULT_PORT);

            if(!clientSocket.isConnected()) return;

            toServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            toServer.write(Protocol.CMD_HELLO + "\n");
            toServer.flush();

            if(!fromServer.readLine().equals(Protocol.CMD_WELCOME)) return;

            toServer.write("1.3" + Protocol.SEPARATOR + Protocol.POW_OPERATOR + Protocol.SEPARATOR + "-2.3\n");
            toServer.flush();

            assertEquals(formater.format(0.54692816626), fromServer.readLine());

            toServer.close();
            fromServer.close();
            clientSocket.close();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Test
    public void shouldEndConnectionOnBye() {
        Server server = new Server(Protocol.DEFAULT_PORT);
        Socket clientSocket = null;
        BufferedWriter toServer = null;
        BufferedReader fromServer = null;

        server.serveClients();
        try {
            clientSocket = new Socket("localhost", Protocol.DEFAULT_PORT);

            if(!clientSocket.isConnected()) return;

            toServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            toServer.write(Protocol.CMD_HELLO + "\n");
            toServer.flush();
            if(fromServer.readLine() != Protocol.CMD_WELCOME) return;

            toServer.write(Protocol.CMD_BYE + "\n");
            toServer.flush();

            assertNull(fromServer.readLine());

            toServer.close();
            fromServer.close();
            clientSocket.close();
        } catch (IOException e) {
            e.getMessage();
        }
    }
}