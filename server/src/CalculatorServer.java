import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalculatorServer {

    public static void main(String[] args) {

        CalculatorSingleThread server = new CalculatorSingleThread(220);
        server.serveClients();

    }

}
