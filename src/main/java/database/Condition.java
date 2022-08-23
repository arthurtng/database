package database;

import java.util.ArrayList;

public class Condition {
    private String attributeName;
    private String operator;
    private String value;
    private String type;
    private ArrayList<Row> result;

    public Condition(String t){
        type = t;
    }

    public void setResult(ArrayList<Row> r){
        result = r;
    }

    public ArrayList<Row> getResult(){
        return result;
    }

    public String getType(){
        return type;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAttributeName(){
        if (attributeName == null){
            return "";
        }
        return attributeName;
    }

    public String getOperator(){
        if (operator == null){
            return "";
        }
        return operator;
    }

    public String getValue(){
        if (value == null){
            return "";
        }
        return value;
    }
}
