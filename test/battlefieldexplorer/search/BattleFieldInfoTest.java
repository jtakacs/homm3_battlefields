package battlefieldexplorer.search;

import static org.junit.Assert.*;
import battlefieldexplorer.generator.Terrain;
import battlefieldexplorer.util.CSVReader;
import org.junit.Test;

public class BattleFieldInfoTest {

  public BattleFieldInfoTest() {
  }

  @Test
  public void testGenerator() {
    long start = System.currentTimeMillis();
    final BattleFieldInfo info = BattleFieldInfo.load();
    /*
    This file was generated with the game's original obstacle placement algorithm,
    using HDmod's plugin system.
     */
    new TestFileReader("test.csv.gz")
            .read((TestLine line) -> {
              assertEquals(
                      "Battlefield generator failure at mapX: " + line.mapX + ", mapY: " + line.mapY + ", terrain: " + line.terrain.name(),
                      line.blockedHexes,
                      info.get(line.mapX, line.mapY, line.terrain).getRawMask()
              );
            });
    System.out.println("time: " + ((System.currentTimeMillis() - start) / 1000.0d));
  }

  private static class TestFileReader extends CSVReader<TestLine> {

    TestFileReader(final String filename) {
      super(filename);
    }

    @Override
    public TestLine parseLine(final String line) {
      final String[] split = line.split(",");
      return new TestLine(
              Terrain.get(Integer.parseUnsignedInt(split[0].trim(), 10)).get(),
              Integer.parseUnsignedInt(split[1].trim(), 10),
              Integer.parseUnsignedInt(split[2].trim(), 10),
              split[3].trim()
      );
    }
  }

  private static class TestLine {

    public final Terrain terrain;
    public final int mapX;
    public final int mapY;
    public final String blockedHexes;

    TestLine(final Terrain terrain, final int mapX, final int mapY, final String blockedHexes) {
      this.terrain = terrain;
      this.mapX = mapX;
      this.mapY = mapY;
      this.blockedHexes = blockedHexes;
    }
  }
}
