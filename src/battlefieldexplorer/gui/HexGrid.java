package battlefieldexplorer.gui;

import static battlefieldexplorer.util.Constants.*;
import static battlefieldexplorer.util.HexCellState.*;
import static battlefieldexplorer.util.HexTools.isOddRow;
import static battlefieldexplorer.util.HexTools.posToHex;
import battlefieldexplorer.util.HexCellState;
import java.util.*;
import javax.swing.JComponent;

public class HexGrid {

  private final Map<Integer, HexCellState> grid = new HashMap<>();

  public HexGrid() {
  }

  public static void createHexGrid(final JComponent hexLayer, final HexGrid grid) {
    for (int y = 0; y < BFIELD_HEIGHT; y++) {
      for (int x = 1; x < BFIELD_WIDTH - 1; x++) {
        final int posX = canvasWoffset + x * (cellW - 1) + (isOddRow(y) ? 0 : (cellW / 2));
        final int posY = canvasHoffset + y * (cellH + cellHoffset + 2);
        new HexButton(x, y, grid).addToContainer(hexLayer, posX, posY);
      }
    }
    hexLayer.updateUI();
  }

  public void clear() {
    for (int i : grid.keySet()) {
      grid.put(i, NONE);
    }
  }

  public boolean isEmpty() {
    return grid.values().stream().noneMatch(s -> !HexCellState.NONE.equals(s));
  }

  public void setState(final int x, final int y, final HexCellState state) {
    if (0 < x && x < BFIELD_WIDTH) {
      if (0 <= y && y < BFIELD_HEIGHT) {
        grid.put(posToHex(x, y), state);
      }
    }
  }

  public HexCellState getState(final int x, final int y) {
    return grid.getOrDefault(posToHex(x, y), NONE);
  }

  public TreeSet<Integer> getPattern() {
    return getPattern(ENABLED);
  }

  public TreeSet<Integer> getPatternMask() {
    return getPattern(DISABLED);
  }

  private TreeSet<Integer> getPattern(final HexCellState state) {
    if (state == null) {
      throw new IllegalArgumentException();
    }
    final TreeSet<Integer> result = new TreeSet<>();
    for (int i = 0; i < BFIELD_SIZE; i++) {
      if (grid.containsKey(i)) {
        if (state.equals(grid.get(i))) {
          result.add(i);
        }
      }
    }
    return result;
  }

}
