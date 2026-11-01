package dao;

import model.Assignment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// Handles all database work for assignments.
public class AssignmentDAO {

    // Returns all assignments of a student, nearest due date first.
    public ArrayList<Assignment> getAssignmentsByStudent(int studentId) {
        return selectAssignments(
                "SELECT a.assignment_id, a.student_id, a.subject_id, a.title, a.due_date, "
                        + "a.status, s.subject_name "
                        + "FROM Assignment a JOIN Subject s ON a.subject_id = s.subject_id "
                        + "WHERE a.student_id = ? ORDER BY a.due_date",
                studentId, null, 0);
    }

    // Returns all assignments of one subject.
    public ArrayList<Assignment> getAssignmentsBySubject(int studentId, int subjectId) {
        return selectAssignments(
                "SELECT a.assignment_id, a.student_id, a.subject_id, a.title, a.due_date, "
                        + "a.status, s.subject_name "
                        + "FROM Assignment a JOIN Subject s ON a.subject_id = s.subject_id "
                        + "WHERE a.student_id = ? AND a.subject_id = ? ORDER BY a.due_date",
                studentId, null, subjectId);
    }

    // Returns only the assignments that have the chosen status.
    public ArrayList<Assignment> getAssignmentsByStatus(int studentId, String status) {
        return selectAssignments(
                "SELECT a.assignment_id, a.student_id, a.subject_id, a.title, a.due_date, "
                        + "a.status, s.subject_name "
                        + "FROM Assignment a JOIN Subject s ON a.subject_id = s.subject_id "
                        + "WHERE a.student_id = ? AND a.status = ? ORDER BY a.due_date",
                studentId, status, 0);
    }

    // Returns only the assignments that are not completed yet, nearest due date first.
    public ArrayList<Assignment> getIncompleteAssignments(int studentId) {
        return selectAssignments(
                "SELECT a.assignment_id, a.student_id, a.subject_id, a.title, a.due_date, "
                        + "a.status, s.subject_name "
                        + "FROM Assignment a JOIN Subject s ON a.subject_id = s.subject_id "
                        + "WHERE a.student_id = ? AND a.status != 'Completed' "
                        + "ORDER BY a.due_date ASC",
                studentId, null, 0);
    }

    // Counts the Pending, In Progress and Completed assignments of a student.
    public int[] getStatusCounts(int studentId) {
        int[] counts = new int[3];
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(
                    "SELECT status, COUNT(*) AS total FROM Assignment "
                            + "WHERE student_id = ? GROUP BY status");
            statement.setInt(1, studentId);
            result = statement.executeQuery();

            while (result.next()) {
                String status = result.getString("status");
                int total = result.getInt("total");

                if (status.equals("Pending")) {
                    counts[0] = total;
                } else if (status.equals("In Progress")) {
                    counts[1] = total;
                } else if (status.equals("Completed")) {
                    counts[2] = total;
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error while counting statuses: " + e.getMessage());
        } finally {
            closeResult(result);
            closeStatement(statement);
        }
        return counts;
    }

    // Searches assignments by a word in the title or in the subject name.
    public ArrayList<Assignment> searchByTitleOrSubject(int studentId, String keyword) {
        ArrayList<Assignment> assignments = new ArrayList<Assignment>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(
                    "SELECT a.assignment_id, a.student_id, a.subject_id, a.title, a.due_date, "
                            + "a.status, s.subject_name "
                            + "FROM Assignment a JOIN Subject s ON a.subject_id = s.subject_id "
                            + "WHERE a.student_id = ? "
                            + "AND (a.title LIKE ? OR s.subject_name LIKE ?) "
                            + "ORDER BY a.due_date ASC");
            statement.setInt(1, studentId);
            statement.setString(2, "%" + keyword + "%");
            statement.setString(3, "%" + keyword + "%");
            result = statement.executeQuery();

            while (result.next()) {
                assignments.add(makeAssignment(result));
            }
        } catch (SQLException e) {
            System.out.println("Database error while searching assignments: " + e.getMessage());
        } finally {
            closeResult(result);
            closeStatement(statement);
        }
        return assignments;
    }

