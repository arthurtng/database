package database;

import java.util.ArrayList;

public class Parser {
    private ArrayList<Token> tokens;
    private int currentIndex;
    private String statusMessage;
    public DBcmd command;

    public Parser(String userInput){
        Tokenizer t = new Tokenizer();
        tokens = t.getTokens(userInput);
        currentIndex = 0;
    }

    public String getStatusMessage(){
        return statusMessage;
    }

    public DBcmd getCommand(){
        return command;
    }

    public DBcmd parse(){
        if (!parseCommand()){
            return null;
        }
        incrementToken();
        if (!hasMoreTokens() || !nextToken().getWord().equals(";")){
            statusMessage = "Missing semi-colon";
            return null;
        }
        incrementToken();
        if (hasMoreTokens()){
            statusMessage = "No more commands after semi-colon";
            return null;
        }
        statusMessage = "[OK]";
        return command;
    }

    private boolean parseCommand(){
        String cw = nextToken().getWord().toUpperCase();

        switch (cw) {
            case "SELECT":
                return parseSelect();
            case "ALTER":
                return parseAlter();
            case "USE":
                return parseUse();
            case "CREATE":
                return parseCreate();
            case "DROP":
                return parseDrop();
            case "INSERT":
                return parseInsert();
            case "UPDATE":
                return parseUpdate();
            case "DELETE":
                return parseDelete();
            case "JOIN":
                return parseJoin();
            default:
                statusMessage = "incorrect command syntax";
                return false;
        }
    }

    public boolean parseJoin(){
        incrementToken();
        JoinCMD command = new JoinCMD();
        if (!hasMoreTokens() || !isValidName(nextToken())){
            statusMessage = "Missing table one";
            return false;
        }
        command.setTableOne(nextToken().getWord());
        incrementToken();
        if (!hasMoreTokens() || !nextToken().getWord().equalsIgnoreCase("AND")){
            statusMessage = "Missing AND";
            return false;
        }
        incrementToken();
        if (!hasMoreTokens() || !isValidName(nextToken())){
            statusMessage = "Missing table two";
            return false;
        }
        command.setTableTwo(nextToken().getWord());
        incrementToken();
        return parseOn(command);
    }

    private boolean parseOn(JoinCMD command){
        if (!hasMoreTokens() || !nextToken().getWord().equalsIgnoreCase("ON")){
            statusMessage = "Missing ON";
            return false;
        }
        incrementToken();
        if (!hasMoreTokens() || !isValidName(nextToken())){
            statusMessage = "Missing attribute one";
            return false;
        }
        command.setAttributeOne(nextToken().getWord());
        incrementToken();
        if (!hasMoreTokens() || !nextToken().getWord().equalsIgnoreCase("AND")){
            statusMessage = "Missing AND";
            return false;
        }
        incrementToken();
        if (!hasMoreTokens() || !isValidName(nextToken())){
            statusMessage = "Missing attribute two";
            return false;
        }
        command.setAttributeTwo(nextToken().getWord());
        this.command = command;
        return true;
    }

    public boolean parseDelete(){
        incrementToken();
        if (!hasMoreTokens() || !nextToken().getWord().equalsIgnoreCase("FROM")){
            statusMessage = "Incorrect command syntax";
            return false;
        }
        DeleteCMD command = new DeleteCMD();
        incrementToken();
        if (!hasMoreTokens() || !isValidName(nextToken())){
            statusMessage = "Missing table";
            return false;
        }
        command.setTableName(nextToken().getWord());
        incrementToken();
        return parseWhere(command);
    }

    public boolean parseUpdate(){
        incrementToken();
        UpdateCMD command = new UpdateCMD();
        if (!hasMoreTokens() || !isValidName(nextToken())){
            statusMessage = "Missing table name";
            return false;
        }
        command.setTableName(nextToken().getWord());
        incrementToken();
        if (!hasMoreTokens() || !nextToken().getWord().equalsIgnoreCase("SET")){
            statusMessage = "Missing SET";
            return false;
        }
        incrementToken();
        while (hasMoreTokens() && !nextToken().getWord().equalsIgnoreCase("WHERE")){
            if (!parseNameValueList(command)){
                return false;
            }
        }
        return parseWhere(command);
    }

