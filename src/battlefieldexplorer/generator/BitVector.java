package battlefieldexplorer.generator;

public final class BitVector {

  private static final long masks[] = new long[64];
  private static final long notmasks[] = new long[64];
  private final long vector[] = new long[4];

  static {
    for (int i = 0; i < 64; i++) {
      masks[i] = 1L << i;
      notmasks[i] = ~masks[i];
    }
  }

  public void set(final int n, final boolean value) {
    if (value) {
      this.set(n);
    } else {
      this.clear(n);
    }
  }

  public void set(final int n) {
    vector[n / 64] |= masks[n % 64];
  }

  public void clear(final int n) {
    vector[n / 64] &= notmasks[n % 64];
  }

  public boolean get(final int n) {
    return 0 != (vector[n / 64] & masks[n % 64]);
  }

}
