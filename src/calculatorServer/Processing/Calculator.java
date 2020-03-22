package calculatorServer.Processing;

public class Calculator {
    public Calculator(){}

    public static final String[] Operators = {"+", "-", "*", "/"};

    public double operate(double nb1, double nb2, String op){
        final int OPERATOR_LENGTH = 1;
        if(op.length() != OPERATOR_LENGTH) throw new RuntimeException("Operator length error");
        double result = 0;
        switch (op){
            case "+":
                result = nb1 + nb2;
                break;
            case "-" :
                result = nb1 - nb2;
                break;
            case "*" :
                result = nb1 * nb2;
                break;
            case "/" :
                result = nb1 / nb2;
                break;
            default: throw new RuntimeException("Invalid operator");
        }
        return result;
    }
}
