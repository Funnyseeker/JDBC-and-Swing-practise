package application.config;

import application.ConnectionInfo;

import javax.swing.*;

public class ConfigEditor extends JFrame {
    private JTextField loginField;
    private JTextField passwordField;
    private JTextField driverField;
    private JTextField hostField;
    private JTextField portField;
    private JTextField SidField;
    private JButton okButton;

    public ConfigEditor(ConnectionInfo connectionInfo)
    {
        super("Edit config");
        setVisible(true);
    }
}
