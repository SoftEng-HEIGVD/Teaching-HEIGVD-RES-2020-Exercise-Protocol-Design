public class protocol {

    public static final String CMD_CLIENTIN = "Hello calculator";
    public static final String CMD_SERVERIN = "Connection operational";
    public static final String CMD_CLIENTOUT = "Quit";
    public static final String CMD_CLIENTCALC = "calculate";
    public static final String CMD_SERVERCALC = "calculated";
    public static final String CMD_SERVERNOOP = "Unrecognized command";

    public static Boolean checkSyntax(String s){
        if(s.length() != 15)
            return false;

        if(!Character.isDigit(s.charAt(0)) || s.charAt(1) != ' ' || !Character.isDigit(s.charAt(4)) || s.charAt(3) != ' ' || !s.substring(6).equals(CMD_CLIENTCALC))
            return false;

        return true;
    }

}
