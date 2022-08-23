package database;

import java.util.ArrayList;

public class JoinCMD extends DBcmd{
    private String tableOne;
    private String tableTwo;

    public JoinCMD(){
        conditions = new ArrayList<>();
        colNames = new ArrayList<>();
        tableNames = new ArrayList<>();
        commandType = "JoinCMD";
    }

    public void setTableOne(String tableOne) {
        this.tableOne = tableOne;
    }

    public void setTableTwo(String tableTwo) {
        this.tableTwo = tableTwo;
    }

    public void setAttributeOne(String attributeOne) {
        colNames.add(attributeOne);
    }

    public void setAttributeTwo(String attributeTwo) {
        colNames.add(attributeTwo);
    }

    public String query(DBServer server){
        if (server.getDB()==null){
            return "No database selected.";
        }
        Table joinedTable = server.getDB().join(tableOne, colNames.get(0), tableTwo, colNames.get(1));
        isSuccessful = true;
        return joinedTable.toString();
    }
}
