package Application;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class CusTableModel extends AbstractTableModel {
    DataManager dm;
    ResultSetMetaData metaData;
    private String tabName;
    private String[] columns;
    private Object[][] data;

    public CusTableModel(ResultSet dataSet, String tabName) {
        try {
            dm = DataManager.getInstance();
            metaData = dataSet.getMetaData();
            this.tabName = tabName;
            columns = new String[metaData.getColumnCount() + 1];
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columns[i - 1] = metaData.getColumnName(i);
            }
            columns[columns.length - 1] = "New column";
            dataSet.last();
            int totalRows = dataSet.getRow();
            dataSet.absolute(1);
            data = new Object[totalRows][columns.length];
            for (int i = 0; i < totalRows; i++) {
                for (int j = 0; j < columns.length - 1; j++) {
                    data[i][j] = dataSet.getObject(j + 1);
                    if (metaData.getColumnTypeName(j+1) == "CHAR") {
                        if(data[i][j] != null) {
                            int rez = Integer.parseInt(dataSet.getObject(j + 1).toString());
                            if (rez == 1)
                                data[i][j] = Boolean.TRUE;
                            else
                                data[i][j] = Boolean.FALSE;
                        }
                    }
                }
                data[i][columns.length - 1] = new ImageIcon(getClass().getClassLoader().getResource("plus.png"));
                dataSet.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getColumnCount() {
        return columns.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columns[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public Class getColumnClass(int c) {
        Object value = null;
        for (int i = 0; i < data.length; i++) {
            value = getValueAt(i, c);
            if(value!=null)
                break;
        }
        if (value == null) {
            try {
                return Class.forName(metaData.getColumnClassName(c+1));
            } catch (Exception e) {
                e.printStackTrace();
                return "".getClass();
            }
        }
        return value.getClass();
    }

    public boolean isCellEditable(int row, int col) {
        if (col == 0 || col == columns.length-1) {
            return false;
        } else {
            return true;
        }
    }

    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
        try {
            if (metaData.getColumnTypeName(col+1) == "CHAR")
            {
                if ((Boolean)value == false)
                    value = "0";
                else
                    value = "1";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dm.updateDate(data[row][0], tabName, columns[col], value);
    }

    public void removeRow(int row) {
        dm.deleteRow(tabName, data[row][0]);
        Object[][] newData = new Object[data.length-1][columns.length];
        for(int i=0, k =0; i < data.length; i++)
        {
            if (i == row)
            {
                continue;
            }
            for (int j = 0; j < columns.length; j++)
            {
                System.arraycopy(data[i], 0, newData[k], 0, columns.length);
               //newData[k][j]=data[i][j];
            }
            k++;
        }
        data = newData;
        fireTableRowsDeleted(row, row);
    }
}
