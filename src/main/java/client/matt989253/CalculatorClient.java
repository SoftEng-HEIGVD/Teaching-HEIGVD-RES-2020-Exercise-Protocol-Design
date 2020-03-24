package client.matt989253;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import java.io.IOException;
import java.io.*;
import java.net.Socket;

public class CalculatorClient {

    final static int BUFFER_SIZE = 1024;
    String host;
    int port;

    // constants
    private final int buttonWidth = 75;
    private final int buttonHeight = 75;
    private final int numButtonWidth = 4;
    private final int numButtonHeight = 6;
    private final int gap = 1;

    private final int frameWidth = numButtonWidth*buttonWidth + (numButtonWidth - 1)*gap;
    private final int frameHeight = numButtonHeight*buttonHeight + (numButtonHeight - 1)*gap;

    private final Color darkDarkGray = new Color(0x2B2D2F);
    private final Color darkGray = new Color(0x3F4143);
    private final Color lightGray = new Color(0x5F6062);
    private final Color orange = new Color(0xF79E00);

    private final String fontName = "Arial";
    private final Font font = new Font(fontName, Font.PLAIN, 23);
    private final Font opFont = new Font(fontName, Font.PLAIN, 35);

    private enum Action {
        NUM,
        DEC,
        OP,
        EQUAL,
        CLEAR,
        ERROR
    }

    private JFrame frame;
    private JTextField field;

    private String calculus;
    private boolean decimalNumber = false;
    private Action lastAction;

    public CalculatorClient(String host, int port) {
        // Network stuff
        this.host = host;
        this.port = port;


        //GUI Stuff
        // setting frame size
        frame = new JFrame(); //creating instance of JFrame
        frame.getContentPane().setPreferredSize(new Dimension(frameWidth, frameHeight));
        frame.getContentPane().setBackground(darkDarkGray);
        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(createButton(0, 1, "AC", darkGray, Color.WHITE,  e -> clear()));
        frame.add(createButton(1, 1, "+/-", darkGray, Color.WHITE, e -> changeSign()));

        frame.add(createButton(2, 1, "%", darkGray, Color.WHITE, e -> appendOp('%')));

        frame.add(createButton(3, 1, 1, 1, "÷", orange, Color.WHITE, opFont, e -> appendOp('÷')));
        frame.add(createButton(3, 2, 1, 1, "×", orange, Color.WHITE, opFont, e -> appendOp('x')));
        frame.add(createButton(3, 3, 1, 1, "−", orange, Color.WHITE, opFont, e -> appendOp('−')));
        frame.add(createButton(3, 4, 1, 1, "+", orange, Color.WHITE, opFont, e -> appendOp('+')));
        frame.add(createButton(3, 5, 1, 1, "=", orange, Color.WHITE, opFont, e -> equal()));

        frame.add(createButton(0, 5, 2, 1, "0", lightGray, Color.WHITE, font, e -> appendNum(0)));
        frame.add(createButton(0, 4, "1", lightGray, Color.WHITE, e -> appendNum(1)));
        frame.add(createButton(1, 4, "2", lightGray, Color.WHITE, e -> appendNum(2)));
        frame.add(createButton(2, 4, "3", lightGray, Color.WHITE, e -> appendNum(3)));
        frame.add(createButton(0, 3, "4", lightGray, Color.WHITE, e -> appendNum(4)));
        frame.add(createButton(1, 3, "5", lightGray, Color.WHITE, e -> appendNum(5)));
        frame.add(createButton(2, 3, "6", lightGray, Color.WHITE, e -> appendNum(6)));
        frame.add(createButton(0, 2, "7", lightGray, Color.WHITE, e -> appendNum(7)));
        frame.add(createButton(1, 2, "8", lightGray, Color.WHITE, e -> appendNum(8)));
        frame.add(createButton(2, 2, "9", lightGray, Color.WHITE, e -> appendNum(9)));
        frame.add(createButton(2, 5, ".", lightGray, Color.WHITE, e -> appendDecimalpoint()));

        field = new JTextField();
        field.setEditable(false);
        field.setBounds(gap, gap, buttonWidth * 4 + gap * (4 - 1), buttonHeight);
        update(calculus);
        field.setHorizontalAlignment(SwingConstants.RIGHT);
        field.setBackground(darkDarkGray);
        field.setForeground(Color.WHITE);
        field.setFont(new Font(fontName, Font.PLAIN, 20));
        field.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        frame.add(field);

        frame.setLayout(null); //using no layout managers
        frame.setVisible(true); //making the frame visible

        clear();
    }

    private void appendNum(int i) {
        switch(lastAction) {
            case CLEAR:
                if (i == 0) {
                    return;
                } else {
                    calculus = "+" + i;
                }
                break;
            case EQUAL:
                calculus = "+" + i;
                break;
            case OP:
                calculus += "+" + i;
                break;
            case ERROR:
                return;
            default:
                if (singleZero()) {
                    calculus = calculus.substring(0, calculus.length() - 2) + "+" + i;
                } else {
                    calculus += i;
                }
        }

        lastAction = Action.NUM;
        update(calculus);
    }

