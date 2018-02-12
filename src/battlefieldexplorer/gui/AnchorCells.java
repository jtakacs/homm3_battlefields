package battlefieldexplorer.gui;

import static battlefieldexplorer.util.Constants.*;
import static battlefieldexplorer.util.HexTools.isOddRow;
import static battlefieldexplorer.util.HexTools.posToHex;
import static javax.swing.SwingConstants.CENTER;
import battlefieldexplorer.generator.PositionedObstacle;
import java.awt.Dimension;
import java.util.List;
import javax.swing.*;

public class AnchorCells {

  public static final ImageIcon anchorcell = new ImageIcon(AnchorCells.class.getResource("/CCellAnchor.png"));

  public static void createOverlay(final JComponent anchorLayer, final List<PositionedObstacle> obstacles) {
    anchorLayer.removeAll();
    for (int y = 0; y < BFIELD_HEIGHT; y++) {
      for (int x = 1; x < BFIELD_WIDTH - 1; x++) {
        final int hex = posToHex(x, y);
        if (obstacles.stream().anyMatch(o -> o.hex == hex)) {
          final int screenX = canvasWoffset + x * (cellW - 1) + (isOddRow(y) ? 0 : (cellW / 2));
          final int screenY = canvasHoffset + y * (cellH + cellHoffset + 2);
          createHex(anchorLayer, screenX, screenY);
        }
      }
    }
    anchorLayer.updateUI();
  }

  private static void createHex(final JComponent layer, final int screenX, final int screenY) {
    final Dimension size = new Dimension(cellW, cellH);
    final JLabel L = new JLabel();
    L.setSize(size);
    L.setMinimumSize(size);
    L.setPreferredSize(size);
    L.setMaximumSize(new Dimension(cellW + 8, cellH));
    L.setBackground(new java.awt.Color(255, 255, 255, 0));
    L.setDoubleBuffered(true);
    L.setBorder(null);
    L.setOpaque(false);
    L.setHorizontalTextPosition(CENTER);
    L.setHorizontalAlignment(CENTER);
    L.setVerticalTextPosition(CENTER);
    L.setIconTextGap(0);
    L.setIcon(anchorcell);
    L.setVerifyInputWhenFocusTarget(false);
    L.setFocusable(false);
    layer.add(L);
    L.setBounds(screenX, screenY, cellW + 8, cellH);
  }

  private AnchorCells() {
  }
}
