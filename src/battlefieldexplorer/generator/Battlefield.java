package battlefieldexplorer.generator;

import static battlefieldexplorer.util.Constants.BFIELD_SIZE;
import static battlefieldexplorer.util.Constants.BFIELD_WIDTH;
import static battlefieldexplorer.util.HexTools.calcBitMask;
import static java.util.Collections.unmodifiableSet;
import java.math.BigInteger;
import java.util.*;

public final class Battlefield {

  public final int mapX;
  public final int mapY;
  public final Terrain terrain;
  public final List<PositionedObstacle> obstacles = new ArrayList<>();
  private Set<Integer> blocked = null;
  private final int coveredArea;
  private BigInteger obstacleMask = null;

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
      Collections.sort(obstacles);
    }
    this.coveredArea = area;
  }

  public final Set<Integer> getBlockedHexes() {
    if (blocked == null) {
      blocked = new TreeSet<>();
      for (final PositionedObstacle po : obstacles) {
        blocked.addAll(po.getBlockedCells());
      }
    }
    return unmodifiableSet(blocked);
  }

  public BigInteger getObstacleMask() {
    if (obstacleMask == null) {
      obstacleMask = calcBitMask(getBlockedHexes());
    }
    return obstacleMask;
  }

  public boolean compare(final int size, final BigInteger blocked, final BigInteger empty) {
    return compare(size, blocked, empty, false);
  }

  public boolean compare(final int size, final BigInteger blocked, final BigInteger empty, final boolean fixed) {
    if (coveredArea < size) {
      return false;
    }
    for (int i = 0; i < BFIELD_SIZE - BFIELD_WIDTH; i++) {
      final BigInteger tmp = getObstacleMask().shiftRight(i);
      if (tmp.and(blocked).equals(blocked)) {
        if (tmp.andNot(empty).equals(tmp)) {
          return true;
        }
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
      .append(", ").append(terrain.name()).append(" [").append(terrain.type).append(",").append(terrain.special ? " S" : "  ")
      .append("]")
      .append(System.lineSeparator())
      .append("obstacles: ");
    obstacles
      .forEach(po -> sb.append(po).append(System.lineSeparator()));
    return sb.toString();
  }

}
