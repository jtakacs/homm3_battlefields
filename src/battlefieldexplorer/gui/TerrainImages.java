package battlefieldexplorer.gui;

import battlefieldexplorer.generator.Terrain;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.swing.ImageIcon;

public final class TerrainImages {

  private static final ConcurrentMap<Terrain, ImageIcon> images = new ConcurrentHashMap<>();

  public static ImageIcon getImage(final Terrain terrain) {
    return images.computeIfAbsent(terrain, t -> new ImageIcon(TerrainImages.class.getResource("/battlefields/" + t.image)));
  }

  private TerrainImages() {
  }

}
