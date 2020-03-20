package doeni98;

public class Worker {
    public double calc(String request) throws Exception{
        double operand1, operand2;
        String requestSplit[];
        if((requestSplit = request.split(";")).length != 3){
            throw new Exception("Wrong formatted request");
        }
        operand1 = Double.parseDouble(requestSplit[1]); // can throw
        operand2 = Double.parseDouble(requestSplit[2]); // can throw
        switch (requestSplit[0]){
            case "+":
                return operand1 + operand2;
            case "-":
                return operand1 - operand2;
            case "*":
                return operand1 * operand2;
            case "/":
                return operand1 / operand2;
            default:
                throw new Exception("Unknown operator");
        }

    }
}
