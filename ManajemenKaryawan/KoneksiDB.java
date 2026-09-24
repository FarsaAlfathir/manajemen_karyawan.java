package manajemenkaryawan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class KoneksiDB {

    private static final String URL =
        "jdbc:mysql://localhost:3306/manajemen_karyawan";

    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean testConnection() {
        try (Connection con = getConnection()) {
            return con != null;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}