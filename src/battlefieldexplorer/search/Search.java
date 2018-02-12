package battlefieldexplorer.search;

import static battlefieldexplorer.search.BattleFieldInfo.load;
import static battlefieldexplorer.search.SearchParams.from;
import static java.util.Optional.of;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import battlefieldexplorer.generator.Battlefield;
import battlefieldexplorer.gui.HexGrid;
import java.awt.Rectangle;
import java.util.*;

public class Search {

  private Search() {
  }

  public static List<Battlefield> search(final HexGrid hexGrid, final boolean fixed, final boolean mirrorV, final boolean mirrorH, final Rectangle area) {
    final boolean limit = area.x > -1 && area.y > -1 && area.width > -1 && area.height > -1;
    final SearchParams p = from(hexGrid, fixed, mirrorV, mirrorH);
    return of(load().getAllBattlefield())
            .map(
                    battlefields -> battlefields
                            .keySet()
                            .stream()
                            .parallel()
                            .map(battlefields::get)
                            .map(Collection::stream)
                            .map(list -> list.filter(b -> limit
                                                          ? (area.x <= b.mapX && b.mapX <= area.width
                                                             && area.y <= b.mapY && b.mapY <= area.height)
                                                          : true)
                            )
                            .map(list -> list.filter(battlefield -> battlefield.compare(p)))
                            .flatMap(identity())
                            .collect(toList())
            )
            .orElseGet(Collections::emptyList);
  }
}