    private boolean parseNameValueList(UpdateCMD command){
        if (!parseNameValuePair(command)){
            return false;
        }
        incrementToken();
        if (hasMoreTokens() && nextToken().getWord().equals(",")){
            incrementToken();
            if (!parseNameValueList(command)){
                return false;
            }
        }
        if (!hasMoreTokens() || !nextToken().getWord().equals("WHERE")){
            statusMessage = "Missing comma or WHERE keyword";
            return false;
        }
        return true;
    }

    private boolean parseNameValuePair(UpdateCMD command){
        if (!hasMoreTokens() || !isValidName(nextToken())){
            statusMessage = "Missing attribute";
            return false;
        }
        NameValuePair n = new NameValuePair();
        n.setAttributeName(nextToken().getWord());
        incrementToken();
        if (!hasMoreTokens() || !nextToken().getWord().equals("=")){
            statusMessage = "Missing operator";
            return false;
        }
        n.setOperator(nextToken().getWord());
        incrementToken();
        if (!hasMoreTokens() || (!isValue(nextToken()))){
            statusMessage = "Missing value";
            return false;
        }
        if (nextToken().getTokenType().equals("BL") || nextToken().getTokenType().equals("NL")){
            n.setValue(nextToken().getWord().toUpperCase());
        }
        else {
            n.setValue(nextToken().getWord());
        }
        command.addNameValuePair(n);
        return true;
    }


    private boolean isValidName(Token t){
        return !t.getWord().matches("^.*[^a-zA-Z0-9 ].*$");
    }

    public boolean parseSelect(){
        SelectCMD command = new SelectCMD();
        incrementToken();
        if (!hasMoreTokens() || !parseWildAttribList(command)){
            statusMessage = "Missing attribute";
            return false;
        }
        incrementToken();
        if (!hasMoreTokens() || !nextToken().getWord().equalsIgnoreCase("FROM")){
            statusMessage = "Missing <FROM> keyword";
            return false;
        }
        incrementToken();
        if (!hasMoreTokens() || !isValidName(nextToken())){
            statusMessage = "Missing table name";
            return false;
        }
        command.setTable(nextToken().getWord());
        incrementToken();
        if (hasMoreTokens() && nextToken().getWord().equals(";")){
            decrementToken();
            this.command = command;
            return true;
        }
        return parseWhere(command);
    }

    private boolean parseWhere (DBcmd command){
        if (!hasMoreTokens()){
            statusMessage = "Missing semi-colon";
            return false;
        }
        if (!nextToken().getWord().equalsIgnoreCase("WHERE")) {
            decrementToken();
            statusMessage = "Missing WHERE keyword";
            return false;
        }
        incrementToken();
        while (hasMoreTokens() && !nextToken().getWord().equals(";")) {
            if (!parseCondition(command)) {
                return false;
            }
            incrementToken();
        }
        decrementToken();
        if (!command.hasCondition()){
            statusMessage = "Missing condition";
            return false;
        }
        this.command = command;
        return true;
    }

    private boolean parseCondition (DBcmd command){

        // Brackets
        if (hasMoreTokens() && nextToken().getWord().equals("(")){
            return parseMultConditions(command);
        }

        // Main condition
        if (!hasMoreTokens() || !isValidName(nextToken())){
            statusMessage = "Missing attribute";
            return false;
        }
        Condition c = new Condition("FULL");
        c.setAttributeName(nextToken().getWord());

        incrementToken();
        if (!hasMoreTokens() || !nextToken().getTokenType().equals("OP")){
            statusMessage = "Missing operator";
            return false;
        }
        c.setOperator(nextToken().getWord());

        incrementToken();
        if (!hasMoreTokens() || !isValue(nextToken())){
            statusMessage = "Missing value";
            return false;
        }
        if (nextToken().getTokenType().equals("BL") || nextToken().getTokenType().equals("NL")) {
            c.setValue(nextToken().getWord().toUpperCase());
        }
        else {
            c.setValue(nextToken().getWord());
        }
        command.addCondition(c);
        return true;
    }

