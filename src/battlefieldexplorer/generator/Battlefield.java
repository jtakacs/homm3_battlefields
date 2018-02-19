package battlefieldexplorer.generator;

import static battlefieldexplorer.util.Constants.BFIELD_SIZE;
import static battlefieldexplorer.util.Constants.BFIELD_WIDTH;
import static battlefieldexplorer.util.HexTools.calcBitMask;
import static battlefieldexplorer.util.HexTools.posToHex;
import static java.util.Collections.unmodifiableSet;
import battlefieldexplorer.search.SearchParams;
import battlefieldexplorer.search.SearchPattern;
import java.util.*;

public final class Battlefield {

  public final int mapX;
  public final int mapY;
  public final Terrain terrain;
  public final List<PositionedObstacle> obstacles = new ArrayList<>();
  private Set<Integer> blocked = null;
  private final int coveredArea;
  private BitVector obstacleMask = null;

  public Battlefield(final int mapX, final int mapY, final Terrain terrain, final List<PositionedObstacle> obstacles) {
    this.mapX = mapX;
    this.mapY = mapY;
    this.terrain = terrain;
    int area = 0;
    if (obstacles != null) {
      for (final PositionedObstacle o : obstacles) {
        area += o.obstacle.cellCount;
        this.obstacles.add(o);
      }
    }
    this.coveredArea = area;
  }

  public Set<Integer> getBlockedHexes() {
    if (blocked == null) {
      final Set<Integer> tmp = new TreeSet<>();
      for (final PositionedObstacle po : obstacles) {
        tmp.addAll(po.getBlockedCells());
      }
      blocked = unmodifiableSet(tmp);
    }
    return blocked;
  }

  public BitVector getObstacleMask() {
    if (obstacleMask == null) {
      obstacleMask = calcBitMask(getBlockedHexes());
    }
    return (BitVector) obstacleMask.clone();
  }

  public String getRawMask() {
    final Set<Integer> blocked = getBlockedHexes();
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < BFIELD_SIZE; i++) {
      if (blocked.contains(i)) {
        sb.append("1");
      } else {
        sb.append("0");
      }
    }
    return sb.toString();
  }

  public boolean compare(final SearchParams p) {
    if (p.size <= coveredArea) {
      for (final SearchPattern s : p.patterns) {
        if (compare(s.getBit1(), s.getBit0(), p.fixed)) {
          return true;
        }
      }
    }
    return false;
  }

  public PositionedObstacle getObstacleAt(int x, int y) {
    final int hex = posToHex(x, y);
    for (PositionedObstacle po : obstacles) {
      if (po.getBlockedCells().contains(hex)) {
        return po;
      }
    }
    return null;
  }

  private boolean compare(final BitVector blocked, final BitVector empty, final boolean fixed) {
    final BitVector tmp1 = getObstacleMask();
    for (int i = 0; i < BFIELD_SIZE - BFIELD_WIDTH; i++) {
      final BitVector tmp = (BitVector) tmp1.clone();
      tmp.shiftRight(i);
      if (tmp.and(blocked).equals(blocked)
          && tmp.andNot(empty).equals(tmp)) {
        return true;
      }
      if (fixed) {
        return false;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Battlefield: ")
            .append("mapX: ").append(mapX)
            .append(", mapY: ").append(mapY)
            .append(", ").append(terrain.name())
            .append(" [")
            .append(terrain.type).append(",").append(terrain.special ? " S" : "  ")
            .append("]")
            .append(System.lineSeparator())
            .append("obstacles: ");
    obstacles
            .forEach(po -> sb.append(po).append(System.lineSeparator()));
    return sb.toString();
  }

  public String toCSVrow() {
    return "" + mapX + "," + mapY + "," + terrain.description + System.lineSeparator();
  }
}
