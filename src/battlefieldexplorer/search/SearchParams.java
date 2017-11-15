package battlefieldexplorer.search;

import static battlefieldexplorer.util.Constants.BFIELD_WIDTH;
import static battlefieldexplorer.util.HexTools.calcBitMask;
import static battlefieldexplorer.util.HexTools.distort;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.math.BigInteger.ZERO;
import battlefieldexplorer.gui.HexGrid;
import java.math.BigInteger;
import java.util.Set;

public class SearchParams {

  public final int size;
  public final BigInteger bit1;
  public final BigInteger bit0;
  public final boolean fixed;

  private SearchParams(final int size, final BigInteger bit1, final BigInteger bit0, final boolean fixed) {
    this.size = size;
    this.bit1 = bit1;
    this.bit0 = bit0;
    this.fixed = fixed;
  }

  public static SearchParams from(final HexGrid hexGrid, final boolean fixed) {
    final Set<Integer> pattern = hexGrid.getPattern();
    final Set<Integer> mask = hexGrid.getPatternMask();
    if ((!pattern.isEmpty()) || (!mask.isEmpty())) {
      final int shift;
      final int size;
      if (pattern.isEmpty()) {
        shift = distort(mask.iterator().next());
        size = mask.size();
      } else if (mask.isEmpty()) {
        shift = distort(pattern.iterator().next());
        size = pattern.size();
      } else {
        shift = distort(min(pattern.iterator().next(), mask.iterator().next()));
        size = max(pattern.size(), mask.size());
      }
      return new SearchParams(
        size,
        calcBitMask(pattern).shiftRight(shift).shiftLeft(BFIELD_WIDTH),
        calcBitMask(mask).shiftRight(shift).shiftLeft(BFIELD_WIDTH),
        fixed
      );
    }
    return new SearchParams(0, ZERO, ZERO, true);
  }

}
