package server;

public class Calculator {

    /**
     * Do the operation asked by the client
     *
     * @param op1 rhs operand
     * @param op2 lhs operand
     * @param op operator
     * @return the result of the calculation asked by the client
     * @throws InterruptedException if the calculation is not supported, throws an exception
     */
    public static double operation (double op1, double op2, char op) throws InterruptedException {
        double result;

        switch (op) {
            case '+' :
                result = op1 + op2;
                break;
            case '-' :
                result = op1 - op2;
                break;
            case '*' :
                result = op1 * op2;
                break;
            case '/' :
                result = op1 / op2;
                break;
            case '%' :
                throw new InterruptedException("faut pas déconner");
            default:
                throw new InterruptedException("Opérateur invalide");
        }

        return result;
    }
}
