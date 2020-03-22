package calculatorServer.Processing;


public class Parser {

    public Parser(){
    }

    public double parseCalculationRequest(String s){
        String[] parts = s.split(" ");
        Calculator c = new Calculator();
        return c.operate(parseNumber(parts[1]), parseNumber(parts[3]), parts[2]);
    }

    public double parseNumber(String s){
        SyntaxChecker checker = new SyntaxChecker();
        return Double.parseDouble(s);
    }
}
