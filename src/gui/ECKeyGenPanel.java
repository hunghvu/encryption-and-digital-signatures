/**
 * This provides a panel for keypair generator functionality of the application
 * @author Phong Le
 */
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

public class ECKeyGenPanel extends JPanel {

    /** Out path button. */
    private static final JButton foutButton = new JButton("Select Output Path Folder");

    /** A component to show the output path. */
    private static final JTextField outText = new JTextField(UtilGui.X_AXIS - 10);

    /** Passphrase request label. */
    private static final JLabel passLabel = new JLabel("Put passphrase to generate key pair here:");

    /** A field to type passphrase. */
    private static final JTextField passText = new JTextField(UtilGui.X_AXIS - 10);

    /** A tick box to show optional private key file feature. */
    private static final JCheckBox tickBox = new JCheckBox(
            "Tick this box if you want to generate " + "private key file as well!");

    /** A boolean value indicating the status of the tick box. */
    private static boolean optionalFlag = false;

    /** Private key password request label. */
    private static final JLabel passLabel2 = new JLabel("Put password to write private key file here:");

    /** A field to type private key password. */
    private static final JTextField passText2 = new JTextField(UtilGui.X_AXIS - 10);

    /** Generate button. */
    private static final JButton genButton = new JButton("Generate");

    /**
     * Construct new panel for decryption scheme based on KMACXOF256.
     */
    public ECKeyGenPanel(JTextArea console) {
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

        passLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        passLabel2.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        passText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
        passText.setMaximumSize(passText.getPreferredSize());

        passText2.setAlignmentX(JTextField.LEFT_ALIGNMENT);
        passText2.setMaximumSize(passText2.getPreferredSize());

        passLabel2.setVisible(optionalFlag);
        passText2.setVisible(optionalFlag);

        tickBox.addActionListener(event -> {
            optionalFlag ^= true;
            passLabel2.setVisible(optionalFlag);
            passText2.setVisible(optionalFlag);
        });
        tickBox.setAlignmentX(JCheckBox.LEFT_ALIGNMENT);

        genButton.addActionListener(event -> {
            ECKeyPair key = new ECKeyPair(passText.getText());
            console.setText(key.writePubToFile(outText.getText()));
            if (optionalFlag)
                console.append("\n" + key.writePrvToFile(passText2.getText(), outText.getText()));
        });

        this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
        this.add(foutButton);
        this.add(outText);
        this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
        this.add(passLabel);
        this.add(passText);
        this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
        this.add(tickBox);
        this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
        this.add(passLabel2);
        this.add(passText2);
        this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
        this.add(genButton);
        this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space

    }

}
