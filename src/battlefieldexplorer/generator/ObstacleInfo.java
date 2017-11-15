package battlefieldexplorer.generator;

import battlefieldexplorer.util.CSVReader;
import java.util.*;

public final class ObstacleInfo {

  private static class ObstacleLoader {

    static final ObstacleInfo INSTANCE = new ObstacleInfo();

    private ObstacleLoader() {
    }
  }

  public static ObstacleInfo obstacleInfo() {
    return ObstacleLoader.INSTANCE;
  }

  public final List<Obstacle> absolute = new LinkedList<>();
  public final List<Obstacle> usual = new LinkedList<>();

  public ObstacleInfo() {
    new ObstacleReader()
      .read("obstacles.csv", o -> {
            if (o.absolute) {
              absolute.add(o);
            } else {
              usual.add(o);
            }
          });
  }

  private static class ObstacleReader extends CSVReader<Obstacle> {

    ObstacleReader() {
    }

    @Override
    public Obstacle parseLine(final String line) {
      final String[] split = line.split(";");
      final int ID = Integer.parseInt(split[0], 10);
      final boolean absolute = Boolean.parseBoolean(split[1]);
      final int battlefields = Integer.parseInt(split[2], 16);
      final int screenX = Integer.parseInt(split[3], 10);
      final int screenY = Integer.parseInt(split[4], 10);
      final int width = Integer.parseInt(split[5], 10);
      final int height = Integer.parseInt(split[6], 10);
      final String image = split[7];
      final List<Integer> cells = new ArrayList<>();
      for (final String s : split[8].split(",")) {
        cells.add(Integer.parseInt(s, 10));
      }
      return Obstacle
        .obstacleID(ID)
        .absolute(absolute)
        .battlefields(battlefields)
        .specialbattlefields(0)
        .cells(cells)
        .posX(screenX)
        .posY(screenY)
        .width(width)
        .height(height)
        .image(image);
    }
  }

}
