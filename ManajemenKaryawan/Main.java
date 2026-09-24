package manajemenkaryawan;
import java.util.Scanner;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true); // buka login dulu!
        });
    }
}