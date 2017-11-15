package battlefieldexplorer.gui;

import static battlefieldexplorer.util.HexTools.hexdump;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.UIManager.getInstalledLookAndFeels;
import static javax.swing.UIManager.setLookAndFeel;
import battlefieldexplorer.generator.*;
import battlefieldexplorer.search.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import org.oxbow.swingbits.table.filter.TableRowFilterSupport;

public final class Gui extends javax.swing.JFrame {

  private static final long serialVersionUID = 1L;
  private final HexGrid hexGrid;
  private final ResultTableModel tm = new ResultTableModel();
  private final Loading loadingIndicator;

  public Gui() {
    this.hexGrid = new HexGrid();
    setEnabled(false);
    initComponents();
    //<editor-fold defaultstate="collapsed" desc="set cursor">
    try {
      setIconImage(ImageIO.read(Gui.class.getResource("/Heroes_III_Icon.png")));
      final BufferedImage cursorImage = ImageIO.read(Gui.class.getResource("/cursor/cursors_205.png"));
      final Cursor cursor = Toolkit
        .getDefaultToolkit()
        .createCustomCursor(cursorImage, new Point(0, 0), "CustomCursor");
      getRootPane().setCursor(cursor);
      setCursor(cursor);
    } catch (IOException ex) {
    }
    //</editor-fold>
    loadingIndicator = new Loading(loading);
    jTable1.getSelectionModel().addListSelectionListener(new RowSelectionListener(jTable1, tm, background, obstacleLayer));
    HexGrid.createHexGrid(hexLayer, hexGrid);
    debugBtn.setVisible(false);
    canvas.updateUI();
    loadingIndicator.start();
    TableRowFilterSupport
      .forTable(jTable1)
      .actions(true)
      .searchable(true)
      .apply();
    final JFrame parent = this;
    new Thread(() -> {
      BattleFieldInfo.load();
      invokeLater(() -> {
        loadingIndicator.stop();
        parent.setEnabled(true);
      });
    }).start();
  }

  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    results = new javax.swing.JScrollPane();
    jTable1 = new javax.swing.JTable();
    controls = new javax.swing.JPanel();
    showgrid = new javax.swing.JCheckBox();
    jButton2 = new javax.swing.JButton();
    showObst = new javax.swing.JCheckBox();
    cursorNavigation = new javax.swing.JToggleButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    jList1 = new javax.swing.JList<>();
    jLabel2 = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    resultLabel = new javax.swing.JLabel();
    xSpinner = new javax.swing.JSpinner();
    ySpinner = new javax.swing.JSpinner();
    jButton1 = new javax.swing.JButton();
    debugBtn = new javax.swing.JButton();
    canvas = new javax.swing.JLayeredPane();
    backgroundLayer = new javax.swing.JPanel();
    background = new javax.swing.JLabel();
    hexLayer = new javax.swing.JPanel();
    obstacleLayer = new javax.swing.JPanel();
    loading = new javax.swing.JPanel();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("Battlefield Explorer");
    setMinimumSize(new java.awt.Dimension(1020, 750));
    setName("Battlefield Explorer"); // NOI18N

    results.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    results.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    results.setDoubleBuffered(true);
    results.setHorizontalScrollBar(null);
    results.setMaximumSize(new java.awt.Dimension(800, 200));
    results.setMinimumSize(new java.awt.Dimension(800, 200));
    results.setPreferredSize(new java.awt.Dimension(800, 200));

    jTable1.setAutoCreateRowSorter(true);
    jTable1.setModel(tm);
    jTable1.setDoubleBuffered(true);
    jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    results.setViewportView(jTable1);

    getContentPane().add(results, java.awt.BorderLayout.SOUTH);

    controls.setMaximumSize(new java.awt.Dimension(500, 2000));
    controls.setMinimumSize(new java.awt.Dimension(200, 556));
    controls.setPreferredSize(new java.awt.Dimension(200, 556));

