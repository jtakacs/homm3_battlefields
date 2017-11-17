package battlefieldexplorer.gui;

import static javax.swing.SwingConstants.LEFT;
import static javax.swing.SwingConstants.TOP;
import static javax.swing.SwingUtilities.convertPoint;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.UIManager.getInstalledLookAndFeels;
import static javax.swing.UIManager.setLookAndFeel;
import battlefieldexplorer.generator.*;
import battlefieldexplorer.search.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import javax.swing.text.*;
import org.oxbow.swingbits.table.filter.TableRowFilterSupport;

//TODO cleanup
public class Gui2 extends JFrame {

  private static final long serialVersionUID = 1L;
  private final FakeCursor fakeCursor;
  private final SpinnerIntModel mapX = new SpinnerIntModel(3, 0, 143, 1);
  private final SpinnerIntModel mapY = new SpinnerIntModel(0, 0, 143, 1);
  private final HexGrid hexGrid;
  private final ResultTableModel tm = new ResultTableModel();
  private final Loading loading;
  private final Gui2 rootFrame;

  public Gui2() {
    rootFrame = this;
    fakeCursor = new FakeCursor(this);
    initComponents();
    setLocationRelativeTo(null);
    setEnabled(false);
    loading = new Loading(loadingIndicator);
    loading.start();
    this.hexGrid = new HexGrid();
    jTable1.getSelectionModel().addListSelectionListener(new RowSelectionListener(jTable1, tm, rootFrame));
    HexGrid.createHexGrid(hexLayer, hexGrid);
    TableRowFilterSupport
      .forTable(jTable1)
      .actions(true)
      .searchable(true)
      .apply();
    KeyboardFocusManager.getCurrentKeyboardFocusManager()
      .addKeyEventDispatcher(e -> {
        if (KeyEvent.KEY_RELEASED == e.getID()) {
          return keyNavigation(e);
        }
        return false;
      });
    new Thread(() -> {
      BattleFieldInfo.load();
      invokeLater(() -> {
        loading.stop();
        rootFrame.setEnabled(true);
      });
    }).start();
  }

  public void loadBattleField() {
    TerrainInfo.instance()
      .get(terrainList.getSelectedIndex())
      .ifPresent(terrain -> displayBattlefield(
      BattleFieldInfo.load().get(mapX.value(), mapY.value(), terrain))
      );
  }

  public void displayBattlefield(final Battlefield bf) {
    invokeLater(() -> {
      background.setIcon(TerrainImages.getImage(bf.terrain));
      background.updateUI();
      placeObstacles(bf.obstacles);
      setPassability(bf.getBlockedHexes());
      setControlState(bf);
    });
  }

  private void placeObstacles(java.util.List<PositionedObstacle> obstacles) {
    obstacleLayer.removeAll();
    Collections.sort(obstacles);
    for (final PositionedObstacle po : obstacles) {
      final ImageIcon im = po.obstacle.getImage();
      final JLabel L = new JLabel(im);
      L.setVerticalAlignment(TOP);
      L.setHorizontalAlignment(LEFT);
      obstacleLayer.add(L);
      L.setBounds(po.getScreenX(), po.getScreenY(), im.getIconWidth(), im.getIconHeight());
    }
    obstacleLayer.updateUI();
  }

  private void setPassability(java.util.Set<Integer> obstacles) {
    Passability.createOverlay(passabilityLayer, obstacles);
  }

  public void setControlState(final Battlefield bf) {
    xSpinner.setValue(bf.mapX);
    ySpinner.setValue(bf.mapY);
    terrainList.getSelectionModel().setSelectionInterval(0, bf.terrain.ID);
    xSpinner.updateUI();
    ySpinner.updateUI();
    terrainList.updateUI();
  }

  private void clearHexGrid() {
    hexGrid.clear();
    hexLayer.updateUI();
    for (Component c : hexLayer.getComponents()) {
      if (c instanceof JComponent) {
        ((JComponent) c).updateUI();
      }
    }
  }

  private void changeTerrainListSelection(final int dir) {
    if (dir == 1 || dir == -1) {
      final int s = dir + terrainList.getSelectedIndex();
      if (s >= 0 && s < Terrain.values().length) {
        terrainList.getSelectionModel().setSelectionInterval(0, s);
      }
    }
  }

  public void terrainCursor(MouseEvent evt) {
    final MouseEvent e = SwingUtilities.convertMouseEvent(evt.getComponent(), evt, terrainList);
    final int idx = terrainList.locationToIndex(new Point(e.getX(), e.getY()));
    Terrain.get(idx).ifPresent(terrain -> {
      if (Terrain.SHIP.equals(terrain)) {
        fakeCursor.setImg(2);
      } else {
        fakeCursor.setImg(1);
      }
    });
  }

