package application.model;

import application.data.CustomData;
import application.data.DataManager;
import com.sun.media.jfxmediaimpl.MediaDisposer;
import com.sun.org.apache.xml.internal.security.signature.ReferenceNotInitializedException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CustomTabbedPane extends JTabbedPane implements MediaDisposer.Disposable {
    public CustomTabbedPane(final List<CustomData> tabbesList) {
        try {
            for(int i=0; i<tabbesList.size(); i++) {
                this.addTab(tabbesList.get(i).getCustomMetaData().getTableName(),
                        TableBuilder.createTable(tabbesList.get(i)));
            }
            JPanel panel =  new JPanel();

            this.addTab("", new ImageIcon(getClass().getClassLoader().getResource("plus.png")), panel);
            this.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                    int index = sourceTabbedPane.getSelectedIndex();
                    if (index == getTabCount()-1)
                    {
                        sourceTabbedPane.setSelectedIndex(0);
                        ///final TableBuilder dialog = new TableBuilder();
                        //dialog.createTable();
                        //dialog.setVisible(true);
                        //JPanel panel = new JPanel(new FlowLayout());
                        String tableName = JOptionPane.showInputDialog(new JFrame(), "Enter new table name?");
                        if (tableName == null)
                            return;

                        try {
                            sourceTabbedPane.insertTab(tableName, new ImageIcon(),
                                    TableBuilder.createTable(DataManager.getInstance().CreateTable(tableName)),
                                    "", index);
                        } catch (ReferenceNotInitializedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
            final JTabbedPane pane = this;
            JPopupMenu pmenu = new JPopupMenu();
            JMenuItem editItem = new JMenuItem("edit table");
            editItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TableBuilder tableBuilder = new TableBuilder();
                    tableBuilder.editTable(pane);
                    tableBuilder.setVisible(true);
                }
            });
            JMenuItem delItem = new JMenuItem("del table");
            delItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            });
            pmenu.add(editItem);
            pmenu.add(delItem);
            this.setComponentPopupMenu(pmenu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
            removeAll();
    }
}
