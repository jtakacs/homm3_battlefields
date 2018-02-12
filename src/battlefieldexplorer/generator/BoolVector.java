package battlefieldexplorer.generator;

import static battlefieldexplorer.util.Constants.ABSOLUTE_OBSTACLES_COUNT;
import static battlefieldexplorer.util.Constants.USUAL_OBSTACLES_COUNT;

public final class BoolVector {
  //TODO this is a giant mess. more refactoring needed.

  private final int max;
  private final boolean clearValue;
  private final BitVector vector;
  private int value;

  public static BoolVector bvWCanUse() {
    //34 kinds of absolute obstacles
    return new BoolVector(ABSOLUTE_OBSTACLES_COUNT, true);
  }

  public static BoolVector bvRCanUse() {
    //92 kinds of relative obstacles
    return new BoolVector(1 + USUAL_OBSTACLES_COUNT, true);
  }

  public static BoolVector bvRCanPos() {
    //152 coordinates that can hold obstacles
    return new BoolVector(152, true);
  }

  public static BoolVector bvHasOb() {
    //191 all coordinates (including hidden rows and columns that can not be reached)
    return new BoolVector(191, false);
  }

  private BoolVector(final int max, final boolean clearValue) {
    this.max = max;
    this.clearValue = clearValue;
    vector = new BitVector();
    clear();
    reset();
  }

  private void reset() {
    if (max == ABSOLUTE_OBSTACLES_COUNT) {
      value = max;
    } else {
      value = max - 1;
    }
  }

  private void clear() {
    for (int i = 0; i < max; i++) {
      vector.set(i, clearValue);
    }
  }

  public boolean get(final int idx) {
    return vector.get(idx);
  }

  public void set(final int idx, final boolean value) {
    vector.set(idx, value);
  }

  public int change(final RNG rnd) {
    if (value <= 0) {
      return -1;
    }
    if (value == 1) {
      for (int i = 0; i < max; i++) {
        if (vector.get(i)) {
          vector.set(i, false);
          value = 0;
          return i;
        }
      }
    }
    final int r = rnd.getRandom(value);
    this.value--;
    return change2(r);
  }

  private int change2(int temp) {
    //TODO original code didn't have an exit condition for the loop
    for (int i = 0; i < max; i++) {
      //TODO index out of bound exception occurred for x=27,y=0,t=swamp
      if (vector.get(i)) {
        if (temp == 0) {
          vector.set(i, false);
          return i;
        }
        temp--;
      }
    }
    return -1;
  }

}
