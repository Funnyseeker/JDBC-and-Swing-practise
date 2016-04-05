package application.model;

import application.data.CustomData;
import application.data.DataManager;
import com.sun.org.apache.xml.internal.security.signature.ReferenceNotInitializedException;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class TableBuilder extends JDialog {
    private JTextField tabNameField;
    private JButton okButton;

    public static JPanel createTable(CustomData customData) {
        JPanel panel = new JPanel(new BorderLayout());
        final JTable table = new JTable(new CustomTableModel(customData)) {
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
                    ((CustomTableModel)table.getModel()).addColumn();
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
                ((CustomTableModel) table.getModel()).addRow();
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
                        ((CustomTableModel) table.getModel()).removeRow(table.getSelectedRow());
                    } else {
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

    public void editTable(final JTabbedPane pane) {
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        final int i = pane.getSelectedIndex();
        final String tabName = pane.getTitleAt(i);
        tabNameField = new JTextField(tabName, tabName.length());
        okButton = new JButton();
        okButton.setText("Ok");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!tabNameField.getText().isEmpty()) {
                    if (tabName.compareTo(tabNameField.getText()) != 0) {
                        try {
                            DataManager.getInstance().RenameTable(tabName, tabNameField.getText());
                            pane.setTitleAt(i, tabNameField.getText());
                        } catch (ReferenceNotInitializedException e1) {
                            e1.printStackTrace();
                        }
                    }
                    setVisible(false);
                }
            }
        });

        Component comp = pane.getComponentAt(i);
        comp = ((JPanel)comp).getComponent(0);
        comp = ((JScrollPane)comp).getComponent(0);
        comp = ((JViewport)comp).getComponent(0);
        CustomTableModel customTableModel = (CustomTableModel)((JTable)comp).getModel();

        Object[][] data = new Object[customTableModel.getColumnCount()-1][2];
        for(int j = 0; j < customTableModel.getColumnCount()-1; j++) {
            data[j][0] = customTableModel.customData.getCustomMetaData().getColumnName(j);
            data[j][1] = customTableModel.customData.getCustomMetaData().getColumnClassName(j);
        }
        String[]  columnNames = {"Column Name", "Column Class"};
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        JPanel panel = new JPanel(new GridLayout());
        panel.add(tabNameField);
        panel.add(okButton);
        getContentPane().setLayout(new GridLayout());
        getContentPane().add(scrollPane);
        getContentPane().add(panel);
        pack();
    }
}
