package util;

import java.io.File;

import javax.swing.JFileChooser;

public class UtilGui {
	
	/** Static X-axis value. */
	public static final int X_AXIS = 700;
	
	/** Static Y-axis value. */
	public static final int Y_AXIS = 600;
  
    // Open a file chooser
    public static File actionBrowse() {
      final JFileChooser MYF_FILE_CHOOSER = new JFileChooser();
      MYF_FILE_CHOOSER.setFileSelectionMode(JFileChooser.FILES_ONLY);
      int choice = MYF_FILE_CHOOSER.showOpenDialog(null);

      File directory = null;
      if (choice == JFileChooser.APPROVE_OPTION) {
          directory = MYF_FILE_CHOOSER.getSelectedFile();
      } 
      return directory;
    }
    
    /**
     * Browse and get a directory.
     * @return ouput folder directoryu
     */
    public static File pathBrowse() {
        final JFileChooser MYF_FILE_CHOOSER = new JFileChooser();
        MYF_FILE_CHOOSER.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int choice = MYF_FILE_CHOOSER.showOpenDialog(null);

        File directory = null;
        if (choice == JFileChooser.APPROVE_OPTION) {
            directory = MYF_FILE_CHOOSER.getSelectedFile();
        } 
        return directory;
      }
}
