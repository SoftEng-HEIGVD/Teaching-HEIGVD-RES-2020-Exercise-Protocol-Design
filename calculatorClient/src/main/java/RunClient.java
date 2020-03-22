
public class RunClient {


    public static void main(String args[]){
        System.out.println("Please enter a calculation request");
        CalculatorClient cli = new CalculatorClient(1777, "localhost");
        cli.requestServer();
    }



}
