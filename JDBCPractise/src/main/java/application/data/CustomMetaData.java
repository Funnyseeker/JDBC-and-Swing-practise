package application.data;

public class CustomMetaData {
    private String[] columnTypeNames;
    private String[] columnClasses;
    private String tableName;
    private String[] columnNames;
    private int columnCount;

    CustomMetaData(String tableName, String[] columnClasses, String[] columnTypeNames, String[] columnNames) {
        this.tableName = tableName;
        this.columnClasses = columnClasses;
        this.columnTypeNames = columnTypeNames;
        this.columnCount = columnNames.length;
        this.columnNames = columnNames;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName(int index) {
        return columnNames[index];
    }

    public String getColumnClassName(int index) {
        return columnClasses[index];
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public String getColumnTypeName(int index)
    {
        return columnTypeNames[index];
    }

    public String[] getColumnClasses()
    {
        return  columnClasses;
    }
}
