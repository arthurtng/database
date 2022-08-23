package database;

import java.util.ArrayList;
import java.util.List;

public class SelectCMD extends DBcmd {
    private List<String> attributeList;
    private String tableName;
    boolean selectAllColumns;
    private Table targetTable;

    public SelectCMD(){
        attributeList = new ArrayList<>();
        conditions = new ArrayList<>();
        selectAllColumns = false;
    }

    public void setSelectAllColumns(){
        selectAllColumns = true;
    }

    public void addAttribute(String attribute){
        attributeList.add(attribute);
    }

    public void setTable(String table){
        tableName = table;
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
        Table result = new Table("result");
        ArrayList<Integer> columnNums = new ArrayList<>();
        result.removeColumnHeading("id");
        if (selectAllColumns) {
            int col = 0;
            for (String column : targetTable.getColumnHeadings()) {
                result.addColumnHeading(column);
                columnNums.add(col);
                col++;
            }
        }
        else {
            for (String column: targetTable.getColumnHeadings()){
                if (attributeList.contains(column)){
                    result.addColumnHeading(column);
                    columnNums.add(targetTable.getColumnIndex(column));
                }
            }
        }
        return processConditions(result, columnNums);
    }

    private String processConditions(Table result, ArrayList<Integer> columnNums){
        ArrayList<Row> rows;
        if (conditions.size()==0){
            for (Row r : targetTable.getRows()){
                Row newRow = buildRow(r, columnNums);
                result.insertRow(newRow.toString(), false);
            }
            isSuccessful = true;
            return result.toString();
        }
        if (conditions.size()==1){
            rows = interpret(targetTable, conditions.get(0), columnNums);
        }
        else {
            rows = evaluateConditions(targetTable, columnNums);
        }
        if (rows == null){
            return "Could not evaluate expression";
        }
        for (Row r : rows){
            Row newRow = buildRow(r, columnNums);
            result.insertRow(newRow.toString(), false);
        }
        isSuccessful = true;
        return result.toString();
    }


    private Row buildRow(Row r, ArrayList<Integer> columnNums){
        ArrayList<String> resultingRow = new ArrayList<>();
        for (int i = 0; i<r.getListOfValues().size(); i++){
            if (columnNums.contains(i)){
                resultingRow.add(r.getListOfValues().get(i));
            }
        }
        Row newRow = new Row();
        newRow.fillRow(resultingRow);
        return newRow;
    }


}
