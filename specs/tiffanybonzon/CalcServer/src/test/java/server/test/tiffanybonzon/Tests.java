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

    /*****************************************
     * COMMANDS TESTS
     *****************************************/
    @Test
    void theServerShouldRespondToTheHelpCommand() {
        Socket client = null;
        String expectedResponse = "> Syntax: <OP> <NUM1> <NUM2> Each element is separated by a space, decimal is specified with a dot. Connection can be closed by typing END";
        BufferedWriter clientRequest = null;
        BufferedReader serverResponse = null;

        try {
            client = new Socket("localhost", 2112);
            clientRequest = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            serverResponse = new BufferedReader(new InputStreamReader(client.getInputStream()));

            clientRequest.write("HELP\n");
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
    void theServerShouldNotAnswerToEndCommand() {
        Socket client = null;
        String expectedResponse = "> Syntax: <OP> <NUM1> <NUM2> Each element is separated by a space, decimal is specified with a dot. Connection can be closed by typing END";
        BufferedWriter clientRequest = null;
        BufferedReader serverResponse = null;

        try {
            client = new Socket("localhost", 2112);
            clientRequest = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            serverResponse = new BufferedReader(new InputStreamReader(client.getInputStream()));

            clientRequest.write("END\n");
            clientRequest.flush();

            assertNull(serverResponse.readLine());

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
    void theServerShouldSendAnErrorWhenReceivingAnUnknownCommand() {
        Socket client = null;
        String expectedResponse = "> ERROR: Unknown command!";
        BufferedWriter clientRequest = null;
        BufferedReader serverResponse = null;

        try {
            client = new Socket("localhost", 2112);
            clientRequest = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            serverResponse = new BufferedReader(new InputStreamReader(client.getInputStream()));

            clientRequest.write("HALLO\n");
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

    /*****************************************
     * OPERATIONS TESTS
     *****************************************/
    @Test
    void theServerCanAddTwoIntergers() {
        Socket client = null;
        String expectedResponse = "> Result: 5.0";
        BufferedWriter clientRequest = null;
        BufferedReader serverResponse = null;

        try {
            client = new Socket("localhost", 2112);
            clientRequest = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            serverResponse = new BufferedReader(new InputStreamReader(client.getInputStream()));

            clientRequest.write("ADD 2 3\n");
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
    void theServerCanAddTwoFloats() {
        Socket client = null;
        String expectedResponse = "> Result: 7.321";
        BufferedWriter clientRequest = null;
        BufferedReader serverResponse = null;

        try {
            client = new Socket("localhost", 2112);
            clientRequest = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            serverResponse = new BufferedReader(new InputStreamReader(client.getInputStream()));

            clientRequest.write("ADD 3.3 4.021\n");
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
    void theServerCanAddNegativeNumbers() {
        Socket client = null;
        String expectedResponse = "> Result: -2.4";
        BufferedWriter clientRequest = null;
        BufferedReader serverResponse = null;

        try {
            client = new Socket("localhost", 2112);
            clientRequest = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            serverResponse = new BufferedReader(new InputStreamReader(client.getInputStream()));

            clientRequest.write("ADD -1.2 -1.2\n");
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
    void theServerCanSubstractTwoNumbers() {
        Socket client = null;
        String expectedResponse = "> Result: 3.9";
        BufferedWriter clientRequest = null;
        BufferedReader serverResponse = null;

        try {
            client = new Socket("localhost", 2112);
            clientRequest = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            serverResponse = new BufferedReader(new InputStreamReader(client.getInputStream()));

            clientRequest.write("SUB 7 3.1\n");
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
    void theServerCanMulitplyTwoNumbers() {
        Socket client = null;
        String expectedResponse = "> Result: 15.0";
        BufferedWriter clientRequest = null;
        BufferedReader serverResponse = null;

        try {
            client = new Socket("localhost", 2112);
            clientRequest = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            serverResponse = new BufferedReader(new InputStreamReader(client.getInputStream()));

            clientRequest.write("MUL 5 3\n");
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
    void theServerCanDivideTwoNumbers() {
        Socket client = null;
        String expectedResponse = "> Result: 5.0";
        BufferedWriter clientRequest = null;
        BufferedReader serverResponse = null;

        try {
            client = new Socket("localhost", 2112);
            clientRequest = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            serverResponse = new BufferedReader(new InputStreamReader(client.getInputStream()));

            clientRequest.write("DIV 15 3\n");
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
    void theServerCanExponentiateTwoNumbers() {
        Socket client = null;
        String expectedResponse = "> Result: 27.0";
        BufferedWriter clientRequest = null;
        BufferedReader serverResponse = null;

        try {
            client = new Socket("localhost", 2112);
            clientRequest = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            serverResponse = new BufferedReader(new InputStreamReader(client.getInputStream()));

            clientRequest.write("POW 3 3\n");
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

    /*****************************************
     * INVALID OPERATIONS TESTS
     *****************************************/
    @Test
    void theServerSendsErrorWhenOperationIsNotCorrectlyFormatted() {
        Socket client = null;
        String expectedResponse = "> ERROR: Invalid syntax!";
        BufferedWriter clientRequest = null;
        BufferedReader serverResponse = null;

        try {
            client = new Socket("localhost", 2112);
            clientRequest = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            serverResponse = new BufferedReader(new InputStreamReader(client.getInputStream()));

            clientRequest.write("ADD 3 3 3\n");
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
    void theServerSendsErrorWhenOperandsAreIncorrectlyFormatted() {
        Socket client = null;
        String expectedResponse = "> ERROR: Invalid operands!";
        BufferedWriter clientRequest = null;
        BufferedReader serverResponse = null;

        try {
            client = new Socket("localhost", 2112);
            clientRequest = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            serverResponse = new BufferedReader(new InputStreamReader(client.getInputStream()));

            clientRequest.write("ADD 3 3a\n");
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
    void theServerShouldSendErrorWhenTheClientWantsTODivideByZero() {
        Socket client = null;
        String expectedResponse = "> ERROR: Can't divide by 0!";
        BufferedWriter clientRequest = null;
        BufferedReader serverResponse = null;

        try {
            client = new Socket("localhost", 2112);
            clientRequest = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            serverResponse = new BufferedReader(new InputStreamReader(client.getInputStream()));

            clientRequest.write("DIV 79 0\n");
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
    void theServerSendsErrorWhenOperationsAreIncorrectlyFormatted() {
        Socket client = null;
        String expectedResponse = "> ERROR: Unknown operation!";
        BufferedWriter clientRequest = null;
        BufferedReader serverResponse = null;

        try {
            client = new Socket("localhost", 2112);
            clientRequest = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            serverResponse = new BufferedReader(new InputStreamReader(client.getInputStream()));

            clientRequest.write("ADDE 3 3\n");
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
}
