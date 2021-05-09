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
        final JButton buttonBrowse = new JButton("SHA3 - file");

        final JLabel labelInput = new JLabel("Enter text to get SHA3 below");
        final JTextArea textInput = new JTextArea();
        final JButton buttonTextInput = new JButton("SHA3 - text input");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println(e);
        } 
        // buttonBrowse.setBounds(100,25,125,75);
        // textInput.setBounds(100,110,125,75);
        // buttonTextInput.setBounds(100,130,125,75);
        buttonBrowse.addActionListener(event -> {
            final File directory = actionBrowse();
            // System.out.println(directory.getAbsolutePath());
            System.out.println("SHA3 of your file is: " + get_sha3_file(directory));
        });
        buttonTextInput.addActionListener(event -> {
            String m = textInput.getText();
            System.out.println("SHA3 of your text input is: " + get_sha3_text(m));
        });
        panel.add(buttonBrowse);
        panel.add(labelInput);
        panel.add(textInput);
        panel.add(buttonTextInput);
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

    private static String get_sha3_file(File theDirectory) {
        StringBuilder sb = new StringBuilder();
        try {
            byte[] data = Files.readAllBytes(theDirectory.toPath());
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

    private static String get_sha3_text(String m) {
        byte[] data = m.getBytes();
        int length = 512;
        String s = "D";
        byte[] outval = SHA3.KMACXOF256("".getBytes(), data, length, s);
        return bytesToHex(outval);
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
