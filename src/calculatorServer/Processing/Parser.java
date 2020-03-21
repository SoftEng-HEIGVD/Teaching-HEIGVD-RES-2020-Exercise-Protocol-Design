package calculatorServer.Processing;

public class Parser {

    private String numberRegex;

    public Parser(String numberRegex){
        this.numberRegex = "(+|-)?[0-9]";
    }

    public boolean checkNumber(String s){
        return s.matches(numberRegex);
    }
}
