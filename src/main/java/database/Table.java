package database;

import java.io.*;
import java.util.*;

public class Table {

    private ArrayList<Column> columnHeadings;
    private ArrayList<Row> rows;
    private String name;
    private File DBDirectory;
    private int uniqueRowID;

public Table(String tableName){
        columnHeadings = new ArrayList<>();
        rows = new ArrayList<>();
        name = tableName;
        addColumnHeading("id");
        uniqueRowID = 1;
    }

    public void setDBDirectory(String path){
        if (path == null){
            return;
        }
        DBDirectory = new File(path);
    }

    public int getUniqueRowID(){
         return uniqueRowID;
    }

    public void setUniqueRowId(int id){
        uniqueRowID = id;
    }

    private boolean containsHeading(String heading){
        for (Column c : columnHeadings){
            if (c.getColumnHeading().equals(heading)){
                return true;
            }
        }
        return false;
    }

    public int getColumnIndex(String heading){
        for (Column c : columnHeadings){
            if (c.getColumnHeading().equals(heading)){
                return c.getColumnIndex();
            }
        }
        return -1;
    }

    public String getDBDirectory(){
        return DBDirectory.getPath();
    }

    public void insertRow(String rowContents, boolean needInsertID){
        String[] str = rowContents.split("\\s+");
        ArrayList<String> al = new ArrayList<>();
        if (needInsertID) {
            al.add(String.valueOf(uniqueRowID));
            uniqueRowID++;
        }
        Collections.addAll(al, str);
        if (al.size() == columnHeadings.size()){
            Row newRow = new Row();
            newRow.fillRow(al);
            if (!needInsertID && containsHeading("id")){
                newRow.setId(Integer.parseInt(al.get(0)));
                uniqueRowID = Integer.parseInt(al.get(0));
            }
            if (needInsertID){
                newRow.setId(uniqueRowID-1);
            }
            rows.add(newRow);
        }
    }

    public int getRowId(int rowNumber){
        return rows.get(rowNumber).getId();
    }

    public void removeRowById(int id){
        int rowIndex = -1;
        for (int i=0; i<rows.size(); i++){
            if (rows.get(i).getId()==id){
                rowIndex = i;
            }
        }
        if (rowIndex != -1){
            rows.remove(rowIndex);
        }
    }

    public void editRowById(int id, String column, String newValue){
        getRowById(id).setValueByIndex(getColumnIndex(column), newValue);
    }

    public ArrayList<Row> getRows(){
        return rows;
    }

    public void addColumnHeading(String columnHeading){
        Column c = new Column(columnHeading, columnHeadings.size());
        columnHeadings.add(c);
    }

    public boolean removeColumnHeading(String columnHeading){
        if (!containsHeading(columnHeading)){
            return false;
        }
        for (int i= getColumnIndex(columnHeading)+1; i < columnHeadings.size(); i++){
            columnHeadings.get(i).setColumnIndex(i-1);
        }
        columnHeadings.remove(getColumnIndex(columnHeading));
        return true;
    }

    public ArrayList<String> getColumnHeadings(){
        ArrayList<String> result = new ArrayList<>();
        for (Column c : columnHeadings){
            result.add(c.getColumnHeading());
        }
        return result;
    }

    public Row getRow(int idx){
        return rows.get(idx);
    }

    public Row getRowById(int id){
        for (Row r : rows){
            if (r.getId()==id){
                return r;
            }
        }
        return null;
    }

    public int getNumOfRows(){
        return rows.size();
    }

    public void readFile(File fileToRead) throws IOException {
        if (fileToRead.exists()) {
            FileReader reader = new FileReader(fileToRead);
            BufferedReader buffReader = new BufferedReader(reader);
            String line = buffReader.readLine();
            List<String> listOfStr = null;
            removeColumnHeading("id");
            if (line != null) {
                listOfStr = new ArrayList<>(Arrays.asList(line.split("\\s+")));

                for (String s : listOfStr) {
                    addColumnHeading(s);
                }
            }
            line = buffReader.readLine();
            while (line != null) {
                if (listOfStr != null) {
                    insertRow(line, !listOfStr.get(0).equalsIgnoreCase("id"));
                }
                line = buffReader.readLine();
            }
            buffReader.close();
        }
    }



