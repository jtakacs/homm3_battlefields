package battlefieldexplorer.gui;

import battlefieldexplorer.generator.Battlefield;
import battlefieldexplorer.generator.PositionedObstacle;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RowSelectionListener implements ListSelectionListener {

  private final JTable jTable1;
  private final ResultTableModel tm;
  private final JLabel background;
  private final JPanel obstacleLayer;

  public RowSelectionListener(final JTable jTable1, final ResultTableModel tm, final JLabel background, final JPanel obstacleLayer) {
    this.jTable1 = jTable1;
    this.tm = tm;
    this.background = background;
    this.obstacleLayer = obstacleLayer;
  }

  @Override
  public void valueChanged(final ListSelectionEvent e) {
    if (e.getValueIsAdjusting()) {
      return;
    }
    final int row = jTable1.getSelectedRow();
    if (row >= 0) {
      final Battlefield d = tm.getValue(jTable1.convertRowIndexToModel(row));
      obstacleLayer.removeAll();
      background.setIcon(TerrainImages.getImage(d.terrain));
      for (final PositionedObstacle po : d.obstacles) {
        final ImageIcon im = po.obstacle.getImage();
        final JLabel L = new JLabel(im);
        obstacleLayer.add(L);
        L.setBounds(po.getScreenX(), po.getScreenY(), im.getIconWidth(), im.getIconHeight());
      }
      background.updateUI();
      obstacleLayer.updateUI();
    }
  }
}
