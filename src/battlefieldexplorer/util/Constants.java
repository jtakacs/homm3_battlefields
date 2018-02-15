package battlefieldexplorer.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class Constants {

  public static final int ABSOLUTE_OBSTACLES_COUNT = 34;
  public static final int USUAL_OBSTACLES_COUNT = 91;
  public static final int BFIELD_WIDTH = 17;
  public static final int BFIELD_HEIGHT = 11;
  public static final int BFIELD_SIZE = BFIELD_WIDTH * BFIELD_HEIGHT;
  public static final int BattleHex_LEFT = -1;
  public static final int MAP_SIZE = 144;
  public static final int cellH = 52;
  public static final int cellW = 45;
  public static final int canvasW = 800;
  public static final int canvasH = 556;
  public static final int canvasHoffset = 100 - 14;
  public static final int canvasWoffset = 10;
  public static final int cellHoffset = -12;
  public static final int ABSOLUTE_OBSTACLE_CHANCE = 40;
  public static final Set<Integer> ShipToShip = new HashSet<>(Arrays.asList(
          0x006, 0x007, 0x008, 0x009, 0x018, 0x019, 0x01A, 0x03A,
          0x03B, 0x03C, 0x04B, 0x04C, 0x04D, 0x05C, 0x05D, 0x05E,
          0x06D, 0x06E, 0x06F, 0x07E, 0x07F, 0x080, 0x09F, 0x0A0,
          0x0A1, 0x0A2, 0x0A3, 0x0B0, 0x0B1, 0x0B2, 0x0B3, 0x0B4
  ));

  private Constants() {
  }

}
