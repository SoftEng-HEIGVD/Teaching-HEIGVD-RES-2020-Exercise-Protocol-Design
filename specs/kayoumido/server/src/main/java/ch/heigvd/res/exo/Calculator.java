package ch.heigvd.res.exo;

public class Calculator {

    public Double calculate(String op, String rhs, String lhs) throws Exception {
        switch(op) {
            case "ADD":
                return Double.parseDouble(rhs) + Double.parseDouble(lhs);
            case "SUB":
                return Double.parseDouble(rhs) - Double.parseDouble(lhs);
            case "MUL":
                return Double.parseDouble(rhs) * Double.parseDouble(lhs);
            case "DIV":
                return Double.parseDouble(rhs) / Double.parseDouble(lhs);
            case "MOD":
                return Double.parseDouble(rhs) % Double.parseDouble(lhs);
            default:
                throw new Exception("Unknown operator");
        }
    }
}
