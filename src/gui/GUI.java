// A simple GUI to retrieve a File
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class GUI {

    public static void main(String[] args) {
        display();
    }

    // Show application window
    public static void display() {
        final JFrame frame = new JFrame();
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        final JButton buttonBrowseSha3 = new JButton("SHA3 - file");

        final JLabel labelInput = new JLabel("Enter text to get SHA3 below");
        final JTextArea textInput = new JTextArea();
        final JButton buttonTextInput = new JButton("SHA3 - text input");

        final JButton buttonBrowseMac = new JButton("MAC - file");
        final JLabel labelMac = new JLabel("Enter MAC passphrase below (the default is an empty passphrase)");
        final JTextArea textMacPassphrase = new JTextArea();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println(e);
        }

        buttonBrowseSha3.addActionListener(event -> {
            final File directory = actionBrowse();
            // System.out.println(directory.getAbsolutePath());
            System.out.println("SHA3 of your file is: " + KCrypt.get_sha3_file(directory));
        });
        buttonTextInput.addActionListener(event -> {
            final String m = textInput.getText();
            System.out.println("SHA3 of your text input is: " + KCrypt.get_sha3_text(m));
        });
        buttonBrowseMac.addActionListener(event -> {
            final File directory = actionBrowse();
            String passphrase = textMacPassphrase.getText();
            System.out.println("MAC of your file is: " + KCrypt.get_mac_file(directory, passphrase));

        });
        panel.add(buttonBrowseSha3);
        panel.add(labelInput);
        panel.add(textInput);
        panel.add(buttonTextInput);
        panel.add(labelMac);
        panel.add(textMacPassphrase);
        panel.add(buttonBrowseMac);
        panel.setVisible(true);
        frame.setSize(600, 600);
        frame.setVisible(true);
        frame.add(panel);
    }

    // Open a file chooser
    public static File actionBrowse() {
        final JFileChooser MYF_FILE_CHOOSER = new JFileChooser();
        MYF_FILE_CHOOSER.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int choice = MYF_FILE_CHOOSER.showOpenDialog(null);

        File directory = null;
        if (choice == JFileChooser.APPROVE_OPTION) {
            directory = MYF_FILE_CHOOSER.getSelectedFile();
        } else {
            System.out.println("You must choose a file!");
        }
        return directory;
    }
}
