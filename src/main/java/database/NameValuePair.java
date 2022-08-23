package database;


public class NameValuePair {
    private String attributeName;
    private String operator;
    private String value;

    public NameValuePair(){

    }

    public void setAttributeName(String name){
        attributeName = name;
    }

    public String getAttributeName(){
        return attributeName;
    }

    public void setOperator(String op){
        operator = op;
    }

    public void setValue(String val){
        value = val;
    }

    public String getValue(){
        return value;
    }
}
