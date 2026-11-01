import gui.MainWindow;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

// Starts the whole Student Assignment Tracker program.
public class Main {

    // Sets a look and feel that respects our colors, then opens the window.
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Could not set the look and feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
