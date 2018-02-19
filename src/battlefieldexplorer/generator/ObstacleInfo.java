package battlefieldexplorer.generator;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
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

  private final Map<Integer, Obstacle> absolute = new TreeMap<>();
  private final Map<Integer, Obstacle> usual = new TreeMap<>();

  private ObstacleInfo() {
    new ObstacleReader("obstacles.csv")
            .read(this::store);
  }

  private void store(final Obstacle o) {
    if (o.absolute) {
      absolute.put(o.ID, o);
    } else {
      usual.put(o.ID, o);
    }
  }

  public Obstacle get(final boolean absolute, final int id) {
    if (absolute) {
      return this.absolute.get(id);
    }
    return usual.get(id);
  }

  public List<Obstacle> all() {
    final LinkedList<Obstacle> result = new LinkedList<>();
    for (Obstacle o : usual.values()) {
      result.add(o);
    }
    for (Obstacle o : absolute.values()) {
      result.add(o);
    }
    return result;
  }

  private static class ObstacleReader extends CSVReader<Obstacle> {

    ObstacleReader(final String filename) {
      super(filename);
    }

    @Override
    public Obstacle parseLine(final String line) {
      final String[] split = line.split(",");
      return Obstacle
              .obstacleID(parseInt(split[0]))
              .absolute(parseBoolean(split[1]))
              .battlefields(parseInt(split[2], 16))
              .posX(parseInt(split[3]))
              .posY(parseInt(split[4]))
              .width(parseInt(split[5]))
              .height(parseInt(split[6]))
              .image(split[7])
              .cells(toNumberList(split[8], ";", Integer::parseInt));
    }
  }

}
