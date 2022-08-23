package database;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

public class DB {
    private Hashtable<String, Table> tableList;
    private String databaseName;
    private String directory;
    protected Table idsTable;

    public DB(String name){
        databaseName = name;
        tableList = new Hashtable<>();
    }

    public ArrayList<String> getTableList(){
        ArrayList<String> str = new ArrayList<>(tableList.keySet());
        return str;
    }

    public void makeIdsTable(){
        idsTable = new Table("ids");
        idsTable.addColumnHeading("tableName");
        idsTable.addColumnHeading("currentId");
        idsTable.setDBDirectory(directory);
        try {
            idsTable.writeToFile(".id");
        } catch (Exception e) {
            return;
        }
    }

    public void updateIdsTable(String tableName) {
        for (Row r : idsTable.getRows()){
            if (r.getValueByIndex(1).equals(tableName)){
                r.setValueByIndex(2, String.valueOf(getTable(tableName).getUniqueRowID()));
                try {
                    idsTable.writeToFile(".id");
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public void setDirectory(String path){
        directory = path;
    }

    public String getDirectory(){
        return directory;
    }

    public void addTable(String name){
        Table newTable = new Table(name);
        tableList.put(name, newTable);
        tableList.get(name).setDBDirectory(directory);
        if (idsTable != null) {
            idsTable.insertRow(name + "\t0", true);
            try {
                idsTable.writeToFile(".id");
            } catch (Exception e) {
                return;
            }
        }
    }

    public boolean deleteTable(String name){
        if (tableList.containsKey(name)){
            try {
                String filePath = tableList.get(name).getDBDirectory() + "/" + name + ".tab";
                File fileToBeDeleted = new File(filePath);
                System.out.println(fileToBeDeleted.getPath());
                if(!fileToBeDeleted.delete()){
                    return false;
                }
            }
            catch (Exception e){
                System.out.println("Error deleting file.");
                return false;
            }
            tableList.remove(name);
            return true;
        }
        System.out.println("table not found");
        return false;
    }

    public String getDatabaseName(){
        return databaseName;
    }

    public Table getTable(String name){
        return tableList.get(name);
    }

    public Table join(String tableOne, String tableOneColumn, String tableTwo, String tableTwoColumn){
        Table newTable = new Table("joined");
        newTable.removeColumnHeading("id");
        // Add attributes from Table One
        for (String heading : tableList.get(tableOne).getColumnHeadings()){
            newTable.addColumnHeading(heading);
        }
        // Add attributes from Table Two
        for (String heading : tableList.get(tableTwo).getColumnHeadings()){
            if (!heading.equals(tableTwoColumn) && !heading.equals("id")){
                newTable.addColumnHeading(heading);
            }
        }
        return buildNewTable(newTable, tableOne, tableOneColumn, tableTwo, tableTwoColumn);
    }

    private Table buildNewTable(Table newTable, String tableOne, String tableOneColumn, String tableTwo, String tableTwoColumn){
        for (int i=0; i < tableList.get(tableOne).getNumOfRows(); i++){
            Row r = new Row();
            r.fillRow(tableList.get(tableOne).getRow(i).getListOfValues());
            int idx1 = tableList.get(tableOne).getColumnIndex(tableOneColumn);
            int idx2 = tableList.get(tableTwo).getColumnIndex(tableTwoColumn);
            for (int j=0; j < tableList.get(tableTwo).getNumOfRows(); j++){
                if (r.getValueByIndex(idx1).equals(tableList.get(tableTwo).getRow(j).getValueByIndex(idx2))){
                    // Do not add id from TableTwo
                    for (int k = 1; k < tableList.get(tableTwo).getRow(j).getListOfValues().size(); k++){
                        // Do not duplicate joined column
                        if (k != idx2) {
                            r.addValue(tableList.get(tableTwo).getRow(j).getValueByIndex(k));
                        }
                    }
                }
            }
            StringBuilder sb = new StringBuilder();
            for (String item : r.getListOfValues()){
                sb.append(item).append("\t");
            }
            newTable.insertRow(sb.toString(), false);
        }
        return newTable;
    }
}
