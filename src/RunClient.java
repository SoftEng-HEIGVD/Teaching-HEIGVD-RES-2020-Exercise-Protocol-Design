import calculatorClient.CalculatorClient;
import calculatorServer.Processing.Calculator;

public class RunClient {


    public static void main(String args[]){
        System.out.println("client main : ");
        CalculatorClient cli = new CalculatorClient(31666, "localhost");
        cli.requestServer();
    }



}