    // Searches assignments by a word in the title.
    public ArrayList<Assignment> searchByTitle(int studentId, String keyword) {
        ArrayList<Assignment> assignments = new ArrayList<Assignment>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(
                    "SELECT a.assignment_id, a.student_id, a.subject_id, a.title, a.due_date, "
                            + "a.status, s.subject_name "
                            + "FROM Assignment a JOIN Subject s ON a.subject_id = s.subject_id "
                            + "WHERE a.student_id = ? AND a.title LIKE ? ORDER BY a.due_date");
            statement.setInt(1, studentId);
            statement.setString(2, "%" + keyword + "%");
            result = statement.executeQuery();

            while (result.next()) {
                assignments.add(makeAssignment(result));
            }
        } catch (SQLException e) {
            System.out.println("Database error while searching titles: " + e.getMessage());
        } finally {
            closeResult(result);
            closeStatement(statement);
        }
        return assignments;
    }

    // Searches assignments by a word in the subject name.
    public ArrayList<Assignment> searchBySubject(int studentId, String keyword) {
        ArrayList<Assignment> assignments = new ArrayList<Assignment>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(
                    "SELECT a.assignment_id, a.student_id, a.subject_id, a.title, a.due_date, "
                            + "a.status, s.subject_name "
                            + "FROM Assignment a JOIN Subject s ON a.subject_id = s.subject_id "
                            + "WHERE a.student_id = ? AND s.subject_name LIKE ? ORDER BY a.due_date");
            statement.setInt(1, studentId);
            statement.setString(2, "%" + keyword + "%");
            result = statement.executeQuery();

            while (result.next()) {
                assignments.add(makeAssignment(result));
            }
        } catch (SQLException e) {
            System.out.println("Database error while searching subjects: " + e.getMessage());
        } finally {
            closeResult(result);
            closeStatement(statement);
        }
        return assignments;
    }

    // Adds a new assignment with the Pending status.
    public boolean addAssignment(int studentId, int subjectId, String title, String dueDate) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(
                    "INSERT INTO Assignment (student_id, subject_id, title, due_date, status) "
                            + "VALUES (?, ?, ?, ?, 'Pending')");
            statement.setInt(1, studentId);
            statement.setInt(2, subjectId);
            statement.setString(3, title);
            statement.setString(4, dueDate);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Database error while adding the assignment: " + e.getMessage());
            return false;
        } finally {
            closeStatement(statement);
        }
    }

    // Changes the status of one assignment.
    public boolean updateStatus(int assignmentId, int studentId, String status) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(
                    "UPDATE Assignment SET status = ? WHERE assignment_id = ? AND student_id = ?");
            statement.setString(1, status);
            statement.setInt(2, assignmentId);
            statement.setInt(3, studentId);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Database error while changing the status: " + e.getMessage());
            return false;
        } finally {
            closeStatement(statement);
        }
    }

    // Changes the title and due date of one assignment.
    public boolean updateAssignment(int assignmentId, int studentId, String title, String dueDate) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(
                    "UPDATE Assignment SET title = ?, due_date = ? "
                            + "WHERE assignment_id = ? AND student_id = ?");
            statement.setString(1, title);
            statement.setString(2, dueDate);
            statement.setInt(3, assignmentId);
            statement.setInt(4, studentId);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Database error while editing the assignment: " + e.getMessage());
            return false;
        } finally {
            closeStatement(statement);
        }
    }

    // Deletes one assignment.
    public boolean deleteAssignment(int assignmentId, int studentId) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(
                    "DELETE FROM Assignment WHERE assignment_id = ? AND student_id = ?");
            statement.setInt(1, assignmentId);
            statement.setInt(2, studentId);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Database error while deleting the assignment: " + e.getMessage());
            return false;
        } finally {
            closeStatement(statement);
        }
    }

    // Counts the assignments of one subject.
    public int countAssignmentsForSubject(int subjectId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(
                    "SELECT COUNT(*) AS total FROM Assignment WHERE subject_id = ?");
            statement.setInt(1, subjectId);
            result = statement.executeQuery();

            if (result.next()) {
                return result.getInt("total");
            }
            return 0;
        } catch (SQLException e) {
            System.out.println("Database error while counting assignments: " + e.getMessage());
            return 0;
        } finally {
            closeResult(result);
            closeStatement(statement);
        }
    }

    // Runs one assignment SELECT query and returns the list.
    private ArrayList<Assignment> selectAssignments(String sql, int studentId, String status, int subjectId) {
        ArrayList<Assignment> assignments = new ArrayList<Assignment>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, studentId);

            if (status != null) {
                statement.setString(2, status);
            } else if (subjectId != 0) {
                statement.setInt(2, subjectId);
            }

            result = statement.executeQuery();

            while (result.next()) {
                assignments.add(makeAssignment(result));
            }
        } catch (SQLException e) {
            System.out.println("Database error while loading assignments: " + e.getMessage());
        } finally {
            closeResult(result);
            closeStatement(statement);
        }
        return assignments;
    }

    // Builds an Assignment object from the current database row.
    private Assignment makeAssignment(ResultSet result) throws SQLException {
        return new Assignment(
                result.getInt("assignment_id"),
                result.getInt("student_id"),
                result.getInt("subject_id"),
                result.getString("title"),
                result.getString("due_date"),
                result.getString("status"),
                result.getString("subject_name"));
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
