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

// The registration screen where a new student creates an account.
public class RegisterPanel extends JPanel {

    private MainWindow window;
    private StudentDAO studentDAO;
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField questionField;
    private JTextField answerField;
    private JLabel messageLabel;
    private JButton registerButton;

    // Builds the register card and connects its buttons.
    public RegisterPanel(MainWindow window) {
        this.window = window;
        this.studentDAO = new StudentDAO();

        setLayout(new BorderLayout());
        setBackground(Theme.PAGE_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 240, 20, 240));

        JPanel card = new JPanel();
        Theme.makeCard(card);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Student Assignment Tracker");
        title.setFont(Theme.TITLE_FONT);
        title.setForeground(Theme.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading = new JLabel("Create account");
        heading.setFont(Theme.HEADING_FONT);
        heading.setForeground(Theme.TEXT_DARK);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = makeLabel("Name");
        nameField = makeField();

        JLabel emailLabel = makeLabel("Email");
        emailField = makeField();

        JLabel passwordLabel = makeLabel("Password");
        passwordField = new JPasswordField();
        Theme.styleTextField(passwordField);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel questionLabel = makeLabel("Security question");
        questionField = makeField();

        JLabel answerLabel = makeLabel("Security answer");
        answerField = makeField();

        registerButton = new JButton("Create account");
        Theme.styleButton(registerButton);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        registerButton.addActionListener(event -> handleRegister());

        JButton loginLink = new JButton("Back to login");
        Theme.styleLinkButton(loginLink);
        loginLink.addActionListener(event -> window.showLogin());

        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        linkPanel.setBackground(Theme.CARD_BACKGROUND);
        linkPanel.add(loginLink);
        linkPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        linkPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        messageLabel = new JLabel(" ");
        messageLabel.setFont(Theme.BODY_FONT);
        messageLabel.setForeground(Theme.OVERDUE_TEXT);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(heading);
        card.add(Box.createVerticalStrut(12));
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(3));
        card.add(nameField);
        card.add(Box.createVerticalStrut(7));
        card.add(emailLabel);
        card.add(Box.createVerticalStrut(3));
        card.add(emailField);
        card.add(Box.createVerticalStrut(7));
        card.add(passwordLabel);
        card.add(Box.createVerticalStrut(3));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(7));
        card.add(questionLabel);
        card.add(Box.createVerticalStrut(3));
        card.add(questionField);
        card.add(Box.createVerticalStrut(7));
        card.add(answerLabel);
        card.add(Box.createVerticalStrut(3));
        card.add(answerField);
        card.add(Box.createVerticalStrut(14));
        card.add(registerButton);
        card.add(Box.createVerticalStrut(6));
        card.add(messageLabel);
        card.add(Box.createVerticalStrut(4));
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

    // Validates the form, saves the student and opens the dashboard.
    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String question = questionField.getText().trim();
        String answer = answerField.getText().trim();

        if (name.length() == 0 || email.length() == 0 || password.length() == 0
                || question.length() == 0 || answer.length() == 0) {
            showError("Please fill in every field.");
            return;
        }

        if (studentDAO.emailExists(email)) {
            showError("That email is already registered. Please log in.");
            return;
        }

        Student student = new Student(0, name, email, password, question, answer);

        if (!studentDAO.register(student)) {
            showError("Could not create the account. Please try again.");
            return;
        }

        Student savedStudent = studentDAO.findByEmail(email);

        if (savedStudent == null) {
            showError("Account created, but it could not be loaded. Please log in.");
            return;
        }

        clearFields();
        window.showDashboard(savedStudent);
    }

    // Shows a red error message under the fields.
    private void showError(String message) {
        messageLabel.setForeground(Theme.OVERDUE_TEXT);
        messageLabel.setText(message);
    }

    // Empties the form after a successful registration.
    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        questionField.setText("");
        answerField.setText("");
        messageLabel.setText(" ");
    }

    // Returns the register button so MainWindow can use it as the default button.
    public JButton getRegisterButton() {
        return registerButton;
    }

    // Clears the old error message and password when the screen is opened again.
    public void reset() {
        passwordField.setText("");
        messageLabel.setText(" ");
    }
}
