package gui;

import dao.StudentDAO;
import exception.InvalidLoginException;
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

// The login screen with email, password and links to the other account screens.
public class LoginPanel extends JPanel {

    private MainWindow window;
    private StudentDAO studentDAO;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private JButton loginButton;

    // Builds the login card and connects its buttons.
    public LoginPanel(MainWindow window) {
        this.window = window;
        this.studentDAO = new StudentDAO();

        setLayout(new BorderLayout());
        setBackground(Theme.PAGE_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(30, 240, 30, 240));

        JPanel card = new JPanel();
        Theme.makeCard(card);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Student Assignment Tracker");
        title.setFont(Theme.TITLE_FONT);
        title.setForeground(Theme.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading = new JLabel("Log in");
        heading.setFont(Theme.HEADING_FONT);
        heading.setForeground(Theme.TEXT_DARK);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = makeLabel("Email");
        emailField = new JTextField();
        Theme.styleTextField(emailField);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passwordLabel = makeLabel("Password");
        passwordField = new JPasswordField();
        Theme.styleTextField(passwordField);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginButton = new JButton("Login");
        Theme.styleButton(loginButton);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginButton.addActionListener(event -> handleLogin());

        JButton registerLink = new JButton("Create account");
        Theme.styleLinkButton(registerLink);
        registerLink.addActionListener(event -> window.showRegister());

        JButton forgotLink = new JButton("Forgot password?");
        Theme.styleLinkButton(forgotLink);
        forgotLink.addActionListener(event -> window.showForgotPassword());

        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        linkPanel.setBackground(Theme.CARD_BACKGROUND);
        linkPanel.add(registerLink);
        linkPanel.add(forgotLink);
        linkPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        linkPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        messageLabel = new JLabel(" ");
        messageLabel.setFont(Theme.BODY_FONT);
        messageLabel.setForeground(Theme.OVERDUE_TEXT);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(heading);
        card.add(Box.createVerticalStrut(20));
        card.add(emailLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(emailField);
        card.add(Box.createVerticalStrut(12));
        card.add(passwordLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(20));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(10));
        card.add(messageLabel);
        card.add(Box.createVerticalStrut(8));
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

    // Checks the email and password, then opens the dashboard.
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.length() == 0 || password.length() == 0) {
            showError("Please enter your email and password.");
            return;
        }

        try {
            Student student = studentDAO.login(email, password);

            if (student == null) {
                showError("Could not log in. Please try again.");
                return;
            }

            messageLabel.setText(" ");
            passwordField.setText("");
            window.showDashboard(student);
        } catch (InvalidLoginException e) {
            showError(e.getMessage());
        }
    }

    // Shows a red error message under the fields.
    private void showError(String message) {
        messageLabel.setForeground(Theme.OVERDUE_TEXT);
        messageLabel.setText(message);
    }

    // Returns the Login button so MainWindow can set it as the default button.
    public JButton getLoginButton() {
        return loginButton;
    }

    // Clears the password and the message when the login screen is shown again.
    public void reset() {
        passwordField.setText("");
        messageLabel.setText(" ");
    }
}
