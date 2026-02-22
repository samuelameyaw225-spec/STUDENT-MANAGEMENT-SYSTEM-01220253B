package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

public class DatabaseUtil {
    // The database will be stored in the "data" folder in your project root
    private static final String URL = "jdbc:sqlite:data/student_management.db";

    public static Connection getConnection() throws SQLException {
        try {
            // Force Java to load the SQLite driver
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found. Check your Maven dependencies!");
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        // Ensure the data directory exists
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }

        // SQL statement with all required constraints (Primary Key, NOT NULL, CHECK)
        String createTableSQL = "CREATE TABLE IF NOT EXISTS students (" +
                "student_id TEXT PRIMARY KEY, " +
                "full_name TEXT NOT NULL, " +
                "programme TEXT NOT NULL, " +
                "level INTEGER NOT NULL CHECK(level IN (100, 200, 300, 400)), " +
                "gpa REAL NOT NULL CHECK(gpa >= 0.0 AND gpa <= 5.0), " +
                "email TEXT, " +
                "phone_number TEXT, " +
                "date_added TEXT NOT NULL, " +
                "status TEXT NOT NULL CHECK(status IN ('Active', 'Inactive'))" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }
}