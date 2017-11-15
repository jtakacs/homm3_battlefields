package battlefieldexplorer.generator;

import static battlefieldexplorer.util.HexTools.*;
import java.util.ArrayList;
import java.util.List;

public class PositionedObstacle implements Comparable<PositionedObstacle> {

  public final Obstacle obstacle;
  public final int hex;
  public final int posX;
  public final int posY;
  private List<Integer> blocked = null;

  public PositionedObstacle(final Obstacle obstacle, final int hex) {
    this.obstacle = obstacle;
    this.hex = hex;
    this.posX = getX(hex);
    this.posY = getY(hex);
  }

  public List<Integer> getBlockedCells() {
    if (blocked == null) {
      blocked = new ArrayList<>();
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
    return calc(false);
  }

  public final int getScreenY() {
    if (obstacle.absolute) {
      return obstacle.screenY;
    }
    return calc(true);
  }

  private int calc(final boolean coord_Y) {
    final int half = posY / 2;
    final int tmp = 84 * half + 52;
    final int rpY1 = tmp + (isOddRow(posY) ? 42 : 0);
    final int canvasY = 87 + rpY1 - (50 * obstacle.height);
    final int rem = (posY + 1) % 2;
    final int canvasX = 12 + 22 * rem + posX * 44;
    if (coord_Y) {
      return canvasY;
    } else {
      return canvasX;
    }
  }

  @Override
  public String toString() {
    return "PositionedObstacle: " + "posX=" + posX + ", posY=" + posY + ", HEX: " + posToHex(posX, posY)
           + "\nobstacle=(" + obstacle.ID + "," + obstacle.absolute + ") " + ", blocked hexes= " + getBlockedCells();
  }

  @Override
  public int compareTo(final PositionedObstacle o) {
    if (o == null) {
      return 1;
    }
    return Integer.compare(o.getScreenY(), this.getScreenY());
  }

}
