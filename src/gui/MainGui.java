// A simple GUI to retrieve a File
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MainGui {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final JFrame frame = new JFrame();
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
                frame.setSize(700, 400);
                frame.add(new PanelPartOne());
                frame.setVisible(true);
            }
        });       
    }

}
