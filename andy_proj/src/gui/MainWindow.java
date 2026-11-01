package gui;

import dao.DBConnection;
import model.Student;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// The main frame that switches between the account screens and the dashboard.
public class MainWindow extends JFrame {

    private static final String LOGIN_CARD = "login";
    private static final String REGISTER_CARD = "register";
    private static final String FORGOT_CARD = "forgot";
    private static final String DASHBOARD_CARD = "dashboard";

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private ForgotPasswordPanel forgotPasswordPanel;
    private DashboardPanel dashboardPanel;

    // Builds the window and adds all the account screens to the card layout.
    public MainWindow() {
        setTitle("Student Assignment Tracker");
        setSize(1000, 650);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setBackground(Theme.PAGE_BACKGROUND);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Theme.PAGE_BACKGROUND);

        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        forgotPasswordPanel = new ForgotPasswordPanel(this);

        cardPanel.add(loginPanel, LOGIN_CARD);
        cardPanel.add(registerPanel, REGISTER_CARD);
        cardPanel.add(forgotPasswordPanel, FORGOT_CARD);

        add(cardPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            // Closes the database and the program when the window is closed.
            @Override
            public void windowClosing(WindowEvent event) {
                DBConnection.closeConnection();
                dispose();
                System.exit(0);
            }
        });

        showLogin();
    }

    // Shows the login card and makes its Login button the Enter-key button.
    public void showLogin() {
        loginPanel.reset();
        cardLayout.show(cardPanel, LOGIN_CARD);
        getRootPane().setDefaultButton(loginPanel.getLoginButton());
    }

    // Shows the register card and makes its button the Enter-key button.
    public void showRegister() {
        registerPanel.reset();
        cardLayout.show(cardPanel, REGISTER_CARD);
        getRootPane().setDefaultButton(registerPanel.getRegisterButton());
    }

    // Shows the forgot-password card and makes its Next button the Enter-key button.
    public void showForgotPassword() {
        forgotPasswordPanel.reset();
        cardLayout.show(cardPanel, FORGOT_CARD);
        getRootPane().setDefaultButton(forgotPasswordPanel.getNextButton());
    }

    // Creates a fresh dashboard for the student and shows it.
    public void showDashboard(Student student) {
        if (dashboardPanel != null) {
            cardPanel.remove(dashboardPanel);
        }

        dashboardPanel = new DashboardPanel(this, student);
        cardPanel.add(dashboardPanel, DASHBOARD_CARD);
        cardLayout.show(cardPanel, DASHBOARD_CARD);
        getRootPane().setDefaultButton(null);
        cardPanel.revalidate();
        cardPanel.repaint();
    }
}
