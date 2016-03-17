package Application;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CusTabbedPane extends JTabbedPane {
    public CusTabbedPane(List<ResultSet> tabbesList) {
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
                        JPanel panel = TableBuilder.createTable();
                        sourceTabbedPane.setSelectedIndex(0);
                        sourceTabbedPane.insertTab("new", new ImageIcon(),panel, "", index);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