    public void writeToFile(String extension) throws IOException {
        String fileName = name + extension;
        File fileToWrite = new File(DBDirectory, fileName);
        FileWriter writer = new FileWriter(fileToWrite, false);
        BufferedWriter buffWriter = new BufferedWriter(writer);
        for (String heading : getColumnHeadings()) {
            buffWriter.write(heading + "\t");
        }
        buffWriter.newLine();
        for (Row row : rows) {
            buffWriter.write(row.toString());
            buffWriter.newLine();
        }
        buffWriter.close();
    }

    public String toString(){
        StringBuilder output = new StringBuilder();
        ArrayList<String> keys = getColumnHeadings();
        for (String key : keys){
            output.append(key).append("\t");
        }
        output.append("\n");

        if (rows.size() == 0){
            return output.toString();
        }
        for (Row r : rows){
            output.append(r.toString()).append("\n");
        }
        return output.toString();
    }

    public ArrayList<Row> notEqual(ArrayList<Integer> columnNumbers, String column, String value){
        int colNumber = getColumnIndex(column);
        ArrayList<Row> resultingRows = new ArrayList<>();
        for (Row row : rows) {
            if (!row.getValueByIndex(colNumber).equals(value)) {
                Row result = new Row();
                for (int col : columnNumbers) {
                    result.addColNumber(col);
                }
                result.fillRow(row.getListOfValues());
                resultingRows.add(result);
            }
        }
        return resultingRows;
    }

    public ArrayList<Row> equal(ArrayList<Integer> columnNumbers, String column, String value){
        int colNumber = getColumnIndex(column);
        ArrayList<Row> resultingRows = new ArrayList<>();
        for (Row row : rows) {
            if (row.getValueByIndex(colNumber).equals(value)) {
                Row result = new Row();
                for (int col : columnNumbers) {
                    result.addColNumber(col);
                }
                result.fillRow(row.getListOfValues());
                resultingRows.add(result);
            }
        }
        return resultingRows;
    }

    public ArrayList<Row> greater(ArrayList<Integer> columnNumbers, String column, String value){
        int colNumber = getColumnIndex(column);
        ArrayList<Row> resultingRows = new ArrayList<>();
        for (Row row : rows) {
            if (row.getValueByIndex(colNumber).compareTo(value) > 0) {
                Row result = new Row();
                for (int col : columnNumbers) {
                    result.addColNumber(col);
                }
                result.fillRow(row.getListOfValues());
                resultingRows.add(result);
            }
        }
        return resultingRows;
    }

    public ArrayList<Row> less(ArrayList<Integer> columnNumbers, String column, String value){
        int colNumber = getColumnIndex(column);
        ArrayList<Row> resultingRows = new ArrayList<>();
        for (Row row : rows) {
            if (row.getValueByIndex(colNumber).compareTo(value) < 0) {
                Row result = new Row();
                for (int col : columnNumbers) {
                    result.addColNumber(col);
                }
                result.fillRow(row.getListOfValues());
                resultingRows.add(result);
            }
        }
        return resultingRows;
    }

    public ArrayList<Row> greaterEqual(ArrayList<Integer> columnNumbers, String column, String value){
        int colNumber = getColumnIndex(column);
        ArrayList<Row> resultingRows = new ArrayList<>();
        for (Row row : rows) {
            if (row.getValueByIndex(colNumber).compareTo(value) >= 0) {
                Row result = new Row();
                for (int col : columnNumbers) {
                    result.addColNumber(col);
                }
                result.fillRow(row.getListOfValues());
                resultingRows.add(result);
            }
        }
        return resultingRows;
    }

    public ArrayList<Row> lessEqual(ArrayList<Integer> columnNumbers, String column, String value){
        int colNumber = getColumnIndex(column);
        ArrayList<Row> resultingRows = new ArrayList<>();
        for (Row row : rows) {
            if (row.getValueByIndex(colNumber).compareTo(value) <= 0) {
                Row result = new Row();
                for (int col : columnNumbers) {
                    result.addColNumber(col);
                }
                result.fillRow(row.getListOfValues());
                resultingRows.add(result);
            }
        }
        return resultingRows;
    }

    public ArrayList<Row> like(ArrayList<Integer> columnNumbers, String column, String value){
        int colNumber = getColumnIndex(column);
        ArrayList<Row> resultingRows = new ArrayList<>();
        for (Row row : rows) {
            if (row.getValueByIndex(colNumber).contains(value)) {
                Row result = new Row();
                for (int col : columnNumbers) {
                    result.addColNumber(col);
                }
                result.fillRow(row.getListOfValues());
                resultingRows.add(result);
            }
        }
        return resultingRows;
    }
}


