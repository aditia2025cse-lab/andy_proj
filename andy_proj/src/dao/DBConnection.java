package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Opens one shared SQLite connection and creates the database on first start.
public class DBConnection {

    private static final String DATABASE_URL = "jdbc:sqlite:student_tracker.db";
    private static Connection connection = null;

    // Returns the shared connection, creating the schema if the database is new.
    public static Connection getConnection() throws SQLException {
        if (connection == null) {
            boolean firstStart = !new File("student_tracker.db").exists();

            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC driver was not found.");
            }

            connection = DriverManager.getConnection(DATABASE_URL);
            enableForeignKeys(connection);

            if (firstStart) {
                createSchema(connection);
            }
        }
        return connection;
    }

    // Turns on foreign key checking for this SQLite connection.
    private static void enableForeignKeys(Connection database) throws SQLException {
        Statement statement = null;

        try {
            statement = database.createStatement();
            statement.execute("PRAGMA foreign_keys = ON");
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    System.out.println("Could not close the foreign key statement.");
                }
            }
        }
    }

    // Reads schema.sql and runs each SQL statement in it.
    private static void createSchema(Connection database) throws SQLException {
        BufferedReader reader = null;
        Statement statement = null;
        String sql = "";

        try {
            reader = new BufferedReader(new FileReader("schema.sql"));
            statement = database.createStatement();
            String line = reader.readLine();

            while (line != null) {
                line = line.trim();

                // Skip blank lines and SQL comments.
                if (line.length() > 0 && !line.startsWith("--")) {
                    sql = sql + line + " ";

                    if (line.endsWith(";")) {
                        String finishedSql = sql.trim();
                        finishedSql = finishedSql.substring(0, finishedSql.length() - 1);
                        statement.execute(finishedSql);
                        sql = "";
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new SQLException("Could not read schema.sql.");
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    System.out.println("Could not close the schema statement.");
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Could not close schema.sql.");
                }
            }
        }
    }

    // Closes the shared connection when the program ends.
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Could not close the database connection.");
            }
            connection = null;
        }
    }
}
