package model;

// Holds the progress numbers shown by the subject_progress view.
public class SubjectProgress {

    private int subjectId;
    private String subjectName;
    private int totalAssignments;
    private int completedAssignments;

    // Creates a progress object with all view values.
    public SubjectProgress(int subjectId, String subjectName,
                           int totalAssignments, int completedAssignments) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.totalAssignments = totalAssignments;
        this.completedAssignments = completedAssignments;
    }

    // Returns the subject id.
    public int getSubjectId() {
        return subjectId;
    }

    // Returns the subject name.
    public String getSubjectName() {
        return subjectName;
    }

    // Returns how many assignments the subject has.
    public int getTotalAssignments() {
        return totalAssignments;
    }

    // Returns how many assignments are completed.
    public int getCompletedAssignments() {
        return completedAssignments;
    }

    // Calculates the completion percentage.
    public int getPercentage() {
        if (totalAssignments == 0) {
            return 0;
        }
        return completedAssignments * 100 / totalAssignments;
    }
}
