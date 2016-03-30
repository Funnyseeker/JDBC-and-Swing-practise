package Application;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;

public class ConfigManager {
    private static ConfigManager Instance = new ConfigManager();
    private DocumentBuilderFactory dbf;

    private ConfigManager() {
        dbf = DocumentBuilderFactory.newInstance();
    }

    public static ConfigManager getInstance() {
        return Instance;
    }

    public void WriteXmlConfigFile(ConnectionInfo connectionInfo) throws ParserConfigurationException {
        DocumentBuilder builder = dbf.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();
        Document doc = impl.createDocument(null, null, null);
        Element conn = doc.createElement("connection");
        Element driver = doc.createElement("driver");
        driver.appendChild(doc.createTextNode(connectionInfo.getDriver()));
        conn.appendChild(driver);
        Element host = doc.createElement("host");
        host.appendChild(doc.createTextNode(connectionInfo.getHost()));
        conn.appendChild(host);
        Element port = doc.createElement("port");
        port.appendChild(doc.createTextNode(connectionInfo.getPort()));
        conn.appendChild(port);
        Element sid = doc.createElement("sid");
        sid.appendChild(doc.createTextNode(connectionInfo.getSid()));
        conn.appendChild(sid);
        Element user = doc.createElement("user");
        user.appendChild(doc.createTextNode(connectionInfo.getUsername()));
        conn.appendChild(user);
        Element pass = doc.createElement("password");
        pass.appendChild(doc.createTextNode(connectionInfo.getPassword()));
        conn.appendChild(pass);
        doc.appendChild(conn);
        try {
            Transformer t=TransformerFactory.newInstance().newTransformer();
            t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream("config.xml")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ConnectionInfo ReadXmlConfigFile() {
        ConnectionInfo connectionInfo = new ConnectionInfo();
        //todo: Где тело? :)


        return connectionInfo;
    }
}
