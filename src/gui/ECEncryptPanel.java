/**
 * This provides a panel for public key encryption of the application
 * @author Duy Nguyen
 */
package gui;

import ec.ECCrypt;
import ec.ECKeyPair;
import ec.ECPoint;
import util.UtilGui;
import util.UtilMethods;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class ECEncryptPanel extends JPanel {

    class CardPanel extends JPanel {
        private String name;

        CardPanel(String name) {
            super();
            this.name = name;
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /** Input file button. */
    private static final JButton finButton = new JButton("Select File for Encryption");

    /** A component to show the input path. */
    private static final JTextField inText = new JTextField(UtilGui.X_AXIS - 10);

    /** Out path button. */
    private static final JButton foutButton = new JButton("Select Output Path Folder");

    /** A component to show the output path. */
    private static final JTextField outText = new JTextField(UtilGui.X_AXIS - 10);

    /** private key file path request button. */
    private static final JButton pfButton = new JButton("Select public Key File Path");

    /** A component to show the private key file path. */
    private static final JTextField pfText = new JTextField(UtilGui.X_AXIS - 10);

    /** Decrypt text input prompt label. */
    private static final JLabel inLabel = new JLabel("Put decrypt text input here:");

    /** Decrypt button. */
    private static final JButton decryptButton = new JButton("Encrypt");

    /** An to type direct text input for decryption. */
    private static final JTextArea inArea = new JTextArea();
    
    /** A tick box to show optional private key file feature. */
    private static final JCheckBox tickBox = new JCheckBox(
            "Tick this box if you want to generate signature file as well!");

    /** A boolean value indicating the status of the tick box. */
    private static boolean optionalFlag = false;
    
    /** Passphrase request label. */
    private static final JLabel passLabel2 = new JLabel(
        "Put passphrase to generate signature here:");

    /** A field to type passphrase. */
    private static final JTextField passText2 = new JTextField(UtilGui.X_AXIS - 10);

    private static final JPanel inCards = new JPanel(new CardLayout());
    private static final JPanel skCards = new JPanel(new CardLayout());
    private static final JComboBox combo1 = new JComboBox();
    private final CardPanel inCard1 = new CardPanel("1. Encrypt a data file");
    private final CardPanel inCard2 = new CardPanel(
            "2. Encrypt direct text input to the app");
    private final CardPanel skCard1 = new CardPanel(
            "1. Get public key via file and its password");
    
    

    /**
     * Construct new panel for decryption scheme based on KMACXOF256.
     */
    public ECEncryptPanel(JTextArea console) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        initialize(console); // Set up components
        setVisible(true);
    }

    /**
     * Add components and their functionalities for the panel
     */
    private void initialize(JTextArea console) {

        inLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        finButton.setAlignmentX(JButton.LEFT_ALIGNMENT);
        finButton.addActionListener(event -> {
            File inPath = UtilGui.fileBrowse();
            if (inPath != null)
                inText.setText(inPath.getPath());
        });

        pfButton.setAlignmentX(JButton.LEFT_ALIGNMENT);
        pfButton.addActionListener(event -> {
            File inPath = UtilGui.fileBrowse();
            if (inPath != null)
                pfText.setText(inPath.getPath());
        });

        inText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
        inText.setMaximumSize(inText.getPreferredSize());
        inText.setBackground(Color.ORANGE);
        inText.setEditable(false);

        pfText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
        pfText.setMaximumSize(inText.getPreferredSize());
        pfText.setBackground(Color.YELLOW);
        pfText.setEditable(false);

        foutButton.addActionListener(event -> {
            File outPath = UtilGui.pathBrowse();
            if (outPath != null)
                outText.setText(outPath.getPath());
        });
        foutButton.setAlignmentX(JButton.LEFT_ALIGNMENT);

        outText.setMaximumSize(outText.getPreferredSize());
        outText.setBackground(Color.ORANGE);
        outText.setEditable(false);
        outText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
        
        tickBox.addActionListener(event -> {
            optionalFlag ^= true;
            passLabel2.setVisible(optionalFlag);
            passText2.setVisible(optionalFlag);
        });
        tickBox.setAlignmentX(JCheckBox.LEFT_ALIGNMENT);
        
        passLabel2.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        passText2.setAlignmentX(JTextField.LEFT_ALIGNMENT);
        passText2.setMaximumSize(passText2.getPreferredSize());
        passLabel2.setVisible(optionalFlag);
        passText2.setVisible(optionalFlag);

        decryptButton.addActionListener(event -> {
            String outputPath;
            byte[] enc;
            ECPoint key;
            if (combo1.getSelectedIndex() == 0) {
                outputPath = UtilGui.createEncOutPath(inText.getText(), outText.getText());
                enc = UtilMethods.readFileBytes(inText.getText());
                if (enc == null) {
                    console.setText("Error occurred while reading file.");
                    return;
                }
            } else {
                outputPath = outText.getText() + "\\ECDecrypted";
                enc = UtilMethods.hexToBytes(inArea.getText());
                if (enc == null) {
                    console.setText(
                            "Invalid text input. Please make sure its length is even, and it does not have any invalid hex character.");
                    return;
                }
            }

            key = ECKeyPair.readPubKeyFile(pfText.getText());
            if (key == null) {
                console.setText("Invalid public key file path.");
                return;
            }

            try {
                console.setText(ECCrypt.encryptFile(enc, key, outputPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            if (optionalFlag)
                console.append("\n" + ECCrypt.writeSignatureToFile(passText2.getText(), outputPath + ECCrypt.encFileFormat));
        });

        inArea.setAlignmentX(JTextArea.LEFT_ALIGNMENT);

        this.add(foutButton);
        this.add(outText);
        this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space

        inCard1.add(finButton);
        inCard1.add(inText);
        inCard1.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
        inCard1.add(Box.createRigidArea(new Dimension(0, 10))); // Add space

        inCard2.add(inLabel);
        inCard2.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
        inCard2.add(inArea);
        inCard2.add(Box.createRigidArea(new Dimension(0, 10))); // Add space

        skCard1.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
        skCard1.add(pfButton);
        skCard1.add(pfText);
        skCard1.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
        skCard1.add(Box.createRigidArea(new Dimension(0, 10))); // Add space

        combo1.addItem(inCard1);
        inCards.add(inCard1, inCard1.toString());
        combo1.addItem(inCard2);
        inCards.add(inCard2, inCard2.toString());
        inCards.setAlignmentX(Component.LEFT_ALIGNMENT);

        skCards.add(skCard1, skCard1.toString());
        skCards.setAlignmentX(Component.LEFT_ALIGNMENT);

        combo1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox jcb = (JComboBox) e.getSource();
                CardLayout cl = (CardLayout) inCards.getLayout();
                cl.show(inCards, jcb.getSelectedItem().toString());
            }
        });
        combo1.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.add(combo1);
        this.add(inCards);
        this.add(skCards);
        this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
        this.add(tickBox);
        this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
        this.add(passLabel2);
        this.add(passText2);
        this.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
        this.add(decryptButton);

    }

}
