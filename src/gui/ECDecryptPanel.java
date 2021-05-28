package gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ec.ECCrypt;
import ec.ECKeyPair;
import util.UtilGui;
import util.UtilMethods;

public class ECDecryptPanel extends JPanel {
	
	class CardPanel extends JPanel {
		private String name;
		
		CardPanel(String name){
			super();
			this.name = name;
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		}
		
		@Override
	    public String toString() {
	        return name;
	    }	
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2529286463705430968L;

	/** Input file button. */
	private static final JButton finButton = new JButton("Select Encrypted File Path");
	
	/** A component to show the input path. */
	private static final JTextField inText = new JTextField(UtilGui.X_AXIS - 10);
	
	/** Out path button. */
	private static final JButton foutButton = new JButton("Select Output Path Folder");
	
	/** A component to show the output path. */
	private static final JTextField outText = new JTextField(UtilGui.X_AXIS - 10);
	
	/** private key file path request button. */
	private static final JButton sfButton = new JButton("Select private Key File Path");
	
	/** A component to show the private key file path. */
	private static final JTextField sfText = new JTextField(UtilGui.X_AXIS - 10);
	
	/** Decrypt text input prompt label. */
	private static final JLabel inLabel = new JLabel("Put decrypt text input here:");
	
	/** Passphrase request label. */
	private static final JLabel passLabel = new JLabel("Put passphrase for private key here:");
	
	/** privatekey password request label. */
	private static final JLabel passLabel2 = new JLabel("Put password for private key file here:");
	
	/** A field to type passpharse. */
	private static final JTextField passText = new JTextField(UtilGui.X_AXIS - 10);
	
	/** A field to type password for private key file. */
	private static final JTextField passText2 = new JTextField(UtilGui.X_AXIS - 10);
	
	/** Decrypt button. */
	private static final JButton decryptButton = new JButton("Decrypt");
	
	/** An to type direct text input for decryption. */
	private static final JTextArea inArea = new JTextArea();
	
	private static final JPanel inCards = new JPanel(new CardLayout());
	private static final JPanel skCards = new JPanel(new CardLayout());
    private static final JComboBox combo1 = new JComboBox();
    private static final JComboBox combo2 = new JComboBox();
    private final CardPanel inCard1 = new CardPanel("1. Decrypt a data file");
    private final CardPanel inCard2 = new CardPanel("2. Decrypt direct text input to the app");
    private final CardPanel skCard1 = new CardPanel("1. Get private key via passphrase");
    private final CardPanel skCard2 = new CardPanel("2. Get private key via file and its password");
    
	
	
	/**
	 * Construct new panel for decryption scheme based on KMACXOF256.
	 */
	public ECDecryptPanel(JTextArea console) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		initialize(console); // Set up components
		setVisible(true);
	}
	
	/**
	 * Add components and their functionalities for the panel
	 */
	private void initialize(JTextArea console) {
		
		passLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		passLabel2.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		inLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		
		finButton.setAlignmentX(JButton.LEFT_ALIGNMENT);
		finButton.addActionListener(event -> {
			File inPath = UtilGui.actionBrowse();
			if (inPath != null) inText.setText(inPath.getPath());
		});
		
		sfButton.setAlignmentX(JButton.LEFT_ALIGNMENT);
		sfButton.addActionListener(event -> {
			File inPath = UtilGui.actionBrowse();
			if (inPath != null) sfText.setText(inPath.getPath());
		});
			
		inText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
		inText.setMaximumSize(inText.getPreferredSize());
		inText.setBackground(Color.ORANGE);
		inText.setEditable(false);
		
		sfText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
		sfText.setMaximumSize(inText.getPreferredSize());
		sfText.setBackground(Color.YELLOW);
		sfText.setEditable(false);
		
		foutButton.addActionListener(event -> {
			File outPath = UtilGui.pathBrowse();
			if (outPath != null) outText.setText(outPath.getPath());
		});
		foutButton.setAlignmentX(JButton.LEFT_ALIGNMENT);
		
		outText.setMaximumSize(outText.getPreferredSize());
		outText.setBackground(Color.ORANGE);
		outText.setEditable(false);
		outText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
		
		passText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
		passText.setMaximumSize(passText.getPreferredSize());
		
		passText2.setAlignmentX(JTextField.LEFT_ALIGNMENT);
		passText2.setMaximumSize(passText.getPreferredSize());
		
		decryptButton.addActionListener(event -> {
			String outputPath;
			byte[] enc;
			ECKeyPair key;
			if (combo1.getSelectedIndex() == 0) {
				outputPath = UtilGui.createDecOutPath(inText.getText(), outText.getText());
				enc = UtilMethods.readFileBytes(inText.getText());
				if (enc == null) {
					console.setText("Error occurred while reading file.");
					return;
				}
			} else {
				outputPath = outText.getText() + "\\ECDecrypted";
				enc = UtilMethods.hexToBytes(inArea.getText());
				if (enc == null) {
					console.setText("Invalid text input. Please make sure its length is even, and it does not have any invalid hex character.");
					return;
				}
			}
			if (combo2.getSelectedIndex() == 0) {
				key = new ECKeyPair(passText.getText());
			} else {
				key = ECKeyPair.readPrivateKeyFile(sfText.getText(), passText2.getText());
				if (key == null) {
					console.setText("Invalid key file path or password.");
					return;
				}
			}
			console.setText(ECCrypt.decryptToFile(enc, key.getPrivateKey(), outputPath));
		});
		
		inArea.setAlignmentX(JTextArea.LEFT_ALIGNMENT);
		
		this.add(foutButton);
		this.add(outText);
		this.add(Box.createRigidArea(new Dimension(0,10))); // Add space

		inCard1.add(finButton);
		inCard1.add(inText);
		inCard1.add(Box.createRigidArea(new Dimension(0,10))); // Add space
		inCard1.add(Box.createRigidArea(new Dimension(0,10))); // Add space
		
		inCard2.add(inLabel);
		inCard2.add(Box.createRigidArea(new Dimension(0,10))); // Add space
		inCard2.add(inArea);
		inCard2.add(Box.createRigidArea(new Dimension(0,10))); // Add space
		
		skCard1.add(Box.createRigidArea(new Dimension(0,10))); // Add space
		skCard1.add(passLabel);
		skCard1.add(passText);
		skCard1.add(Box.createRigidArea(new Dimension(0,10))); // Add space
		
		skCard2.add(Box.createRigidArea(new Dimension(0,10))); // Add space
		skCard2.add(sfButton);
		skCard2.add(sfText);
		skCard2.add(Box.createRigidArea(new Dimension(0,10))); // Add space
		skCard2.add(passLabel2);
		skCard2.add(passText2);
		skCard2.add(Box.createRigidArea(new Dimension(0,10))); // Add space
		
		combo1.addItem(inCard1);
		inCards.add(inCard1, inCard1.toString());
		combo1.addItem(inCard2);
		inCards.add(inCard2, inCard2.toString());
		inCards.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		combo2.addItem(skCard1);
		skCards.add(skCard1, skCard1.toString());
		combo2.addItem(skCard2);
		skCards.add(skCard2, skCard2.toString());
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
		
		combo2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox jcb = (JComboBox) e.getSource();
                CardLayout cl = (CardLayout) skCards.getLayout();
                cl.show(skCards, jcb.getSelectedItem().toString());
            }
        });
		combo2.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		this.add(combo1);
		this.add(inCards);
		this.add(combo2);
		this.add(skCards);
		this.add(decryptButton);

	}

}
