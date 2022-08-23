package database;

import java.util.ArrayList;

public class CreateCMD extends DBcmd{
    private String structure;

    public CreateCMD(){
        tableNames = new ArrayList<>();
        colNames = new ArrayList<>();
        commandType = "CreateCMD";
    }

    public void setTableName(String name){
        if (tableNames.size() == 0){
            tableNames.add(name);
        }
        tableNames.set(0, name);
    }

    public void addAttribute(String attribute){
        colNames.add(attribute);
    }

    public void setStructure(String s){
        structure = s;
    }

    public String query(DBServer server) {
        if (structure.equals("DATABASE")) {
            DB newDB = new DB(DBname);
            if(!server.addDB(newDB)){
                return "database already exists.";
            }
            isSuccessful = true;
            return DBname + " database created.";
        }
        else {
            if (!createTable(server)){
                return "Error creating table.";
            }
            isSuccessful = true;
            return tableNames.get(0) + " table created.";
        }
    }

    private boolean createTable(DBServer server){
        try {
            server.getDB().addTable(tableNames.get(0));
            for (String colName : colNames) {
                server.getDB().getTable(tableNames.get(0)).addColumnHeading(colName);
            }
            server.getDB().getTable(tableNames.get(0)).writeToFile(".tab");
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
