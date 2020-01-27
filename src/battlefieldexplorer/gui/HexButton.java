package battlefieldexplorer.gui;

import static battlefieldexplorer.util.Constants.cellH;
import static battlefieldexplorer.util.Constants.cellW;
import static battlefieldexplorer.util.HexCellState.*;
import static battlefieldexplorer.util.HexTools.posToHex;
import static java.awt.event.MouseEvent.BUTTON1;
import static java.awt.event.MouseEvent.BUTTON3;
import battlefieldexplorer.util.HexCellState;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public final class HexButton extends JButton {

  private static final ImageIcon iconNormal = new ImageIcon(HexButton.class.getResource("/CCellGrd.png"));
  private static final ImageIcon iconEnabled = new ImageIcon(HexButton.class.getResource("/CCellGrdSel.png"));
  private static final ImageIcon iconDisabled = new ImageIcon(HexButton.class.getResource("/CCellGrdDis.png"));
  private static final long serialVersionUID = 1L;
  private final int x;
  private final int y;
  private final HexGrid grid;

  public HexButton(final int x, final int y, final HexGrid grid) {
    this.x = x;
    this.y = y;
    this.grid = grid;
    init();
  }

  private void init() {
    final Dimension size = new Dimension(cellW, cellH);
    setSize(size);
    setMinimumSize(size);
    setPreferredSize(size);
    setMaximumSize(new Dimension(cellW + 8, cellH));
    setBackground(new java.awt.Color(255, 255, 255, 0));
    setBounds(0, 0, cellW, cellH);
    setDoubleBuffered(true);
    setBorder(null);
    setBorderPainted(false);
    setContentAreaFilled(false);
    setHorizontalTextPosition(CENTER);
    setHorizontalAlignment(CENTER);
    setVerticalTextPosition(CENTER);
    setIconTextGap(0);
    setMargin(new Insets(0, 0, 0, 0));
    setIcon(iconNormal);
    setPressedIcon(iconEnabled);
    setSelectedIcon(iconEnabled);
    setToolTipText("" + posToHex(x, y));
    setForeground(new Color(255, 255, 0));
    setText("" + posToHex(x, y));
    setVerifyInputWhenFocusTarget(false);
    setInheritsPopupMenu(false);
    setFocusable(false);
    setFocusPainted(false);
  }

  public void addToContainer(final JComponent container, final int posX, final int posY) {
    container.add(this);
    setBounds(posX, posY, cellW + 8, cellH);
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e) {
        final HexCellState actual = grid.getState(x, y);
        if (BUTTON1 == e.getButton()) {
          if (ENABLED.equals(actual)) {
            grid.setState(x, y, NONE);
            setIcon(iconNormal);
            setSelectedIcon(iconEnabled);
            setPressedIcon(iconEnabled);
          } else {
            grid.setState(x, y, ENABLED);
            setIcon(iconEnabled);
            setSelectedIcon(iconEnabled);
            setPressedIcon(iconEnabled);
          }
        } else if (BUTTON3 == e.getButton()) {
          if (DISABLED.equals(actual)) {
            grid.setState(x, y, NONE);
            setIcon(iconNormal);
            setSelectedIcon(iconEnabled);
            setPressedIcon(iconEnabled);
          } else {
            grid.setState(x, y, DISABLED);
            setIcon(iconDisabled);
            setPressedIcon(iconDisabled);
            setSelectedIcon(iconDisabled);
          }
        }
      }
    });
  }

  @Override
  public void updateUI() {
    super.updateUI();
    if (grid != null) {
      switch (grid.getState(x, y)) {
        case DISABLED:
          setIcon(iconDisabled);
          setPressedIcon(iconDisabled);
          setSelectedIcon(iconDisabled);
          break;
        case ENABLED:
          setIcon(iconEnabled);
          setSelectedIcon(iconEnabled);
          setPressedIcon(iconEnabled);
          break;
        case NONE:
          setIcon(iconNormal);
          setSelectedIcon(iconEnabled);
          setPressedIcon(iconEnabled);
          break;
      }
    }
  }
}
