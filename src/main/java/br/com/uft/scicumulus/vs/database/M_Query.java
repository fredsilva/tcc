package br.com.uft.scicumulus.vs.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

public class M_Query {

    private Statement st = null;
    private ResultSet rs = null;
    private Connection conn = null;

    M_Query(Connection conn) {
        this.conn = conn;
    }

    static M_Query openQuery(Connection conn, String strQuery) throws SQLException {
        M_Query qry = new M_Query(conn);
        qry.st = qry.conn.createStatement();
        qry.rs = qry.st.executeQuery(strQuery);
        return qry;
    }

    static M_Query prepareQuery(Connection conn, String strQuery) throws SQLException {
        M_Query qry = new M_Query(conn);
        qry.st = qry.conn.prepareStatement(strQuery);
        return qry;
    }

    public ResultSet openQuery() throws SQLException {
        closeQuery();
        if ((this.st instanceof PreparedStatement)) {
            this.rs = ((PreparedStatement) this.st).executeQuery();
        }
        return getRs();
    }

    public void executeUpdate() throws SQLException {
        closeQuery();
        if ((this.st instanceof PreparedStatement)) {
            ((PreparedStatement) this.st).executeUpdate();
        }
    }

    public void closeQuery() throws SQLException {
        if (!(this.st instanceof PreparedStatement)) {
            this.st = null;
        }
        this.rs = null;
    }

    public void closeStatement() throws SQLException {
        this.st = null;
    }

    public void setParDate(int i, Date value) throws SQLException {
        if (value == null) {
            getSt().setNull(i, 93);
        } else {
            Timestamp valueStamp = new Timestamp(value.getTime());
            getSt().setTimestamp(i, valueStamp);
        }
    }

    public void setParInt(int i, Integer value) throws SQLException {
        if (value == null) {
            getSt().setNull(i, 4);
        } else {
            getSt().setInt(i, value.intValue());
        }
    }

    public void setParDouble(int i, Double value) throws SQLException {
        if (value == null) {
            getSt().setNull(i, 8);
        } else {
            getSt().setDouble(i, value.doubleValue());
        }
    }

    public void setParString(int i, String value) throws SQLException {
        if (value == null) {
            getSt().setNull(i, 12);
        } else {
            getSt().setString(i, value);
        }
    }

    protected void finalize() throws Throwable {
        closeQuery();
        closeStatement();
        super.finalize();
    }

    public ResultSet getRs() {
        return this.rs;
    }

    public PreparedStatement getSt() {
        if ((this.st instanceof PreparedStatement)) {
            return (PreparedStatement) this.st;
        }
        return null;
    }
}