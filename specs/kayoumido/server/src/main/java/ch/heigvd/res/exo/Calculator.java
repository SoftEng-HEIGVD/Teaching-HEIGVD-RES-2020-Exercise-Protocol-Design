package ch.heigvd.res.exo;

public class Calculator {

    public int calculate(String op, String rhs, String lhs) {

        switch (op) {
            case "ADD":
                return Integer.parseInt(rhs) + Integer.parseInt(lhs);
        }
        return 0;
    }
}
