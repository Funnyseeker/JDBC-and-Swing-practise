package Application;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class TableBuilder extends JFrame {

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
        panel.add(table);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);

        //popup menu
        JPopupMenu pmenu = new JPopupMenu();
        JMenuItem addItem = new JMenuItem("add row");
        addItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createRow(table);
            }
        });
        JMenuItem delItem = new JMenuItem("del row");
        delItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((CusTableModel)table.getModel()).removeRow(table.getSelectedRow());
            }
        });
        delItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"Yes", "No"};
                if (JOptionPane.showOptionDialog(getWindows()[0], "Do you want to delete selected row?",
                        "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                        options, options[0]) == JOptionPane.YES_OPTION) {
                    //table.removeRowSelectionInterval(0,1);
                }
            }
        });
        pmenu.add(addItem);
        pmenu.add(delItem);

        table.setPreferredScrollableViewportSize(table.getPreferredSize());

        table.setComponentPopupMenu(pmenu);
        return panel;
    }

    public static void createRow(JTable table)
    {
    }
}
