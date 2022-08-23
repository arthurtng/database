package database;

public class Token {
    private String tokenType;
    private String word;

    public Token(String type, String tokenWord) {
        tokenType = type;
        word = tokenWord;
    }

    public String getTokenType(){
        return tokenType;
    }

    public String getWord() { return word; }
}
