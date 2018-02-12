package battlefieldexplorer;

import static battlefieldexplorer.util.Constants.MAP_SIZE;
import battlefieldexplorer.generator.Battlefield;
import battlefieldexplorer.generator.Terrain;
import battlefieldexplorer.gui.Gui;
import battlefieldexplorer.search.BattleFieldInfo;

public final class BattlefieldExplorer {

  private BattlefieldExplorer() {
  }

  public static void main(final String[] args) {
    if (args.length > 0) {
      dumpBlockedHexes();
    } else {
      Gui.start();
    }
  }

  public static void dumpBlockedHexes() {
    BattleFieldInfo info = BattleFieldInfo.load();
    System.out.println("terrainID, mapX, mapY, obstacleMask");
    for (Terrain terrain : Terrain.values()) {
      for (int mapX = 0; mapX < MAP_SIZE; mapX++) {
        for (int mapY = 0; mapY < MAP_SIZE; mapY++) {
          final Battlefield bf = info.get(mapX, mapY, terrain);
          System.out.println(
                  String.format("%2d, %3d, %3d, ", terrain.ID, mapX, mapY)
                  + bf.getRawMask()
          );
        }
      }
    }
  }

}
