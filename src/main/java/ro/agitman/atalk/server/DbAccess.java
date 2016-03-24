package ro.agitman.atalk.server;

import ro.agitman.atalk.model.TextMsg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by edi on 3/23/2016.
 */
public class DbAccess {

    private Connection c = null;
    private static DbAccess instance = new DbAccess();

    private DbAccess() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:d:\\test.db");
            System.out.println("Opened database successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void save(TextMsg msg) {
        Statement stmt = null;
        String sql = "insert into message (type, sender, text, date) values " +
                "('" + msg.getType() + "', '" + msg.getSender() + "', '" + msg.getText() + "', '" + msg.getDate() + "')";

        try {
            stmt = c.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DbAccess getInst() {
        return instance;
    }
}
