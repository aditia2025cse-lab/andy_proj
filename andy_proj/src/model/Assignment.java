package model;

// An assignment is work that belongs to one subject and one student.
public class Assignment implements Displayable {

    private int assignmentId;
    private int studentId;
    private int subjectId;
    private String title;
    private String dueDate;
    private String status;
    private String subjectName;

    // Creates an assignment with all its details.
    public Assignment(int assignmentId, int studentId, int subjectId, String title,
                      String dueDate, String status, String subjectName) {
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.title = title;
        this.dueDate = dueDate;
        this.status = status;
        this.subjectName = subjectName;
    }

    // Returns the database id of the assignment.
    public int getAssignmentId() {
        return assignmentId;
    }

    // Changes the database id of the assignment.
    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    // Returns the id of the student who owns this assignment.
    public int getStudentId() {
        return studentId;
    }

    // Changes the owner student id.
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    // Returns the id of the subject.
    public int getSubjectId() {
        return subjectId;
    }

    // Changes the subject id.
    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    // Returns the assignment title.
    public String getTitle() {
        return title;
    }

    // Changes the assignment title.
    public void setTitle(String title) {
        this.title = title;
    }

    // Returns the due date in yyyy-MM-dd format.
    public String getDueDate() {
        return dueDate;
    }

    // Changes the due date.
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    // Returns the current status.
    public String getStatus() {
        return status;
    }

    // Changes the assignment status.
    public void setStatus(String status) {
        this.status = status;
    }

    // Returns the subject name that was loaded with this assignment.
    public String getSubjectName() {
        return subjectName;
    }

    // Changes the subject name.
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    // Shows an assignment in a simple way.
    @Override
    public String getDetails() {
        return assignmentId + " | " + title + " | " + subjectName + " | "
                + dueDate + " | " + status;
    }
}
