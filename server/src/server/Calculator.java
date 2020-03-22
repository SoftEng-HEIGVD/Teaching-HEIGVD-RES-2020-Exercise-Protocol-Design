package server;

public class Calculator {
    public static int operation (int op1, int op2, char op) throws InterruptedException {
        int result;

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
