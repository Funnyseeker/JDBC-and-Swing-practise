package Application;


import com.sun.org.apache.xml.internal.security.signature.ReferenceNotInitializedException;
import oracle.jdbc.proxy.annotation.Pre;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DataManager {
    private List<Statement> openStatementsList = new ArrayList<Statement>();
    private static DataManager Instance = null;
    private Connection connection = null;
    private Statement statement;
    private String Username;
    private String Password;
    private String URL;

    private DataManager(ConnectionInfo connectionInfo) {
        this.Username = connectionInfo.getUsername();
        this.Password = connectionInfo.getPassword();
        this.URL = connectionInfo.getUrl();
    }

    public static DataManager getInstance() throws ReferenceNotInitializedException {
        if (Instance == null) throw new ReferenceNotInitializedException();
        return Instance;
    }

    public static DataManager initInstance(ConnectionInfo connectionInfo) {
        Instance = new DataManager(connectionInfo);
        return Instance;
    }

    /**
     * @return Connection to database.
     * If "null" - connection was created with error.
     */
    private Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {

                Class.forName("oracle.jdbc.driver.OracleDriver");
                connection = DriverManager.getConnection(URL, Username, Password);
                return connection;
            } else return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void closeConnection() {
        try {
            for (int i = 0; i < openStatementsList.size(); i++) {
                openStatementsList.get(i).close();
            }
            if (connection != null)
                connection.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public List<ResultSet> getDataSet() {
        List<ResultSet> dataSet = new ArrayList<ResultSet>();
        Statement statement = null;
        try {
            statement = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet user_tables = statement.executeQuery("select distinct TABLE_NAME from USER_TABLES order by TABLE_NAME asc");
            dataSet.add(user_tables);
            openStatementsList.add(statement);
            while (user_tables.next()) {
                Statement stmt = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                String tabname = user_tables.getString("TABLE_NAME");

                ResultSet tab = stmt.executeQuery("select * from " + tabname);
                dataSet.add(tab);
                openStatementsList.add(stmt);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return dataSet;
    }

    public void updateDate(Object ID, String tabName, String colName, Object Value) {
        try {
            PreparedStatement stmt = getConnection().prepareStatement("UPDATE " + tabName + " SET " + colName
                    + " = '" + Value.toString() + "' WHERE " + " ID = " + ID.toString());
            stmt.executeUpdate();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteRow(String tabName, Object ID) {
        Statement statement = null;
        try {
            Statement stmt = getConnection().createStatement();
            String req = "DELETE FROM " + tabName
                    + " WHERE " + " ID = " + ID.toString();
            stmt.executeUpdate(req);
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int Insert(String tabName, String[] columns, Object[] values) {
        try {
            String req = "insert into " + tabName + "(";
            String insColumns = "";
            Statement updstmt = getConnection().createStatement();
            for (String colName : columns) {
                insColumns += colName;
                if (colName != columns[columns.length - 1])
                    insColumns += ",";
            }
            req += insColumns + ") values (";
            String insvalues = "";
            for (int i = 0; i < values.length; i++) {
                if (values[i].getClass() == String.class) {
                    insvalues += "'" + values[i].toString() + "'";
                }
                if (values[i].getClass() == Integer.class) {
                    insvalues += values.toString();
                }
                if (values[i].getClass() == Boolean.class) {
                    if ((Boolean) values[i] == Boolean.FALSE)
                        insvalues += '0';
                    else
                        insvalues += '1';
                }
                if (i + 1 < values.length) {
                    insvalues += ",";
                }
            }
            req += insvalues + ")";
            updstmt.executeUpdate(req);
            PreparedStatement selstmt = getConnection().prepareStatement("select max(ID) from " + tabName
                    + " where " + insColumns.replace(",", "=? and ") + "=?");
            for (int i = 0; i < values.length; i++) {
                selstmt.setObject(i + 1, values[i]);
            }
            updstmt.close();
            ResultSet genId = selstmt.executeQuery();
            int Id = 0;
            if (genId.next()) {
                Id = Integer.parseInt(genId.getObject(1).toString());
            }
            selstmt.close();
            return Id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void RenameTable(String oldName, String newName) {
        Statement statement = null;
        try {
            Statement stmt = getConnection().createStatement();
            String req = "ALTER TABLE " + oldName + " RENAME TO " + newName;
            stmt.executeUpdate(req);
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void RenameColumn(String tabName, String oldName, String newName)
    {
        Statement statement = null;
        try {
            Statement stmt = getConnection().createStatement();
            String req = "RENAME COLUMN " + tabName+"."+oldName + " TO " + newName;
            stmt.executeUpdate(req);
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void EditTable(String tabName, String colName, Class type)
    {

    }

    public ResultSet CreateTable(String tabName)
    {
        try {
            Statement stmt = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String req = "Create table "+tabName+" (" +
                    "\"ID\" NUMBER(*,0) NOT NULL ENABLE, " +
                    "col1 VARCHAR2(20))";
            stmt.executeUpdate(req);
            req = " CREATE SEQUENCE  \""+getConnection().getMetaData().getUserName()+"\".\""+tabName+"_SEQ\"  MINVALUE 1 MAXVALUE 9999999999999999999999999999" +
                    " INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE";
            stmt.executeUpdate(req);
            req = "  CREATE OR REPLACE TRIGGER \""+getConnection().getMetaData().getUserName()
                    +"\".\""+tabName+"_TRG\" \n" +
                    "BEFORE INSERT ON " + tabName + "\n"+
                    "FOR EACH ROW \n" +
                    "BEGIN \n" +
                    "  <<COLUMN_SEQUENCES>> \n" +
                    "  BEGIN \n" +
                    "    IF INSERTING AND :NEW.ID IS NULL THEN \n" +
                    "      SELECT \""+getConnection().getMetaData().getUserName()+"\".\""+tabName+"_SEQ\".NEXTVAL INTO :NEW.ID FROM SYS.DUAL; \n" +
                    "    END IF; \n" +
                    "  END COLUMN_SEQUENCES; \n" +
                    "END; ";
            stmt.executeUpdate(req);
            req = "ALTER TRIGGER \""+getConnection().getMetaData().getUserName()+"\".\""+tabName+"_TRG\" ENABLE";
            stmt.executeUpdate(req);
            req = "insert into "+tabName+"(col1) values ('')";
            stmt.executeUpdate(req);
            req = "SELECT * FROM "+tabName;
            ResultSet newTab = stmt.executeQuery(req);
            openStatementsList.add(stmt);
            return newTab;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
