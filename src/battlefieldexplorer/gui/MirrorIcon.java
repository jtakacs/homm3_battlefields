package battlefieldexplorer.gui;

import java.awt.*;
import java.net.URL;
import javax.swing.ImageIcon;

@SuppressWarnings("serial")
public class MirrorIcon extends ImageIcon{

  private static final long serialVersionUID = 1L;

  public MirrorIcon(String filename, String description) {
    super(filename, description);
  }

  public MirrorIcon(String filename) {
    super(filename);
  }

  public MirrorIcon(URL location, String description) {
    super(location, description);
  }

  public MirrorIcon(URL location) {
    super(location);
  }

  public MirrorIcon(Image image, String description) {
    super(image, description);
  }

  public MirrorIcon(Image image) {
    super(image);
  }

  public MirrorIcon(byte[] imageData, String description) {
    super(imageData, description);
  }

  public MirrorIcon(byte[] imageData) {
    super(imageData);
  }

  public MirrorIcon() {
  }
  
  @Override
  public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D g2 = (Graphics2D)g.create();
        g2.translate(getIconWidth(), 0);
        g2.scale(-1, 1);
    super.paintIcon(c, g2, x, y);
     }

}
