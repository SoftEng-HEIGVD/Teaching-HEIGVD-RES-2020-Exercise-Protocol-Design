package common;

public enum Operator {
    ADD, SUB, MULTIPLY, DIVIDE;

    @Override
    public String toString() {
        switch (Operator.this) {
            case ADD:
                return "+";
            case SUB:
                return "-";
            case MULTIPLY:
                return "*";
            case DIVIDE:
                return "/";
            default:
                return "";
        }
    }
}
