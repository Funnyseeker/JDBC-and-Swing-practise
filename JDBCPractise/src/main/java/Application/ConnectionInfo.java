package Application;


public class ConnectionInfo {
    //todo: лучше избегать такой линейной инициализации
    private String Driver, Host, Port, Sid, Username, Password;

    public ConnectionInfo() {
    }

    //todo: самое место для this при правильном наимонавнии полей класса
    public ConnectionInfo(String driver, String host, String port, String sid, String username, String password) {
        //todo: префикс "jdbc:" не зависит от типа базы данных и никогда не меняется --> в константу
        Driver = driver;
        Host = host;
        Port = port;
        Sid = sid;
        Username = username;
        Password = password;
    }

    public String getUrl() {
        return Driver + ":" + Host + ":" + Port + ":" + Sid;
    }

    public String getHost(){
        return  Host;
    }

    public String getPort(){
        return Port;
    }

    public String getSid(){
        return Sid;
    }

    public String getDriver() {
        return Driver;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }
}
