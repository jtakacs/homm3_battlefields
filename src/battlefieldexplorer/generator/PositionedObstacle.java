package battlefieldexplorer.generator;

import static battlefieldexplorer.util.HexTools.*;
import static java.lang.Integer.MIN_VALUE;
import java.util.Set;
import java.util.TreeSet;

public class PositionedObstacle implements Comparable<PositionedObstacle> {

  public final Obstacle obstacle;
  public final int hex;
  public final int posX;
  public final int posY;
  private int screenX = MIN_VALUE;
  private int screenY = MIN_VALUE;
  private Set<Integer> blocked = null;

  public PositionedObstacle(final Obstacle obstacle, final int hex) {
    this.obstacle = obstacle;
    this.hex = hex;
    this.posX = getX(hex);
    this.posY = getY(hex);
  }

  public Set<Integer> getBlockedCells() {
    if (blocked == null) {
      blocked = new TreeSet<>();
      if (obstacle.absolute) {
        blocked.addAll(obstacle.cells);
      } else {
        for (final int offset : obstacle.cells) {
          blocked.add(relocateHex(hex, offset));
        }
      }
    }
    return blocked;
  }

  public final int getScreenX() {
    if (obstacle.absolute) {
      return obstacle.screenX;
    }
    if (Integer.MIN_VALUE == screenX) {
      calcX();
    }
    return screenX;
  }

  public final int getScreenY() {
    if (obstacle.absolute) {
      return obstacle.screenY;
    }
    if (Integer.MIN_VALUE == screenY) {
      calcY();
    }
    return screenY;
  }

  private void calcX() {
    screenX = 12 + 22 * ((posY + 1) % 2) + posX * 44;
  }

  private void calcY() {
    //TODO these magic numbers need some tweaking
    screenY = 139 + 84 * (posY / 2) - (50 * obstacle.height) + (isOddRow(posY) ? 42 : 0);
  }

  @Override
  public String toString() {
    return "PositionedObstacle: " + "posX=" + posX + ", posY=" + posY + ", HEX: " + posToHex(posX, posY)
           + "\nobstacle=(" + obstacle.ID + "," + obstacle.absolute + ") " + ", blocked hexes= " + getBlockedCells();
  }

  @Override
  public int compareTo(final PositionedObstacle other) {
    if (other == null) {
      return 1;
    }
    return Integer.compare(
            other.getScreenY() + other.obstacle.getImage().getIconHeight(),
            this.getScreenY() + obstacle.getImage().getIconHeight()
    );
  }

}
