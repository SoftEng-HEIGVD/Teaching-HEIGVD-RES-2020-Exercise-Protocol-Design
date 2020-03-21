import calculatorServer.CalculatorServer;

public class RunServer {
    public static void main(String args[]){
        System.out.println("server main :");
        CalculatorServer srv = new CalculatorServer(31666);
        srv.serveClients();
    }
}
