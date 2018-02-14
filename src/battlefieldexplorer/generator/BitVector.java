package battlefieldexplorer.generator;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class BitVector implements Cloneable {

  private static final long masks[] = new long[64];
  private static final long notmasks[] = new long[64];
  private static final long shiftRightMasks[] = new long[64];
  private static final long shiftLeftMasks[] = new long[64];
  protected long vector0 = 0L;
  protected long vector1 = 0L;
  protected long vector2 = 0L;
  protected long vector3 = 0L;

  static {
    for (int i = 0; i < 64; i++) {
      if (i == 0) {
        shiftRightMasks[0] = 0L;
      } else {
        shiftRightMasks[i] = (shiftRightMasks[i - 1] << 1) | 1L;
      }
      masks[i] = 1L << i;
      notmasks[i] = ~masks[i];
    }
    shiftLeftMasks[0] = 0L;
    shiftLeftMasks[1] = masks[63];
    for (int i = 2; i < 64; i++) {
      shiftLeftMasks[i] = (shiftLeftMasks[i - 1] >>> 1) | masks[63];
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
    int c;
    if (n >= (c = 3 * 64)) {
      vector3 |= masks[n - c];
    } else if (n >= (c = 2 * 64)) {
      vector2 |= masks[n - c];
    } else if (n >= (c = 64)) {
      vector1 |= masks[n - c];
    } else {
      vector0 |= masks[n];
    }
  }

  public void clear(final int n) {
    int c;
    if (n >= (c = 3 * 64)) {
      vector3 &= notmasks[n - c];
    } else if (n >= (c = 2 * 64)) {
      vector2 &= notmasks[n - c];
    } else if (n >= (c = 64)) {
      vector1 &= notmasks[n - c];
    } else {
      vector0 &= notmasks[n];
    }
  }

  public boolean get(final int n) {
    int c;
    if (n >= (c = 3 * 64)) {
      return 0 != (vector3 & masks[n - c]);
    } else if (n >= (c = 2 * 64)) {
      return 0 != (vector2 & masks[n - c]);
    } else if (n >= (c = 64)) {
      return 0 != (vector1 & masks[n - c]);
    } else {
      return 0 != (vector0 & masks[n]);
    }
  }

  public BitVector not() {
    final BitVector result = new BitVector();
    result.vector0 = ~vector0;
    result.vector1 = ~vector1;
    result.vector2 = ~vector2;
    result.vector3 = ~vector3;
    return result;
  }

  public BitVector and(final BitVector b) {
    final BitVector result = new BitVector();
    result.vector0 = this.vector0 & b.vector0;
    result.vector1 = this.vector1 & b.vector1;
    result.vector2 = this.vector2 & b.vector2;
    result.vector3 = this.vector3 & b.vector3;
    return result;
  }

  public BitVector andNot(final BitVector b) {
    return this.and(b.not());
  }

  public void shiftRight(final int i) {
    int c;
    if (i >= (c = 3 * 64)) {
      final int n = i - c;
      vector0 = vector3 >>> n;
      vector1 = 0;
      vector2 = 0;
      vector3 = 0;
    } else if (i >= (c = 2 * 64)) {
      final int n = i - c;
      vector0 = (vector2 >>> n) | ((vector3 & shiftRightMasks[n]) << (64 - n));
      vector1 = vector3 >>> n;
      vector2 = 0;
      vector3 = 0;
    } else if (i >= (c = 64)) {
      final int n = i - c;
      vector0 = (vector1 >>> n) | ((vector2 & shiftRightMasks[n]) << (64 - n));
      vector1 = (vector2 >>> n) | ((vector3 & shiftRightMasks[n]) << (64 - n));
      vector2 = vector3 >>> n;
      vector3 = 0;
    } else {
      vector0 = (vector0 >>> i) | ((vector1 & shiftRightMasks[i]) << (64 - i));
      vector1 = (vector1 >>> i) | ((vector2 & shiftRightMasks[i]) << (64 - i));
      vector2 = (vector2 >>> i) | ((vector3 & shiftRightMasks[i]) << (64 - i));
      vector3 = vector3 >>> i;
    }
  }

  public void shiftLeft(final int i) {
    int c;
    if (i >= (c = 3 * 64)) {
      final int n = i - c;
      vector3 = vector0 << n;
      vector2 = 0;
      vector1 = 0;
      vector0 = 0;
    } else if (i >= (c = 2 * 64)) {
      final int n = i - c;
      vector3 = (vector1 << n) | ((vector0 & shiftLeftMasks[n]) >>> (64 - n));
      vector2 = vector0 << n;
      vector1 = 0;
      vector0 = 0;
    } else if (i >= (c = 64)) {
      final int n = i - c;
      vector3 = (vector2 << n) | ((vector1 & shiftLeftMasks[n]) >>> (64 - n));
      vector2 = (vector1 << n) | ((vector0 & shiftLeftMasks[n]) >>> (64 - n));
      vector1 = vector0 << n;
      vector0 = 0;
    } else {
      vector3 = (vector3 << i) | ((vector2 & shiftLeftMasks[i]) >>> (64 - i));
      vector2 = (vector2 << i) | ((vector1 & shiftLeftMasks[i]) >>> (64 - i));
      vector1 = (vector1 << i) | ((vector0 & shiftLeftMasks[i]) >>> (64 - i));
      vector0 = vector0 << i;
    }
  }

  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException ex) {
      System.out.println("CLONING ERROR: " + ex.getMessage());
      ex.printStackTrace();
    }
    return new BitVector();
  }

  public static void main(String[] args) {
    for (int i = 0; i < 64; i++) {
      System.out.println(String.format("    mask[%02d]= %64s", i, Long.toBinaryString(masks[i])));
    }
    for (int i = 0; i < 64; i++) {
      System.out.println(String.format(" notmask[%02d]= %64s", i, Long.toBinaryString(notmasks[i])));
    }
    for (int i = 0; i < 64; i++) {
      System.out.println(String.format("rghtmask[%02d]= %64s", i, Long.toBinaryString(shiftRightMasks[i])));
    }
    for (int i = 0; i < 64; i++) {
      System.out.println(String.format("leftmask[%02d]= %64s", i, Long.toBinaryString(shiftLeftMasks[i])));
    }
  }

//  public BitVector copy() {
//    final BitVector result = new BitVector();
//    result.vector0 = this.vector0;
//    result.vector1 = this.vector1;
//    result.vector2 = this.vector2;
//    result.vector3 = this.vector3;
//    return result;
//  }
  @Override
  public int hashCode() {
    int hash = 7;
    hash = 79 * hash + (int) (this.vector0 ^ (this.vector3 >>> 32));
    hash = 79 * hash + (int) (this.vector1 ^ (this.vector2 >>> 32));
    hash = 79 * hash + (int) (this.vector2 ^ (this.vector1 >>> 32));
    hash = 79 * hash + (int) (this.vector3 ^ (this.vector0 >>> 32));
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final BitVector other = (BitVector) obj;
    if (this.vector0 != other.vector0) {
      return false;
    }
    if (this.vector1 != other.vector1) {
      return false;
    }
    if (this.vector2 != other.vector2) {
      return false;
    }
    return this.vector3 == other.vector3;
  }

  @Override
  public String toString() {
    return ""
           + Long.toBinaryString(vector3)
           + Long.toBinaryString(vector2)
           + Long.toBinaryString(vector1)
           + Long.toBinaryString(vector0);
  }

}
