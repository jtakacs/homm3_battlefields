package battlefieldexplorer.generator;

import static java.lang.Integer.toHexString;
import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class Obstacle {

  public final int ID;
  public final boolean absolute;
  public final int battlefields;
  public final int screenX;
  public final int screenY;
  public final int width;
  public final int height;
  public final String image;
  private ImageIcon imageIcon = null;
  public final List<Integer> cells;
  public final int cellCount;
  public final int measure;

  private Obstacle(
          final int ID, final boolean absolute, final int battlefields,
          final int screenX, final int screenY,
          final int width, final int height,
          final String image,
          final List<Integer> cells
  ) {
    this.ID = ID;
    this.absolute = absolute;
    this.battlefields = battlefields;
    this.screenX = screenX;
    this.screenY = screenY;
    this.width = width;
    this.height = height;
    this.image = image;
    this.cells = (cells == null)
                 ? unmodifiableList(new ArrayList<>())
                 : unmodifiableList(cells);
    this.cellCount = this.cells.size();
    this.measure = absolute ? (this.cellCount / 2) : this.cellCount;
  }

  public ImageIcon getImage() {
    if (imageIcon == null) {
      imageIcon = new ImageIcon(Obstacle.class.getResource("/obstacles/" + image));
    }
    return imageIcon;
  }

  public static B1 obstacleID(final int ID) {
    return absolute -> battlefields -> x -> y -> width -> height -> image -> cells
            -> new Obstacle(ID, absolute, battlefields, x, y, width, height, image, cells);
  }

  public static interface B1 {

    B2 absolute(boolean absolute);
  }

  public static interface B2 {

    B3 battlefields(int battlefields);
  }

  public static interface B3 {

    B4 posX(int x);
  }

  public static interface B4 {

    B5 posY(int y);
  }

  public static interface B5 {

    B6 width(int width);
  }

  public static interface B6 {

    B7 height(int height);
  }

  public static interface B7 {

    B8 image(String image);
  }

  public static interface B8 {

    Obstacle cells(List<Integer> cells);
  }

  public static final String csvHeader() {
    return "ID;"
           + "absolute;"
           + "battlefields(hex);"
           + "screenX;"
           + "screenY;"
           + "width;"
           + "height;"
           + "image;"
           + "cells";
  }

  @Override
  public final String toString() {
    final StringBuilder sb = new StringBuilder()
            .append(ID).append(";")
            .append(absolute).append(";")
            .append(toHexString(battlefields)).append(";")
            .append(screenX).append(";")
            .append(screenY).append(";")
            .append(width).append(";")
            .append(height).append(";")
            .append(image).append(";");
    for (int i = 0; i < cellCount; i++) {
      sb.append(i == 0 ? "" : ",").append(cells.get(i));
    }
    return sb.toString();
  }

}
