package database;

import java.util.ArrayList;

public class UpdateCMD extends DBcmd {
    private String tableName;
    private ArrayList<NameValuePair> nameValueList;
    private Table targetTable;

    public UpdateCMD(){
        nameValueList = new ArrayList<>();
        conditions = new ArrayList<>();
        commandType = "UpdateCMD";
    }

    public void setTableName(String name){
        tableName = name;
    }

    public void addNameValuePair(NameValuePair n){
        nameValueList.add(n);
    }

    public void addCondition(Condition c){
        conditions.add(c);
    }

    public String query(DBServer server){
        if (server.getDB()==null){
            return "No database selected.";
        }
        targetTable = server.getDB().getTable(tableName);
        if (targetTable == null){
            return "No such table";
        }
        ArrayList<Integer> columnNums = new ArrayList<>();
        int col = 0;
        for (String column : targetTable.getColumnHeadings()) {
            columnNums.add(col);
            col++;
        }
        return processConditions(targetTable, columnNums);
    }

    private String processConditions(Table targetTable, ArrayList<Integer> columnNums) {
        ArrayList<Row> rows;
        if (conditions.size()==1){
            rows = interpret(targetTable, conditions.get(0), columnNums);
        }
        else {
            rows = evaluateConditions(targetTable, columnNums);
        }
        if (rows == null){
            return "Could not evaluate expression";
        }
        boolean tableChanged = false;
        for (Row r : rows){
            if (editRow(r)){
                tableChanged = true;
            }
        }
        if (tableChanged) {
            try {
                targetTable.writeToFile(".tab");
            } catch (Exception e) {
                return "error writing to disk";
            }
        }
        isSuccessful = true;
        return "values updated.";
    }

    private boolean editRow(Row r){
        boolean changed = false;
        for (int i=0; i<targetTable.getRows().size(); i++){
            String listString = String.join(" ", targetTable.getRow(i).toString());
            if (r.toString().equals(listString)){
                for (NameValuePair n : nameValueList) {
                    targetTable.editRowById(targetTable.getRowId(i), n.getAttributeName(), n.getValue());
                    changed = true;
                }
            }
        }
        return changed;
    }

}
