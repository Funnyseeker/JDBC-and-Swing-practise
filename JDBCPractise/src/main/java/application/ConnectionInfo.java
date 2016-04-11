package application;

public class ConnectionInfo implements Comparable<ConnectionInfo> {
    private String driver;
    private String host;
    private String port;
    private String sid;
    private String username;
    private String password;

    public ConnectionInfo() {
    }

    public ConnectionInfo(String driver, String host, String port, String sid, String username, String password) {
        this.driver = driver;
        this.host = host;
        this.port = port;
        this.sid = sid;
        this.username = username;
        this.password = password;
    }

    public void setUrl(String driver, String host, String port, String sid, String username, String password) {
        this.driver = driver;
        this.host = host;
        this.port = port;
        this.sid = sid;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return "jdbc:" + driver + ":" + host + ":" + port + ":" + sid;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getSid() {
        return sid;
    }

    public String getDriver() {
        return driver;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int compareTo(ConnectionInfo o) {
        if (this.getUrl().compareTo(o.getUrl()) != 0) {
            return -1;
        }
        if (this.getUsername().compareTo(o.getUsername()) != 0) {
            return -1;
        }
        if (this.getPassword().compareTo(o.getPassword()) != 0) {
            return -1;
        }
        return 0;
    }
}
