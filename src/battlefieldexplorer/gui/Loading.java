package battlefieldexplorer.gui;

import java.awt.Component;
import java.util.*;
import javax.swing.*;

public class Loading {

  private final Object LOCK = new Object();
  private final List<ImageIcon> frames = new ArrayList<>();
  private volatile boolean animate;
  private final JPanel panel;
  private final int w;
  private final int h;

  public Loading(final JPanel panel) {
    this.panel = panel;
    Arrays.asList(
            "Sp01_01.png",
            "Sp01_02.png",
            "Sp01_03.png",
            "Sp01_04.png",
            "Sp01_05.png",
            "Sp01_06.png",
            "Sp01_07.png",
            "Sp01_08.png",
            "Sp01_09.png",
            "Sp01_10.png",
            "Sp01_11.png",
            "Sp01_12.png",
            "Sp01_13.png",
            "Sp01_14.png",
            "Sp01_15.png",
            "Sp01_16.png",
            "Sp01_17.png",
            "Sp01_18.png",
            "Sp01_19.png",
            "Sp01_20.png")
            .forEach(icon -> frames.add(new ImageIcon(Loading.class.getResource("/loading/" + icon))));
    w = frames.get(0).getIconWidth();
    h = frames.get(0).getIconHeight();
    addToPanel();
  }

  private final void addToPanel() {
    for (int i = 0; i < frames.size(); i++) {
      final JLabel jLabel1 = new JLabel(frames.get(i));
      panel.add(jLabel1);
      jLabel1.setBounds((panel.getWidth() - w) / 2, (panel.getHeight() - h) / 2, w, h);
      jLabel1.setVisible(false);
    }
    new Thread(this::drawFrames).start();
  }

  public void start() {
    synchronized (LOCK) {
      animate = true;
      panel.setVisible(animate);
      LOCK.notifyAll();
    }
  }

  public void stop() {
    synchronized (LOCK) {
      animate = false;
      panel.setVisible(animate);
      LOCK.notifyAll();
    }
  }

  private void drawFrames() {
    try {
      while (true) {
        synchronized (LOCK) {
          LOCK.wait();
        }
        int count = 0;
        while (animate) {
          final Component[] c = panel.getComponents();
          for (int i = 0; i < c.length; i++) {
            c[i].setVisible(i == count);
          }
          count++;
          if (count >= c.length) {
            count = 0;
          }
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
          }
        }
      }
    } catch (InterruptedException ie) {
    }
  }
}
