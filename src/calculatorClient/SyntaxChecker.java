package calculatorClient;

import calculatorServer.Processing.Calculator;

import java.util.Arrays;

public class SyntaxChecker {
    private final String NUMBER_REGEX;
    private final int CALCULATION_REQUEST_NB_TOKENS;
    private final String CALCULATION_REQUEST_HEAD;
    private final int NUMBER_MAX_DIGITS;
    private final String[] OPERATORS;

    public SyntaxChecker(){
        NUMBER_REGEX = "(\\+|-)?[0-9]+(.[0-9]+)?";
        CALCULATION_REQUEST_HEAD = "CALC";
        CALCULATION_REQUEST_NB_TOKENS = 4;
        NUMBER_MAX_DIGITS = 10;
        OPERATORS = new String[]{"+", "-", "*", "/"};
    }

    private boolean checkNumber(String s){
        return s.matches(NUMBER_REGEX) && s.length() <= NUMBER_MAX_DIGITS;
    }

    private boolean checkOperator(String s){
        return Arrays.asList(OPERATORS).contains(s);
    }

    public boolean checkCalculationRequest(String s){
        String[] parts = s.split(" ");
        return (parts.length == CALCULATION_REQUEST_NB_TOKENS && parts[0].equals(CALCULATION_REQUEST_HEAD)
                && checkNumber(parts[1]) && checkNumber(parts[3]) && checkOperator(parts[2]));
    }


}
