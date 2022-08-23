package database;

import java.util.ArrayList;
import java.util.List;

abstract class DBcmd {
    protected List<Condition> conditions;
    protected List<String> colNames;
    protected List<String> tableNames;
    protected String DBname;
    protected String commandType;
    protected boolean isSuccessful;

    public DBcmd(){
        conditions = new ArrayList<>();
    }

    public void setDBname(String name) {
        DBname = name;
    }

    protected abstract String query(DBServer server);

    public void addCondition(Condition c){
        conditions.add(c);
    }

    public boolean isSuccessful(){
        return isSuccessful;
    }

    protected boolean hasCondition(){
        return conditions.size() > 0;
    }

    protected ArrayList<Row> interpret(Table targetTable, Condition c, ArrayList<Integer> colNums){
        if (!targetTable.getColumnHeadings().contains(c.getAttributeName())){
            return null;
        }
        if (!checkValidCondition(c)){
            return null;
        }
        return switch (c.getOperator()) {
            case "!=" -> targetTable.notEqual(colNums, c.getAttributeName(), c.getValue());
            case "==" -> targetTable.equal(colNums, c.getAttributeName(), c.getValue());
            case ">" -> targetTable.greater(colNums, c.getAttributeName(), c.getValue());
            case "<" -> targetTable.less(colNums, c.getAttributeName(), c.getValue());
            case ">=" -> targetTable.greaterEqual(colNums, c.getAttributeName(), c.getValue());
            case "<=" -> targetTable.lessEqual(colNums, c.getAttributeName(), c.getValue());
            default -> targetTable.like(colNums, c.getAttributeName(), c.getValue());
        };
    }

    protected ArrayList<Row> evaluateConditions(Table targetTable, ArrayList<Integer> columnNums){
        ConditionStack cs = new ConditionStack();
        for (Condition i : conditions) {
            if (i.getType().equals("FULL")) {
                i.setResult(interpret(targetTable, i, columnNums));
            }
        }
        return cs.evaluate(conditions);
    }

    protected boolean checkValidCondition(Condition c){
        if (c.getValue().equalsIgnoreCase("NULL") || c.getValue().equalsIgnoreCase("TRUE") || c.getValue().equalsIgnoreCase("FALSE")){
            if (!c.getOperator().equals("!=") && !c.getOperator().equals("==")){
                return false;
            }
        }
        if (c.getOperator().equalsIgnoreCase("LIKE")){
            return !isNumeric(c.getValue());
        }
        return true;
    }

    protected boolean isNumeric(String strNum){
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


}
