/**
 * This provides a panel for signature functionality of the application
 * @author Hung Vu
 * @author Phong Le
 */
package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ec.ECCrypt;
import util.UtilGui;

public class ECSignaturePanel extends JPanel {
  /** Option 1 label */
  private static final JLabel optionOne = new JLabel("1. Generate a signature from a given file.");

  /** Passphrase request label. */
  private static final JLabel passLabel = new JLabel(
      "Put passphrase to generate signature here:");

  /** A field to type passphrase. */
  private static final JTextField passText = new JTextField(UtilGui.X_AXIS - 10);

  /** File path button. */
  private static final JButton finButton = new JButton("Select File Input");

  /** A component to show the output path. */
  private static final JTextField inText = new JTextField(UtilGui.X_AXIS - 10);

  /** Generate signature button. */
  private static final JButton genButton = new JButton("Generate signature file");

  /** Option 2 label */
  private static final JLabel optionTwo = new JLabel("2. Verify signature");
  /** To-be-verified file path button. */
  private static final JButton toBeVerifiedButton = new JButton("Select To-be-verified File");

  /** A component to show the to-be-verified file path. */
  private static final JTextField toBeVerifiedPathText = new JTextField(UtilGui.X_AXIS - 10);
  /** Signature file path button. */
  private static final JButton signatureButton = new JButton("Select Signature File Input");

  /** Signature path Text. */
  private static final JTextField signaturePathText = new JTextField(UtilGui.X_AXIS - 10);

  /** Signature file path button. */
  private static final JButton publicKeyButton = new JButton("Select Public Key File Input");

  /** Signature path Text. */
  private static final JTextField publicKeyPathText = new JTextField(UtilGui.X_AXIS - 10);

  /** Verify signature button. */
  private static final JButton verifyButton = new JButton("Verify file");

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

    finButton.addActionListener(event -> {
      File inPath = UtilGui.fileBrowse();
      if (inPath != null)
        inText.setText(inPath.getPath());
    });
    genButton.addActionListener(event -> {
      console.setText(ECCrypt.writeSignatureToFile(passText.getText(), inText.getText()));
    });

    toBeVerifiedButton.addActionListener(event -> {
      File inPath = UtilGui.fileBrowse();
      if (inPath != null)
        toBeVerifiedPathText.setText(inPath.getPath());
    });

    signatureButton.addActionListener(event -> {
      File inPath = UtilGui.fileBrowse();
      if (inPath != null)
        signaturePathText.setText(inPath.getPath());
    });

    publicKeyButton.addActionListener(event -> {
      File inPath = UtilGui.fileBrowse();
      if (inPath != null)
        publicKeyPathText.setText(inPath.getPath());
    });

    verifyButton.addActionListener(event -> {
      console.setText(ECCrypt.verify_signature(toBeVerifiedPathText.getText(), signaturePathText.getText(),
          publicKeyPathText.getText()));
    });

    inText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
    inText.setMaximumSize(inText.getPreferredSize());
    inText.setBackground(Color.ORANGE);
    inText.setEditable(false);

    passLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);

    passText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
    passText.setMaximumSize(passText.getPreferredSize());

    toBeVerifiedPathText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
    toBeVerifiedPathText.setMaximumSize(toBeVerifiedPathText.getPreferredSize());
    toBeVerifiedPathText.setBackground(Color.ORANGE);
    toBeVerifiedPathText.setEditable(false);

    signaturePathText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
    signaturePathText.setMaximumSize(signaturePathText.getPreferredSize());
    signaturePathText.setBackground(Color.ORANGE);
    signaturePathText.setEditable(false);

    publicKeyPathText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
    publicKeyPathText.setMaximumSize(publicKeyPathText.getPreferredSize());
    publicKeyPathText.setBackground(Color.ORANGE);
    publicKeyPathText.setEditable(false);

    this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
    this.add(optionOne);
    this.add(passLabel);
    this.add(passText);
    this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
    this.add(finButton);
    this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
    this.add(inText);

    this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
    this.add(genButton);
    this.add(Box.createRigidArea(new Dimension(0, 30))); // Add space

    this.add(optionTwo);
    this.add(toBeVerifiedButton);
    this.add(toBeVerifiedPathText);
    this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
    this.add(signatureButton);
    this.add(signaturePathText);
    this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
    this.add(publicKeyButton);
    this.add(publicKeyPathText);
    this.add(verifyButton);

  }

}
