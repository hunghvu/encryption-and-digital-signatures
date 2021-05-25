// package gui;

// import javax.swing.BoxLayout;
// import javax.swing.JButton;
// import javax.swing.JLabel;
// import javax.swing.JPanel;
// import javax.swing.JScrollPane;
// import javax.swing.JTextArea;
// import javax.swing.UIManager;

// public class ECSignaturePanel extends JPanel{

//   private static final JLabel LABEL_OPTION_2 = new JLabel("1. Enter passphrase.");
//   private static final JTextArea TEXT_OPTION_2 = new JTextArea();
//   private static final JScrollPane SCROLL_OPTION_2 = new JScrollPane(TEXT_OPTION_2);
//   private static final JLabel LABEL_OPTION_1 = new JLabel("2. Choose a file and get its signature.");
//   private static final JButton BUTTON_OPTION_1 = new JButton("Generate signature");


//   private static final JLabel LABEL_EMPTY_3 = new JLabel(" ");
//   private static final JLabel LABEL_OPTION_3 = new JLabel(
//       "3. Get MAC of a chosen file. Enter MAC passphrase below (empty field is an empty passphrase)");
//   private static final JTextArea TEXT_OPTION_3 = new JTextArea();
//   private static final JScrollPane SCROLL_OPTION_3 = new JScrollPane(TEXT_OPTION_3);
//   private static final JButton BUTTON_OPTION_3 = new JButton("MAC - file");

//   private static final JLabel LABEL_EMPTY_4 = new JLabel(" ");

//   public ECSignaturePanel(JTextArea console) {
//     this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
//     TEXT_OPTION_2.setLineWrap(true);
//     TEXT_OPTION_3.setLineWrap(true);

//     try {
//       UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//     } catch (Exception e) {
//       System.out.println(e);
//     }

//     addButtonBehavior(console);

//     this.add(LABEL);

//     this.add(LABEL_EMPTY_1);
//     this.add(LABEL_OPTION_1);
//     this.add(BUTTON_OPTION_1);

//     this.add(LABEL_EMPTY_2);
//     this.add(LABEL_OPTION_2);
//     this.add(SCROLL_OPTION_2);
//     this.add(BUTTON_OPTION_2);

//     this.add(LABEL_EMPTY_3);
//     this.add(LABEL_OPTION_3);
//     this.add(SCROLL_OPTION_3);
//     this.add(BUTTON_OPTION_3);

//     this.add(LABEL_EMPTY_4);

//     this.setVisible(true);

//   }

//   private void addButtonBehavior(JTextArea console) {
//     BUTTON_OPTION_1.addActionListener(event -> {
//       final File directory = UtilGui.actionBrowse();
//       String outval = directory == null ? "Error: You must chose a file!"
//           : "SHA3 of your file is: " + KCrypt.get_sha3_file(directory);
//       console.setText(outval);
//     });
//     BUTTON_OPTION_2.addActionListener(event -> {
//       final String m = TEXT_OPTION_2.getText();
//       console.setText("SHA3 of your text input is: " + KCrypt.get_sha3_text(m));
//     });
//     BUTTON_OPTION_3.addActionListener(event -> {
//       final File directory = UtilGui.actionBrowse();
//       String passphrase = TEXT_OPTION_3.getText();
//       String outval = directory == null ? "Error: You must chose a file!"
//           : "MAC of your file is: " + KCrypt.get_mac_file(directory, passphrase);
//       console.setText(outval);

//     });
//   }
// }
