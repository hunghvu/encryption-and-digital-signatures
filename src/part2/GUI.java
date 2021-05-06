// A simple GUI to retrieve a File
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class GUI {

    public static void main(String[] args) {
        display();
    }

    // Show application window
    public static void display() {
        final JFrame frame = new JFrame();
        final JButton buttonBrowse = new JButton("Sha3");
        buttonBrowse.setBounds(100,100,75,50);
        buttonBrowse.addActionListener(event -> {
            final File directory = actionBrowse();
            System.out.println(directory.getAbsolutePath());
            System.out.println(get_sha3(directory));
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
        }
        return directory;
    }


  private static String get_sha3(File theDirectory) {
    StringBuilder sb = new StringBuilder();
    try {
        byte[] key = new byte[32]; // 256 bits empty key
        byte[] data = Files.readAllBytes(theDirectory.toPath());
        int length  = 512;
        String s = "D";
        byte[] outval = SHA3.KMACXOF256(key, data, length, s);
        String sha3 = new String(outval);
        sb.append(sha3);

    } catch (IOException e) {
        System.out.println ("get_sha3 IOException");
    }

    return sb.toString();
  }
}
