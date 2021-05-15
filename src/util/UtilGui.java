import java.io.File;

import javax.swing.JFileChooser;

public class UtilGui {
  
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
}
