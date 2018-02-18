package battlefieldexplorer.gui;

import battlefieldexplorer.generator.Battlefield;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RowSelectionListener implements ListSelectionListener {

  private final JTable jTable1;
  private final ResultTableModel tm;
  private final Gui frame;

  public RowSelectionListener(final JTable jTable1, final ResultTableModel tm, final Gui frame) {
    this.jTable1 = jTable1;
    this.tm = tm;
    this.frame = frame;
  }

  @Override
  public void valueChanged(final ListSelectionEvent e) {
    if (e.getValueIsAdjusting()) {
      return;
    }
    final int row = jTable1.getSelectedRow();
    if (row >= 0) {
      final Battlefield bf = tm.getValue(jTable1.convertRowIndexToModel(row));
      frame.setControlState(bf);
    }
  }
}
