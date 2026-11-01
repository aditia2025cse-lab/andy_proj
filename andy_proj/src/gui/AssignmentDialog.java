package gui;

import dao.AssignmentDAO;
import dao.SubjectDAO;
import exception.DuplicateSubjectException;
import model.Assignment;
import model.Student;
import model.Subject;
import util.DateHelper;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;

// A small modal dialog that is used for both adding and editing an assignment.
public class AssignmentDialog extends JDialog {

    private Student student;
    private Assignment assignment;
    private AssignmentDAO assignmentDAO;
    private SubjectDAO subjectDAO;
    private ArrayList<Subject> subjects;
    private JTextField titleField;
    private JTextField dueDateField;
    private JComboBox<String> subjectCombo;

    // Builds the dialog and fills the fields when an assignment is being edited.
    public AssignmentDialog(MainWindow owner, Student student, Assignment assignment) {
        super(owner, assignment == null ? "Add Assignment" : "Edit Assignment", true);
        this.student = student;
        this.assignment = assignment;
        this.assignmentDAO = new AssignmentDAO();
        this.subjectDAO = new SubjectDAO();

        setSize(440, 360);
        setResizable(false);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.PAGE_BACKGROUND);

        JPanel card = new JPanel();
        Theme.makeCard(card);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel(assignment == null ? "Add Assignment" : "Edit Assignment");
        heading.setFont(Theme.HEADING_FONT);
        heading.setForeground(Theme.TEXT_DARK);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleField = makeField();
        dueDateField = makeField();
        dueDateField.setToolTipText("yyyy-MM-dd");

        subjectCombo = new JComboBox<String>();
        subjectCombo.setFont(Theme.BODY_FONT);
        subjectCombo.setForeground(Theme.TEXT_DARK);
        subjectCombo.setBackground(Theme.CARD_BACKGROUND);
        subjectCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        subjectCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        subjects = subjectDAO.getSubjectsByStudent(student.getStudentId());

        for (int i = 0; i < subjects.size(); i++) {
            subjectCombo.addItem(subjects.get(i).getSubjectName());
        }

        if (assignment != null) {
            titleField.setText(assignment.getTitle());
            dueDateField.setText(assignment.getDueDate());
            subjectCombo.setSelectedItem(assignment.getSubjectName());
            subjectCombo.setEditable(false);
            subjectCombo.setEnabled(false);
        } else {
            subjectCombo.setEditable(true);
        }

        JButton saveButton = new JButton("Save");
        Theme.styleButton(saveButton);
        saveButton.addActionListener(event -> handleSave());

        JButton cancelButton = new JButton("Cancel");
        Theme.styleSecondaryButton(cancelButton);
        cancelButton.addActionListener(event -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Theme.CARD_BACKGROUND);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        card.add(heading);
        card.add(Box.createVerticalStrut(14));
        card.add(makeLabel("Title"));
        card.add(Box.createVerticalStrut(4));
        card.add(titleField);
        card.add(Box.createVerticalStrut(12));
        card.add(makeLabel("Due date (yyyy-MM-dd)"));
        card.add(Box.createVerticalStrut(4));
        card.add(dueDateField);
        card.add(Box.createVerticalStrut(12));
        card.add(makeLabel("Subject (choose one or type a new name)"));
        card.add(Box.createVerticalStrut(4));
        card.add(subjectCombo);
        card.add(Box.createVerticalStrut(18));
        card.add(buttonPanel);

        add(card, BorderLayout.CENTER);
        getRootPane().setDefaultButton(saveButton);
    }

    // Creates a small dark label for the fields.
    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.BODY_FONT);
        label.setForeground(Theme.TEXT_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // Creates a theme-styled text field that fits the dialog width.
    private JTextField makeField() {
        JTextField field = new JTextField();
        Theme.styleTextField(field);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    // Validates the form and either adds or updates the assignment.
    private void handleSave() {
        String title = titleField.getText().trim();
        String dueDate = dueDateField.getText().trim();

        if (title.length() == 0) {
            showError("The title cannot be empty.");
            return;
        }

        if (!DateHelper.isValidDueDate(dueDate)) {
            showError("Please use the yyyy-MM-dd format and choose today or a future date.");
            return;
        }

        if (assignment == null) {
            saveNewAssignment(title, dueDate);
        } else {
            saveEditedAssignment(title, dueDate);
        }
    }

    // Finds or creates the chosen subject, then inserts a new Pending assignment.
    private void saveNewAssignment(String title, String dueDate) {
        Object selectedSubject = subjectCombo.getEditor().getItem();
        String subjectName = selectedSubject == null ? "" : selectedSubject.toString().trim();

        if (subjectName.length() == 0) {
            showError("Please choose a subject or type a new subject name.");
            return;
        }

        int subjectId = findSubjectId(subjectName);

        if (subjectId == 0) {
            subjectId = addNewSubject(subjectName);
        }

        if (subjectId == 0) {
            return;
        }

        if (!assignmentDAO.addAssignment(student.getStudentId(), subjectId, title, dueDate)) {
            showError("Could not add the assignment. Please try again.");
            return;
        }

        dispose();
    }

    // Updates the title and due date of the assignment being edited.
    private void saveEditedAssignment(String title, String dueDate) {
        if (!assignmentDAO.updateAssignment(assignment.getAssignmentId(),
                student.getStudentId(), title, dueDate)) {
            showError("Could not update the assignment. Please try again.");
            return;
        }

        dispose();
    }

    // Returns the id of a subject with this name, or zero when it is new.
    private int findSubjectId(String subjectName) {
        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);

            if (subject.getSubjectName().equalsIgnoreCase(subjectName)) {
                return subject.getSubjectId();
            }
        }
        return 0;
    }

    // Saves a typed subject name and returns its new id.
    private int addNewSubject(String subjectName) {
        try {
            Subject subject = subjectDAO.addSubject(student.getStudentId(), subjectName);

            if (subject == null) {
                showError("Could not add the subject. Please try again.");
                return 0;
            }
            return subject.getSubjectId();
        } catch (DuplicateSubjectException e) {
            showError(e.getMessage());
            return 0;
        }
    }

    // Shows an error message in a small pop-up.
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Cannot save",
                JOptionPane.ERROR_MESSAGE);
    }
}