    private boolean parseMultConditions(DBcmd command){

        Condition c = new Condition("LBRACKET");
        c.setOperator("(");
        command.addCondition(c);
        incrementToken();
        if (!parseCondition(command)){
            return false;
        }
        incrementToken();
        if (!hasMoreTokens() || !nextToken().getWord().equals(")")){
            statusMessage = "Missing closing bracket";
            return false;
        }
        Condition e = new Condition("RBRACKET");
        e.setOperator(")");
        command.addCondition(e);
        incrementToken();
        if (!hasMoreTokens() || (!nextToken().getWord().equalsIgnoreCase("AND") && !nextToken().getWord().equalsIgnoreCase("OR"))){
            statusMessage = "Missing AND, OR operator";
            return false;
        }
        Condition d = new Condition("OP");
        d.setOperator(nextToken().getWord().toUpperCase());
        command.addCondition(d);
        incrementToken();
        if (!hasMoreTokens() || !nextToken().getWord().equals("(")){
            statusMessage = "Missing second opening bracket";
            return false;
        }
        Condition f = new Condition("LBRACKET");
        f.setOperator("(");
        command.addCondition(f);
        incrementToken();
        if (!parseCondition(command)){
            return false;
        }
        incrementToken();
        if (!hasMoreTokens() || !nextToken().getWord().equals(")")){
            statusMessage = "Missing closing bracket";
            return false;
        }
        Condition g = new Condition("RBRACKET");
        g.setOperator(")");
        command.addCondition(g);
        return true;
    }

    private boolean parseWildAttribList(SelectCMD s){
        if (nextToken().getWord().equals("*")){
            s.addAttribute("*");
            s.setSelectAllColumns();
            return true;
        }
        if (!hasMoreTokens() || !isValidName(nextToken())){
            statusMessage = "Missing attribute";
            return false;
        }
        s.addAttribute(nextToken().getWord());
        incrementToken();
        if (!hasMoreTokens() || !nextToken().getWord().equals(",")){
            if (hasMoreTokens() && nextToken().getWord().equalsIgnoreCase("FROM")){
                decrementToken();
                return true;
            }
            if (hasMoreTokens() || isValidName(nextToken())){
                statusMessage = "Missing comma";
                return false;
            }
            statusMessage = "Missing FROM";
            return false;
        }
        incrementToken();
        return parseWildAttribList(s);
    }

    public boolean parseInsert(){
        incrementToken();
        if (!hasMoreTokens() || !nextToken().getWord().equalsIgnoreCase("INTO")){
            statusMessage = "Wrong command syntax";
            return false;
        }
        InsertCMD command = new InsertCMD();
        incrementToken();
        if (!hasMoreTokens() || !isValidName(nextToken())){
            statusMessage = "Missing table name";
            return false;
        }
        command.setTableName(nextToken().getWord());
        incrementToken();
        if (!hasMoreTokens() || !nextToken().getWord().equalsIgnoreCase("VALUES")){
            statusMessage = "Missing VALUES";
            return false;
        }
        incrementToken();
        if (!hasMoreTokens() || !nextToken().getWord().equals("(")){
            statusMessage = "Missing opening bracket";
            return false;
        }
        incrementToken();
        return parseValueList(command);
    }

    private boolean parseValueList(InsertCMD command){
        boolean hasValue = false;
        while (hasMoreTokens() && !nextToken().getWord().equals(")")){
            if (!isValue(nextToken()) && !nextToken().getWord().equals(",")){
                if (hasValue){
                    statusMessage = "No closing bracket";
                    return false;
                }
                statusMessage = "Incorrect values statement";
                return false;
            }
            if (!nextToken().getWord().equals((","))){
                hasValue = true;
                if (nextToken().getTokenType().equals("BL") || nextToken().getTokenType().equals("NL")){
                    command.addValue(nextToken().getWord().toUpperCase());
                }
                else if(command.isNumeric(nextToken().getWord()) && nextToken().getWord().charAt(0) == '+'){
                    command.addValue(nextToken().getWord().substring(1));
                }
                else {
                    command.addValue(nextToken().getWord());
                }
            }
            incrementToken();
        }
        if (!hasValue){
            statusMessage = "Missing value";
            return false;
        }
        if (!hasMoreTokens() || !nextToken().getWord().equals(")")){
            statusMessage = "No closing bracket";
            return false;
        }
        this.command = command;
        return true;
    }

    private boolean isValue(Token t){
        return t.getWord().equalsIgnoreCase("NULL")
                || t.getTokenType().equals("SL")
                || t.getTokenType().equals("BL") || t.getTokenType().equals("FL")
                || t.getTokenType().equals("IL");
    }

