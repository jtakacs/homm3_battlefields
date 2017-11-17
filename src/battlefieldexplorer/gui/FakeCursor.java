package battlefieldexplorer.gui;

import static java.awt.Toolkit.getDefaultToolkit;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static javax.swing.JLayeredPane.DRAG_LAYER;
import static javax.swing.SwingUtilities.convertMouseEvent;
import static javax.swing.SwingUtilities.invokeAndWait;
import java.awt.AWTEvent;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;

public class FakeCursor extends JLabel {

  private static final ImageIcon sword = new ImageIcon(FakeCursor.class.getResource("/cursor/cursors_205.png"));
  private static final ImageIcon horse = new ImageIcon(FakeCursor.class.getResource("/cursor/cursors_209.png"));
  private static final ImageIcon ship = new ImageIcon(FakeCursor.class.getResource("/cursor/cursors_228.png"));
  private static final long serialVersionUID = 1L;
  private final ImageIcon[] spell = new ImageIcon[20];
  private int x = -100;
  private int y = -100;
  private int hx = 0;
  private int hy = 0;
  private final Object LOCK = new Object();
  private volatile boolean animate = false;
  private final FakeCursor cursor;
  private final AtomicInteger count = new AtomicInteger(0);

  public FakeCursor(final JFrame rootFrame) {
    cursor = this;
    init();
    for (int i = 0; i < 20; i++) {
      spell[i] = new ImageIcon(FakeCursor.class.getResource("/cursor/spell/Crspl" + (i < 10 ? "0" : "") + i + ".png"));
    }
    final BufferedImage bufferedImage = new BufferedImage(1, 1, TYPE_INT_ARGB);
    bufferedImage.setRGB(0, 0, 0);
    rootFrame.getRootPane().setCursor(getDefaultToolkit().createCustomCursor(bufferedImage, new Point(0, 0), "hack"));
    rootFrame.getRootPane().getLayeredPane().add(this, DRAG_LAYER);
    getDefaultToolkit().addAWTEventListener(event -> {
      if (event instanceof MouseEvent) {
        MouseEvent me = (MouseEvent) event;
        MouseEvent me2 = convertMouseEvent(me.getComponent(), me, rootFrame);
        this.setPos(me2.getX(), me2.getY());
      }
    }, AWTEvent.MOUSE_MOTION_EVENT_MASK);
    new Thread(() -> {
      try {
        while (true) {
          synchronized (LOCK) {
            LOCK.wait();
          }
          while (animate) {
            invokeAndWait(() -> cursor.setIcon(spell[count.getAndAccumulate(1, (a, b) -> (a + b >= 20) ? 0 : (a + b))]));
            try {
              Thread.sleep(50);
            } catch (Exception ex) {
            }
          }
        }
      } catch (Exception e) {
      }
    }).start();;
  }

  private final void init() {
    setAutoscrolls(false);
    setHorizontalAlignment(LEFT);
    setVerticalAlignment(TOP);
    enableInputMethods(false);
    setFocusable(false);
    setOpaque(false);
    setImg(0);
  }

  public final void setPos(final int x, final int y) {
    this.x = x;
    this.y = y;
    setBounds(x - 5 - hx, y - 30 - hy, 40, 40);
  }

  public final void setImg(final int n) {
    switch (n) {
      case 1:
        setIcon(horse);
        hx = 10;
        hy = 16;
        break;
      case 2:
        setIcon(ship);
        hx = 10;
        hy = 25;
        break;
      case 3:
        hx = 15;
        hy = 20;
        animate = true;
        synchronized (LOCK) {
          LOCK.notify();
        }
        break;
      case 0:
      default:
        animate = false;
        synchronized (LOCK) {
          LOCK.notify();
        }
        setIcon(sword);
        hx = 6;
        hy = 6;
    }
  }

}
