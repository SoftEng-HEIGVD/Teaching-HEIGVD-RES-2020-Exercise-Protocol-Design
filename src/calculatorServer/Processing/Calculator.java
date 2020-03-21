package calculatorServer.Processing;

public class Calculator {
    public Calculator(){}

    public double operate(double nb1, double nb2, char op){
        double result = 0;
        switch (op){
            case '+':
                result = nb1 + nb2;
                break;
            case '-' :
                result = nb1 - nb2;
                break;
            case '*' :
                result = nb1 * nb2;
                break;
            case '/' :
                result = nb1 / nb2;
                break;
            default: throw new RuntimeException("Invalid operator");
        }
        return result;
    }
}
