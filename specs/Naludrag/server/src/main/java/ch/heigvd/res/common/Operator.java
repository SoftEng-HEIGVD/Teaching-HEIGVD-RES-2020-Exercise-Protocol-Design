package ch.heigvd.res.common;

public enum Operator {
    ADD {
        @Override
        public int eval(int a, int b) {
            return a + b;
        }

        @Override
        public String toString() {
            return "+";
        }
    },
    SUB {
        @Override
        public int eval(int a, int b) {
            return a - b;
        }

        @Override
        public String toString() {
            return "-";
        }
    },
    MULTIPLY {
        @Override
        public int eval(int a, int b) {
            return a * b;
        }

        @Override
        public String toString() {
            return "*";
        }
    },
    DIVIDE {
        @Override
        public int eval(int a, int b) {
            return a / b;
        }

        @Override
        public String toString() {
            return "/";
        }
    };

    /**
     * Evaluate the result of a Operator b
     * @param a first operand
     * @param b second operand
     * @return a Operator b
     */
    public abstract int eval(int a, int b);

    /**
     * Find the matching Operator from a symbol
     * @param op String symbol
     * @return Operator matching the symbol
     */
    public static Operator getOperator(String op) {
        for (Operator operator : Operator.values()) {
            if (op.equals(operator.toString()))
                return operator;
        }

        return null;
    }
}
