package battlefieldexplorer.search;

import static battlefieldexplorer.search.SearchParams.from;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import battlefieldexplorer.generator.Battlefield;
import battlefieldexplorer.generator.Terrain;
import battlefieldexplorer.gui.HexGrid;
import java.math.BigInteger;
import java.util.*;

public class Search {

  private Search() {
  }

  public static List<Battlefield> search(final HexGrid hexGrid, final boolean fixed) {
    final SearchParams p = from(hexGrid, fixed);
    if (p.size == 0) {
      return new LinkedList<>();
    }
    final Map<Terrain, List<Battlefield>> bfs = BattleFieldInfo.load().getAllBattlefield();
    return search(p.size, p.bit1, p.bit0, p.fixed, bfs);
  }

  public static List<Battlefield> search(final int size, final BigInteger bit1, final BigInteger bit0, final boolean fixed, final Map<Terrain, List<Battlefield>> bfs) {
    return bfs
      .keySet()
      .stream()
      .parallel()
      .map(bfs::get)
      .map(Collection::stream)
      .map(list -> list.filter(bf -> bf.compare(size, bit1, bit0, fixed)))
      .flatMap(identity())
      .collect(toList());
  }
}
