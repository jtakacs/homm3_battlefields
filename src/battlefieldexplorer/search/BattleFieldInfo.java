package battlefieldexplorer.search;

import static battlefieldexplorer.generator.BattlefieldGenerator.createBattlefield;
import static battlefieldexplorer.util.Constants.MAP_SIZE;
import static battlefieldexplorer.util.HexTools.pair;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;
import battlefieldexplorer.generator.*;
import java.util.*;
import java.util.Map.Entry;

public final class BattleFieldInfo {

  private static class BattleFieldLoader {

    static final BattleFieldInfo INSTANCE = new BattleFieldInfo();

    private BattleFieldLoader() {
    }

  }

  public static BattleFieldInfo load() {
    return BattleFieldLoader.INSTANCE;
  }
  private final Map<Terrain, List<Battlefield>> all;

  private BattleFieldInfo() {
    all = Terrain
            .stream()
            .parallel()
            .map(terrain -> {
              final List<Battlefield> list = new LinkedList<>();
              for (int y = 0; y < MAP_SIZE; y++) {
                for (int x = 0; x < MAP_SIZE; x++) {
                  list.add(createBattlefield(x, y, terrain));
                }
              }
              return pair(terrain, list);
            })
            .collect(toMap(Entry::getKey, Entry::getValue));
  }

  public Battlefield get(final int x, final int y, final Terrain t) {
    return all.get(t)
            .get(y * MAP_SIZE + x);
  }

  public Map<Terrain, List<Battlefield>> getAllBattlefield() {
    return unmodifiableMap(all);
  }

}
