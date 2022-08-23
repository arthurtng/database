package database;

public class UseCMD extends DBcmd {
    private String databaseName;

    public UseCMD(){

    }

    public void setDatabaseName(String name){
        databaseName = name;
    }

    public String query(DBServer server){
        if (!server.setDatabase(databaseName)){
            return "Database does not exist.";
        }
        isSuccessful = true;
        return databaseName + " selected.";
    }
}