  private void showHelpText() {
    jTextPane1.setText("");

  }

  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPanel1 = new JPanel();
    jScrollPane1 = new JScrollPane();
    terrainList = new JList<>();
    jLabel1 = new JLabel();
    xSpinner = new JSpinner();
    jLabel2 = new JLabel();
    ySpinner = new JSpinner();
    showHex = new JCheckBox();
    showObst = new JCheckBox();
    showBlocked = new JCheckBox();
    searchButton = new JButton();
    clearButton = new JButton();
    resultsLabel = new JLabel();
    jLayeredPane1 = new JLayeredPane();
    backgroundLayer = new JPanel();
    background = new JLabel();
    hexLayer = new JPanel();
    obstacleLayer = new JPanel();
    passabilityLayer = new JPanel();
    loadingIndicator = new JPanel();
    jScrollPane2 = new JScrollPane();
    jTable1 = new JTable();
    jScrollPane3 = new JScrollPane();
    jTextPane1 = new JTextPane();

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setMinimumSize(new Dimension(820, 600));

    jPanel1.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
    jPanel1.setMaximumSize(new Dimension(220, 556));
    jPanel1.setMinimumSize(new Dimension(220, 556));
    jPanel1.setPreferredSize(new Dimension(220, 556));

    terrainList.setModel(TerrainInfo.instance());
    terrainList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    terrainList.setDoubleBuffered(true);
    terrainList.setMaximumSize(new Dimension(150, 85));
    terrainList.setMinimumSize(new Dimension(150, 85));
    terrainList.setPreferredSize(new Dimension(150, 85));
    terrainList.setRequestFocusEnabled(false);
    terrainList.setSelectedIndex(0);
    terrainList.setVisibleRowCount(19);
    terrainList.addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent evt) {
        terrainListMouseMoved(evt);
      }
    });
    terrainList.addMouseListener(new MouseAdapter() {
      public void mouseExited(MouseEvent evt) {
        terrainListMouseExited(evt);
      }
    });
    terrainList.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent evt) {
        terrainListValueChanged(evt);
      }
    });
    jScrollPane1.setViewportView(terrainList);

    jLabel1.setText("X: ");

    xSpinner.setModel(mapX);
    xSpinner.setMaximumSize(new Dimension(56, 20));
    xSpinner.setRequestFocusEnabled(false);
    xSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        xSpinnerStateChanged(evt);
      }
    });

    jLabel2.setText("Y: ");

    ySpinner.setModel(mapY);
    ySpinner.setMaximumSize(new Dimension(56, 20));
    ySpinner.setRequestFocusEnabled(false);
    ySpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        ySpinnerStateChanged(evt);
      }
    });

    showHex.setSelected(true);
    showHex.setText("Show hex grid");
    showHex.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        showHexStateChanged(evt);
      }
    });

    showObst.setSelected(true);
    showObst.setText("Show obstacles");
    showObst.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        showObstStateChanged(evt);
      }
    });

    showBlocked.setSelected(true);
    showBlocked.setText("Passability");
    showBlocked.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        showBlockedStateChanged(evt);
      }
    });

    searchButton.setText("Search");
    searchButton.addMouseListener(new MouseAdapter() {
      public void mouseExited(MouseEvent evt) {
        searchButtonMouseExited(evt);
      }
      public void mouseEntered(MouseEvent evt) {
        searchButtonMouseEntered(evt);
      }
    });
    searchButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        searchButtonActionPerformed(evt);
      }
    });

    clearButton.setText("Clear grid");
    clearButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        clearButtonActionPerformed(evt);
      }
    });

    resultsLabel.setText("   ");
    resultsLabel.setMaximumSize(new Dimension(300, 15));

    GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(resultsLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING, false)
              .addComponent(showBlocked)
              .addComponent(showObst)
              .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(xSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addGap(6, 6, 6)
                .addComponent(ySpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
              .addComponent(showHex)
              .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(searchButton)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(clearButton))
              .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
            .addGap(0, 4, Short.MAX_VALUE)))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(xSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel2)
          .addComponent(ySpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(showHex)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(showObst)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(showBlocked)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
          .addComponent(searchButton)
          .addComponent(clearButton))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(resultsLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(78, Short.MAX_VALUE))
    );

    jLayeredPane1.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
    jLayeredPane1.setDoubleBuffered(true);
    jLayeredPane1.setMaximumSize(new Dimension(800, 556));
    jLayeredPane1.setMinimumSize(new Dimension(800, 556));
    jLayeredPane1.setLayout(new OverlayLayout(jLayeredPane1));

    backgroundLayer.setMaximumSize(new Dimension(800, 556));
    backgroundLayer.setMinimumSize(new Dimension(800, 556));
    backgroundLayer.setOpaque(false);
    backgroundLayer.setPreferredSize(new Dimension(800, 556));
    backgroundLayer.setLayout(new BorderLayout());

    background.setHorizontalAlignment(SwingConstants.CENTER);
    background.setIcon(new ImageIcon(getClass().getResource("/battlefields/CmBkBoat.png"))); // NOI18N
    background.setDoubleBuffered(true);
    background.setHorizontalTextPosition(SwingConstants.CENTER);
    backgroundLayer.add(background, BorderLayout.CENTER);

    jLayeredPane1.setLayer(backgroundLayer, 10);
    jLayeredPane1.add(backgroundLayer);

    hexLayer.setMaximumSize(new Dimension(800, 556));
    hexLayer.setMinimumSize(new Dimension(800, 556));
    hexLayer.setOpaque(false);
    hexLayer.setLayout(null);
    jLayeredPane1.setLayer(hexLayer, 20);
    jLayeredPane1.add(hexLayer);

    obstacleLayer.setMaximumSize(new Dimension(800, 556));
    obstacleLayer.setMinimumSize(new Dimension(800, 556));
    obstacleLayer.setOpaque(false);
    obstacleLayer.setPreferredSize(new Dimension(800, 556));
    obstacleLayer.setLayout(null);
    jLayeredPane1.setLayer(obstacleLayer, 30);
    jLayeredPane1.add(obstacleLayer);

    passabilityLayer.setMaximumSize(new Dimension(800, 556));
    passabilityLayer.setMinimumSize(new Dimension(800, 556));
    passabilityLayer.setOpaque(false);
    passabilityLayer.setLayout(null);
    jLayeredPane1.setLayer(passabilityLayer, 40);
    jLayeredPane1.add(passabilityLayer);

    loadingIndicator.setMaximumSize(new Dimension(800, 556));
    loadingIndicator.setMinimumSize(new Dimension(800, 556));
    loadingIndicator.setOpaque(false);
    loadingIndicator.setLayout(new BorderLayout());
    jLayeredPane1.setLayer(loadingIndicator, 50);
    jLayeredPane1.add(loadingIndicator);

    jTable1.setModel(tm);
    jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jScrollPane2.setViewportView(jTable1);

    jScrollPane3.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));

    jTextPane1.setEditable(false);
    jTextPane1.setBorder(null);
    jTextPane1.setContentType("text/html"); // NOI18N
    jTextPane1.setText("<html>\n  <head>\n  </head>\n  <body>\n    <table>\n<tr><td><b>W, S:</b></td><td> change Y pos.</td></tr>\n<tr><td><b>A, D:</b></td><td> change X pos.</td></tr>\n<tr><td><b>T, G:</b></td><td> change terrain</td></tr>\n<tr><td><b>O:</b></td><td> toggle obstacles</td></tr>\n<tr><td><b>P:</b></td><td> toggle passability</td></tr>\n<tr><td><b>H: </b></td><td> toggle hex layer</td></tr>\n    </table>\n  </body>\n</html>\n"); // NOI18N
    jTextPane1.setMaximumSize(new Dimension(220, 100));
    jTextPane1.setMinimumSize(new Dimension(220, 100));
    jTextPane1.setRequestFocusEnabled(false);
    jScrollPane3.setViewportView(jTextPane1);

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
          .addComponent(jScrollPane2)
          .addComponent(jLayeredPane1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
          .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
          .addComponent(jScrollPane3))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
          .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(jLayeredPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
          .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
          .addComponent(jScrollPane3))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void terrainListValueChanged(ListSelectionEvent evt) {//GEN-FIRST:event_terrainListValueChanged
    loadBattleField();
  }//GEN-LAST:event_terrainListValueChanged

  private void searchButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
    loading.start();
    new Thread(() -> {
      java.util.List<Battlefield> result = Search.search(hexGrid, false);
      tm.addAll(result);
      invokeLater(() -> {
        resultsLabel.setText("" + result.size() + " matches");
        jTable1.updateUI();
        loading.stop();
      });
    }).start();

  }//GEN-LAST:event_searchButtonActionPerformed

  private void clearButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
    clearHexGrid();
  }//GEN-LAST:event_clearButtonActionPerformed

  private void xSpinnerStateChanged(ChangeEvent evt) {//GEN-FIRST:event_xSpinnerStateChanged
    loadBattleField();
  }//GEN-LAST:event_xSpinnerStateChanged

  private void ySpinnerStateChanged(ChangeEvent evt) {//GEN-FIRST:event_ySpinnerStateChanged
    loadBattleField();
  }//GEN-LAST:event_ySpinnerStateChanged

  //<editor-fold defaultstate="collapsed" desc="keyNavigation">
  private boolean keyNavigation(final KeyEvent evt) {
//    System.out.println(evt.getComponent());
    switch (evt.getKeyCode()) {
      case KeyEvent.VK_P: {
        showBlocked.setSelected(!showBlocked.isSelected());
        passabilityLayer.setVisible(showBlocked.isSelected());
        return true;
      }
      case KeyEvent.VK_C: {
        clearHexGrid();
        return true;
      }
      case KeyEvent.VK_O: {
        showObst.setSelected(!showObst.isSelected());
        obstacleLayer.setVisible(showObst.isSelected());
        return true;
      }
      case KeyEvent.VK_H: {
        showHex.setSelected(!showHex.isSelected());
        hexLayer.setVisible(showHex.isSelected());
        return true;
      }
      case KeyEvent.VK_T: {
        changeTerrainListSelection(-1);
        return true;
      }
      case KeyEvent.VK_G: {
        changeTerrainListSelection(1);
        return true;
      }
      case KeyEvent.VK_W: {
        mapY.decrement();
        return true;
      }
      case KeyEvent.VK_S: {
        mapY.increment();
        return true;
      }
      case KeyEvent.VK_A: {
        mapX.decrement();
        return true;
      }
      case KeyEvent.VK_D: {
        mapX.increment();
        return true;
      }
      default:
    }
    return false;
  }
  //</editor-fold>

  private void showHexStateChanged(ChangeEvent evt) {//GEN-FIRST:event_showHexStateChanged
    hexLayer.setVisible(showHex.isSelected());
  }//GEN-LAST:event_showHexStateChanged

  private void showObstStateChanged(ChangeEvent evt) {//GEN-FIRST:event_showObstStateChanged
    obstacleLayer.setVisible(showObst.isSelected());
  }//GEN-LAST:event_showObstStateChanged

  private void showBlockedStateChanged(ChangeEvent evt) {//GEN-FIRST:event_showBlockedStateChanged
    passabilityLayer.setVisible(showBlocked.isSelected());
  }//GEN-LAST:event_showBlockedStateChanged

  private void terrainListMouseMoved(MouseEvent evt) {//GEN-FIRST:event_terrainListMouseMoved
    terrainCursor(evt);
  }//GEN-LAST:event_terrainListMouseMoved

  private void terrainListMouseExited(MouseEvent evt) {//GEN-FIRST:event_terrainListMouseExited
    fakeCursor.setImg(0);
  }//GEN-LAST:event_terrainListMouseExited

  private void searchButtonMouseEntered(MouseEvent evt) {//GEN-FIRST:event_searchButtonMouseEntered
    fakeCursor.setImg(3);
  }//GEN-LAST:event_searchButtonMouseEntered

  private void searchButtonMouseExited(MouseEvent evt) {//GEN-FIRST:event_searchButtonMouseExited
    fakeCursor.setImg(0);
  }//GEN-LAST:event_searchButtonMouseExited

  //<editor-fold defaultstate="collapsed" desc=" Generated code ">
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private JLabel background;
  private JPanel backgroundLayer;
  private JButton clearButton;
  private JPanel hexLayer;
  private JLabel jLabel1;
  private JLabel jLabel2;
  private JLayeredPane jLayeredPane1;
  private JPanel jPanel1;
  private JScrollPane jScrollPane1;
  private JScrollPane jScrollPane2;
  private JScrollPane jScrollPane3;
  private JTable jTable1;
  private JTextPane jTextPane1;
  private JPanel loadingIndicator;
  private JPanel obstacleLayer;
  private JPanel passabilityLayer;
  private JLabel resultsLabel;
  private JButton searchButton;
  private JCheckBox showBlocked;
  private JCheckBox showHex;
  private JCheckBox showObst;
  private JList<String> terrainList;
  private JSpinner xSpinner;
  private JSpinner ySpinner;
  // End of variables declaration//GEN-END:variables
  //</editor-fold>

  public static void start() {
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    try {
      for (LookAndFeelInfo info : getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
    }
    //</editor-fold>
    invokeLater(() -> new Gui2().setVisible(true));
  }

}
