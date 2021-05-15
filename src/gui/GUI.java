// A simple GUI to retrieve a File
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final JFrame frame = new JFrame();
                frame.addWindowListener(new WindowListener() { // Completely kill JVM upon pressing X
                    @Override
                    public void windowActivated(WindowEvent e) {
                        // TODO Auto-generated method stub
                        
                    }

                    @Override
                    public void windowClosed(WindowEvent e) {
                        // TODO Auto-generated method stub
                        
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        frame.dispose();
                        System.exit(0);
                        
                    }

                    @Override
                    public void windowDeactivated(WindowEvent e) {
                        // TODO Auto-generated method stub
                        
                    }

                    @Override
                    public void windowDeiconified(WindowEvent e) {
                        // TODO Auto-generated method stub
                        
                    }

                    @Override
                    public void windowIconified(WindowEvent e) {
                        // TODO Auto-generated method stub
                        
                    }

                    @Override
                    public void windowOpened(WindowEvent e) {
                        // TODO Auto-generated method stub
                        
                    }
                });
                frame.setSize(700, 400);
                displayPart1(frame);
                frame.setVisible(true);
            }
        });       
    }

    // Show application window
    public static void displayPart1(JFrame frame) {

        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        final JLabel label = new JLabel("Choose one of 3 options below.");

        final JLabel labelEmpty1 = new JLabel(" ");
        final JLabel labelOption1 = new JLabel("1. Get SHA3 of a chosen file.");
        final JButton buttonOption1 = new JButton("SHA3 - file");

        final JLabel labelEmpty2 = new JLabel(" ");
        final JLabel labelOption2 = new JLabel("2. Get SHA3 of an input text. Enter your text in blank field below.");
        final JTextArea textOption2 = new JTextArea();
        textOption2.setLineWrap(true);
        final JScrollPane scrollOption2 = new JScrollPane(textOption2);
        final JButton buttonOption2 = new JButton("SHA3 - text input");

        final JLabel labelEmpty3 = new JLabel(" ");
        final JLabel labelOption3 = new JLabel("3. Get MAC of a chosen file. Enter MAC passphrase below (empty field is an empty passphrase)");
        final JTextArea textOption3 = new JTextArea();
        textOption3.setLineWrap(true);
        final JScrollPane scrollOption3 = new JScrollPane(textOption3);
        final JButton buttonOption3 = new JButton("MAC - file");

        final JLabel labelEmpty4 = new JLabel(" ");
        final JLabel labelOutput = new JLabel("Console (for output)");
        final JTextArea textOutput = new JTextArea();
        textOutput.setEditable(false);
        textOutput.setLineWrap(true);
        final JScrollPane scrollOutput = new JScrollPane(textOutput);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println(e);
        }

        buttonOption1.addActionListener(event -> {
            final File directory = actionBrowse();
            String outval = directory == null ? "Error: You must chose a file!" : "SHA3 of your file is: " + KCrypt.get_sha3_file(directory);
            textOutput.setText(outval);
        });
        buttonOption2.addActionListener(event -> {
            final String m = textOption2.getText();
            textOutput.setText("SHA3 of your text input is: " + KCrypt.get_sha3_text(m));
        });
        buttonOption3.addActionListener(event -> {
            final File directory = actionBrowse();
            String passphrase = textOption3.getText();
            String outval = directory == null ? "Error: You must chose a file!" : "MAC of your file is: " + KCrypt.get_mac_file(directory, passphrase);
            textOutput.setText(outval);

        });
        panel.add(label);

        panel.add(labelEmpty1);
        panel.add(labelOption1);
        panel.add(buttonOption1);

        panel.add(labelEmpty2);
        panel.add(labelOption2);
        panel.add(scrollOption2);
        panel.add(buttonOption2);

        panel.add(labelEmpty3);
        panel.add(labelOption3);
        panel.add(scrollOption3);
        panel.add(buttonOption3);

        panel.add(labelEmpty4);
        panel.add(labelOutput);
        panel.add(scrollOutput);

        panel.setVisible(true);
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
        } 
        return directory;
    }
}
