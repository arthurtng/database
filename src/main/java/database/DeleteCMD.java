package database;

import java.util.ArrayList;

public class DeleteCMD extends DBcmd{

    public DeleteCMD(){
        conditions = new ArrayList<>();
        tableNames = new ArrayList<>();
        commandType = "DeleteCMD";
    }

    public void setTableName(String tableName) {
        tableNames.add(tableName);
    }

    public void addCondition(Condition c){
        conditions.add(c);
    }

    public String query(DBServer server){
        if (server.getDB()==null){
            return "No database selected.";
        }
        Table targetTable = server.getDB().getTable(tableNames.get(0));
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
            ConditionStack cs = new ConditionStack();
            for (Condition i : conditions) {
                if (i.getType().equals("FULL")) {
                    i.setResult(interpret(targetTable, i, columnNums));
                }
            }
            rows = cs.evaluate(conditions);
        }
        if (rows == null){
            return "Could not evaluate expression";
        }
        boolean tableChanged = false;
        for (Row r : rows){
            for (int i=0; i<targetTable.getRows().size(); i++){
                String listString = String.join(" ", targetTable.getRow(i).toString());
                if (r.toString().equals(listString)){
                    int id = Integer.parseInt(r.getListOfValues().get(0));
                    targetTable.removeRowById(id);
                    tableChanged = true;
                }
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
        return "";
    }


}


