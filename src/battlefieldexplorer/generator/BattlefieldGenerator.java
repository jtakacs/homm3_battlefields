package battlefieldexplorer.generator;

import static battlefieldexplorer.generator.BitVector.*;
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

  private final BitVector WCanUse = bvWCanUse();
  private final BitVector RCanUse = bvRCanUse();
  private final BitVector HasOb = bvHasOb();

  private int RandomObMeasure;

  public BattlefieldGenerator(final int mapX, final int mapY, final Terrain terrain) {
    this.mapX = mapX;
    this.mapY = mapY;
    this.terrain = terrain;
    rnd = RNG.randomSeed(mapX, mapY);
  }

  public Battlefield createBattlefield() {
    RandomObMeasure = rnd.getRandomInterval(12, 5);
    final List<PositionedObstacle> obstacles = new ArrayList<>();
    if (ABSOLUTE_OBSTACLE_CHANCE >= rnd.getRandomInterval(100, 1)) {
      obstacles.addAll(createAbsoluteObstacle());
    }
    if (RandomObMeasure > 0) {
      obstacles.addAll(createUsualObstacles());
    }
    return new Battlefield(mapX, mapY, terrain, obstacles);
  }
  
  //TODO these functions look too similar. consider refactoring.
  private List<PositionedObstacle> createAbsoluteObstacle() {
    final List<PositionedObstacle> result = new ArrayList<>();
    int id;
    do {
      id = WCanUse.change(rnd);
      if (id >= 0) {
        final Obstacle obst = obstacleInfo().absolute.get(id);
        if ((obst.battlefields & terrain.type) != 0) {
          int pos = GetRPos(obst);
          //Get the total number of local terrain obstacles
          RandomObMeasure -= obst.measure;
          result.add(new PositionedObstacle(obst, pos));
          return result;
        }
      }
    } while (id >= 0);
    return result;
  }

  private List<PositionedObstacle> createUsualObstacles() {
    final List<PositionedObstacle> result = new ArrayList<>();
    int id = 0;
    do {
      while (id >= 0) {
        id = RCanUse.change(rnd);
        if (id >= 0) {
          final Obstacle obst = obstacleInfo().usual.get(id);
          if ((obst.battlefields & terrain.type) != 0) {
            int pos = GetRPos(obst);
            if (pos >= 0) {
              RandomObMeasure -= obst.measure;
              result.add(new PositionedObstacle(obst, pos));
              break;
            }
          }
        }
      }
    } while ((id >= 0) && (0 < RandomObMeasure));
    return result;
  }

  private int GetRPos(final Obstacle obstacle) {
    int hex;
    if (obstacle.absolute) {
      hex = 0;
      obstacle.cells.forEach(c -> HasOb.set(c, true));
    } else {
      final BitVector RCanPos = bvRCanPos();
      do {
        //Randomly get a position between [18..168] 
        hex = RCanPos.change(rnd);
        if (hex < 12) {
          //TODO here is a possible failure point
          return -1;
        }
      } while (checkBoundaries(obstacle, hex));
      HasOb.set(hex, true);
      for (int i = 0; i < obstacle.cellCount; i++) {
        HasOb.set(relocateHex(hex, obstacle.cells.get(i)), true);
      }
    }
    return hex;
  }

  private boolean checkBoundaries(final Obstacle us, final int hex) {
    final int posX = getX(hex);
    final int posY = getY(hex);
    //Can not occupy the first grid of each line, can not take up the last grid, can not occupy the lattice has been obstacles
    if (((us.height > posY) || (posX == 0) || (posX + us.width > 15) || HasOb.get(hex))) {
      return true;
    }
    return checkCells(hex, us);
  }

  private boolean checkCells(final int hex, final Obstacle us) {
    for (int i = 0; i < us.cellCount; i++) {
      final int pos = relocateHex(hex, us.cells.get(i));
      final int posX = getX(pos);
      if (HasOb.get(pos) || (posX <= 2) || (posX >= 14)) {
        return true;
      }
    }
    return false;
  }

}
