package calculatorServer.Processing;

import java.util.Arrays;

public class SyntaxChecker {
    private final String NUMBER_REGEX;
    private final int CALCULATION_REQUEST_NB_TOKENS;
    private final String CALCULATION_REQUEST_HEAD;

    public SyntaxChecker(){
        this.NUMBER_REGEX = "(\\+|-)?[0-9]";
        this.CALCULATION_REQUEST_HEAD = "CALC";
        this.CALCULATION_REQUEST_NB_TOKENS = 4;
    }

    private boolean checkNumber(String s){
        return s.matches(NUMBER_REGEX);
    }

    private boolean checkOperator(String s){
        return Arrays.asList(Calculator.Operators).contains(s);
    }

    public boolean checkCalculationRequest(String s){
        String[] parts = s.split(" ");
        return (parts.length == CALCULATION_REQUEST_NB_TOKENS && parts[0].equals(CALCULATION_REQUEST_HEAD)
                && checkNumber(parts[1]) && checkNumber(parts[3]) && checkOperator(parts[2]));
    }


}
