package battlefieldexplorer.search;

import static battlefieldexplorer.util.Constants.BFIELD_WIDTH;
import static battlefieldexplorer.util.HexTools.calcBitMask;
import static battlefieldexplorer.util.HexTools.distort;
import static java.lang.Math.min;
import battlefieldexplorer.generator.BitVector;
import java.util.Set;
import java.util.TreeSet;

public class SearchPattern {

  private final BitVector bit1;
  private final BitVector bit0;
  private final int shift;

  private SearchPattern(final Set<Integer> pattern, final TreeSet<Integer> mask, final boolean fixed) {
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
      bit1 = calcBitMask(pattern);
      bit1.shiftRight(shift);
      bit1.shiftLeft(BFIELD_WIDTH);
      bit0 = calcBitMask(mask);
      bit0.shiftRight(shift);
      bit0.shiftLeft(BFIELD_WIDTH);
    }
  }

  public BitVector getBit1() {
    return (BitVector) bit1.clone();
  }

  public BitVector getBit0() {
    return (BitVector) bit0.clone();
  }

  public static B1 pattern(final Set<Integer> pattern) {
    return mask -> fixed -> new SearchPattern(pattern, mask, fixed);
  }

  public static interface B1 {

    B2 mask(TreeSet<Integer> mask);
  }

  public static interface B2 {

    SearchPattern fixed(boolean fixed);
  }

}
