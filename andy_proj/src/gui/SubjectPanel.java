package gui;

import dao.AssignmentDAO;
import dao.SubjectDAO;
import exception.DuplicateSubjectException;
import model.Student;
import model.Subject;
import model.SubjectProgress;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

// The right-hand card that shows every subject and its completion progress.
public class SubjectPanel extends JPanel {

    private DashboardPanel dashboard;
    private Student student;
    private SubjectDAO subjectDAO;
    private AssignmentDAO assignmentDAO;
    private JPanel listPanel;
    private SubjectProgress selectedSubject;

    // Builds the subject card for the logged-in student.
    public SubjectPanel(DashboardPanel dashboard, Student student) {
        this.dashboard = dashboard;
        this.student = student;
        this.subjectDAO = new SubjectDAO();
        this.assignmentDAO = new AssignmentDAO();

        setLayout(new BorderLayout(0, 10));
        setPreferredSize(new Dimension(280, 0));
        Theme.makeCard(this);

        JLabel heading = new JLabel("Subjects");
        heading.setFont(Theme.HEADING_FONT);
        heading.setForeground(Theme.TEXT_DARK);

        JButton showAllButton = new JButton("Show all");
        Theme.styleLinkButton(showAllButton);
        showAllButton.addActionListener(event -> handleShowAll());

        JPanel headingRow = new JPanel(new BorderLayout());
        headingRow.setBackground(Theme.CARD_BACKGROUND);
        headingRow.add(heading, BorderLayout.WEST);
        headingRow.add(showAllButton, BorderLayout.EAST);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Theme.CARD_BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.CARD_BACKGROUND);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JButton addButton = new JButton("+ Add Subject");
        Theme.styleButton(addButton);
        addButton.addActionListener(event -> handleAddSubject());

        JButton removeButton = new JButton("Remove Subject");
        Theme.styleSecondaryButton(removeButton);
        removeButton.addActionListener(event -> handleRemoveSubject());

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 6));
        buttonPanel.setBackground(Theme.CARD_BACKGROUND);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        add(headingRow, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refresh();
    }

    // Reloads the subject list and progress bars from the subject_progress view.
    public void refresh() {
        listPanel.removeAll();

        ArrayList<SubjectProgress> progressList =
                subjectDAO.getSubjectProgress(student.getStudentId());

        if (progressList.isEmpty()) {
            selectedSubject = null;
            JLabel emptyLabel = new JLabel("No subjects yet.");
            emptyLabel.setFont(Theme.BODY_FONT);
            emptyLabel.setForeground(Theme.TEXT_DARK);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(8, 2, 8, 2));
            listPanel.add(emptyLabel);
        } else {
            for (int i = 0; i < progressList.size(); i++) {
                listPanel.add(createSubjectRow(progressList.get(i)));
            }
            listPanel.add(Box.createVerticalGlue());
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    // Forgets the highlighted subject when the dashboard clears its filter.
    public void clearSelection() {
        selectedSubject = null;
    }

    // Creates one clickable row with the subject name and progress bar.
    private JPanel createSubjectRow(SubjectProgress progress) {
        boolean isActive = dashboard.getSubjectFilterId() == progress.getSubjectId();
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setBackground(isActive ? Theme.SECONDARY : Theme.CARD_BACKGROUND);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.TABLE_GRID),
                BorderFactory.createEmptyBorder(6, 2, 10, 2)));

        JButton nameButton = new JButton(progress.getSubjectName());
        Theme.styleLinkButton(nameButton);
        nameButton.setFont(Theme.BODY_FONT);
        nameButton.setForeground(isActive ? Theme.ACCENT : Theme.TEXT_DARK);
        nameButton.addActionListener(event -> handleSubjectClick(progress));

        JLabel percentLabel = new JLabel(progress.getPercentage() + "%");
        percentLabel.setFont(Theme.BODY_FONT);
        percentLabel.setForeground(Theme.ACCENT);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(isActive ? Theme.SECONDARY : Theme.CARD_BACKGROUND);
        titleRow.add(nameButton, BorderLayout.WEST);
        titleRow.add(percentLabel, BorderLayout.EAST);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(progress.getPercentage());
        progressBar.setOpaque(true);
        progressBar.setForeground(Theme.ACCENT);
        progressBar.setBackground(Theme.PAGE_BACKGROUND);
        progressBar.setBorder(BorderFactory.createLineBorder(Theme.TABLE_GRID));
        progressBar.setPreferredSize(new Dimension(100, 16));

        row.add(titleRow, BorderLayout.NORTH);
        row.add(progressBar, BorderLayout.CENTER);
        return row;
    }

    // Filters the assignments table to the clicked subject.
    private void handleSubjectClick(SubjectProgress progress) {
        selectedSubject = progress;
        dashboard.showSubjectFilter(progress.getSubjectId(), progress.getSubjectName());
    }

    // Returns to the default incomplete view.
    private void handleShowAll() {
        selectedSubject = null;
        dashboard.showAllAssignments();
    }

    // Asks for a new subject name and saves it.
    private void handleAddSubject() {
        String name = JOptionPane.showInputDialog(this,
                "New subject name:", "Add Subject", JOptionPane.QUESTION_MESSAGE);

        if (name == null) {
            return;
        }

        name = name.trim();

        if (name.length() == 0) {
            showError("Please type a subject name.");
            return;
        }

        try {
            Subject subject = subjectDAO.addSubject(student.getStudentId(), name);

            if (subject == null) {
                showError("Could not add the subject. Please try again.");
                return;
            }
            dashboard.refresh();
        } catch (DuplicateSubjectException e) {
            showError(e.getMessage());
        }
    }

    // Removes the selected subject and its assignments after a confirmation.
    private void handleRemoveSubject() {
        if (selectedSubject == null) {
            JOptionPane.showMessageDialog(this, "Please select a subject first.");
            return;
        }

        int subjectId = selectedSubject.getSubjectId();
        int count = assignmentDAO.countAssignmentsForSubject(subjectId);
        String message = "Remove \"" + selectedSubject.getSubjectName() + "\"?\n"
                + count + " assignment(s) will also be deleted.";

        int answer = JOptionPane.showConfirmDialog(this, message, "Remove Subject",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        if (!subjectDAO.removeSubject(student.getStudentId(), subjectId)) {
            showError("Could not remove the subject. Please try again.");
            return;
        }

        selectedSubject = null;

        if (dashboard.getSubjectFilterId() == subjectId) {
            dashboard.clearSubjectFilter();
        }
        dashboard.refresh();
    }

    // Shows an error message in a small pop-up.
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Subjects",
                JOptionPane.ERROR_MESSAGE);
    }
}