    private void appendOp(char c) {
        String newOp = " " + c + " ";
        switch(lastAction) {
            case OP:
                calculus = calculus.substring(0, calculus.length() - 3) + newOp;
                break;
            case EQUAL:
                calculus = calculus + newOp;
                break;
            case DEC:
                calculus += "0" + newOp;
                break;
            case ERROR:
                return;
            default:
                calculus += newOp;
        }

        decimalNumber = false;
        lastAction = Action.OP;
        update(calculus);
    }

    private void appendDecimalpoint() {
        if (lastAction == Action.ERROR) {
            return;
        }

        if (decimalNumber) {
            return;
        }

        switch(lastAction) {
            case OP:
                calculus += "+0.";
                break;
            case DEC:
                return;
            default:
                calculus += ".";
        }

        decimalNumber = true;
        lastAction = Action.DEC;
        update(calculus);
    }

    private void changeSign() {
        if (lastAction == Action.ERROR) {
            return;
        }

        int index = lastIndexOfSign(calculus);
        if (index == -1) {
            displayError("SIGN ERROR"); // no sign was found, error.
            return;
        }

        char newSign = calculus.charAt(index) == '+' ? '-' : '+';
        calculus = calculus.substring(0, index) + newSign + calculus.substring(index + 1);

        update(calculus);
    }

    private void equal() {
        if (lastAction == Action.ERROR) {
            return;
        }

        String answer = sendMessage(calculus);
        if (answer.startsWith("ERROR")) {
            displayError(answer);
            return;
        }

        if (!answer.contains("+")) {
            answer = "+" + answer;
        }

        if (answer.contains(".")) {
            decimalNumber = true;
        } else {
            decimalNumber = false;
        }

        calculus = answer;

        lastAction = Action.EQUAL;
        update(calculus);
    }

    private void clear() {
        calculus = "+0";
        decimalNumber = false;

        lastAction = Action.CLEAR;
        update(calculus);
    }

    private void update(String s) {
        field.setText(s);
    }

    private boolean singleZero() {
        String[] split = calculus.split(" ");
        return split[split.length-1].equals("+0");
    }

    /**
     * Finds the last index of the + or - char that isn't surrounded by spaces
     * @return Said index
     */
    private int lastIndexOfSign(String string) {
        for (int i = string.length() - 1; i >= 0; i--) {
            if (string.charAt(i) == '+' || string.charAt(i) == '-') {
                if (i == 0) {
                    return 0;
                } else if (!(string.charAt(i-1) == ' ' && string.charAt(i+1) == ' ')) {
                    return i;
                }
            }
        }
        return -1; // There was an error, no sign char was found
    }

    private void displayError(String error) {
        calculus = error;
        lastAction = Action.ERROR;
        update(calculus);
    }

    private JButton createButton(int horizIndex, int vertIndex, int widthIndex, int heightIndex, String label, Color backgroundColor, Color foregroundColor, Font font, ActionListener actionListener) {
        JButton button = new JButton(label);
        button.setBounds(gap + (buttonWidth + gap)*horizIndex, gap + (buttonHeight + gap)*vertIndex, buttonWidth*widthIndex + gap*(widthIndex - 1), buttonHeight*heightIndex + gap*(heightIndex - 1));
        button.setFont(font);
        button.setBackground(backgroundColor);
        button.setForeground(foregroundColor);
        button.setBorderPainted(false);
        button.addActionListener(actionListener);

        return button;
    }

    private JButton createButton(int horizIndex, int vertIndex, String label, Color backgroundColor, Color foregroundColor, ActionListener actionListener) {
        return createButton(horizIndex, vertIndex, 1, 1, label, backgroundColor, foregroundColor, font, actionListener);
    }

    /**
     * This method does the whole network thing.
     * @param msg The message to send
     * @return True if the server answered normally, false if there was an error.
     */
    public String sendMessage(String msg) {
        Socket clientSocket = null;
        OutputStream os = null;
        InputStream is = null;

        try {
            clientSocket = new Socket(host, port);
            os = clientSocket.getOutputStream();
            is = clientSocket.getInputStream();

            String request = msg;
            os.write(request.getBytes());

            ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int newBytes;
            while ((newBytes = is.read(buffer)) != -1) {
                responseBuffer.write(buffer, 0, newBytes);
            }

            System.out.println("Response sent by the server: " + responseBuffer.toString());
            return responseBuffer.toString();

        } catch (IOException ex) {
            System.out.println("Une erreur IO est survenue");
            return "ERROR";
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                System.out.println("Une erreur IO est survenue");
                return "ERROR";
            }
            try {
                os.close();
            } catch (IOException ex) {
                System.out.println("Une erreur IO est survenue");
                return "ERROR";
            }
            try {
                clientSocket.close();
            } catch (IOException ex) {
                System.out.println("Une erreur IO est survenue");
                return "ERROR";
            }
        }
    }
}