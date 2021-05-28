package gui;// A simple GUI to retrieve a File
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import util.UtilGui;

public class MainGui {

    private static final JTabbedPane TABBED_PANE = new JTabbedPane();
    private static final JPanel PANEL_CONSOLE = new PanelConsole();

    public static void main(String[] args) {

        TABBED_PANE.addTab("KMAC Hashing",new PanelPartOne(PanelConsole.getConsoleElement()));     
        TABBED_PANE.addTab("KMAC Encrypt", new KEncryptPanel(PanelConsole.getConsoleElement()));
        TABBED_PANE.addTab("KMAC Decrypt",new KDecryptPanel(PanelConsole.getConsoleElement()));
        TABBED_PANE.addTab("EC Key Gen",new ECKeyGenPanel(PanelConsole.getConsoleElement()));
        TABBED_PANE.addTab("EC Encryption", new JPanel() /* Change this panel to a proper one */);       
        TABBED_PANE.addTab("EC Decryption",new ECDecryptPanel(PanelConsole.getConsoleElement()));
        TABBED_PANE.addTab("EC Signature", new JPanel() /* Change this panel to a proper one */);


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
                frame.setSize(UtilGui.X_AXIS, UtilGui.Y_AXIS);
                frame.add(TABBED_PANE);
                frame.add(PANEL_CONSOLE);
                frame.setVisible(true);
            }
        });       
    }

}
