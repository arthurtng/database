package database;

import java.util.ArrayList;

public class DropCMD extends DBcmd {
    private String structure;


    public DropCMD(){
        commandType = "DropCMD";
        tableNames = new ArrayList<>();
    }

    @Override
    protected String query(DBServer server) {

        if (structure.equals("DATABASE")){
            if (!server.removeDB(DBname)){
                return "Failure dropping database.";
            }
            isSuccessful = true;
            return DBname + " removed.";
        }
        else {
            if (server.getDB()==null){
                return "No database selected.";
            }
            if (!server.getDB().deleteTable(tableNames.get(0))){
                return "Error deleting table.";
            }
            isSuccessful = true;
            return this.getTable() + " removed.";
        }
    }

    public void setTable(String table){
        tableNames.add(0, table);
    }

    public String getTable(){
        return tableNames.get(0);
    }

    public void setStructure(String s){
        structure = s;
    }

}
