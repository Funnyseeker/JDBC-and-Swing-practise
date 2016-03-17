package Application;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;

public class TableBuilder {

    public static JPanel createTable()
    {
        JPanel panel = new JPanel(new BorderLayout());
        return panel;
    }

    public static JPanel createTable(ResultSet dataset, String tableName)
    {
        JPanel panel = new JPanel(new BorderLayout());
        final JTable table = new JTable(new CusTableModel(dataset, tableName)) {
            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (getValueAt(row, column) == null) {
                    return new CustomCellRenderer();
                }
                return super.getCellRenderer(row, column);
            }
        };
        table.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == table.getColumnCount()) {
                    //((CusTableModel)table.getModel()).addColumn(createRow(table));
                }
            }
        });
        panel.add(table);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);

        //popup menu
        JPopupMenu pmenu = new JPopupMenu();
        JMenuItem addItem = new JMenuItem("add row");
        addItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((CusTableModel) table.getModel()).addRow();
            }
        });
        JMenuItem delItem = new JMenuItem("del row");
        delItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"Yes", "No"};
                if (JOptionPane.showOptionDialog(new JFrame().getWindows()[0], "Do you want to delete selected row?",
                        "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                        options, options[0]) == JOptionPane.YES_OPTION) {
                    if (table.getSelectedRow() != -1) {
                        ((CusTableModel) table.getModel()).removeRow(table.getSelectedRow());
                    }
                    else {
                        JOptionPane.showMessageDialog(new JFrame().getWindows()[0], "You should to select row to remove");
                    }
                }
            }
        });
        pmenu.add(addItem);
        pmenu.add(delItem);

        table.setPreferredScrollableViewportSize(table.getPreferredSize());

        table.setComponentPopupMenu(pmenu);
        return panel;
    }
}
