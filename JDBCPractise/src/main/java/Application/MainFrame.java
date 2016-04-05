package application;

import application.config.ConfigManager;
import application.data.CustomData;
import application.data.DataManager;
import application.model.CustomTabbedPane;
import com.sun.org.apache.xml.internal.security.signature.ReferenceNotInitializedException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class MainFrame extends JFrame {
    private JMenuBar menuBar;
    private CustomTabbedPane customTabbedPane;
    private JButton refreshButton;

    public void run() {
        InitializeComponents();
        this.setVisible(true);
    }

    private void InitializeComponents() {
        //Frame settings
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        setBounds(100, 100, 800, 600);
        setName("MainFrame");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(e.getWindow(), "Do you want to close window?",
                        "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    try {
                        DataManager.getInstance().closeConnection();
                    } catch (ReferenceNotInitializedException e1) {
                        e1.printStackTrace();
                    }
                    System.exit(0);
                }
            }
        });

        //Menu settings
        menuBar = new JMenuBar();
        JMenu menuConn = new JMenu("File");
        final JMenuItem editConn = new JMenuItem("Edit connection");
        menuConn.add(editConn);
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Window[] windows = getWindows();
                for (Window window : windows) {
                    if (window.getName().compareTo("MainFrame") == 0) {
                        dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
                        break;
                    }
                }
            }
        });
        menuConn.add(exit);
        menuBar.add(menuConn);
        setJMenuBar(menuBar);

        //Button settings
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    //todo: Connection - это бэкэнд, его можно не "рефрешить"
                    DataManager.getInstance().closeConnection();
                    //todo: Получилось так: скрываешь из виду, инициализируешь по второму и более разу один и тот же объект, сновка показываешь (возможно наслоение)
                    customTabbedPane.setVisible(false);
                    InitializeComponents();
                    customTabbedPane.setVisible(true);
                } catch (ReferenceNotInitializedException e1) {
                    e1.printStackTrace();
                }
            }
        });
        add(refreshButton, BorderLayout.NORTH);

        //CustomTabbedPane settings
        ConfigManager xm = ConfigManager.getInstance();
        ConnectionInfo connectionInfo;
        //xm.WriteXmlConfigFile(connectionInfo);
        connectionInfo = xm.ReadXmlConfigFile();
        if (connectionInfo == null) {
            JOptionPane.showMessageDialog(this, "не удалость прочесть конфиг файл");
        }
        DataManager dm = null;
        try {
            dm = DataManager.getInstance(connectionInfo);
        } catch (ReferenceNotInitializedException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            e.printStackTrace();
        }
        List<CustomData> rezSet = dm.getDataSet();
        customTabbedPane = new CustomTabbedPane(rezSet);

        add(customTabbedPane, BorderLayout.CENTER);
    }
}
