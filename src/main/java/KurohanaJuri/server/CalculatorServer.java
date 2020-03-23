package KurohanaJuri.server;

public class CalculatorServer {

    public CalculatorServer(int port) {

        //CalculatorSingleThread server = new CalculatorSingleThread(9999);
        //server.serveClients();

        CalculatorMultiThread serverMultithread = new CalculatorMultiThread(port);
        serverMultithread.serveClients();
    }

}
