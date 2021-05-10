// A simple GUI to retrieve a File
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
            System.out.println("SHA3 of your file is: " + get_sha3_file(directory));
        });
        buttonTextInput.addActionListener(event -> {
            final String m = textInput.getText();
            System.out.println("SHA3 of your text input is: " + get_sha3_text(m));
        });
        buttonBrowseMac.addActionListener(event -> {
            final File directory = actionBrowse();
            String passphrase = textMacPassphrase.getText();
            System.out.println("MAC of your file is: " + get_mac_file(directory, passphrase));

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

    // Return SHA3 in hex string of a file
    private static String get_sha3_file(File directory) {
        StringBuilder sb = new StringBuilder();
        try {
            byte[] data = Files.readAllBytes(directory.toPath());
            int length  = 512;
            String s = "D";
            // Must use "".getBbytes(), not an empty key byte array.
            byte[] outval = SHA3.KMACXOF256("".getBytes(), data, length, s);
            sb.append(bytesToHex(outval));

        } catch (IOException e) {
            System.out.println ("get_sha3_file IOException");
        }
        return sb.toString();
    }

    // Return SHA3 in hex string of a given text input
    private static String get_sha3_text(String m) {
        byte[] data = m.getBytes();
        int length = 512;
        String s = "D";
        byte[] outval = SHA3.KMACXOF256("".getBytes(), data, length, s);
        return bytesToHex(outval);
    }

    // Return MAC of a given file in hex string
    private static String get_mac_file(File directory, String passphrase) {
        StringBuilder sb = new StringBuilder();
        try {
            byte[] data = Files.readAllBytes(directory.toPath());
            int length  = 512;
            String s = "T";
            // Must use "".getBbytes(), not an empty key byte array.
            byte[] outval = SHA3.KMACXOF256(passphrase.getBytes(), data, length, s);
            sb.append(bytesToHex(outval));

        } catch (IOException e) {
            System.out.println ("get_sha3_file IOException");
        }
        return sb.toString();
    }
    // https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
