package application.config;

import application.ConnectionInfo;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConfigManager {
    private static ConfigManager Instance = new ConfigManager();
    private static DocumentBuilderFactory dbf;

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
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream("src/main/resources/config.xml")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ConnectionInfo ReadXmlConfigFile() {
        ConnectionInfo connectionInfo;
        Document dom;
        String driver = "";
        String host = "";
        String port = "";
        String sid = "";
        String user = "";
        String password = "";
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse("src/main/resources/config.xml");

            Element doc = dom.getDocumentElement();
            NodeList nodeList =doc.getChildNodes();
            for(int i=0; i < nodeList.getLength(); i++) {
                Node nd = nodeList.item(i);
                if (nd.getNodeName() == "driver") {
                    driver = nd.getTextContent();
                    continue;
                }
                if (nd.getNodeName() == "host") {
                    host = nd.getTextContent();
                    continue;
                }
                if (nd.getNodeName() == "port") {
                    port = nd.getTextContent();
                    continue;
                }
                if (nd.getNodeName() == "sid") {
                    sid = nd.getTextContent();
                    continue;
                }
                if (nd.getNodeName() == "user") {
                    user = nd.getTextContent();
                    continue;
                }
                if (nd.getNodeName() == "password") {
                    password = nd.getTextContent();
                    continue;
                }
            }

            connectionInfo = new ConnectionInfo(driver, host, port, sid, user, password);
            return  connectionInfo;
        } catch (ParserConfigurationException pce) {
            JOptionPane.showMessageDialog(new JFrame(), pce.getMessage());
        } catch (SAXException se) {
            JOptionPane.showMessageDialog(new JFrame(), se.getMessage());
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(new JFrame(), ioe.getMessage());
        }

        return null;
    }
}
