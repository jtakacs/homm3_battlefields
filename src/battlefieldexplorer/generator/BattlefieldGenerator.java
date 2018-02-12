package battlefieldexplorer.generator;

import static battlefieldexplorer.generator.BoolVector.*;
import static battlefieldexplorer.generator.ObstacleInfo.obstacleInfo;
import static battlefieldexplorer.util.Constants.ABSOLUTE_OBSTACLE_CHANCE;
import static battlefieldexplorer.util.HexTools.*;
import java.util.ArrayList;
import java.util.List;

public final class BattlefieldGenerator {

  private final int mapX;
  private final int mapY;
  private final Terrain terrain;
  private final RNG rnd;

  private final BoolVector WCanUse = bvWCanUse();
  private final BoolVector RCanUse = bvRCanUse();
  private final BoolVector HasOb = bvHasOb();

  private int RandomObMeasure;
  private final List<PositionedObstacle> obstacles = new ArrayList<>();

  private BattlefieldGenerator(final int mapX, final int mapY, final Terrain terrain) {
    this.mapX = mapX;
    this.mapY = mapY;
    this.terrain = terrain;
    rnd = RNG.randomSeed(mapX, mapY);
  }

  public static Battlefield createBattlefield(final int mapX, final int mapY, final Terrain terrain) {
    return new BattlefieldGenerator(mapX, mapY, terrain).createBattlefield();
  }

  private Battlefield createBattlefield() {
    RandomObMeasure = rnd.getRandomInterval(12, 5);
    if (ABSOLUTE_OBSTACLE_CHANCE >= rnd.getRandomInterval(100, 1)) {
      createObstacle(true);
    }
    if (RandomObMeasure > 0) {
      createObstacle(false);
    }
    return new Battlefield(mapX, mapY, terrain, obstacles);
  }

  private void createObstacle(final boolean absolute) {
    int id = 0;
    while ((0 <= id) && (0 < RandomObMeasure)) {
      id = getID(absolute);
      if (0 <= id) {
        final Obstacle obst = obstacleInfo().get(absolute, id);
        if ((obst.battlefields & terrain.type) != 0) {
          final int pos = getObstaclePosition(obst);
          if (0 <= pos) {
            RandomObMeasure -= obst.measure;
            obstacles.add(new PositionedObstacle(obst, pos));
            if (absolute) {
              return;
            }
          }
        }
      }
    }
  }

  private int getID(final boolean absolute) {
    if (absolute) {
      return WCanUse.change(rnd);
    }
    return RCanUse.change(rnd);
  }

  private int getObstaclePosition(final Obstacle obstacle) {
    final int hex;
    if (obstacle.absolute) {
      hex = 0;
      obstacle.cells.forEach(c -> HasOb.set(c, true));
    } else {
      hex = randPos(obstacle);
      if (hex >= 0) {
        HasOb.set(hex, true);
        obstacle.cells.forEach(c -> HasOb.set(relocateHex(hex, c), true));
      }
    }
    return hex;
  }

  private int randPos(final Obstacle obstacle) {
    final BoolVector RCanPos = bvRCanPos();
    int hex;
    do {
      //Randomly get a position between [18..168]
      hex = RCanPos.change(rnd);
      if (hex < 0) {
        //TODO here is a possible failure point
        return -1;
      }
      hex += 18;
    } while (checkBoundaries(obstacle, hex));
    return hex;
  }

  private boolean checkBoundaries(final Obstacle obstacle, final int hex) {
    final int posX = getX(hex);
    final int posY = getY(hex);
    //Can not occupy the first grid of each line, can not take up the last grid, can not occupy the lattice has been obstacles
    if (((obstacle.height > posY) || (posX == 0) || (posX + obstacle.width > 15) || HasOb.get(hex))) {
      return true;
    }
    return checkCells(hex, obstacle);
  }

  private boolean checkCells(final int hex, final Obstacle obstacle) {
    for (int i = 0; i < obstacle.cellCount; i++) {
      final int pos = relocateHex(hex, obstacle.cells.get(i));
      final int posX = getX(pos);
      if (HasOb.get(pos) || (posX <= 2) || (posX >= 14)) {
        return true;
      }
    }
    return false;
  }

}
