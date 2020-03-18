
public class Calculator {
    private String sign;
    private Float op1;
    private Float op2;

    /**
     * configure for a calcul with two operands
     * @param sign operation to do
     * @param op1 first operand
     * @param op2 second operand
     */
    public Calculator(String sign, Float op1, Float op2) {
        this.sign = sign;
        this.op1 = op1;
        this.op2 = op2;
    }

    /**
     * do the operation
     * @return result of the operation
     */
    public Float Op(){
        switch (sign){
            case "+":
                return op1 + op2;
            case "-":
                return op1 - op2;
            case "/":
                return op1 / op2;
            case "*":
                return op1 * op2;
            default:
                throw new IllegalArgumentException("the sign is not code yet");
        }
    }
}
