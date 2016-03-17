package Application;

import com.sun.org.apache.xml.internal.security.signature.ReferenceNotInitializedException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.ResultSet;
import java.util.List;

public class MainFrame extends JFrame {
    private JMenuBar menuBar;
    private CusTabbedPane cusTabbedPane;
    private JButton refreshButton;

    public void Run(){
        InitializeComponents();
        this.setVisible(true);
    }

    private void InitializeComponents(){
        //Frame settings
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setBounds(100, 100, 800, 600);
        this.addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent e) {
            }

            public void windowClosing(WindowEvent e) {
                Object[] options = { "Yes", "No" };
                if (JOptionPane.showOptionDialog(e.getWindow(), "Do you want to close window?",
                        "Confirm", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]) == JOptionPane.YES_OPTION) {
                    try {
                        DataManager.getInstance().closeConnection();
                    } catch (ReferenceNotInitializedException e1) {
                        e1.printStackTrace();
                    }
                    System.exit(0);
                }
            }

            public void windowClosed(WindowEvent e) {
            }

            public void windowIconified(WindowEvent e) {

            }

            public void windowDeiconified(WindowEvent e) {

            }

            public void windowActivated(WindowEvent e) {

            }

            public void windowDeactivated(WindowEvent e) {

            }
        });

        //Menu settings
        menuBar = new JMenuBar();
        JMenu menuConn = new JMenu("File");
        JMenuItem editConn = new JMenuItem("Edit connection");
        menuConn.add(editConn);
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                WindowListener[] listeners = getWindowListeners();
                Window window = getWindows()[0];
                listeners[0].windowClosing(new WindowEvent(window, 1));
            }
        });
        menuConn.add(exit);
        menuBar.add(menuConn);
        this.setJMenuBar(menuBar);

        //Button settings
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    DataManager.getInstance().closeConnection();
                    cusTabbedPane.setVisible(false);
                    InitializeComponents();
                    cusTabbedPane.setVisible(true);
                } catch (ReferenceNotInitializedException e1) {
                    e1.printStackTrace();
                }
            }
        });
        this.add(refreshButton, BorderLayout.NORTH);
        //CusTabbedPane settings
        ConnectionInfo connectionInfo = new ConnectionInfo("jdbc:oracle:thin","localhost","1521", "orcl", "vsams", "vsams");
        ConfigManager xm = ConfigManager.getInstance();
        try {
            xm.WriteXmlConfigFile(connectionInfo);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        DataManager dm = DataManager.initInstance(connectionInfo);
        List<ResultSet> rezSet = dm.getDataSet();
        cusTabbedPane = new CusTabbedPane(rezSet);

        this.add(cusTabbedPane, BorderLayout.CENTER);
    }
}
