package model;

// A subject belongs to one student.
public class Subject implements Displayable {

    private int subjectId;
    private int studentId;
    private String subjectName;

    // Creates a subject with all its details.
    public Subject(int subjectId, int studentId, String subjectName) {
        this.subjectId = subjectId;
        this.studentId = studentId;
        this.subjectName = subjectName;
    }

    // Returns the database id of the subject.
    public int getSubjectId() {
        return subjectId;
    }

    // Changes the database id of the subject.
    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    // Returns the id of the student who owns this subject.
    public int getStudentId() {
        return studentId;
    }

    // Changes the owner student id.
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    // Returns the subject name.
    public String getSubjectName() {
        return subjectName;
    }

    // Changes the subject name.
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    // Shows a subject in a simple way.
    @Override
    public String getDetails() {
        return "Subject: " + subjectName;
    }
}
