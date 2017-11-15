package battlefieldexplorer.generator;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class Obstacle {

  public final int ID;
  public final boolean absolute;
  public final int battlefields;
  public final int cellCount;
  public final List<Integer> cells;
  public final int screenX;
  public final int screenY;
  public final int width;
  public final int height;
  public final String image;
  public final int measure;
  private javax.swing.ImageIcon imageIcon = null;

  private Obstacle(
    final int ID, final boolean absolute, final int battlefields, final int specialbattlefields, final List<Integer> cells,
    final int screenX, final int screenY, final int width, final int height, final String image
  ) {
    this.ID = ID;
    this.absolute = absolute;
    this.battlefields = (specialbattlefields << 16) | battlefields;
    this.cells = new ArrayList<>();
    if (cells != null) {
      this.cells.addAll(cells);
    }
    this.screenX = screenX;
    this.screenY = screenY;
    this.width = width;
    this.height = height;
    this.image = image;
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
    return absolute -> battlefields -> specialbattlefields -> cells -> x -> y -> width -> height -> image
      -> new Obstacle(ID, absolute, battlefields, specialbattlefields, cells, x, y, width, height, image);
  }

  public static interface B1 {

    B2 absolute(boolean absolute);
  }

  public static interface B2 {

    B3 battlefields(int battlefields);
  }

  public static interface B3 {

    B4 specialbattlefields(int specialbattlefields);
  }

  public static interface B4 {

    B5 cells(List<Integer> cells);
  }

  public static interface B5 {

    B6 posX(int x);
  }

  public static interface B6 {

    B7 posY(int y);
  }

  public static interface B7 {

    B8 width(int width);
  }

  public static interface B8 {

    B9 height(int height);
  }

  public static interface B9 {

    Obstacle image(String image);
  }

  public static final String header() {
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
    return new StringBuilder()
      .append(ID).append(";")
      .append(absolute).append(";")
      .append(Integer.toHexString(battlefields)).append(";")
      .append(screenX).append(";")
      .append(screenY).append(";")
      .append(width).append(";")
      .append(height).append(";")
      .append(image).append(";")
      .append(cells.toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", ""))
      .toString();
  }

}
