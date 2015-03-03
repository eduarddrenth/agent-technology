package com.logica.amc.base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Eduard Drenth: Logica, 21-mei-2010
 * 
 */
public class SQLiteDB {
    private static final Log log = LogFactory.getLog(SQLiteDB.class);

    private SQLiteDB() {}

    public static Connection getConnection(String url) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection(url);
    }

    public static String findUser(Connection con, String workflow) throws SQLException {
        try {
            con.setReadOnly(true);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select user from workflows where id='" + workflow + "';");
            if (rs.next()) {
                String s = rs.getString("user");
                if (rs.next()) {
                    log.error("found more then one user for workflow: " + workflow);
                    return null;
                } else {
                    return s;
                }
            }
            return null;
        } finally {
            con.close();
        }
    }
}
