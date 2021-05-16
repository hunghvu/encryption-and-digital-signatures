package gui;

/**
 *   This is the panel for console, all tab should use the same console for better UX.
 */
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class PanelConsole extends JPanel{
  private static final JLabel LABEL_OUTPUT = new JLabel("Console (for output)");
  private static final JTextArea TEXT_OUTPUT = new JTextArea();
  private static final JScrollPane SCROLL_OUTPUT = new JScrollPane(TEXT_OUTPUT);
  public PanelConsole (){
    TEXT_OUTPUT.setEditable(false);
    TEXT_OUTPUT.setLineWrap(true);

    this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    this.add(LABEL_OUTPUT);
    this.add(SCROLL_OUTPUT);
  }

  public static JTextArea getConsoleElement() {
    return TEXT_OUTPUT;
  }
}
