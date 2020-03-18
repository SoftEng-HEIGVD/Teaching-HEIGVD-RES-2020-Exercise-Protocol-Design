
public class Calculator {
    private String sign;
    private Float op1;
    private Float op2;

    public Calculator(String sign, Float op1, Float op2) {
        this.sign = sign;
        this.op1 = op1;
        this.op2 = op2;
    }

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
