/**
 * This provides a panel for KMAC decryption functionality of the application
 * @author Duy Nguyen
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

import kmac.KCrypt;
import util.UtilGui;

public class KDecryptPanel extends JPanel {

	/** Input file button. */
	private static final JButton finButton = new JButton("Select Encrypted File Path");

	/** A component to show the input path. */
	private static final JTextField inText = new JTextField(UtilGui.X_AXIS - 10);

	/** Out path button. */
	private static final JButton foutButton = new JButton("Select Output Path Folder");

	/** A component to show the output path. */
	private static final JTextField outText = new JTextField(UtilGui.X_AXIS - 10);

	/** Passphrase request label. */
	private static final JLabel passLabel = new JLabel("Put passphrase here:");

	/** A field to type passpharse. */
	private static final JTextField passText = new JTextField(UtilGui.X_AXIS - 10);

	/** Decrypt button. */
	private static final JButton decryptButton = new JButton("Decrypt");

	/**
	 * Construct new panel for decryption scheme based on KMACXOF256.
	 */
	public KDecryptPanel(JTextArea console) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		initialize(console); // Set up components
		setVisible(true);
	}

	/**
	 * Add components and their functionalities for the panel
	 */
	private void initialize(JTextArea console) {
		finButton.setAlignmentX(JButton.LEFT_ALIGNMENT);
		finButton.addActionListener(event -> {
			File inPath = UtilGui.fileBrowse();
			if (inPath != null)
				inText.setText(inPath.getPath());
		});

		inText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
		inText.setMaximumSize(inText.getPreferredSize());
		inText.setBackground(Color.ORANGE);
		inText.setEditable(false);

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

		passLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);

		passText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
		passText.setMaximumSize(passText.getPreferredSize());

		decryptButton.addActionListener(event -> {
			String outputPath = UtilGui.createDecOutPath(inText.getText(), outText.getText());
			console.setText(KCrypt.decryptFile(inText.getText(), passText.getText(), outputPath));
		});

		this.add(finButton);
		this.add(inText);
		this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
		this.add(foutButton);
		this.add(outText);
		this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
		this.add(passLabel);
		this.add(passText);
		this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
		this.add(decryptButton);
		this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space

	}

}
