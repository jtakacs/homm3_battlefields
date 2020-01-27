package battlefieldexplorer.generator;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public enum Terrain {
  DIRT(0, 1, false, "Dirt", "CmBkDrMt.png"),
  SAND(1, 2, false, "Sand", "CmBkDes.png"),
  GRASS(2, 4, false, "Grass", "CmBkGrMt.png"),
  SNOW(3, 8, false, "Snow", "CmBkSnTr.png"),
  SWAMP(4, 16, false, "Swamp", "CmBkSwmp.png"),
  ROUGH(5, 32, false, "Rough", "CmBkRgh.png"),
  UNDERGROUND(6, 64, false, "Underground", "CmBkSub.png"),
  LAVA(7, 128, false, "Lava", "CmBkLava.png"),
  SHIP(8, 256, false, "Ship deck", "CmBkDeck.png"),
  SHORE(9, 1, true, "Coast", "CmBkBch.png"),
  MAGIC_PLAINS(10, 2, true, "Magic plains", "CmBkMag.png"),
  CURSED_GROUND(11, 4, true, "Cursed ground", "CmBkCur.png"),
  HOLY_GROUND(12, 8, true, "Holy ground", "CmBkHG.png"),
  EVIL_FOG(13, 16, true, "Evil fog", "CmBkEF.png"),
  CLOVER_FIELDS(14, 32, true, "Clover fields", "CmBkCF.png"),
  LUCID_POOLS(15, 64, true, "Lucid pools", "CmBkLP.png"),
  FIERY_FIELDS(16, 128, true, "Fiery fields", "CmBkFF.png"),
  ROCKLANDS(17, 256, true, "Rocklands", "CmBkRK.png"),
  MAGIC_CLOUDS(18, 512, true, "Magic clouds", "CmBkMC.png");

  public final int ID;
  public final int type;
  public final boolean special;
  public final String description;
  public final String image;

  private Terrain(final int ID, final int type, final boolean special, final String description, final String image) {
    this.ID = ID;
    this.special = special;
    this.type = special ? (type << 16) : type;
    this.description = description;
    this.image = image;
  }

  public static Optional<Terrain> get(final int idx) {
    return stream().filter(t -> t.ID == idx).findFirst();
  }

  public static Stream<Terrain> stream() {
    return Arrays.asList(Terrain.values()).stream();
  }

  @Override
  public String toString() {
    return "Terrain{" + "ID=" + ID + ", type=" + type + ", special=" + special + ", description=" + description + ", image=" + image + '}';
  }

}
