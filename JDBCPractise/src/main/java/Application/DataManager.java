package Application;


import com.sun.org.apache.xml.internal.security.signature.ReferenceNotInitializedException;

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
            getConnection().commit();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object Insert(String tabName, String[] columns, Object[] values) {
        try {
            String req = "insert into " +tabName +"(";
            Statement stmt = getConnection().createStatement();
            for(String colName : columns) {
                req+=colName;
                if (colName != columns[columns.length-1])
                    req+=",";
                else
                    req+=")";
            }
            req+= " values (";
            for(int i=0; i<values.length; i++)
            {
                if (values[i].getClass() == String.class) {
                    req += "'"+values[i].toString()+"'";
                }
                if (values[i].getClass() == Integer.class)
                {
                    req+=values.toString();
                }
                if (values[i].getClass() == Boolean.class) {
                    if((Boolean)values[i] == Boolean.FALSE)
                        req+='0';
                    else
                        req+='1';
                }
                if (i+1<values.length)
                {
                    req+=",";
                }
                else
                {
                    req+=")";
                }
            }
            stmt.executeUpdate(req, Statement.RETURN_GENERATED_KEYS);
            ResultSet keys = stmt.getGeneratedKeys();
            Object autoIncKeyFromApi = -1;
            if (keys.next())
            {
                autoIncKeyFromApi = keys.getObject(1);
            }
            getConnection().commit();
            stmt.close();
            return autoIncKeyFromApi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
