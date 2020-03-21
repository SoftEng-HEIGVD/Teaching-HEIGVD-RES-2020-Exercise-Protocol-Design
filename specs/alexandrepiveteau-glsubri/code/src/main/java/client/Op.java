package client;

import java.util.InputMismatchException;

public enum Op {
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/");

    private String op;

    Op(String op) {
        this.op = op;
    }

    public static Op fromToken(String token) throws InputMismatchException {
        for (Op operator : values()) {
            if (operator.op.equals(token))
                return operator;
        }
        throw new InputMismatchException();
    }
}
