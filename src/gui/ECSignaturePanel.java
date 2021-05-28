package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ec.ECKeyPair;
import util.UtilGui;

public class ECSignaturePanel extends JPanel {

  /** Out path button. */
  private static final JButton foutButton = new JButton("Select Output Path Folder");

  /** A component to show the output path. */
  private static final JTextField outText = new JTextField(UtilGui.X_AXIS - 10);

  /** Passphrase request label. */
  private static final JLabel passLabel = new JLabel("Put passphrase to generate signature here, must be the same as passphrase used to create public key:");

  /** A field to type passphrase. */
  private static final JTextField passText = new JTextField(UtilGui.X_AXIS - 10);

  /** File path button. */
  private static final JButton finButton = new JButton("Select File Input");

  /** A component to show the output path. */
  private static final JTextField inText = new JTextField(UtilGui.X_AXIS - 10);

  /** Generate button. */
  private static final JButton genButton = new JButton("Generate signature file");

  /**
   * Construct new panel for signature.
   */
  public ECSignaturePanel(JTextArea console) {
    super();
    setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    initialize(console); // Set up components
    setVisible(true);
  }

  /**
   * Add components and their functionalities for the panel
   */
  private void initialize(JTextArea console) {

    foutButton.setAlignmentX(JButton.LEFT_ALIGNMENT);
    foutButton.addActionListener(event -> {
      File outPath = UtilGui.pathBrowse();
      if (outPath != null)
        outText.setText(outPath.getPath());
    });

    outText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
    outText.setMaximumSize(outText.getPreferredSize());
    outText.setBackground(Color.ORANGE);
    outText.setEditable(false);

    inText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
    inText.setMaximumSize(outText.getPreferredSize());
    inText.setBackground(Color.ORANGE);
    inText.setEditable(false);

    passLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);

    passText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
    passText.setMaximumSize(passText.getPreferredSize());





    // genButton.addActionListener(event -> {
    //   ECKeyPair key = new ECKeyPair(passText.getText());
    //   console.setText(key.writePubToFile(outText.getText()));
    //   console.append("\n" + key.writePrvToFile(passText2.getText(), outText.getText()));
    // });

    this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
    this.add(foutButton);
    this.add(outText);
    this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
    this.add(passLabel);
    this.add(passText);
    this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
    this.add(finButton);
    this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
    this.add(inText);
    // this.add(passText2);
    this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
    this.add(genButton);
    this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space

  }

}
