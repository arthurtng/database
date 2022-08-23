package database;

public class AlterCMD extends DBcmd {
    private String tableName;
    private String alterationType;
    private String attributeName;

    public AlterCMD(){

    }

    public void setTable(String table){
        tableName = table;
    }

    public void setAlterationType(String type){
        alterationType = type;
    }

    public void setAttributeName(String name){
        attributeName = name;
    }

    public String query(DBServer server){
        if (server.getDB()==null){
            return "No database selected.";
        }
        if (attributeName.equals("id")){
            return "cannot alter id attribute";
        }
        Table toBeAltered = server.getDB().getTable(tableName);
        if(toBeAltered==null){
            return "could not retrieve table";
        }
        if (alterationType.equals("ADD")){
            if (!addAttribute(toBeAltered)){
                return "error adding column to table";
            }
            isSuccessful = true;
            return "attribute added.";
        }
        else {
            if (!dropAttribute(toBeAltered)){
                return "error removing attribute.";
            }
            isSuccessful = true;
            return "attribute dropped.";
        }
    }

    private boolean dropAttribute(Table toBeAltered){
        if(!toBeAltered.removeColumnHeading(attributeName)){
            return false;
        }
        try {
            toBeAltered.writeToFile(".tab");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean addAttribute(Table toBeAltered){
        try {
            toBeAltered.addColumnHeading(attributeName);
            for (Row r : toBeAltered.getRows()){
                r.addValue("NULL");
            }
            toBeAltered.writeToFile(".tab");
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

}
