package database;

import java.util.ArrayList;

public class InsertCMD extends DBcmd {
    private String tableName;
    private ArrayList<String> valueList;

    public InsertCMD(){
        valueList = new ArrayList<>();
    }

    public void setTableName(String name) {
        tableName = name;
    }

    public void addValue(String value){
        valueList.add(value);
    }

    public String query(DBServer server){
        if (server.getDB()==null){
            return "No database selected.";
        }
        if (!server.getDB().getTableList().contains(tableName)){
            return "No such table.";
        }
        Table toBeChanged = server.getDB().getTable(tableName);
        if (valueList.size() != toBeChanged.getColumnHeadings().size()-1){
            return "The number of values does not match number of columns.";
        }
        String listString = String.join("\t", valueList);
        toBeChanged.insertRow(listString, true);
        try {
            toBeChanged.writeToFile(".tab");
        } catch (Exception e) {
            return "error writing to disk.";
        }
        server.getDB().updateIdsTable(tableName);
        isSuccessful = true;
        return "values inserted.";
    }
}
