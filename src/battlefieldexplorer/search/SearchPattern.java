package battlefieldexplorer.search;

import static battlefieldexplorer.util.Constants.BFIELD_WIDTH;
import static battlefieldexplorer.util.HexTools.calcBitMask;
import static battlefieldexplorer.util.HexTools.distort;
import static java.lang.Math.min;
import java.math.BigInteger;
import java.util.Set;

public class SearchPattern {

  private final BigInteger bit1;
  private final BigInteger bit0;
  private final int shift;

  private SearchPattern(final Set<Integer> pattern, final Set<Integer> mask, final boolean fixed) {
    if (fixed) {
      bit1 = calcBitMask(pattern);
      bit0 = calcBitMask(mask);
      shift = 0;
    } else {
      if (pattern.isEmpty()) {
        shift = distort(mask.iterator().next());
      } else if (mask.isEmpty()) {
        shift = distort(pattern.iterator().next());
      } else {
        shift = distort(min(pattern.iterator().next(), mask.iterator().next()));
      }
      bit1 = calcBitMask(pattern).shiftRight(shift).shiftLeft(BFIELD_WIDTH);
      bit0 = calcBitMask(mask).shiftRight(shift).shiftLeft(BFIELD_WIDTH);
    }
  }

  public BigInteger getBit1() {
    return bit1;
  }

  public BigInteger getBit0() {
    return bit0;
  }

  public static B1 pattern(final Set<Integer> pattern) {
    return mask -> fixed -> new SearchPattern(pattern, mask, fixed);
  }

  public static interface B1 {

    B2 mask(Set<Integer> mask);
  }

  public static interface B2 {

    SearchPattern fixed(boolean fixed);
  }

}
