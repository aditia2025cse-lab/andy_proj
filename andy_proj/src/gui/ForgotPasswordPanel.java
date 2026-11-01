package gui;

import dao.StudentDAO;
import model.Student;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

// The two-step screen for resetting a forgotten password.
public class ForgotPasswordPanel extends JPanel {

    private MainWindow window;
    private StudentDAO studentDAO;
    private JTextField emailField;
    private JLabel questionLabel;
    private JPanel stepTwoPanel;
    private JTextField answerField;
    private JPasswordField newPasswordField;
    private JLabel messageLabel;
    private JButton nextButton;
    private JButton resetButton;
    private Student currentStudent;

    // Builds the forgot-password card and connects its buttons.
    public ForgotPasswordPanel(MainWindow window) {
        this.window = window;
        this.studentDAO = new StudentDAO();

        setLayout(new BorderLayout());
        setBackground(Theme.PAGE_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(15, 240, 15, 240));

        JPanel card = new JPanel();
        Theme.makeCard(card);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Student Assignment Tracker");
        title.setFont(Theme.TITLE_FONT);
        title.setForeground(Theme.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading = new JLabel("Forgot password");
        heading.setFont(Theme.HEADING_FONT);
        heading.setForeground(Theme.TEXT_DARK);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = makeLabel("Email");
        emailField = makeField();

        nextButton = new JButton("Next");
        Theme.styleButton(nextButton);
        nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        nextButton.addActionListener(event -> handleNext());

        questionLabel = new JLabel(" ");
        questionLabel.setFont(Theme.HEADING_FONT);
        questionLabel.setForeground(Theme.TEXT_DARK);
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionLabel.setVisible(false);

        stepTwoPanel = new JPanel();
        stepTwoPanel.setBackground(Theme.CARD_BACKGROUND);
        stepTwoPanel.setLayout(new BoxLayout(stepTwoPanel, BoxLayout.Y_AXIS));
        stepTwoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        stepTwoPanel.setVisible(false);

        JLabel answerLabel = makeLabel("Security answer");
        answerField = makeField();
        JLabel newPasswordLabel = makeLabel("New password");
        newPasswordField = new JPasswordField();
        Theme.styleTextField(newPasswordField);
        newPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        newPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        resetButton = new JButton("Reset Password");
        Theme.styleButton(resetButton);
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        resetButton.addActionListener(event -> handleReset());

        stepTwoPanel.add(answerLabel);
        stepTwoPanel.add(Box.createVerticalStrut(3));
        stepTwoPanel.add(answerField);
        stepTwoPanel.add(Box.createVerticalStrut(7));
        stepTwoPanel.add(newPasswordLabel);
        stepTwoPanel.add(Box.createVerticalStrut(3));
        stepTwoPanel.add(newPasswordField);
        stepTwoPanel.add(Box.createVerticalStrut(12));
        stepTwoPanel.add(resetButton);

        messageLabel = new JLabel(" ");
        messageLabel.setFont(Theme.BODY_FONT);
        messageLabel.setForeground(Theme.OVERDUE_TEXT);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginLink = new JButton("Back to login");
        Theme.styleLinkButton(loginLink);
        loginLink.addActionListener(event -> window.showLogin());

        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        linkPanel.setBackground(Theme.CARD_BACKGROUND);
        linkPanel.add(loginLink);
        linkPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        linkPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        card.add(Box.createVerticalGlue());
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(heading);
        card.add(Box.createVerticalStrut(14));
        card.add(emailLabel);
        card.add(Box.createVerticalStrut(3));
        card.add(emailField);
        card.add(Box.createVerticalStrut(10));
        card.add(nextButton);
        card.add(Box.createVerticalStrut(12));
        card.add(questionLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(stepTwoPanel);
        card.add(Box.createVerticalStrut(6));
        card.add(messageLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(linkPanel);
        card.add(Box.createVerticalGlue());

        add(card, BorderLayout.CENTER);
    }

    // Creates a small dark label for the text fields.
    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.BODY_FONT);
        label.setForeground(Theme.TEXT_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // Creates a theme-styled text field that fits the card width.
    private JTextField makeField() {
        JTextField field = new JTextField();
        Theme.styleTextField(field);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    // Finds the account and reveals the security question and reset fields.
    private void handleNext() {
        String email = emailField.getText().trim();

        if (email.length() == 0) {
            showError("Please enter your email.");
            return;
        }

        Student student = studentDAO.findByEmail(email);

        if (student == null) {
            currentStudent = null;
            questionLabel.setVisible(false);
            stepTwoPanel.setVisible(false);
            nextButton.setVisible(true);
            emailField.setEditable(true);
            getRootPane().setDefaultButton(nextButton);
            showError("No account was found for that email.");
            return;
        }

        currentStudent = student;
        emailField.setEditable(false);
        questionLabel.setText("Security question: " + student.getSecurityQuestion());
        questionLabel.setVisible(true);
        stepTwoPanel.setVisible(true);
        nextButton.setVisible(false);
        messageLabel.setText(" ");
        answerField.setText("");
        newPasswordField.setText("");
        getRootPane().setDefaultButton(resetButton);
        revalidate();
        repaint();
    }

    // Checks the security answer and saves the new password.
    private void handleReset() {
        if (currentStudent == null) {
            showError("Please enter your email and click Next first.");
            return;
        }

        String answer = answerField.getText().trim();
        String newPassword = new String(newPasswordField.getPassword());

        if (answer.length() == 0 || newPassword.length() == 0) {
            showError("Please enter the security answer and a new password.");
            return;
        }

        if (!currentStudent.getSecurityAnswer().equalsIgnoreCase(answer)) {
            showError("The security answer is wrong.");
            return;
        }

        if (!studentDAO.updatePassword(currentStudent.getEmail(), newPassword)) {
            showError("Could not change the password. Please try again.");
            return;
        }

        answerField.setText("");
        newPasswordField.setText("");
        showSuccess("Password changed. You can log in now.");
    }

    // Shows a red error message under the fields.
    private void showError(String message) {
        messageLabel.setForeground(Theme.OVERDUE_TEXT);
        messageLabel.setText(message);
    }

    // Shows a brown success message under the fields.
    private void showSuccess(String message) {
        messageLabel.setForeground(Theme.ACCENT);
        messageLabel.setText(message);
    }

    // Returns the Next button so MainWindow can use it as the default button.
    public JButton getNextButton() {
        return nextButton;
    }

    // Puts the screen back to the email step when it is opened again.
    public void reset() {
        currentStudent = null;
        emailField.setText("");
        emailField.setEditable(true);
        questionLabel.setText(" ");
        questionLabel.setVisible(false);
        stepTwoPanel.setVisible(false);
        nextButton.setVisible(true);
        messageLabel.setText(" ");
    }
}
