public class CalculatorUtils {

    private static final char OP_ADDITION       = '+';
    private static final char OP_SUBSTRACTION   = '-';
    private static final char OP_DIVISION       = '/';
    private static final char OP_MULTIPLICATION = '*';

    public static boolean isInputValid(String[] inputArray) {
        return inputArray.length == 3 && isNumeric(inputArray[0]) && isNumeric(inputArray[2]) && inputArray[1]
                .length() == 1 && isValidOperator(inputArray[1].charAt(0));
    }

    public static boolean isNumeric(String str) {

        if (str == null || str.length() == 0) {
            return false;
        }

        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;

    }

    public static boolean isValidOperator(char c) {
        return c == OP_ADDITION || c == OP_DIVISION ||
                c == OP_MULTIPLICATION || c == OP_SUBSTRACTION;
    }

    public static double computeInput(String[] inputArray) {
        double result = 0;

        switch(inputArray[1].charAt(0)) {
            case OP_ADDITION:
                result = Double.parseDouble(inputArray[0]) + Double.parseDouble(inputArray[2]);
                break;
            case OP_SUBSTRACTION:
                result = Double.parseDouble(inputArray[0]) - Double.parseDouble(inputArray[2]);
                break;
            case OP_MULTIPLICATION:
                result = Double.parseDouble(inputArray[0]) * Double.parseDouble(inputArray[2]);
                break;
            case OP_DIVISION:
                result = Double.parseDouble(inputArray[0]) / Double.parseDouble(inputArray[2]);
                break;
        }

        return result;
    }


}
