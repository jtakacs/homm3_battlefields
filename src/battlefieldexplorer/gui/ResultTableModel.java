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
    columns.add("Terrain");
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
    return columns.size();
  }

  @Override
  public String getColumnName(final int col) {
    return columns.get(col);
  }

  @Override
  public Object getValueAt(final int rowIndex, final int columnIndex) {
    final Battlefield bf = data.get(rowIndex);
    switch (columnIndex) {
      case 0:
        return bf.mapX;
      case 1:
        return bf.mapY;
      case 2:
        return bf.terrain.description;
      default:
        return 0;
    }
  }

  @Override
  public Class<?> getColumnClass(final int columnIndex) {
    if (columnIndex == 0 || columnIndex == 1) {
      return Integer.class;
    }
    return String.class;
  }

  public Battlefield getValue(final int rowIndex) {
    return data.get(rowIndex);
  }

  public String toCSV() {
    final StringBuilder sb = new StringBuilder();
    data.stream().forEach(bf -> sb.append(bf.toCSVrow()));
    return sb.toString();
  }

}
