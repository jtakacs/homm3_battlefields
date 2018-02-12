package battlefieldexplorer.generator;

public final class RNG {

  public static final int W_X = 0x1AED3; // 110291
  public static final int W_Y = 0x28F79; // 167801
  public static final int W_Z = 0x13EA1; // 81569
  public static final int RND_M = 0x343FD; // 214013
  public static final int RND_A = 0x269EC3; // 2531011

  private int seed;

  private RNG(final int seed) {
    this.seed = seed;
    //Battlefield music id.
    getRandomInterval(8, 1);
  }

  public static RNG randomSeed(final int x, final int y) {
    return new RNG(x * W_X + y * W_Y + W_Z);
  }

  private int nextValue() {
    seed = seed * RND_M + RND_A;
    return seed;
  }

  public int getRandom(final int value) {
    return ((nextValue() >>> 16) & 0x7FFF) % value;
  }

  public int getRandomInterval(final int high, final int low) {
    return getRandom(high - low + 1) + low;
  }

}
