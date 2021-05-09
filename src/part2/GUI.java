// A simple GUI to retrieve a File
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class GUI {

    public static void main(String[] args) {
        display();
    }

    // Show application window
    public static void display() {
        final JFrame frame = new JFrame();
        final JButton buttonBrowse = new JButton("Sha3-file");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println(e);
        } 
        buttonBrowse.setBounds(100,50,100,75);
        buttonBrowse.addActionListener(event -> {
            final File directory = actionBrowse();
            // System.out.println(directory.getAbsolutePath());
            System.out.println("SHA3 of your file is: " + get_sha3_file(directory));
        });
        frame.add(buttonBrowse);
        frame.setSize(300, 300);
        frame.setLayout(null);
        frame.setVisible(true);
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
            // Error, must use "".getBbytes(), not an empty key byte array.
            byte[] outval = SHA3.KMACXOF256("".getBytes(), data, length, s);
            System.out.println(bytesToHex(outval));

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
