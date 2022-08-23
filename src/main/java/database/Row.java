package database;

import java.util.ArrayList;

public class Row {
    private ArrayList<Integer> colNumbers;
    private int id;
    private ArrayList<String> row;

    public Row(){
        colNumbers = new ArrayList<>();
        row = new ArrayList<>();
    }

    public String getValueByIndex(int idx){
        return row.get(idx);
    }

    public void setValueByIndex(int idx, String newValue){
        if (idx < 0 || idx >= row.size()){
            return;
        }
        row.set(idx, newValue);
    }

    public void addValue(String newValue){
        row.add(newValue);
    }

    public void addColNumber(int col){
        colNumbers.add(col);
    }

    public String toString(){
        return String.join("\t", row);
    }

    public int getId(){
        return id;
    }

    public void setId(int num){
        id = num;
    }

    public ArrayList<String> getListOfValues(){
        return row;
    }

    public void fillRow(ArrayList<String> original){
        row.addAll(original);
    }

}
