package battlefieldexplorer.search;

import static battlefieldexplorer.search.SearchPattern.pattern;
import static battlefieldexplorer.util.Constants.BFIELD_HEIGHT;
import static battlefieldexplorer.util.Constants.BFIELD_WIDTH;
import static battlefieldexplorer.util.HexTools.getX;
import static battlefieldexplorer.util.HexTools.getY;
import static battlefieldexplorer.util.HexTools.isOddRow;
import static battlefieldexplorer.util.HexTools.posToHex;
import static java.lang.Integer.MAX_VALUE;
import battlefieldexplorer.gui.HexGrid;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SearchParams {

  public final int size;
  public final boolean fixed;
  public final boolean mirrorV;
  public final boolean mirrorH;
  public final List<SearchPattern> patterns;

  private SearchParams() {
    fixed = true;
    mirrorH = false;
    mirrorV = false;
    patterns = new LinkedList<>();
    size = MAX_VALUE;
  }

  private SearchParams(final Set<Integer> pattern, final Set<Integer> mask, final boolean fixed, final boolean mirrorV, final boolean mirrorH) {
    this.fixed = fixed;
    this.mirrorH = mirrorH;
    this.mirrorV = mirrorV;
    patterns = new LinkedList<>();
    size = pattern.size();
    patterns.add(pattern(pattern).mask(mask).fixed(fixed));
    if (!fixed) {
      if (mirrorV) {
        patterns.add(flipV(pattern, mask));
      }
      if (mirrorH) {
        patterns.add(flipH(pattern, mask));
      }
    }
  }

  public static SearchParams from(final HexGrid hexGrid, final boolean fixed, final boolean mirrorV, final boolean mirrorH) {
    final Set<Integer> pattern = hexGrid.getPattern();
    final Set<Integer> mask = hexGrid.getPatternMask();
    if ((!pattern.isEmpty()) || (!mask.isEmpty())) {
      return new SearchParams(pattern, mask, fixed, mirrorV, mirrorH);
    }
    return new SearchParams();
  }

  private SearchPattern flipH(final Set<Integer> pattern, final Set<Integer> mask) {
    final Set<Integer> flipH = new TreeSet<>();
    final Set<Integer> flipHm = new TreeSet<>();
    for (final Integer hex : pattern) {
      int y = getY(hex);
      flipH.add(posToHex(BFIELD_WIDTH - 1 - getX(hex) + (isOddRow(y) ? 1 : 0), y));
    }
    for (final Integer hex : mask) {
      int y = getY(hex);
      flipHm.add(posToHex(BFIELD_WIDTH - 1 - getX(hex) + (isOddRow(y) ? 1 : 0), y));
    }
    return pattern(flipH).mask(flipHm).fixed(false);
  }

  private SearchPattern flipV(final Set<Integer> pattern, final Set<Integer> mask) {
    final Set<Integer> flipV = new TreeSet<>();
    final Set<Integer> flipVm = new TreeSet<>();
    for (final Integer hex : pattern) {
      flipV.add(posToHex(getX(hex), BFIELD_HEIGHT - 1 - getY(hex)));
    }
    for (final Integer hex : mask) {
      flipVm.add(posToHex(getX(hex), BFIELD_HEIGHT - 1 - getY(hex)));
    }
    return pattern(flipV).mask(flipVm).fixed(false);
  }

}
