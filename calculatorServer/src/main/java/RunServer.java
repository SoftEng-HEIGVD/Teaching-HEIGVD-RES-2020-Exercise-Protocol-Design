
public class RunServer {
    public static void main(String args[]){
        CalculatorServer srv = new CalculatorServer(1777);
        srv.serveClients();
    }
}
