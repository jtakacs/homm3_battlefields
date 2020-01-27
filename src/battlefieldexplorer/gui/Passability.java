package battlefieldexplorer.gui;

import static battlefieldexplorer.generator.ObstacleInfo.obstacleInfo;
import static battlefieldexplorer.util.Constants.*;
import static battlefieldexplorer.util.HexTools.isOddRow;
import static battlefieldexplorer.util.HexTools.posToHex;
import static javax.swing.SwingConstants.CENTER;
import battlefieldexplorer.generator.Battlefield;
import battlefieldexplorer.generator.Obstacle;
import battlefieldexplorer.generator.PositionedObstacle;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Set;
import javax.swing.*;

public class Passability {

  public static void createOverlay(final JComponent passabilityLayer, final Battlefield bf) {
    passabilityLayer.removeAll();
    Set<Integer> obstacles = bf.getBlockedHexes();
    for (int y = 0; y < BFIELD_HEIGHT; y++) {
      for (int x = 1; x < BFIELD_WIDTH - 1; x++) {
        if (obstacles.contains(posToHex(x, y))) {
          final int screenX = canvasWoffset + x * (cellW - 1) + (isOddRow(y) ? 0 : (cellW / 2));
          final int screenY = canvasHoffset + y * (cellH + cellHoffset + 2);
          PositionedObstacle po = bf.getObstacleAt(x, y);
          createHex(passabilityLayer, screenX, screenY, po.obstacle.image);
        }
      }
    }
    passabilityLayer.updateUI();
  }

  private static void createHex(final JComponent layer, final int screenX, final int screenY, final String name) {
    final Dimension size = new Dimension(cellW, cellH);
    final JLabel L = new JLabel();
    L.setSize(size);
    L.setMinimumSize(size);
    L.setPreferredSize(size);
    L.setMaximumSize(new Dimension(cellW + 8, cellH));
    L.setBackground(new java.awt.Color(255, 255, 255, 0));
    L.setDoubleBuffered(true);
    L.setBorder(null);
    L.setOpaque(true);
    L.setHorizontalTextPosition(CENTER);
    L.setHorizontalAlignment(CENTER);
    L.setVerticalTextPosition(CENTER);
    L.setIconTextGap(0);
//TODO image caching
    BufferedImage icon = new BufferedImage(45, 52, BufferedImage.TYPE_INT_ARGB);
    Graphics2D gr = icon.createGraphics();
    gr.setColor(stringToColor(name));
    gr.fillArc(2, (52 - 45) / 2 + 2, 41, 41, 0, 360);
    gr.setColor(Color.BLACK);
    gr.setStroke(new BasicStroke(4));
    gr.drawArc(2, (52 - 45) / 2 + 2, 41, 41, 0, 360);
    L.setIcon(new ImageIcon(icon));
    L.setVerifyInputWhenFocusTarget(false);
    L.setFocusable(false);
    layer.add(L);
    L.setBounds(screenX, screenY, cellW + 8, cellH);
  }

  private Passability() {
  }

  public static void main(String[] args) {
    System.out.println("<table>");
    for (Obstacle o : obstacleInfo().all()) {
      Color c = stringToColor(o.image);
      String color = String.format("%02X%02X%02X", c.getRed(), c.getBlue(), c.getGreen());
      System.out.println(
              "<tr><td>" + o.ID + "</td><td>" + o.image + "</td><td>"
              + "![#" + color + "](https://placehold.it/15/" + color + "/000000?text=+) `#" + color + "`"
              + "</td></tr>");
    }
    System.out.println("</table>");
  }

  public static Color stringToColor(final String str) {
    int hash = str.hashCode() % 16777216;
    int r = (hash & 0xFF0000) >> 16;
    int g = (hash & 0x00FF00) >> 8;
    int b = hash & 0x0000FF;
    return new Color(r, g, b, 100);
  }
}
