package View;

import javax.swing.JOptionPane;

public final class ErrorDialog {

    private ErrorDialog() {}

    public static void showError(String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void showInfo(String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                "Info",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
