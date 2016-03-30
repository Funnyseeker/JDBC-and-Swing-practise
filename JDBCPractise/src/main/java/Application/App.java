package Application;

public class App {

    public static void main(String[] args){
        //todo: Свинг лушче выполнять в AWT потоке
        //todo: почитай про SwingUtilities.invokeLater()
        MainFrame frame = new MainFrame();
        frame.Run();
    }
}
