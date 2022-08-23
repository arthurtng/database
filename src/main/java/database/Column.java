package database;

public class Column {
    private String columnHeading;
    private int columnIndex;

    public Column(String heading, int index){
        columnHeading = heading;
        columnIndex = index;
    }

    public String getColumnHeading(){
        return columnHeading;
    }

    public int getColumnIndex(){
        return columnIndex;
    }

    public void setColumnIndex(int idx){
        columnIndex = idx;
    }


}
