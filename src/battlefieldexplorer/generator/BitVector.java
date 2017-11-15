package battlefieldexplorer.generator;

import static battlefieldexplorer.util.Constants.ABSOLUTE_OBSTACLES_COUNT;
import static battlefieldexplorer.util.Constants.USUAL_OBSTACLES_COUNT;

public final class BitVector {
  //TODO this is a giant mess. more refactoring needed.
  
  private final String name;
  private final int max;
  private final int nextValue;
  private final int offset;
  private final boolean clearValue;
  private final boolean vector[];
  private int value;

  public static BitVector bvWCanUse() {
    //34 kinds of overall terrain obstacles
    return new BitVector(ABSOLUTE_OBSTACLES_COUNT, 0, 0, true, "WCanUse");
  }

  public static BitVector bvRCanUse() {
    //92 Local terrain obstacle
    return new BitVector(USUAL_OBSTACLES_COUNT+1, 0, 0, true, "RCanUse");
  }

  public static BitVector bvRCanPos() {
    //152 coordinates that can hold obstacles
    return new BitVector(152, 12, 18, true, "RCanPos");
  }

  public static BitVector bvHasOb() {
    //191 all coordinates (including hidden rows and columns that can not be reached)
    return new BitVector(191, 0, 0, false, "HasOb");
  }

  private BitVector(final int max, final int nextValue, final int offset, final boolean clearValue, final String name) {
    this.name = name;
    this.max = max;
    this.nextValue = nextValue;
    this.offset = offset;
    this.clearValue = clearValue;
      vector = new boolean[max];
    clear();
    reset();
  }

  public final void reset() {
    if (max == 34) {
      value = max;
    } else {
      value = max - 1;
    }
  }

  public final void clear() {
    for (int i = 0; i < max; i++) {
      vector[i] = clearValue;
    }
  }

  public int getMax() {
    return max;
  }

  public boolean get(final int idx) {
    return vector[idx];
  }

  public void set(final int idx, final boolean value) {
    vector[idx] = value;
  }

  public int change(final RNG rnd) {
    if (value <= 0) {
      return -1;
    }
    if (value == 1) {
      for (int i = 0; i < max; i++) {
        if (vector[i]) {
          vector[i] = false;
          value = nextValue;
          return i;
        }
      }
    }
    int r = rnd.getRandom(value);
    this.value--;
    return change2(r);
  }

  private int change2(int temp) {
    //TODO original code didn't hava an exit condition for the loop
    for (int i = 0; i < max; i++) {
      //TODO index out of bound exception occurred for x=27,y=0,t=swamp
      if (vector[i]) {
        if (temp == 0) {
          vector[i] = false;
          return i + offset;
        }
        temp--;
      }
    }
    return -1;
  }
}
