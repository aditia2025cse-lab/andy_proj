package dao;

import exception.DuplicateSubjectException;
import model.Subject;
import model.SubjectProgress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// Handles all database work for subjects.
public class SubjectDAO {

    // Returns all subjects of one student in alphabetical order.
    public ArrayList<Subject> getSubjectsByStudent(int studentId) {
        ArrayList<Subject> subjects = new ArrayList<Subject>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(
                    "SELECT subject_id, student_id, subject_name "
                            + "FROM Subject WHERE student_id = ? ORDER BY subject_name");
            statement.setInt(1, studentId);
            result = statement.executeQuery();

            while (result.next()) {
                subjects.add(new Subject(
                        result.getInt("subject_id"),
                        result.getInt("student_id"),
                        result.getString("subject_name")));
            }
        } catch (SQLException e) {
            System.out.println("Database error while loading subjects: " + e.getMessage());
        } finally {
            closeResult(result);
            closeStatement(statement);
        }
        return subjects;
    }

    // Adds a new subject and returns it, or throws an exception if it already exists.
    public Subject addSubject(int studentId, String subjectName) throws DuplicateSubjectException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(
                    "SELECT subject_id FROM Subject WHERE student_id = ? AND subject_name = ?");
            statement.setInt(1, studentId);
            statement.setString(2, subjectName);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                result.close();
                throw new DuplicateSubjectException("You already have a subject named " + subjectName + ".");
            }
            result.close();
            closeStatement(statement);

            statement = connection.prepareStatement(
                    "INSERT INTO Subject (student_id, subject_name) VALUES (?, ?)");
            statement.setInt(1, studentId);
            statement.setString(2, subjectName);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Database error while adding the subject: " + e.getMessage());
            return null;
        } finally {
            closeStatement(statement);
        }

        return findSubject(studentId, subjectName);
    }

    // Removes a subject and its assignments in one transaction.
    public boolean removeSubject(int studentId, int subjectId) {
        Connection connection = null;
        PreparedStatement deleteAssignments = null;
        PreparedStatement deleteSubject = null;

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            deleteAssignments = connection.prepareStatement(
                    "DELETE FROM Assignment WHERE subject_id = ? AND student_id = ?");
            deleteAssignments.setInt(1, subjectId);
            deleteAssignments.setInt(2, studentId);
            deleteAssignments.executeUpdate();

            deleteSubject = connection.prepareStatement(
                    "DELETE FROM Subject WHERE subject_id = ? AND student_id = ?");
            deleteSubject.setInt(1, subjectId);
            deleteSubject.setInt(2, studentId);
            int rows = deleteSubject.executeUpdate();

            connection.commit();
            return rows == 1;
        } catch (SQLException e) {
            System.out.println("Database error while removing the subject: " + e.getMessage());
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    System.out.println("Could not roll back the subject removal.");
                }
            }
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    System.out.println("Could not turn auto-commit back on.");
                }
            }
            closeStatement(deleteAssignments);
            closeStatement(deleteSubject);
        }
    }

    // Returns total and completed assignment counts for each subject.
    public ArrayList<SubjectProgress> getSubjectProgress(int studentId) {
        ArrayList<SubjectProgress> progressList = new ArrayList<SubjectProgress>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(
                    "SELECT p.subject_id, p.subject_name, p.total_assignments, p.completed_assignments "
                            + "FROM subject_progress p "
                            + "JOIN Subject s ON p.subject_id = s.subject_id "
                            + "WHERE s.student_id = ? ORDER BY p.subject_name");
            statement.setInt(1, studentId);
            result = statement.executeQuery();

            while (result.next()) {
                progressList.add(new SubjectProgress(
                        result.getInt("subject_id"),
                        result.getString("subject_name"),
                        result.getInt("total_assignments"),
                        result.getInt("completed_assignments")));
            }
        } catch (SQLException e) {
            System.out.println("Database error while loading subject progress: " + e.getMessage());
        } finally {
            closeResult(result);
            closeStatement(statement);
        }
        return progressList;
    }

    // Finds one subject by its name.
    private Subject findSubject(int studentId, String subjectName) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(
                    "SELECT subject_id, student_id, subject_name "
                            + "FROM Subject WHERE student_id = ? AND subject_name = ?");
            statement.setInt(1, studentId);
            statement.setString(2, subjectName);
            result = statement.executeQuery();

            if (result.next()) {
                return new Subject(
                        result.getInt("subject_id"),
                        result.getInt("student_id"),
                        result.getString("subject_name"));
            }
        } catch (SQLException e) {
            System.out.println("Database error while finding the subject: " + e.getMessage());
        } finally {
            closeResult(result);
            closeStatement(statement);
        }
        return null;
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
