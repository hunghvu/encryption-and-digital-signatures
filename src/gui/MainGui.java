package gui;// A simple GUI to retrieve a File
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class MainGui {

    private static final JTabbedPane TABBED_PANE = new JTabbedPane();
    private static final JPanel PANEL_CONSOLE = new PanelConsole();

    public static void main(String[] args) {

        TABBED_PANE.addTab("Part 1",new PanelPartOne(PanelConsole.getConsoleElement()));
        TABBED_PANE.setMnemonicAt(0, 1);
        
        /**
         * Add a panel for authenticated decryption scheme based on KMAC.
         */
        TABBED_PANE.addTab("KMAC Decrypt",new KDecryptPanel());
        TABBED_PANE.setMnemonicAt(0, 1);

        TABBED_PANE.addTab("Part 2 - encryption/decryption", new JPanel() /* Change this panel to a proper one */);
        TABBED_PANE.setMnemonicAt(0, 2);

        TABBED_PANE.addTab("Part 2 - signature", new JPanel() /* Change this panel to a proper one */);
        TABBED_PANE.setMnemonicAt(0, 3);


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final JFrame frame = new JFrame();
                frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
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
                frame.setSize(700, 600);
                frame.add(TABBED_PANE);
                frame.add(PANEL_CONSOLE);
                frame.setVisible(true);
            }
        });       
    }

}
