package dao;

import exception.InvalidLoginException;
import model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Handles all database work for student accounts.
public class StudentDAO {

    // Checks the email and password and returns the logged-in student.
    public Student login(String email, String password) throws InvalidLoginException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(
                    "SELECT student_id, name, email, password, security_question, security_answer "
                            + "FROM Student WHERE email = ? AND password = ?");
            statement.setString(1, email);
            statement.setString(2, password);
            result = statement.executeQuery();

            if (result.next()) {
                return makeStudent(result);
            }
            throw new InvalidLoginException("Wrong email or password.");
        } catch (SQLException e) {
            System.out.println("Database error while logging in: " + e.getMessage());
            return null;
        } finally {
            closeResult(result);
            closeStatement(statement);
        }
    }

    // Checks whether an email is already used by another account.
    public boolean emailExists(String email) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(
                    "SELECT student_id FROM Student WHERE email = ?");
            statement.setString(1, email);
            result = statement.executeQuery();
            return result.next();
        } catch (SQLException e) {
            System.out.println("Database error while checking the email: " + e.getMessage());
            return false;
        } finally {
            closeResult(result);
            closeStatement(statement);
        }
    }

    // Saves a new student account in the database.
    public boolean register(Student student) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(
                    "INSERT INTO Student (name, email, password, security_question, security_answer) "
                            + "VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, student.getName());
            statement.setString(2, student.getEmail());
            statement.setString(3, student.getPassword());
            statement.setString(4, student.getSecurityQuestion());
            statement.setString(5, student.getSecurityAnswer());
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Database error while registering: " + e.getMessage());
            return false;
        } finally {
            closeStatement(statement);
        }
    }

    // Finds a student by email for the forgot-password option.
    public Student findByEmail(String email) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(
                    "SELECT student_id, name, email, password, security_question, security_answer "
                            + "FROM Student WHERE email = ?");
            statement.setString(1, email);
            result = statement.executeQuery();

            if (result.next()) {
                return makeStudent(result);
            }
            return null;
        } catch (SQLException e) {
            System.out.println("Database error while finding the account: " + e.getMessage());
            return null;
        } finally {
            closeResult(result);
            closeStatement(statement);
        }
    }

    // Changes the password of the account with this email.
    public boolean updatePassword(String email, String newPassword) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(
                    "UPDATE Student SET password = ? WHERE email = ?");
            statement.setString(1, newPassword);
            statement.setString(2, email);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Database error while changing the password: " + e.getMessage());
            return false;
        } finally {
            closeStatement(statement);
        }
    }

    // Builds a Student object from the current database row.
    private Student makeStudent(ResultSet result) throws SQLException {
        return new Student(
                result.getInt("student_id"),
                result.getString("name"),
                result.getString("email"),
                result.getString("password"),
                result.getString("security_question"),
                result.getString("security_answer"));
    }

    // Closes a result set safely.
    private void closeResult(ResultSet result) {
        if (result != null) {
            try {
                result.close();
            } catch (SQLException e) {
                System.out.println("Could not close the result set.");
            }
        }
    }

    // Closes a prepared statement safely.
    private void closeStatement(PreparedStatement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("Could not close the statement.");
            }
        }
    }
}
