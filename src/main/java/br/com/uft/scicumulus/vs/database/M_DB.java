package br.com.uft.scicumulus.vs.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class M_DB {

    private Connection conn = null;
    public static String DRIVER_POSTGRESQL = "org.postgresql.Driver";
    public static String DRIVER_SQLSERVER = "net.sourceforge.jtds.jdbc.Driver";
    public static String DRIVER_MYSQL = "com.mysql.jdbc.Driver";

    public M_DB(String classname, String url, String username, String password, boolean loadDriver) {
        if (loadDriver) {
            try {
                Class.forName(classname);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password);
        try {
            this.conn = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void finalize() throws Throwable {
        if (this.conn != null) {
            this.conn.close();
        }
        super.finalize();
    }

    public Connection getConn() {
        return this.conn;
    }

    public M_Query openQuery(String strQuery) throws SQLException {
        if (this.conn != null) {
            return M_Query.openQuery(this.conn, strQuery);
        }
        return null;
    }

    public M_Query prepQuery(String strQuery) throws SQLException {
        if (this.conn != null) {
            return M_Query.prepareQuery(this.conn, strQuery);
        }
        return null;
    }
}