    public boolean parseDrop(){
        incrementToken();
        if (hasMoreTokens() && !nextToken().getWord().equalsIgnoreCase("DATABASE") && !nextToken().getWord().equalsIgnoreCase("TABLE")){
            statusMessage = "Wrong command syntax";
            return false;
        }
        DropCMD command = new DropCMD();
        if (nextToken().getWord().equalsIgnoreCase("DATABASE")){
            command.setStructure(nextToken().getWord().toUpperCase());
            incrementToken();
            if (!hasMoreTokens() || !isValidName(nextToken())){
                statusMessage = "Missing database name";
                return false;
            }
            command.setDBname(nextToken().getWord());
        }
        else {
            command.setStructure(nextToken().getWord().toUpperCase());
            incrementToken();
            if (!hasMoreTokens() || !isValidName(nextToken())){
                statusMessage = "Missing table name";
                return false;
            }
            command.setTable(nextToken().getWord());
        }
        this.command = command;
        return true;
    }

    public boolean parseCreate(){
        incrementToken();
        if (!nextToken().getWord().equalsIgnoreCase("DATABASE") && !nextToken().getWord().equalsIgnoreCase("TABLE")){
            statusMessage = "Wrong command syntax";
            return false;
        }
        CreateCMD command = new CreateCMD();
        command.setStructure(nextToken().getWord().toUpperCase());
        if (nextToken().getWord().equalsIgnoreCase("DATABASE")){
            incrementToken();
            if (!isValidName(nextToken())){
                statusMessage = "Missing database name";
                return false;
            }
            command.setDBname(nextToken().getWord());
        }
        else {
            incrementToken();
            if (!isValidName(nextToken())){
                statusMessage = "Missing table name";
                return false;
            }
            command.setTableName(nextToken().getWord());
        }
        incrementToken();
        if (!hasMoreTokens() || nextToken().getWord().equals(";")){
            decrementToken();
            this.command = command;
            return true;
        }
        return parseAttributeList(command);
    }

    private boolean parseAttributeList(CreateCMD command) {
        if (!nextToken().getWord().equals("(")) {
            statusMessage = "Missing opening bracket";
            return false;
        }
        incrementToken();
        if (!hasMoreTokens() || !isValidName(nextToken())){
            statusMessage = "Missing attribute";
            return false;
        }
        while (hasMoreTokens()) {
            if (isValidName(nextToken())){
                command.addAttribute(nextToken().getWord());
            }
            incrementToken();
            if (!hasMoreTokens()) {
                statusMessage = "Missing closing bracket";
                return false;
            }
            if (nextToken().getWord().equals(")")) {
                this.command = command;
                return true;
            }
            if (!nextToken().getWord().equals(",")) {
                statusMessage = "Incorrect attribute syntax";
                return false;
            }
            incrementToken();
        }
        return false;
    }

    public boolean parseUse(){
        currentIndex++;
        if (!isValidName(nextToken())){
            statusMessage = "Missing database name";
            return false;
        }
        UseCMD command = new UseCMD();
        command.setDatabaseName(nextToken().getWord());
        this.command = command;
        return true;
    }

    public boolean parseAlter(){
        incrementToken();
        if (!nextToken().getWord().equalsIgnoreCase("TABLE")){
            statusMessage = "Wrong command syntax";
            return false;
        }
        AlterCMD command = new AlterCMD();
        incrementToken();
        if (!isValidName(nextToken())){
            statusMessage = "Missing table name";
            return false;
        }
        command.setTable(nextToken().getWord());
        incrementToken();
        if (!nextToken().getWord().equalsIgnoreCase("ADD") && !nextToken().getWord().equalsIgnoreCase("DROP")){
            statusMessage = "Missing alteration type";
            return false;
        }
        command.setAlterationType(nextToken().getWord().toUpperCase());
        incrementToken();
        if (!isValidName(nextToken())){
            statusMessage = "Missing attribute name";
            return false;
        }
        command.setAttributeName(nextToken().getWord());
        this.command = command;
        return true;
    }

    private Token nextToken(int increment){
        return tokens.get(currentIndex + increment);
    }

    private Token nextToken(){
        return nextToken(0);
    }

    private boolean hasMoreTokens(int increment){
        return currentIndex + increment <= tokens.size() - 1;
    }

    private boolean hasMoreTokens(){
        return hasMoreTokens(0);
    }

    private void incrementToken(int increment){
        if (currentIndex == tokens.size()){
            return;
        }
        currentIndex += increment;
    }

    private void incrementToken(){
        incrementToken(1);
    }

    private void decrementToken(int increment){
        currentIndex -= increment;
    }

    private void decrementToken(){
        decrementToken(1);
    }


}
