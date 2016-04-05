package application.data;


import application.ConnectionInfo;
import com.sun.org.apache.xml.internal.security.signature.ReferenceNotInitializedException;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class DataManager {
    private static DataManager Instance = null;
    private Connection connection = null;
    private String Username;
    private String Password;
    private String URL;

    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(new JFrame(), e.getMessage());
            e.printStackTrace();
        }
    }

    private DataManager(ConnectionInfo connectionInfo) {
        this.Username = connectionInfo.getUsername();
        this.Password = connectionInfo.getPassword();
        this.URL = connectionInfo.getUrl();
    }

    //todo: следующие два метода можно заменить одним:
    //todo:  if (instance == null){
    //todo:      instance = new DataManager(connectionInfo);
    //todo:  }
    //todo:  return instance;

    public static DataManager getInstance() throws ReferenceNotInitializedException {
        if (Instance == null) throw new ReferenceNotInitializedException();
        return Instance;
    }

    public static DataManager getInstance(ConnectionInfo connectionInfo) throws ReferenceNotInitializedException {
        Instance = new DataManager(connectionInfo);
        if (Instance == null) throw new ReferenceNotInitializedException();
        return Instance;
    }

    /**
     * @return Connection to database.
     * If "null" - connection was created with error.
     */
    private Connection getConnection() {
//        Конец условия звучит как "вернуть соеденение или вернуть соеденение". Для читабельности можно сделать примерно так:
//        if (connection != null && !connection.isClosed()){
//            return connection;
//        }
//        ... Инициализация и возврат соеденения

        try {
            if (connection == null || connection.isClosed()) {
                //todo: Драйвер достаточно загрузить лишь раз при запуске приложения
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
            if (connection != null)
                connection.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public List<CustomData> getDataSet() {
        List<CustomData> dataSet = new ArrayList<CustomData>();
        Statement statement = null;
        try {
            statement = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet user_tables = statement.executeQuery("select distinct TABLE_NAME from USER_TABLES order by TABLE_NAME asc");
            //openStatementsList.add(statement);
            while (user_tables.next()) {
                Statement stmt = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                String tableName = user_tables.getString("TABLE_NAME");

                ResultSet tab = stmt.executeQuery("select * from " + tableName);

                CustomData customData = parseResultSetToMetaData(tableName, tab);

                dataSet.add(customData);
                stmt.close();
            }
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return dataSet;
    }

    public void updateDate(Object ID, String tabName, String colName, Object Value) {
        try {
            Statement stmt = getConnection().createStatement();
            stmt.executeUpdate("UPDATE " + tabName + " SET " + colName
                    + " = '" + Value.toString() + "' WHERE " + " ID = " + ID.toString());
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
                if (i < values.length - 1) {
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
                //todo: Последовательность колонок у тебя сторого детерминировано? Если нет, то лучше брать данные по названию колон
                //Answer: Всего одна колонка в выборке max(id)!
                Id = Integer.parseInt(genId.getObject(1).toString());
            }
            selstmt.close();
            return Id;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(new JFrame(), e.getMessage());
        }

        return -1;
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

    public void RenameColumn(String tabName, String oldName, String newName) {
        Statement statement = null;
        try {
            Statement stmt = getConnection().createStatement();
            String req = "RENAME COLUMN " + tabName + "." + oldName + " TO " + newName;
            stmt.executeUpdate(req);
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void EditTable(String tabName, String colName, Class type) {

    }

    public CustomData CreateTable(String tableName) {
        try {
            Statement stmt = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String req = "Create table " + tableName + " (" +
                    "\"ID\" BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "col1 VARCHAR2(20))";
            stmt.executeUpdate(req);

            //todo: Это ты заново придумал механизм автоинкремента?)
            //todo: ID NUMBER(*,0) NOT NULL ENABLE --> id BIGINT AUTO_INCREMENT PRIMARY KEY
            //todo: и СУБД сама все за тебя сделает
            //answer: чертов oracle! он не кушает auto_increment:( только шаманизм с триггером и перечислением
            req = " CREATE SEQUENCE  \"" + getConnection().getMetaData().getUserName() + "\".\"" + tableName + "_SEQ\"  MINVALUE 1 MAXVALUE 9999999999999999999999999999" +
                    " INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE";
            stmt.executeUpdate(req);
            req = "  CREATE OR REPLACE TRIGGER \"" + getConnection().getMetaData().getUserName()
                    + "\".\"" + tableName + "_TRG\" \n" +
                    "BEFORE INSERT ON " + tableName + "\n" +
                    "FOR EACH ROW \n" +
                    "BEGIN \n" +
                    "  <<COLUMN_SEQUENCES>> \n" +
                    "  BEGIN \n" +
                    "    IF INSERTING AND :NEW.ID IS NULL THEN \n" +
                    "      SELECT \"" + getConnection().getMetaData().getUserName() + "\".\"" + tableName + "_SEQ\".NEXTVAL INTO :NEW.ID FROM SYS.DUAL; \n" +
                    "    END IF; \n" +
                    "  END COLUMN_SEQUENCES; \n" +
                    "END; ";
            stmt.executeUpdate(req);
            req = "ALTER TRIGGER \"" + getConnection().getMetaData().getUserName() + "\".\"" + tableName + "_TRG\" ENABLE";
            stmt.executeUpdate(req);
            req = "insert into " + tableName + "(col1) values ('')";
            stmt.executeUpdate(req);
            req = "SELECT * FROM " + tableName;
            ResultSet newTab = stmt.executeQuery(req);
            //openStatementsList.add(stmt);
            CustomData customData = parseResultSetToMetaData(tableName, newTab);
            return customData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private CustomData parseResultSetToMetaData(String tableName, ResultSet tab) {
        ResultSetMetaData metaData = null;
        try {
            metaData = tab.getMetaData();
            String[] columns = new String[metaData.getColumnCount() + 1];
            String[] columnsClasses = new String[metaData.getColumnCount() + 1];
            String[] columnTypeNames = new String[metaData.getColumnCount() + 1];
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columns[i - 1] = metaData.getColumnName(i);
                columnTypeNames[i - 1] = metaData.getColumnTypeName(i);
                if (columnTypeNames[i-1] == "CHAR")
                {
                    columnsClasses[i - 1] = Boolean.class.toString();
                    continue;
                }
                if (columnTypeNames[i-1] == "NUMBER")
                {
                    columnsClasses[i - 1] = Integer.class.toString();
                    continue;
                }
                columnsClasses[i - 1] = String.class.toString();
            }
            columns[columns.length - 1] = "Add new column";
            columnsClasses[columnsClasses.length - 1] = ImageIcon.class.toString();
            columnTypeNames[columnTypeNames.length - 1] = "ImageIcon";
            tab.last();
            int totalRows = tab.getRow();
            tab.absolute(1);
            Object[][] data = new Object[totalRows][columns.length];
            for (int i = 0; i < totalRows; i++) {
                for (int j = 0; j < columns.length - 1; j++) {
                    data[i][j] = tab.getObject(j + 1);
                    if (data[i][j] != null) {
                        if (metaData.getColumnTypeName(j + 1) == "CHAR") {
                            int rez = Integer.parseInt(tab.getObject(j + 1).toString());
                            if (rez == 1)
                                data[i][j] = Boolean.TRUE;
                            else
                                data[i][j] = Boolean.FALSE;
                            continue;
                        }
                        if (metaData.getColumnTypeName(j + 1) == "NUMBER") {
                            int rez = Integer.parseInt(tab.getObject(j + 1).toString());
                            if (rez == 1)
                                data[i][j] = rez;
                            else
                                data[i][j] = rez;
                            continue;
                        }
                    }
                }
                data[i][columns.length - 1] = new ImageIcon(getClass().getClassLoader().getResource("plus.png"));
                tab.next();
            }
            CustomMetaData customMetaData = new CustomMetaData(tableName, columnsClasses, columnTypeNames, columns);
            CustomData customData = new CustomData(data, customMetaData);

            return customData;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
