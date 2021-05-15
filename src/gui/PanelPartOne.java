/**
 * This class represents panel for part 1 of the project.
 */
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class PanelPartOne extends JPanel {
  static final JLabel label = new JLabel("Choose one of 3 options below.");

  static final JLabel labelEmpty1 = new JLabel(" ");
  static final JLabel labelOption1 = new JLabel("1. Get SHA3 of a chosen file.");
  static final JButton buttonOption1 = new JButton("SHA3 - file");

  static final JLabel labelEmpty2 = new JLabel(" ");
  static final JLabel labelOption2 = new JLabel("2. Get SHA3 of an input text. Enter your text in blank field below.");
  static final JTextArea textOption2 = new JTextArea();
  static final JScrollPane scrollOption2 = new JScrollPane(textOption2);
  static final JButton buttonOption2 = new JButton("SHA3 - text input");

  static final JLabel labelEmpty3 = new JLabel(" ");
  static final JLabel labelOption3 = new JLabel(
      "3. Get MAC of a chosen file. Enter MAC passphrase below (empty field is an empty passphrase)");
  static final JTextArea textOption3 = new JTextArea();
  static final JScrollPane scrollOption3 = new JScrollPane(textOption3);
  static final JButton buttonOption3 = new JButton("MAC - file");

  static final JLabel labelEmpty4 = new JLabel(" ");
  static final JLabel labelOutput = new JLabel("Console (for output)");
  static final JTextArea textOutput = new JTextArea();
  static final JScrollPane scrollOutput = new JScrollPane(textOutput);

  public PanelPartOne() {
    this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    textOption2.setLineWrap(true);
    textOption3.setLineWrap(true);
    textOutput.setEditable(false);
    textOutput.setLineWrap(true);
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      System.out.println(e);
    }

    addButtonBehavior();

    this.add(label);

    this.add(labelEmpty1);
    this.add(labelOption1);
    this.add(buttonOption1);

    this.add(labelEmpty2);
    this.add(labelOption2);
    this.add(scrollOption2);
    this.add(buttonOption2);

    this.add(labelEmpty3);
    this.add(labelOption3);
    this.add(scrollOption3);
    this.add(buttonOption3);

    this.add(labelEmpty4);
    this.add(labelOutput);
    this.add(scrollOutput);

    this.setVisible(true);

  }

  private void addButtonBehavior() {
    buttonOption1.addActionListener(event -> {
      final File directory = UtilGui.actionBrowse();
      String outval = directory == null ? "Error: You must chose a file!"
          : "SHA3 of your file is: " + KCrypt.get_sha3_file(directory);
      textOutput.setText(outval);
    });
    buttonOption2.addActionListener(event -> {
      final String m = textOption2.getText();
      textOutput.setText("SHA3 of your text input is: " + KCrypt.get_sha3_text(m));
    });
    buttonOption3.addActionListener(event -> {
      final File directory = UtilGui.actionBrowse();
      String passphrase = textOption3.getText();
      String outval = directory == null ? "Error: You must chose a file!"
          : "MAC of your file is: " + KCrypt.get_mac_file(directory, passphrase);
      textOutput.setText(outval);

    });
  }
}