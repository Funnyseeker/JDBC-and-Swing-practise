package application;

import application.config.ConfigEditor;
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
    private ConnectionInfo connectionInfo;

    public MainFrame() {
        super("Database viewer");
    }

    public void run() {
        initializeComponents();
        this.setVisible(true);
    }

    private void initializeComponents() {
        //Frame settings
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        setBounds(100, 100, 800, 600);
        setName("application.MainFrame");
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

        //Button settings
        refreshButton = new JButton("Refresh");
        add(refreshButton, BorderLayout.NORTH);
        ConfigManager xm = ConfigManager.getInstance();
        //xm.WriteXmlConfigFile(connectionInfo);
        connectionInfo = xm.ReadXmlConfigFile();
        if (connectionInfo == null) {
            JOptionPane.showMessageDialog(this, "не удалость прочесть конфиг файл");
            runConfigEditor();
        }

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    DataManager.getInstance().closeConnection();
                    customTabbedPane.dispose();
                    initTabbedPane(connectionInfo);
                } catch (ReferenceNotInitializedException e1) {
                    e1.printStackTrace();
                }
            }
        });

        //Menu settings
        menuBar = new JMenuBar();
        JMenu menuConn = new JMenu("File");
        JMenuItem editConn = new JMenuItem("Edit connection");
        editConn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runConfigEditor();
            }
        });
        menuConn.add(editConn);
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Window[] windows = getWindows();
                for (Window window : windows) {
                    if (window.getName().compareTo("application.MainFrame") == 0) {
                        dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
                        break;
                    }
                }
            }
        });
        menuConn.add(exit);
        menuBar.add(menuConn);
        setJMenuBar(menuBar);

        //CustomTabbedPane settings
        initTabbedPane(connectionInfo);
        add(customTabbedPane, BorderLayout.CENTER);
    }

    private void initTabbedPane(ConnectionInfo connectionInfo) {
        DataManager dm = null;
        try {
            dm = DataManager.getInstance(connectionInfo);
        } catch (ReferenceNotInitializedException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            e.printStackTrace();
        }
        customTabbedPane = new CustomTabbedPane(dm.getDataSet());
    }

    private void runConfigEditor()
    {
        ConfigEditor configEditor = new ConfigEditor();
        configEditor.Start(connectionInfo);
        customTabbedPane.dispose();
        initTabbedPane(connectionInfo);
    }
}
