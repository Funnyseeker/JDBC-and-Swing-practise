package Application;

import com.sun.org.apache.xml.internal.security.signature.ReferenceNotInitializedException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.ResultSet;
import java.util.List;

public class MainFrame extends JFrame {
    private JMenuBar menuBar;
    private CusTabbedPane cusTabbedPane;
    private JButton refreshButton;
    //todo: см JavaNamingConvention
    public void Run(){
        InitializeComponents();
        this.setVisible(true);
    }

    private void InitializeComponents(){
        //Frame settings
        //todo: указатель this
        //к полям классы ты обращаешься без 'this', методы вызываешь используя 'this' - лучше что-то одно
        //обычно this используется, когда в скопе есть переменные с одинаковыми именами, стандартный пример - конструктор с аргументами
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setBounds(100, 100, 800, 600);
        //todo: Есть класс удобней - WindowAdapter - все раализовано по дефолту, оверрайдишь только нужный тебе метод
        this.addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent e) {
            }

            public void windowClosing(WindowEvent e) {
                //todo: Можно использовать стандартный showConfirmDialog - у него есть все что тебе требуется, но с меньшим количеством кода
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
                //todo: getWindows() - возвращает список всех окон в твоем приложении. Где гарантия, что первый в списке будет именно тот, который нужен?
                Window window = getWindows()[0];
                //todo: "new WindowEvent(window, 1)"  - единичка:
                //todo: Во-первых, magic-value (не понятно что за значение и откуда взялось);
                //todo: Во-вторых, работать правильно не будет. Посмотри на соответствубщую константу - WindowEvent.WINDOW_CLOSING
                listeners[0].windowClosing(new WindowEvent(window, 1));

                //todo: Что вообще тут происходит? Бросается Close event? Тогда можно сделать проще
                //todo: dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));

                //todo: А можно еще проще - метод dispose() -> оверрайдишь его, чистишь в нем ресурсы (connection), вызываешь без ивентов.
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
                    //todo: Connection - это бэкэнд, его можно не "рефрешить"
                    DataManager.getInstance().closeConnection();
                    //todo: Получилось так: скрываешь из виду, инициализируешь по второму и более разу один и тот же объект, сновка показываешь (возможно наслоение)
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
        //todo: Тут ты создаешь xml с пропертями, но где ты пытаешься прочитать уже существующий?
        ConnectionInfo connectionInfo = new ConnectionInfo("jdbc:oracle:thin","localhost","1521", "orcl", "vsams", "vsams");
        ConfigManager xm = ConfigManager.getInstance();
        try {
            xm.WriteXmlConfigFile(connectionInfo);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        DataManager dm = DataManager.initInstance(connectionInfo);
        //todo: ResultSet - внутренности слоя DAO - класса, который общается с БД (в твоем случае DataManager)
        //todo: Никто и никогда (кроме DataManager) не должен знать КАК DAO-класс общается с БД
        List<ResultSet> rezSet = dm.getDataSet();
        cusTabbedPane = new CusTabbedPane(rezSet);

        this.add(cusTabbedPane, BorderLayout.CENTER);
    }
}
