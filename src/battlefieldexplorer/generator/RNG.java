package battlefieldexplorer.generator;

public final class RNG {

  private volatile int seed;

  private RNG(final int seed) {
    this.seed = seed;
    //Battlefield music id.
    getRandomInterval(8, 1); 
  }

  public static RNG randomSeed(final int x, final int y) {
    return new RNG(0x1AED3 * x+ 0x28F79 * y + 0x13EA1);
  }

  private int nextValue() {
    seed = seed * 0x343FD + 0x269EC3;
    return seed;
  }

  public int getRandom(final int value) {
    return ((nextValue() >>> 16) & 0x7FFF) % value;
  }

  public int getRandomInterval(final int high, final int low) {
    return getRandom(high - low + 1) + low;
  }

}
