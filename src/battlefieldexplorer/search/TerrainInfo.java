package battlefieldexplorer.search;

import battlefieldexplorer.generator.Terrain;
import java.util.Optional;
import javax.swing.AbstractListModel;

public final class TerrainInfo extends AbstractListModel<String> {

  private static final long serialVersionUID = 1L;

  private static class TerrainInfoLoader {

    static final TerrainInfo INSTANCE = new TerrainInfo();

    private TerrainInfoLoader() {
    }
  }

  public static TerrainInfo instance() {
    return TerrainInfoLoader.INSTANCE;
  }

  private TerrainInfo() {
  }

  @Override
  public int getSize() {
    return Terrain.values().length;
  }

  @Override
  public String getElementAt(int index) {
    return Terrain.get(index).map(t -> " "+t.description).orElse("");
  }

  public Optional<Terrain> get(int index) {
    return Terrain.get(index);
  }
}
