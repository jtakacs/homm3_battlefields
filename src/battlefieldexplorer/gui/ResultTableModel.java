package battlefieldexplorer.gui;

import battlefieldexplorer.generator.Battlefield;
import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class ResultTableModel extends AbstractTableModel {

  private static final long serialVersionUID = 1L;

  private final List<Battlefield> data = new LinkedList<>();
  private final List<String> columns = new LinkedList<>();

  public ResultTableModel() {
    columns.add("Position X");
    columns.add("Position Y");
    columns.add("Terrain name");
    columns.add("Terrain ID");
  }

  public void addAll(final List<Battlefield> rows) {
    data.clear();
    if (rows != null) {
      data.addAll(rows);
    }
    fireTableDataChanged();
  }

  public void clear() {
    data.clear();
    fireTableDataChanged();
  }

  @Override
  public int getRowCount() {
    return data.size();
  }

  @Override
  public int getColumnCount() {
    return 4;
  }

  @Override
  public String getColumnName(final int col) {
    return columns.get(col);
  }

  @Override
  public Object getValueAt(final int rowIndex, final int columnIndex) {
    final Battlefield get = data.get(rowIndex);
    switch (columnIndex) {
      case 0:
        return "" + get.mapX;
      case 1:
        return "" + get.mapY;
      case 2:
        return get.terrain.description;
      case 3:
        return "" + get.terrain.ID;
      default:
        return "";
    }
  }

  @Override
  public Class<?> getColumnClass(final int columnIndex) {
    return String.class;
  }

  public Battlefield getValue(final int rowIndex) {
    return data.get(rowIndex);
  }

}
