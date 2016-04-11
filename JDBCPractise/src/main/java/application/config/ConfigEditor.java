package application.config;

import application.ConnectionInfo;
import application.MainFrame;
import application.data.DataManager;
import com.sun.org.apache.xml.internal.security.signature.ReferenceNotInitializedException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConfigEditor extends JFrame {
    private JTextField loginField;
    private JTextField passwordField;
    private JTextField driverField;
    private JTextField hostField;
    private JTextField portField;
    private JTextField sidField;
    private JLabel loginFieldL;
    private JLabel passwordFieldL;
    private JLabel driverFieldL;
    private JLabel hostFieldL;
    private JLabel portFieldL;
    private JLabel sidFieldL;
    private JButton okButton;
    private JFrame pMainFrame;
    private ConnectionInfo rezultConnectionInfo;

    public ConfigEditor() {
        super("Edit config");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(e.getWindow(), "Do you want to close window?",
                        "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        loginField = new JTextField();
        passwordField = new JTextField();
        driverField = new JTextField();
        hostField = new JTextField();
        portField = new JTextField();
        sidField = new JTextField();
        loginFieldL = new JLabel("Loin");
        passwordFieldL = new JLabel("Password");
        driverFieldL = new JLabel("Driver");
        hostFieldL = new JLabel("Host");
        portFieldL = new JLabel("Port");
        sidFieldL = new JLabel("Sid");
        okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ConnectionInfo connectionInfo = new ConnectionInfo(driverField.getText(), hostField.getText(),
                        portField.getText(), sidField.getText(), loginField.getText(), passwordField.getText());
                if (rezultConnectionInfo.compareTo(connectionInfo) != 0) {
                    rezultConnectionInfo.setUrl(driverField.getText(), hostField.getText(),
                            portField.getText(), sidField.getText(), loginField.getText(), passwordField.getText());
                    try {
                        DataManager.getInstance(rezultConnectionInfo);
                        JButton button = ((MainFrame) pMainFrame).getRefreshButton();
                        //Ивент не срабатывает. Пока хз что не так(
                        button.dispatchEvent(new ActionEvent(button, ActionEvent.ACTION_PERFORMED, null));
                        dispose();
                    } catch (ReferenceNotInitializedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(loginFieldL)
                                        .addComponent(loginField))
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(passwordFieldL)
                                        .addComponent(passwordField))
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(driverFieldL)
                                        .addComponent(driverField))
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(hostFieldL)
                                        .addComponent(hostField))
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(portFieldL)
                                        .addComponent(portField))
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(sidFieldL)
                                        .addComponent(sidField))
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(okButton)))
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(loginFieldL)
                                .addComponent(loginField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(passwordFieldL)
                                .addComponent(passwordField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(driverFieldL)
                                .addComponent(driverField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(hostFieldL)
                                .addComponent(hostField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(portFieldL)
                                .addComponent(portField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(sidFieldL)
                                .addComponent(sidField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(okButton))

        );
        add(panel);
    }

    public void Start(ConnectionInfo connectionInfo, JFrame pMainFrame) {
        rezultConnectionInfo = connectionInfo;
        this.pMainFrame = pMainFrame;

        if (connectionInfo != null) {
            loginField.setText(connectionInfo.getUsername());
            passwordField.setText(connectionInfo.getPassword());
            driverField.setText(connectionInfo.getDriver());
            hostField.setText(connectionInfo.getHost());
            portField.setText(connectionInfo.getPort());
            sidField.setText(connectionInfo.getSid());
        } else {
            connectionInfo = new ConnectionInfo();
        }
        setBounds(100, 100, 200, 400);
        setVisible(true);
    }
}
