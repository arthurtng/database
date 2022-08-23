package database;

import java.util.*;

public class Tokenizer {
    private ArrayList<Token> tokens;
    private Hashtable<String, String> tokenTypes;
    private ArrayList<String> preTokens;
    private int currentPreTokenIndex;

    public Tokenizer(){
        tokens = new ArrayList<>();
        tokenTypes = new Hashtable<>();
        initTokenTypes();
    }

    public ArrayList<Token> getTokens(String str){
        if (str==null){
            return null;
        }
        preTokens = new ArrayList<>(Arrays.asList(str.split("((?=[^a-zA-Z0-9])|(?<=[^a-zA-Z0-9]))")));

        // Iterate over input string and build tokens
        while (hasMorePreTokens()){
            if (!makeLiteralsOperators() && hasMorePreTokens() && !nextPreToken().isBlank()){
                tokens.add(new Token(determineType(nextPreToken()), nextPreToken()));
            }
            incrementPreToken();
        }

        return tokens;
    }

    private boolean makeLiteralsOperators(){
        if (makeStringLiteral()){
            return true;
        }
        if (makeDoubleOperatorToken()){
            return true;
        }
        if (makeSignedNumberLiteral()){
            return true;
        }
        return makeUnsignedFloatLiteral();
    }

    private boolean makeStringLiteral(){
        if (nextPreToken().equals("'")){
            StringBuilder stringLiteral = new StringBuilder();
            incrementPreToken();
            while (hasMorePreTokens() && !nextPreToken().equals("'")){
                stringLiteral.append(nextPreToken());
                incrementPreToken();
            }
            if (!hasMorePreTokens()){
                return false;
            }
            tokens.add(new Token("SL", stringLiteral.toString()));
            return true;
        }
        return false;
    }

    private boolean makeSignedNumberLiteral(){
        if (!hasMorePreTokens(1)){
            return false;
        }
        if (isSign(nextPreToken()) && isNumeric(nextPreToken(1))){
            if ((!hasMorePreTokens(2) || (hasMorePreTokens(2) && !nextPreToken(2).equals(".")))){
                tokens.add(new Token("IL", nextPreToken() + nextPreToken(1)));
                incrementPreToken();
                return true;
            }
            else if (hasMorePreTokens(3) && nextPreToken(2).equals(".")){
                tokens.add(new Token("FL", nextPreToken() + nextPreToken(1) + nextPreToken(2) + nextPreToken(3)));
                incrementPreToken(3);
                return true;
            }
        }
        return false;
    }

    private boolean makeUnsignedFloatLiteral(){
        if (hasMorePreTokens(2) && nextPreToken(1).equals(".") && isNumeric(nextPreToken(2))){
            tokens.add(new Token("FL", nextPreToken() + nextPreToken(1) + nextPreToken(2)));
            incrementPreToken(2);
            return true;
        }
        return false;
    }

    private boolean makeDoubleOperatorToken(){
        if (!hasMorePreTokens()){
            return false;
        }
        if (nextPreToken().equals("=") || nextPreToken().equals(">") || nextPreToken().equals("<") || nextPreToken().equals("!")){
            if (hasMorePreTokens(1) && nextPreToken(1).equals("=")){
                tokens.add(new Token("OP", nextPreToken() + nextPreToken(1)));
                incrementPreToken();
                return true;
            }
        }
        return false;
    }

    private String nextPreToken(int increment){
        return preTokens.get(currentPreTokenIndex + increment);
    }

    private String nextPreToken(){
        return nextPreToken(0);
    }

    private void incrementPreToken(int increment){
        if (currentPreTokenIndex == preTokens.size()){
            return;
        }
        currentPreTokenIndex += increment;
    }

    private void incrementPreToken(){
        incrementPreToken(1);
    }

    private boolean hasMorePreTokens(int increment){
        return currentPreTokenIndex + increment <= preTokens.size() - 1;
    }

    private boolean hasMorePreTokens(){
        return hasMorePreTokens(0);
    }

    private boolean isSign(String s){
        return s.equals("+") || s.equals("-");
    }

    private void initTokenTypes(){
        tokenTypes.put("USE", "CT");
        tokenTypes.put("CREATE", "CT");
        tokenTypes.put("DATABASE", "KW");
        tokenTypes.put("TABLE", "KW");
        tokenTypes.put("DROP", "CT");
        tokenTypes.put("ALTER", "CT");
        tokenTypes.put("INSERT", "CT");
        tokenTypes.put("INTO", "KW");
        tokenTypes.put("SELECT", "CT");
        tokenTypes.put("UPDATE", "CT");
        tokenTypes.put("DELETE", "CT");
        tokenTypes.put("FROM", "KW");
        tokenTypes.put("JOIN", "CT");
        tokenTypes.put("VALUES", "KW");
        tokenTypes.put("WHERE", "KW");
        tokenTypes.put(">", "OP");
        tokenTypes.put("<", "OP");
        tokenTypes.put("LIKE", "OP");
        tokenTypes.put("TRUE", "BL");
        tokenTypes.put("FALSE", "BL");
        tokenTypes.put("(", "LB");
        tokenTypes.put(")", "RB");
        tokenTypes.put("NULL", "NL");
        tokenTypes.put("ADD", "KW");
        tokenTypes.put("ON", "KW");
        tokenTypes.put("AND", "KW");
    }

    private String determineType(String preToken){
        if (tokenTypes.containsKey(preToken.toUpperCase())){
            return tokenTypes.get(preToken.toUpperCase());
        }
        else if (preToken.length() == 1 && !Character.isLetterOrDigit(preToken.charAt(0))){
            return "SY";
        }
        else if (isNumeric(preToken)){
            return "IL";
        }
        else {
            return "ID";
        }
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
