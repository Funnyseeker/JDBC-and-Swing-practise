package Application;

import javax.swing.*;

public class Editor extends JFrame {
    private JTextField loginField;
    private JTextField passwordField;
    private JTextField driverField;
    private JTextField hostField;
    private JTextField portField;
    private JTextField SidField;
    private JButton okButton;

    public Editor(ConnectionInfo connectionInfo)
    {
        super("Edit config");
        setVisible(true);
    }
}
