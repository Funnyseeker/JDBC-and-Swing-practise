package Application;

import com.sun.deploy.panel.JavaPanel;
import com.sun.org.apache.xml.internal.security.signature.ReferenceNotInitializedException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
//todo: Cus == Custom? Здесь и всегда --> Понятные названия - один из основных принципов и правил в программировании
public class CusTabbedPane extends JTabbedPane {
    //todo: ResultSet не только выходит из слоя DAO, но и залазит в слой модели :)
    public CusTabbedPane(final List<ResultSet> tabbesList) {
        try {
            //From ResultSets to tabbes
            //Note! Firs result set consider just user table`s names
            ResultSet user_tables = tabbesList.get(0);
            user_tables.beforeFirst();
            List<String> tabNames = new ArrayList<String>();
            while(user_tables.next()) {
                tabNames.add(user_tables.getString("TABLE_NAME"));
            }
            tabbesList.remove(user_tables);
            for(int i=0; i<tabbesList.size(); i++) {


                this.addTab(tabNames.get(i),
                        TableBuilder.createTable(tabbesList.get(i), tabNames.get(i)));
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
                        // prompt the user to enter their name
                        String name = JOptionPane.showInputDialog(new JFrame(), "Enter new table name?");
                        if (name == null)
                            return;

                        try {
                            sourceTabbedPane.insertTab(name, new ImageIcon(),
                                    TableBuilder.createTable(DataManager.getInstance().CreateTable(name), name), "", index);
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
}
