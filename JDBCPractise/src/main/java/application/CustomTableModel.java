package application;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class CustomTableModel extends AbstractTableModel {
    DataManager dm;
    CustomData customData;

    public CustomTableModel(CustomData customData) {
        this.customData = customData;
    }

    public int getColumnCount() {
        return customData.getCustomMetaData().getColumnCount();
    }

    public int getRowCount() {
        return customData.getData().length;
    }

    public String getColumnName(int col) {
        return customData.getCustomMetaData().getColumnName(col);
    }

    public Object getValueAt(int row, int col) {
        return customData.getData()[row][col];
    }

    public Class getColumnClass(int c) {
        Object value = null;
        for (int i = 0; i < customData.getData().length; i++) {
            value = getValueAt(i, c);
            if (value != null)
                break;
        }
        if (value == null) {
            try {
                return Class.forName(customData.getCustomMetaData().getColumnClassName(c));
            } catch (Exception e) {
                e.printStackTrace();
                return "".getClass();
            }
        }
        return value.getClass();
    }

    public boolean isCellEditable(int row, int col) {
        if (col == 0 || col == getColumnCount() - 1) {
            return false;
        } else {
            return true;
        }
    }

    public void setValueAt(Object value, int row, int col) {
        customData.getData()[row][col] = value;
        fireTableCellUpdated(row, col);
        if (customData.getCustomMetaData().getColumnTypeName(col + 1) == "CHAR") {
            if ((Boolean) value == false)
                value = "0";
            else
                value = "1";
        }
        dm.updateDate(customData.getData()[row][0], customData.getCustomMetaData().getTableName(),
                getColumnName(col), value);
    }

    public void removeRow(int row) {
        dm.deleteRow(customData.getCustomMetaData().getTableName(), customData.getData()[row][0]);
        Object[][] newData = new Object[customData.getData().length - 1][getColumnCount()];
        for (int i = 0, k = 0; i < customData.getData().length; i++) {
            if (i == row) {
                continue;
            }
            System.arraycopy(customData.getData()[i], 0, newData[k], 0, getColumnCount());
            k++;
        }
        customData.setData(newData);
        fireTableRowsDeleted(row, row);
    }

    public void addRow() {
        //todo: Ты или переносишь скобку на новубю строку во всем проекте, или нет. Комбинирование этих двух способов единовременно - кощунство
        Object[] values = new Object[getColumnCount() - 2];
        for (int i = 1; i < getColumnCount() - 1; i++) {
            if (getColumnClass(i) == String.class) {
                values[i - 1] = " ";
                continue;
            }
            if (getColumnClass(i) == Integer.class) {
                values[i - 1] = 0;
                continue;
            }
            if (getColumnClass(i) == Boolean.class) {
                values[i - 1] = Boolean.FALSE;
                continue;
            }
        }
        String[] colNames = new String[getColumnCount() - 2];
        System.arraycopy(customData.getCustomMetaData().getColumnNames(), 1, colNames, 0, getColumnCount() - 2);

        Object[] newValues = new Object[getColumnCount()];
        //todo: при вставке данных в БД, если возникло исключение, ты возвращаешь ноль, который должен сигнализировать о неудаче
        //todo: вместо этого ты записвыаешь его, как обычное значение
        newValues[0] = dm.Insert(customData.getCustomMetaData().getTableName(), colNames, values);
        System.arraycopy(values, 0, newValues, 1, getColumnCount() - 2);
        newValues[getColumnCount() - 1] = new ImageIcon(getClass().getClassLoader().getResource("plus.png"));
        Object[][] newData = new Object[customData.getData().length + 1][getColumnCount()];
        //todo: Значение переменных i & k у тебя всегда одинаковы. Инкременатция в заголовке цикла выполняется после выполнения тела цикла
        for (int i = 0; i < getColumnCount(); i++) {
            System.arraycopy(customData.getData()[i], 0, newData[i], 0, getColumnCount());
        }
        System.arraycopy(newValues, 0, newData[customData.getData().length], 0, getColumnCount());
        customData.setData(newData);
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    public void addColumn() {

    }
}