    showgrid.setSelected(true);
    showgrid.setText("Show grid");
    showgrid.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        showgridActionPerformed(evt);
      }
    });

    jButton2.setText("Clear grid");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton2ActionPerformed(evt);
      }
    });

    showObst.setSelected(true);
    showObst.setText("Show obstacles");
    showObst.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        showObstActionPerformed(evt);
      }
    });

    cursorNavigation.setText("Cursor navigation");
    cursorNavigation.setToolTipText("<html><b>Cursors:</b> change map coordinates<br/>\n<b>PageUp, PageDown:</b> change terrain<br/>\n<b>O:</b> toggle obstacle view<br/>\n<b>H:</b> toggle hex grid<br/>");
    cursorNavigation.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        cursorNavigationFocusLost(evt);
      }
    });
    cursorNavigation.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(java.awt.event.KeyEvent evt) {
        cursorNavigationKeyReleased(evt);
      }
    });

    jList1.setModel(TerrainInfo.instance());
    jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    jList1.setDoubleBuffered(true);
    jList1.setMaximumSize(new java.awt.Dimension(1000, 1000));
    jList1.setMinimumSize(new java.awt.Dimension(200, 300));
    jList1.setPreferredSize(new java.awt.Dimension(200, 300));
    jList1.setSelectedIndex(0);
    jList1.setVisibleRowCount(20);
    jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
        jList1ValueChanged(evt);
      }
    });
    jScrollPane1.setViewportView(jList1);

    jLabel2.setText("X:");

    jLabel3.setText("Y: ");

    resultLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    resultLabel.setText(" ");
    resultLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    resultLabel.setDoubleBuffered(true);
    resultLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    resultLabel.setMaximumSize(new java.awt.Dimension(200, 20));
    resultLabel.setMinimumSize(new java.awt.Dimension(200, 20));
    resultLabel.setPreferredSize(new java.awt.Dimension(200, 20));

    xSpinner.setModel(new javax.swing.SpinnerNumberModel(3, 0, 143, 1));
    xSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        xSpinnerStateChanged(evt);
      }
    });

    ySpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 143, 1));
    ySpinner.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        ySpinnerStateChanged(evt);
      }
    });

    jButton1.setText("search");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
      }
    });

    debugBtn.setText("debug");
    debugBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        debugBtnActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout controlsLayout = new javax.swing.GroupLayout(controls);
    controls.setLayout(controlsLayout);
    controlsLayout.setHorizontalGroup(
      controlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(controlsLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(controlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(controlsLayout.createSequentialGroup()
            .addComponent(showgrid)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButton2))
          .addComponent(showObst)
          .addGroup(controlsLayout.createSequentialGroup()
            .addComponent(jLabel2)
            .addGap(4, 4, 4)
            .addComponent(xSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel3)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(ySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(cursorNavigation)
          .addGroup(controlsLayout.createSequentialGroup()
            .addComponent(jButton1)
            .addGap(18, 18, 18)
            .addComponent(debugBtn))
          .addComponent(resultLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(146, Short.MAX_VALUE))
    );
    controlsLayout.setVerticalGroup(
      controlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(controlsLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(controlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(showgrid)
          .addComponent(jButton2))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(showObst)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(cursorNavigation)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(controlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(xSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel3)
          .addComponent(ySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(controlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jButton1)
          .addComponent(debugBtn))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(resultLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(70, Short.MAX_VALUE))
    );

    getContentPane().add(controls, java.awt.BorderLayout.EAST);

    canvas.setDoubleBuffered(true);
    canvas.setMaximumSize(new java.awt.Dimension(800, 556));
    canvas.setMinimumSize(new java.awt.Dimension(800, 556));
    canvas.setLayout(new javax.swing.OverlayLayout(canvas));

    backgroundLayer.setMaximumSize(new java.awt.Dimension(800, 600));
    backgroundLayer.setMinimumSize(new java.awt.Dimension(800, 556));
    backgroundLayer.setLayout(new java.awt.BorderLayout());

    background.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/battlefields/CmBkBoat.png"))); // NOI18N
    background.setDoubleBuffered(true);
    background.setFocusable(false);
    background.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    background.setIconTextGap(0);
    background.setRequestFocusEnabled(false);
    backgroundLayer.add(background, java.awt.BorderLayout.CENTER);

    canvas.setLayer(backgroundLayer, 1);
    canvas.add(backgroundLayer);

    hexLayer.setBackground(new java.awt.Color(238, 238, 255));
    hexLayer.setMaximumSize(new java.awt.Dimension(800, 556));
    hexLayer.setMinimumSize(new java.awt.Dimension(800, 556));
    hexLayer.setOpaque(false);
    hexLayer.setVerifyInputWhenFocusTarget(false);
    hexLayer.setLayout(null);
    canvas.setLayer(hexLayer, 4);
    canvas.add(hexLayer);

    obstacleLayer.setMaximumSize(new java.awt.Dimension(800, 556));
    obstacleLayer.setMinimumSize(new java.awt.Dimension(800, 556));
    obstacleLayer.setOpaque(false);
    obstacleLayer.setPreferredSize(new java.awt.Dimension(800, 556));
    obstacleLayer.setLayout(null);
    canvas.setLayer(obstacleLayer, 6);
    canvas.add(obstacleLayer);

    loading.setFocusable(false);
    loading.setMaximumSize(new java.awt.Dimension(800, 556));
    loading.setMinimumSize(new java.awt.Dimension(800, 556));
    loading.setOpaque(false);
    loading.setRequestFocusEnabled(false);
    loading.setVerifyInputWhenFocusTarget(false);
    loading.setLayout(null);
    canvas.setLayer(loading, 50);
    canvas.add(loading);

    getContentPane().add(canvas, java.awt.BorderLayout.CENTER);

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void debugBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debugBtnActionPerformed
    final SearchParams param = SearchParams.from(hexGrid, false);
    System.out.println("PATTERN: " + param.bit1.toString(2));
    System.out.println("MASK   : " + param.bit0.toString(2));
  }//GEN-LAST:event_debugBtnActionPerformed

  private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    loadingIndicator.start();
    new Thread(() -> {
      java.util.List<Battlefield> result = Search.search(hexGrid, false);
      tm.addAll(result);
      invokeLater(() -> {
        resultLabel.setText("" + result.size() + " matches");
        resultLabel.updateUI();
        jTable1.updateUI();
        loadingIndicator.stop();
      });
    }).start();
  }//GEN-LAST:event_jButton1ActionPerformed

  private void ySpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ySpinnerStateChanged
    loadBattlefield();
  }//GEN-LAST:event_ySpinnerStateChanged

  private void xSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_xSpinnerStateChanged
    loadBattlefield();
  }//GEN-LAST:event_xSpinnerStateChanged

  private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
    loadBattlefield();
  }//GEN-LAST:event_jList1ValueChanged

  private void cursorNavigationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cursorNavigationKeyReleased
    keyNavigation(evt);
  }//GEN-LAST:event_cursorNavigationKeyReleased

  private void cursorNavigationFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cursorNavigationFocusLost
    cursorNavigation.setSelected(false);
  }//GEN-LAST:event_cursorNavigationFocusLost

  private void showObstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showObstActionPerformed
    obstacleLayer.setVisible(!obstacleLayer.isVisible());
  }//GEN-LAST:event_showObstActionPerformed

  private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    hexGrid.clear();
    hexLayer.updateUI();
    for (Component c : hexLayer.getComponents()) {
      if (c instanceof JComponent) {
        ((JComponent) c).updateUI();
      }
    }
  }//GEN-LAST:event_jButton2ActionPerformed

  private void showgridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showgridActionPerformed
    hexLayer.setVisible(showgrid.isSelected());
  }//GEN-LAST:event_showgridActionPerformed

  public void loadBattlefield() {
    int x = Integer.parseInt(xSpinner.getValue().toString());
    int y = Integer.parseInt(ySpinner.getValue().toString());
    Terrain.get(jList1.getSelectedIndex()).ifPresent(t -> {
      final Battlefield d = BattleFieldInfo.load().get(x, y, t);
//      System.out.println(d.toString());
//      hexdump(d.getBlockedHexes());
      background.updateUI();
      obstacleLayer.removeAll();
      background.setIcon(TerrainImages.getImage(d.terrain));
      for (final PositionedObstacle po : d.obstacles) {
        final ImageIcon im = po.obstacle.getImage();
        final JLabel L = new JLabel(im);
        obstacleLayer.add(L);
        L.setBounds(po.getScreenX(), po.getScreenY(), im.getIconWidth(), im.getIconHeight());
      }
      background.updateUI();
      obstacleLayer.updateUI();
    });
  }

  private void keyNavigation(final KeyEvent evt) {
    if (cursorNavigation.isSelected()) {
      //<editor-fold defaultstate="collapsed" desc="keyNavigation">
      switch (evt.getKeyCode()) {
        case KeyEvent.VK_O: {
          showObst.setSelected(!showObst.isSelected());
          showObst.doClick();
        }
        break;
        case KeyEvent.VK_H: {
          showgrid.setSelected(!showgrid.isSelected());
          showgrid.doClick();
        }
        break;
        case KeyEvent.VK_PAGE_UP: {
          final int s = jList1.getSelectedIndex();
          if (s > 0) {
            jList1.getSelectionModel().setSelectionInterval(s - 1, s - 1);
          }
        }
        break;
        case KeyEvent.VK_PAGE_DOWN: {
          final int s = jList1.getSelectedIndex();
          if (s < Terrain.values().length - 1) {
            jList1.getSelectionModel().setSelectionInterval(s + 1, s + 1);
          }
        }
        break;
        case KeyEvent.VK_UP: {
          if (ySpinner.getPreviousValue() != null) {
            ySpinner.setValue(ySpinner.getPreviousValue());
          }
        }
        break;
        case KeyEvent.VK_DOWN: {
          if (ySpinner.getNextValue() != null) {
            ySpinner.setValue(ySpinner.getNextValue());
          }
        }
        break;
        case KeyEvent.VK_LEFT: {
          if (xSpinner.getPreviousValue() != null) {
            xSpinner.setValue(xSpinner.getPreviousValue());
          }
        }
        break;
        case KeyEvent.VK_RIGHT: {
          if (xSpinner.getNextValue() != null) {
            xSpinner.setValue(xSpinner.getNextValue());
          }
        }
        break;
        default:
      }
      //</editor-fold>
    }
  }

  public static void start() {
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    try {
      for (LookAndFeelInfo info : getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
    }
    //</editor-fold>
    invokeLater(() -> {
      new Gui().setVisible(true);
    });
  }

  //<editor-fold defaultstate="collapsed" desc="components">
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel background;
  private javax.swing.JPanel backgroundLayer;
  private javax.swing.JLayeredPane canvas;
  private javax.swing.JPanel controls;
  private javax.swing.JToggleButton cursorNavigation;
  private javax.swing.JButton debugBtn;
  private javax.swing.JPanel hexLayer;
  private javax.swing.JButton jButton1;
  private javax.swing.JButton jButton2;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JList<String> jList1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JTable jTable1;
  private javax.swing.JPanel loading;
  private javax.swing.JPanel obstacleLayer;
  private javax.swing.JLabel resultLabel;
  private javax.swing.JScrollPane results;
  private javax.swing.JCheckBox showObst;
  private javax.swing.JCheckBox showgrid;
  private javax.swing.JSpinner xSpinner;
  private javax.swing.JSpinner ySpinner;
  // End of variables declaration//GEN-END:variables
  //</editor-fold>

}
