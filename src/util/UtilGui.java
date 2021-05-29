/**
 * Provide utility methods. This methods are more on the frontend of an application
 * 
 * @author Phong Hoang Le
 *
 */
package util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFileChooser;

public class UtilGui {

  /** Static X-axis value. */
  public static final int X_AXIS = 900;

  /** Static Y-axis value. */
  public static final int Y_AXIS = 600;

  /**
   * Browse and get a file
   * 
   * @return a file path
   * @author Hung Vu
   */
  public static File fileBrowse() {
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
   * 
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

  /**
   * Create output path for encryption.
   * 
   * @param inURL     input file url
   * @param outFolder output folder
   * @return output path url
   */
  public static String createEncOutPath(String inURL, String outFolder) {
    Path inPath = Paths.get(inURL);
    Path outPath = Paths.get(outFolder);
    return outPath + File.separator + inPath.getFileName();
  }

  /**
   * Create output path for decryption.
   * 
   * @param inURL     input file url
   * @param outFolder output folder
   * @return output path url
   */
  public static String createDecOutPath(String inURL, String outFolder) {
    Path inPath = Paths.get(inURL);
    String fileName = inPath.getFileName().toString();
    String fileNameWithoutExtn = fileName.substring(0, fileName.lastIndexOf('.'));
    return outFolder + File.separator + fileNameWithoutExtn;
  }
}
