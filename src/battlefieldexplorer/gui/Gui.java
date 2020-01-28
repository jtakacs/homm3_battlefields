package battlefieldexplorer.gui;

import static battlefieldexplorer.util.Constants.BFIELD_HEIGHT;
import static battlefieldexplorer.util.Constants.BFIELD_WIDTH;
import static battlefieldexplorer.util.Constants.MAP_SIZE;
import static battlefieldexplorer.util.HexTools.hexIsVisible;
import static battlefieldexplorer.util.HexTools.isOddRow;
import static battlefieldexplorer.util.HexTools.posToHex;
import static java.awt.Toolkit.getDefaultToolkit;
import static javax.swing.SwingConstants.LEFT;
import static javax.swing.SwingConstants.TOP;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.UIManager.getInstalledLookAndFeels;
import static javax.swing.UIManager.getLookAndFeelDefaults;
import static javax.swing.UIManager.setLookAndFeel;
import battlefieldexplorer.generator.*;
import battlefieldexplorer.network.SocketListener;
import battlefieldexplorer.search.*;
import battlefieldexplorer.util.HexCellState;
import battlefieldexplorer.util.HexTools;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;
import org.oxbow.swingbits.misc.JSearchTextField;
import org.oxbow.swingbits.table.filter.TableRowFilterSupport;

//TODO cleanup
public class Gui extends JFrame {

  private static final long serialVersionUID = 1L;
  private final FakeCursor fakeCursor;
  private final SpinnerIntModel mapX = new SpinnerIntModel(3, 0, MAP_SIZE - 1);
  private final SpinnerIntModel mapY = new SpinnerIntModel(0, 0, MAP_SIZE - 1);
  private final HexGrid hexGrid;
  private final ResultTableModel tm = new ResultTableModel();
  private final Loading loading;
  private final Gui rootFrame;
  private final ImageIcon rightBallista;
  private final ImageIcon rightAmmocart;
  private final ImageIcon rightFirstaid;
  private final ImageIcon rightBallistaD;
  private final ImageIcon rightAmmocartD;
  private final ImageIcon rightFirstaidD;
  private final SocketListener socketListener;
  private Rectangle area = new Rectangle(-1, -1, -1, -1);
  private boolean showFirstaidL = false;
  private boolean showCatapultL = false;
  private boolean showBallistaL = false;
  private boolean showAmmocartL = false;
  private boolean showFirstaidR = false;
  private boolean showBallistaR = false;
  private boolean showAmmocartR = false;
  private AtomicBoolean readyToLoadBattleField = new AtomicBoolean(true);

  public Gui() {
    rootFrame = this;
    fakeCursor = new FakeCursor(this);
    rightBallista = new MirrorIcon(Gui.class.getResource("/warmachines/ballista.png"));
    rightAmmocart = new MirrorIcon(Gui.class.getResource("/warmachines/ammocart.png"));
    rightFirstaid = new MirrorIcon(Gui.class.getResource("/warmachines/firstaid.png"));
    rightBallistaD = new MirrorIcon(GrayFilter.createDisabledImage(rightBallista.getImage()));
    rightAmmocartD = new MirrorIcon(GrayFilter.createDisabledImage(rightAmmocart.getImage()));
    rightFirstaidD = new MirrorIcon(GrayFilter.createDisabledImage(rightFirstaid.getImage()));
    initComponents();
    this.setIconImage(Toolkit.getDefaultToolkit().getImage(Gui.class.getResource("/Heroes_III_Icon.png")));
    setLocationRelativeTo(null);
    setEnabled(false);
    imageInfo.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
    imageInfo.setBackground(new Color(0, 0, 0, 0));
    imageTextLayer.setVisible(false);
    Arrays.asList(
            ammocartL,
            ammocartR,
            ballistaL,
            ballistaR,
            catapultL,
            firstaidL,
            firstaidR,
            anchorLayer
    ).forEach(L -> L.setVisible(false));
    spinnerFixNumericInput(xSpinner);
    spinnerFixNumericInput(ySpinner);
    loading = new Loading(loadingIndicator);
    this.hexGrid = new HexGrid();
    HexGrid.createHexGrid(hexLayer, hexGrid);
//    setPassability(ShipToShip);
    jTable1.getSelectionModel().addListSelectionListener(new RowSelectionListener(jTable1, tm, rootFrame));
    TableRowFilterSupport
            .forTable(jTable1)
            .actions(true)
            .searchable(true)
            .apply();
    setupKeyNavigation();
    socketListener = new SocketListener(this);
    socketListener.start();
  }

  @Override
  public void setVisible(boolean b) {
    super.setVisible(b);
    loading.start();
    new Thread(() -> {
      BattleFieldInfo.load();
      invokeLater(() -> {
        loading.stop();
        rootFrame.setEnabled(true);
      });
    }).start();
  }

  final AtomicLong keyTime = new AtomicLong(0L);

  private void setupKeyNavigation() {
    DefaultKeyboardFocusManager
            .getCurrentKeyboardFocusManager().
            addKeyEventPostProcessor(e -> {
              if (e.isConsumed()) {
                return true;
              }
              Object s = e.getSource();
              if (s instanceof Component) {
                //showParents((Component) s);
                if (SwingUtilities.isDescendingFrom((Component) s, rootFrame)) {
                  if (s instanceof JSearchTextField) {
                    return true;
                  }
                  if ( //                          KeyEvent.KEY_RELEASED == e.getID()
                          //                          KeyEvent.KEY_TYPED == e.getID()
                          KeyEvent.KEY_PRESSED == e.getID()) {
                    //System.out.println("" + s.getClass().getCanonicalName());
                    keyNavigation(e);
                  }
                }
              }
              return true;
            });
  }

  public static void showParents(Component c) {
    if (c != null) {
      System.out.println("" + c.getClass().getCanonicalName());
      showParents(c.getParent());
    }
  }

  public static void spinnerFixNumericInput(final JSpinner s) {
    JComponent editor = s.getEditor();
    if (editor instanceof NumberEditor) {
      AbstractFormatter formatter = ((NumberEditor) editor).getTextField().getFormatter();
      if (formatter instanceof NumberFormatter) {
        ((NumberFormatter) formatter).setAllowsInvalid(false);
      }
    }
  }

  public void loadBattleField() {
    TerrainInfo.instance()
            .get(terrainList.getSelectedIndex())
            .ifPresent(terrain
                    -> displayBattlefield(BattleFieldInfo.load().get(mapX.value(), mapY.value(), terrain)));
  }

  public void displayBattlefield(final Battlefield bf) {
    invokeLater(() -> {
      imageInfo.setText(String.format("X: %s  Y: %s\n%s", bf.mapX, bf.mapY, bf.terrain.description));
      background.setIcon(TerrainImages.getImage(bf.terrain));
      background.updateUI();
      placeObstacles(bf.obstacles);
      setPassability(bf);
      setAnchors(bf.obstacles);
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

  private void setPassability(final Battlefield bf) {
    Passability.createOverlay(passabilityLayer, bf);
  }

  private void setAnchors(java.util.List<PositionedObstacle> obstacles) {
    AnchorCells.createOverlay(anchorLayer, obstacles);
  }

  public void setControlState(final Battlefield bf) {
    readyToLoadBattleField.set(false);
    invokeLater(() -> {
      xSpinner.setValue(bf.mapX);
      ySpinner.setValue(bf.mapY);
      terrainList.getSelectionModel().setSelectionInterval(bf.terrain.ID, bf.terrain.ID);
      xSpinner.updateUI();
      ySpinner.updateUI();
      terrainList.updateUI();
      displayBattlefield(bf);
      readyToLoadBattleField.set(true);
    });
  }

  private void clearHexGrid() {
    hexGrid.clear();
    updateHexGrid();
  }

  private void updateHexGrid() {
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

  // <editor-fold defaultstate="collapsed" desc="Generated Code">
  @SuppressWarnings ("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jFileChooser1 = new JFileChooser();
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
    showWarmachines = new JCheckBox();
    showAnchorCells = new JCheckBox();
    alwaysOnTop = new JCheckBox();
    jLayeredPane1 = new JLayeredPane();
    backgroundLayer = new JPanel();
    background = new JLabel();
    hexLayer = new JPanel();
    tacticsLayer = new JPanel();
    obstacleLayer = new JPanel();
    passabilityLayer = new JPanel();
    anchorLayer = new JPanel();
    warmachines = new JPanel();
    faLselector = new JPanel();
    catLselector = new JPanel();
    balLselector = new JPanel();
    amLselector = new JPanel();
    faRselector = new JPanel();
    amRselector = new JPanel();
    balRselector = new JPanel();
    ammocartL = new JLabel();
    firstaidL = new JLabel();
    catapultL = new JLabel();
    ballistaL = new JLabel();
    ammocartR = new JLabel();
    ballistaR = new JLabel();
    firstaidR = new JLabel();
    imageTextLayer = new JPanel();
    jPanel3 = new JPanel();
    imageInfo = new JTextPane();
    loadingIndicator = new JPanel();
    tablePanel = new JScrollPane();
    jTable1 = new JTable();
    helpTextPanel = new JScrollPane();
    jTextPane1 = new JTextPane();
    jPanel2 = new JPanel();
    flipVbtn = new JButton();
    flipHbtn = new JButton();
    searchMirrorV = new JCheckBox();
    searchMirrorH = new JCheckBox();
    fixedShape = new JCheckBox();
    searchButton = new JButton();
    clearButton = new JButton();
    resultsLabel = new JLabel();
    selectAreaBtn = new JButton();
    saveImageBtn = new JButton();
    clipBoardBtn = new JButton();

    jFileChooser1.setAcceptAllFileFilterUsed(false);
    jFileChooser1.setDialogType(JFileChooser.SAVE_DIALOG);
    jFileChooser1.setDialogTitle("Save image");
    jFileChooser1.setFileFilter(new FileNameExtensionFilter("PNG file", "png"));

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setTitle("BattleField Explorer");
    setFont(new Font("Andale Mono", 1, 12)); // NOI18N
    setMinimumSize(new Dimension(820, 600));

    jPanel1.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
    jPanel1.setInheritsPopupMenu(true);
    jPanel1.setMaximumSize(new Dimension(220, 556));
    jPanel1.setMinimumSize(new Dimension(220, 556));
    jPanel1.setPreferredSize(new Dimension(220, 556));

    terrainList.setModel(TerrainInfo.instance());
    terrainList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    terrainList.setDoubleBuffered(true);
    terrainList.setFocusable(false);
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
    xSpinner.setEditor(new NumberEditor(xSpinner, ""));
    xSpinner.setFocusable(false);
    xSpinner.setMaximumSize(new Dimension(56, 20));
    xSpinner.setRequestFocusEnabled(false);
    xSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        xSpinnerStateChanged(evt);
      }
    });

    jLabel2.setText("Y: ");

    ySpinner.setModel(mapY);
    ySpinner.setEditor(new NumberEditor(ySpinner, ""));
    ySpinner.setFocusable(false);
    ySpinner.setMaximumSize(new Dimension(56, 20));
    ySpinner.setRequestFocusEnabled(false);
    ySpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        ySpinnerStateChanged(evt);
      }
    });

    showHex.setSelected(true);
    showHex.setText("Show hex grid");
    showHex.setFocusable(false);
    showHex.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        showHexStateChanged(evt);
      }
    });

    showObst.setSelected(true);
    showObst.setText("Show obstacles");
    showObst.setFocusable(false);
    showObst.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        showObstStateChanged(evt);
      }
    });

    showBlocked.setText("Passability");
    showBlocked.setFocusable(false);
    showBlocked.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        showBlockedStateChanged(evt);
      }
    });

    showWarmachines.setText("War machines");
    showWarmachines.setFocusable(false);
    showWarmachines.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        showWarmachinesActionPerformed(evt);
      }
    });

    showAnchorCells.setText("Show anchor cells");
    showAnchorCells.setFocusable(false);
    showAnchorCells.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        showAnchorCellsStateChanged(evt);
      }
    });

    alwaysOnTop.setText("Always on top");
    alwaysOnTop.setFocusable(false);
    alwaysOnTop.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        alwaysOnTopStateChanged(evt);
      }
    });

    GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(alwaysOnTop)
          .addComponent(showAnchorCells)
          .addComponent(showWarmachines)
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
          .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 192, GroupLayout.PREFERRED_SIZE))
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        .addComponent(showWarmachines)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(showAnchorCells)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(alwaysOnTop)
        .addContainerGap(63, Short.MAX_VALUE))
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

    tacticsLayer.setFocusable(false);
    tacticsLayer.setMaximumSize(new Dimension(800, 556));
    tacticsLayer.setMinimumSize(new Dimension(800, 556));
    tacticsLayer.setOpaque(false);
    tacticsLayer.setLayout(null);
    jLayeredPane1.setLayer(tacticsLayer, 25);
    jLayeredPane1.add(tacticsLayer);

    obstacleLayer.setFocusable(false);
    obstacleLayer.setMaximumSize(new Dimension(800, 556));
    obstacleLayer.setMinimumSize(new Dimension(800, 556));
    obstacleLayer.setOpaque(false);
    obstacleLayer.setPreferredSize(new Dimension(800, 556));
    obstacleLayer.setLayout(null);
    jLayeredPane1.setLayer(obstacleLayer, 30);
    jLayeredPane1.add(obstacleLayer);

    passabilityLayer.setFocusable(false);
    passabilityLayer.setMaximumSize(new Dimension(800, 556));
    passabilityLayer.setMinimumSize(new Dimension(800, 556));
    passabilityLayer.setOpaque(false);
    passabilityLayer.setLayout(null);
    jLayeredPane1.setLayer(passabilityLayer, 40);
    jLayeredPane1.add(passabilityLayer);

    anchorLayer.setFocusable(false);
    anchorLayer.setMaximumSize(new Dimension(800, 556));
    anchorLayer.setMinimumSize(new Dimension(800, 556));
    anchorLayer.setOpaque(false);
    anchorLayer.setLayout(null);
    jLayeredPane1.setLayer(anchorLayer, 45);
    jLayeredPane1.add(anchorLayer);

    warmachines.setFocusable(false);
    warmachines.setMaximumSize(new Dimension(800, 556));
    warmachines.setMinimumSize(new Dimension(800, 556));
    warmachines.setOpaque(false);
    warmachines.setRequestFocusEnabled(false);
    warmachines.setVerifyInputWhenFocusTarget(false);
    warmachines.setLayout(null);

    faLselector.setMaximumSize(new Dimension(50, 52));
    faLselector.setMinimumSize(new Dimension(50, 52));
    faLselector.setOpaque(false);
    faLselector.setPreferredSize(new Dimension(50, 52));
    faLselector.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        faLselectorMouseClicked(evt);
      }
      public void mouseExited(MouseEvent evt) {
        faLselectorMouseExited(evt);
      }
      public void mouseEntered(MouseEvent evt) {
        faLselectorMouseEntered(evt);
      }
    });

    GroupLayout faLselectorLayout = new GroupLayout(faLselector);
    faLselector.setLayout(faLselectorLayout);
    faLselectorLayout.setHorizontalGroup(faLselectorLayout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 80, Short.MAX_VALUE)
    );
    faLselectorLayout.setVerticalGroup(faLselectorLayout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 80, Short.MAX_VALUE)
    );

    warmachines.add(faLselector);
    faLselector.setBounds(0, 435, 80, 80);

    catLselector.setMaximumSize(new Dimension(50, 52));
    catLselector.setMinimumSize(new Dimension(50, 52));
    catLselector.setOpaque(false);
    catLselector.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        catLselectorMouseClicked(evt);
      }
      public void mouseExited(MouseEvent evt) {
        catLselectorMouseExited(evt);
      }
      public void mouseEntered(MouseEvent evt) {
        catLselectorMouseEntered(evt);
      }
    });

    GroupLayout catLselectorLayout = new GroupLayout(catLselector);
    catLselector.setLayout(catLselectorLayout);
    catLselectorLayout.setHorizontalGroup(catLselectorLayout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 80, Short.MAX_VALUE)
    );
    catLselectorLayout.setVerticalGroup(catLselectorLayout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 70, Short.MAX_VALUE)
    );

    warmachines.add(catLselector);
    catLselector.setBounds(0, 350, 80, 70);

    balLselector.setMaximumSize(new Dimension(50, 52));
    balLselector.setMinimumSize(new Dimension(50, 52));
    balLselector.setOpaque(false);
    balLselector.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        balLselectorMouseClicked(evt);
      }
      public void mouseExited(MouseEvent evt) {
        balLselectorMouseExited(evt);
      }
      public void mouseEntered(MouseEvent evt) {
        balLselectorMouseEntered(evt);
      }
    });

    GroupLayout balLselectorLayout = new GroupLayout(balLselector);
    balLselector.setLayout(balLselectorLayout);
    balLselectorLayout.setHorizontalGroup(balLselectorLayout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 80, Short.MAX_VALUE)
    );
    balLselectorLayout.setVerticalGroup(balLselectorLayout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 75, Short.MAX_VALUE)
    );

    warmachines.add(balLselector);
    balLselector.setBounds(0, 180, 80, 75);

    amLselector.setMaximumSize(new Dimension(50, 52));
    amLselector.setMinimumSize(new Dimension(50, 52));
    amLselector.setOpaque(false);
    amLselector.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        amLselectorMouseClicked(evt);
      }
      public void mouseExited(MouseEvent evt) {
        amLselectorMouseExited(evt);
      }
      public void mouseEntered(MouseEvent evt) {
        amLselectorMouseEntered(evt);
      }
    });

    GroupLayout amLselectorLayout = new GroupLayout(amLselector);
    amLselector.setLayout(amLselectorLayout);
    amLselectorLayout.setHorizontalGroup(amLselectorLayout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 80, Short.MAX_VALUE)
    );
    amLselectorLayout.setVerticalGroup(amLselectorLayout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 70, Short.MAX_VALUE)
    );

    warmachines.add(amLselector);
    amLselector.setBounds(0, 100, 80, 70);

    faRselector.setMaximumSize(new Dimension(50, 52));
    faRselector.setMinimumSize(new Dimension(50, 52));
    faRselector.setOpaque(false);
    faRselector.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        faRselectorMouseClicked(evt);
      }
      public void mouseExited(MouseEvent evt) {
        faRselectorMouseExited(evt);
      }
      public void mouseEntered(MouseEvent evt) {
        faRselectorMouseEntered(evt);
      }
    });

    GroupLayout faRselectorLayout = new GroupLayout(faRselector);
    faRselector.setLayout(faRselectorLayout);
    faRselectorLayout.setHorizontalGroup(faRselectorLayout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 100, Short.MAX_VALUE)
    );
    faRselectorLayout.setVerticalGroup(faRselectorLayout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 55, Short.MAX_VALUE)
    );

    warmachines.add(faRselector);
    faRselector.setBounds(700, 463, 100, 55);

    amRselector.setMaximumSize(new Dimension(50, 52));
    amRselector.setMinimumSize(new Dimension(50, 52));
    amRselector.setOpaque(false);
    amRselector.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        amRselectorMouseClicked(evt);
      }
      public void mouseExited(MouseEvent evt) {
        amRselectorMouseExited(evt);
      }
      public void mouseEntered(MouseEvent evt) {
        amRselectorMouseEntered(evt);
      }
    });

    GroupLayout amRselectorLayout = new GroupLayout(amRselector);
    amRselector.setLayout(amRselectorLayout);
    amRselectorLayout.setHorizontalGroup(amRselectorLayout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 100, Short.MAX_VALUE)
    );
    amRselectorLayout.setVerticalGroup(amRselectorLayout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 55, Short.MAX_VALUE)
    );

    warmachines.add(amRselector);
    amRselector.setBounds(700, 127, 100, 55);

    balRselector.setMaximumSize(new Dimension(50, 52));
    balRselector.setMinimumSize(new Dimension(50, 52));
    balRselector.setOpaque(false);
    balRselector.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        balRselectorMouseClicked(evt);
      }
      public void mouseExited(MouseEvent evt) {
        balRselectorMouseExited(evt);
      }
      public void mouseEntered(MouseEvent evt) {
        balRselectorMouseEntered(evt);
      }
    });

    GroupLayout balRselectorLayout = new GroupLayout(balRselector);
    balRselector.setLayout(balRselectorLayout);
    balRselectorLayout.setHorizontalGroup(balRselectorLayout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 100, Short.MAX_VALUE)
    );
    balRselectorLayout.setVerticalGroup(balRselectorLayout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 55, Short.MAX_VALUE)
    );

    warmachines.add(balRselector);
    balRselector.setBounds(700, 210, 100, 55);

    ammocartL.setIcon(new ImageIcon(getClass().getResource("/warmachines/ammocart.png"))); // NOI18N
    ammocartL.setEnabled(false);
    ammocartL.setRequestFocusEnabled(false);
    ammocartL.setVerifyInputWhenFocusTarget(false);
    warmachines.add(ammocartL);
    ammocartL.setBounds(60, 100, 45, 66);

    firstaidL.setIcon(new ImageIcon(getClass().getResource("/warmachines/firstaid.png"))); // NOI18N
    firstaidL.setEnabled(false);
    firstaidL.setMaximumSize(new Dimension(90, 130));
    firstaidL.setMinimumSize(new Dimension(90, 130));
    firstaidL.setPreferredSize(new Dimension(90, 130));
    firstaidL.setRequestFocusEnabled(false);
    firstaidL.setVerifyInputWhenFocusTarget(false);
    warmachines.add(firstaidL);
    firstaidL.setBounds(20, 380, 90, 130);

    catapultL.setIcon(new ImageIcon(getClass().getResource("/warmachines/catapult.png"))); // NOI18N
    catapultL.setEnabled(false);
    catapultL.setRequestFocusEnabled(false);
    catapultL.setVerifyInputWhenFocusTarget(false);
    warmachines.add(catapultL);
    catapultL.setBounds(0, 330, 102, 90);

    ballistaL.setIcon(new ImageIcon(getClass().getResource("/warmachines/ballista.png"))); // NOI18N
    ballistaL.setEnabled(false);
    ballistaL.setRequestFocusEnabled(false);
    ballistaL.setVerifyInputWhenFocusTarget(false);
    warmachines.add(ballistaL);
    ballistaL.setBounds(10, 180, 98, 76);

    ammocartR.setIcon(rightAmmocart);
    ammocartR.setDisabledIcon(rightAmmocartD);
    ammocartR.setEnabled(false);
    ammocartR.setMaximumSize(new Dimension(45, 66));
    ammocartR.setMinimumSize(new Dimension(45, 66));
    ammocartR.setPreferredSize(new Dimension(45, 66));
    ammocartR.setRequestFocusEnabled(false);
    ammocartR.setVerifyInputWhenFocusTarget(false);
    warmachines.add(ammocartR);
    ammocartR.setBounds(680, 100, 45, 66);

    ballistaR.setIcon(rightBallista);
    ballistaR.setDisabledIcon(rightBallistaD);
    ballistaR.setEnabled(false);
    ballistaR.setMaximumSize(new Dimension(98, 76));
    ballistaR.setMinimumSize(new Dimension(98, 76));
    ballistaR.setPreferredSize(new Dimension(98, 76));
    ballistaR.setRequestFocusEnabled(false);
    ballistaR.setVerifyInputWhenFocusTarget(false);
    warmachines.add(ballistaR);
    ballistaR.setBounds(680, 180, 98, 76);

    firstaidR.setIcon(rightFirstaid);
    firstaidR.setDisabledIcon(rightFirstaidD);
    firstaidR.setEnabled(false);
    firstaidR.setMaximumSize(new Dimension(90, 130));
    firstaidR.setMinimumSize(new Dimension(90, 130));
    firstaidR.setPreferredSize(new Dimension(90, 130));
    firstaidR.setRequestFocusEnabled(false);
    firstaidR.setVerifyInputWhenFocusTarget(false);
    warmachines.add(firstaidR);
    firstaidR.setBounds(680, 370, 90, 130);

    jLayeredPane1.setLayer(warmachines, 50);
    jLayeredPane1.add(warmachines);

    imageTextLayer.setFocusable(false);
    imageTextLayer.setMaximumSize(new Dimension(800, 556));
    imageTextLayer.setMinimumSize(new Dimension(800, 556));
    imageTextLayer.setName(""); // NOI18N
    imageTextLayer.setOpaque(false);
    imageTextLayer.setPreferredSize(new Dimension(800, 556));
    imageTextLayer.setRequestFocusEnabled(false);
    imageTextLayer.setVerifyInputWhenFocusTarget(false);

    jPanel3.setBackground(new Color(0,0,0,80));

    imageInfo.setEditable(false);
    imageInfo.setBackground(new Color(0, 0, 0, 50));
    imageInfo.setBorder(null);
    imageInfo.setFont(new Font("Andale Mono", 1, 24)); // NOI18N
    imageInfo.setForeground(new Color(102, 255, 51));
    imageInfo.setMaximumSize(new Dimension(220, 60));
    imageInfo.setMinimumSize(new Dimension(220, 60));
    imageInfo.setName(""); // NOI18N
    imageInfo.setOpaque(false);
    imageInfo.setPreferredSize(new Dimension(220, 60));

    GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
        .addGap(0, 0, Short.MAX_VALUE)
        .addComponent(imageInfo, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
        .addGap(0, 0, 0))
    );
    jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addGap(0, 0, 0)
        .addComponent(imageInfo, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
        .addGap(0, 0, Short.MAX_VALUE))
    );

    GroupLayout imageTextLayerLayout = new GroupLayout(imageTextLayer);
    imageTextLayer.setLayout(imageTextLayerLayout);
    imageTextLayerLayout.setHorizontalGroup(imageTextLayerLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(imageTextLayerLayout.createSequentialGroup()
        .addGap(260, 260, 260)
        .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(320, Short.MAX_VALUE))
    );
    imageTextLayerLayout.setVerticalGroup(imageTextLayerLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(imageTextLayerLayout.createSequentialGroup()
        .addGap(0, 0, 0)
        .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(496, Short.MAX_VALUE))
    );

    jLayeredPane1.setLayer(imageTextLayer, 60);
    jLayeredPane1.add(imageTextLayer);

    loadingIndicator.setFocusable(false);
    loadingIndicator.setMaximumSize(new Dimension(800, 556));
    loadingIndicator.setMinimumSize(new Dimension(800, 556));
    loadingIndicator.setOpaque(false);
    loadingIndicator.setLayout(new BorderLayout());
    jLayeredPane1.setLayer(loadingIndicator, 110);
    jLayeredPane1.add(loadingIndicator);

    jTable1.setModel(tm);
    jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tablePanel.setViewportView(jTable1);

    helpTextPanel.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));

    jTextPane1.setEditable(false);
    jTextPane1.setBorder(null);
    jTextPane1.setContentType("text/html"); // NOI18N
    jTextPane1.setText("<html>\n  <head>\n<style>\nmargin:0;\npadding:0;\n</style>\n  </head>\n  <body>\n    <table>\n<tr><td><b>W, S:</b></td><td> change Y position</td></tr>\n<tr><td><b>A, D:</b></td><td> change X position</td></tr>\n<tr><td><b>T, G:</b></td><td> change terrain</td></tr>\n<tr><td><b>O:</b></td><td> toggle obstacles</td></tr>\n<tr><td><b>P:</b></td><td> toggle passability</td></tr>\n<tr><td><b>H: </b></td><td> toggle hex layer</td></tr>\n<tr><td><b>C: </b></td><td> clear hex grid</td></tr>\n<tr><td><b>M: </b></td><td> show war machines</td></tr>\n<tr><td><b>L: </b></td><td> show anchor cells</td></tr>\n\n    </table>\n  </body>\n</html>\n"); // NOI18N
    jTextPane1.setMaximumSize(new Dimension(220, 100));
    jTextPane1.setMinimumSize(new Dimension(220, 100));
    jTextPane1.setRequestFocusEnabled(false);
    helpTextPanel.setViewportView(jTextPane1);

    jPanel2.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));

    flipVbtn.setText("Flip vertically");
    flipVbtn.setFocusable(false);
    flipVbtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        flipVbtnActionPerformed(evt);
      }
    });

    flipHbtn.setText("Flip Horizontally");
    flipHbtn.setFocusable(false);
    flipHbtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        flipHbtnActionPerformed(evt);
      }
    });

    searchMirrorV.setText("while searching");
    searchMirrorV.setFocusable(false);

    searchMirrorH.setText("while searching");
    searchMirrorH.setFocusable(false);

    fixedShape.setText("Search for exact position match");
    fixedShape.setToolTipText("");
    fixedShape.setFocusable(false);
    fixedShape.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        fixedShapeActionPerformed(evt);
      }
    });

    searchButton.setText("Search");
    searchButton.setFocusable(false);
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
    clearButton.setFocusable(false);
    clearButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        clearButtonActionPerformed(evt);
      }
    });

    resultsLabel.setText("   ");
    resultsLabel.setBorder(BorderFactory.createEtchedBorder());
    resultsLabel.setMaximumSize(new Dimension(300, 15));

    selectAreaBtn.setText("Select map area to search");
    selectAreaBtn.setFocusable(false);
    selectAreaBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        selectAreaBtnActionPerformed(evt);
      }
    });

    saveImageBtn.setText("<html>Save<br>image</html>");
    saveImageBtn.setFocusable(false);
    saveImageBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        saveImageBtnActionPerformed(evt);
      }
    });

    clipBoardBtn.setText("Copy results to clipboard");
    clipBoardBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        clipBoardBtnActionPerformed(evt);
      }
    });

    GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
              .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(flipHbtn)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(searchMirrorH))
              .addComponent(fixedShape)
              .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(flipVbtn)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(searchMirrorV)))
            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addComponent(resultsLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
              .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING, false)
                  .addComponent(selectAreaBtn)
                  .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(searchButton)
                    .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(clearButton)))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(saveImageBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
              .addComponent(clipBoardBtn))
            .addGap(0, 0, Short.MAX_VALUE))))
    );

    jPanel2Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {flipHbtn, flipVbtn});

    jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
          .addComponent(flipVbtn)
          .addComponent(searchMirrorV))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
          .addComponent(flipHbtn)
          .addComponent(searchMirrorH))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(fixedShape)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING, false)
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addComponent(selectAreaBtn)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
              .addComponent(searchButton)
              .addComponent(clearButton)))
          .addComponent(saveImageBtn))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(resultsLabel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(clipBoardBtn)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel2Layout.linkSize(SwingConstants.VERTICAL, new Component[] {flipHbtn, flipVbtn, searchMirrorH, searchMirrorV});

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
          .addComponent(jLayeredPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(tablePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        .addGap(5, 5, 5)
        .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
          .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
          .addComponent(helpTextPanel, GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE))
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
          .addComponent(tablePanel, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
          .addComponent(helpTextPanel)
          .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents
// </editor-fold>

  private void terrainListValueChanged(ListSelectionEvent evt) {//GEN-FIRST:event_terrainListValueChanged
    if (readyToLoadBattleField.get()) {
      loadBattleField();
    }
  }//GEN-LAST:event_terrainListValueChanged

  private void searchButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
    loading.start();
    tm.clear();
    new Thread(() -> {
      java.util.List<Battlefield> result = Search.search(hexGrid, fixedShape.isSelected(), searchMirrorV.isSelected(), searchMirrorH.isSelected(), area);
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
    if (readyToLoadBattleField.get()) {
      loadBattleField();
    }
  }//GEN-LAST:event_xSpinnerStateChanged

  private void ySpinnerStateChanged(ChangeEvent evt) {//GEN-FIRST:event_ySpinnerStateChanged
    if (readyToLoadBattleField.get()) {
      loadBattleField();
    }
  }//GEN-LAST:event_ySpinnerStateChanged
  private static final long keyDelay = 50;

  private boolean keyNavigation(final KeyEvent evt) {
    //<editor-fold defaultstate="collapsed" desc="keyNavigation">
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
        long t = System.currentTimeMillis();
        if (t - keyTime.get() > keyDelay) {
          mapY.decrement();
          keyTime.set(t);
        }
        return true;
      }
      case KeyEvent.VK_S: {
        long t = System.currentTimeMillis();
        if (t - keyTime.get() > keyDelay) {
          mapY.increment();
          keyTime.set(t);
        }
        return true;
      }
      case KeyEvent.VK_A: {
        long t = System.currentTimeMillis();
        if (t - keyTime.get() > keyDelay) {
          mapX.decrement();
          keyTime.set(t);
        }
        return true;
      }
      case KeyEvent.VK_D: {
        long t = System.currentTimeMillis();
        if (t - keyTime.get() > keyDelay) {
          mapX.increment();
          keyTime.set(t);
        }
        return true;
      }
      case KeyEvent.VK_M: {
        showWarmachines.setSelected(!showWarmachines.isSelected());
        showWarmachinesActionPerformed(null);
        return true;
      }
      case KeyEvent.VK_L: {
        showAnchorCells.setSelected(!showAnchorCells.isSelected());
        anchorLayer.setVisible(showAnchorCells.isSelected());
        return true;
      }
      default:
    }
    return false;
    //</editor-fold>
  }

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

  private void faLselectorMouseEntered(MouseEvent evt) {//GEN-FIRST:event_faLselectorMouseEntered
    if (showWarmachines.isSelected()) {
      firstaidL.setEnabled(!showFirstaidL);
    }
  }//GEN-LAST:event_faLselectorMouseEntered

  private void faLselectorMouseExited(MouseEvent evt) {//GEN-FIRST:event_faLselectorMouseExited
    if (showWarmachines.isSelected()) {
      firstaidL.setEnabled(showFirstaidL);
    }
  }//GEN-LAST:event_faLselectorMouseExited

//TODO refactor into components
  private void faLselectorMouseClicked(MouseEvent evt) {//GEN-FIRST:event_faLselectorMouseClicked
    if (showWarmachines.isSelected()) {
      showFirstaidL = !showFirstaidL;
      firstaidL.setEnabled(showFirstaidL);
    }
  }//GEN-LAST:event_faLselectorMouseClicked

  private void catLselectorMouseClicked(MouseEvent evt) {//GEN-FIRST:event_catLselectorMouseClicked
    if (showWarmachines.isSelected()) {
      showCatapultL = !showCatapultL;
      catapultL.setEnabled(showCatapultL);
    }
  }//GEN-LAST:event_catLselectorMouseClicked

  private void catLselectorMouseEntered(MouseEvent evt) {//GEN-FIRST:event_catLselectorMouseEntered
    if (showWarmachines.isSelected()) {
      catapultL.setEnabled(!showCatapultL);
    }
  }//GEN-LAST:event_catLselectorMouseEntered

  private void catLselectorMouseExited(MouseEvent evt) {//GEN-FIRST:event_catLselectorMouseExited
    if (showWarmachines.isSelected()) {
      catapultL.setEnabled(showCatapultL);
    }
  }//GEN-LAST:event_catLselectorMouseExited

  private void balLselectorMouseClicked(MouseEvent evt) {//GEN-FIRST:event_balLselectorMouseClicked
    if (showWarmachines.isSelected()) {
      showBallistaL = !showBallistaL;
      ballistaL.setEnabled(showBallistaL);
    }
  }//GEN-LAST:event_balLselectorMouseClicked

  private void balLselectorMouseExited(MouseEvent evt) {//GEN-FIRST:event_balLselectorMouseExited
    if (showWarmachines.isSelected()) {
      ballistaL.setEnabled(showBallistaL);
    }
  }//GEN-LAST:event_balLselectorMouseExited

  private void balLselectorMouseEntered(MouseEvent evt) {//GEN-FIRST:event_balLselectorMouseEntered
    if (showWarmachines.isSelected()) {
      ballistaL.setEnabled(!showBallistaL);
    }
  }//GEN-LAST:event_balLselectorMouseEntered

  private void amLselectorMouseClicked(MouseEvent evt) {//GEN-FIRST:event_amLselectorMouseClicked
    if (showWarmachines.isSelected()) {
      showAmmocartL = !showAmmocartL;
      ammocartL.setEnabled(showAmmocartL);
    }
  }//GEN-LAST:event_amLselectorMouseClicked

  private void amLselectorMouseEntered(MouseEvent evt) {//GEN-FIRST:event_amLselectorMouseEntered
    if (showWarmachines.isSelected()) {
      ammocartL.setEnabled(!showAmmocartL);
    }
  }//GEN-LAST:event_amLselectorMouseEntered

  private void amLselectorMouseExited(MouseEvent evt) {//GEN-FIRST:event_amLselectorMouseExited
    if (showWarmachines.isSelected()) {
      ammocartL.setEnabled(showAmmocartL);
    }
  }//GEN-LAST:event_amLselectorMouseExited

  private void faRselectorMouseClicked(MouseEvent evt) {//GEN-FIRST:event_faRselectorMouseClicked
    if (showWarmachines.isSelected()) {
      showFirstaidR = !showFirstaidR;
      firstaidR.setEnabled(showFirstaidR);
    }
  }//GEN-LAST:event_faRselectorMouseClicked

  private void faRselectorMouseEntered(MouseEvent evt) {//GEN-FIRST:event_faRselectorMouseEntered
    if (showWarmachines.isSelected()) {
      firstaidR.setEnabled(!showFirstaidR);
    }
  }//GEN-LAST:event_faRselectorMouseEntered

  private void faRselectorMouseExited(MouseEvent evt) {//GEN-FIRST:event_faRselectorMouseExited
    if (showWarmachines.isSelected()) {
      firstaidR.setEnabled(showFirstaidR);
    }
  }//GEN-LAST:event_faRselectorMouseExited

  private void balRselectorMouseClicked(MouseEvent evt) {//GEN-FIRST:event_balRselectorMouseClicked
    if (showWarmachines.isSelected()) {
      showBallistaR = !showBallistaR;
      ballistaR.setEnabled(showBallistaR);
    }
  }//GEN-LAST:event_balRselectorMouseClicked

  private void balRselectorMouseEntered(MouseEvent evt) {//GEN-FIRST:event_balRselectorMouseEntered
    if (showWarmachines.isSelected()) {
      ballistaR.setEnabled(!showBallistaR);
    }
  }//GEN-LAST:event_balRselectorMouseEntered

  private void balRselectorMouseExited(MouseEvent evt) {//GEN-FIRST:event_balRselectorMouseExited
    if (showWarmachines.isSelected()) {
      ballistaR.setEnabled(showBallistaR);
    }
  }//GEN-LAST:event_balRselectorMouseExited

  private void amRselectorMouseClicked(MouseEvent evt) {//GEN-FIRST:event_amRselectorMouseClicked
    if (showWarmachines.isSelected()) {
      showAmmocartR = !showAmmocartR;
      ammocartR.setEnabled(showAmmocartR);
    }
  }//GEN-LAST:event_amRselectorMouseClicked

  private void amRselectorMouseEntered(MouseEvent evt) {//GEN-FIRST:event_amRselectorMouseEntered
    if (showWarmachines.isSelected()) {
      ammocartR.setEnabled(!showAmmocartR);
    }
  }//GEN-LAST:event_amRselectorMouseEntered

  private void amRselectorMouseExited(MouseEvent evt) {//GEN-FIRST:event_amRselectorMouseExited
    if (showWarmachines.isSelected()) {
      ammocartR.setEnabled(showAmmocartR);
    }
  }//GEN-LAST:event_amRselectorMouseExited

  private void flipVbtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_flipVbtnActionPerformed
    //TODO move to HexGrid class
    final Set<Integer> pattern = hexGrid.getPattern();
    final Set<Integer> patternMask = hexGrid.getPatternMask();
    final Set<Integer> flipV = new TreeSet<>();
    final Set<Integer> flipVm = new TreeSet<>();
    for (Integer hex : pattern) {
      int y = HexTools.getY(hex);
      flipV.add(posToHex(HexTools.getX(hex), BFIELD_HEIGHT - 1 - y));
    }
    for (Integer hex : patternMask) {
      int y = HexTools.getY(hex);
      flipVm.add(posToHex(HexTools.getX(hex), BFIELD_HEIGHT - 1 - y));
    }
    hexGrid.clear();
    for (Integer hex : flipV) {
      hexGrid.setState(HexTools.getX(hex), HexTools.getY(hex), HexCellState.ENABLED);
    }
    for (Integer hex : flipVm) {
      hexGrid.setState(HexTools.getX(hex), HexTools.getY(hex), HexCellState.DISABLED);
    }
    updateHexGrid();
  }//GEN-LAST:event_flipVbtnActionPerformed

  private void flipHbtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_flipHbtnActionPerformed
    //TODO move to HexGrid class
    final Set<Integer> pattern = hexGrid.getPattern();
    final Set<Integer> patternMask = hexGrid.getPatternMask();
    final Set<Integer> flipH = new TreeSet<>();
    final Set<Integer> flipHm = new TreeSet<>();
    for (Integer hex : pattern) {
      int y = HexTools.getY(hex);
      int pos = (BFIELD_WIDTH - 1) - HexTools.getX(hex) + (isOddRow(y) ? 1 : 0);
      if (hexIsVisible(pos)) {
        flipH.add(posToHex(pos, y));
      }
    }
    for (Integer hex : patternMask) {
      int y = HexTools.getY(hex);
      int pos = (BFIELD_WIDTH - 1) - HexTools.getX(hex) + (isOddRow(y) ? 1 : 0);
      if (hexIsVisible(pos)) {
        flipHm.add(posToHex(pos, y));
      }
    }
    hexGrid.clear();
    for (Integer hex : flipH) {
      hexGrid.setState(HexTools.getX(hex), HexTools.getY(hex), HexCellState.ENABLED);
    }
    for (Integer hex : flipHm) {
      hexGrid.setState(HexTools.getX(hex), HexTools.getY(hex), HexCellState.DISABLED);
    }
    updateHexGrid();
  }//GEN-LAST:event_flipHbtnActionPerformed

  private void showWarmachinesActionPerformed(ActionEvent evt) {//GEN-FIRST:event_showWarmachinesActionPerformed
    final boolean w = showWarmachines.isSelected();
    Arrays.asList(
            ammocartL,
            ammocartR,
            ballistaL,
            ballistaR,
            catapultL,
            firstaidL,
            firstaidR
    )
            .forEach(L -> L.setVisible(w || L.isEnabled()));
  }//GEN-LAST:event_showWarmachinesActionPerformed

  private void fixedShapeActionPerformed(ActionEvent evt) {//GEN-FIRST:event_fixedShapeActionPerformed
    if (fixedShape.isSelected()) {
      searchMirrorH.setEnabled(false);
      searchMirrorV.setEnabled(false);
    } else {
      searchMirrorH.setEnabled(true);
      searchMirrorV.setEnabled(true);
    }

  }//GEN-LAST:event_fixedShapeActionPerformed

  private void selectAreaBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_selectAreaBtnActionPerformed
    invokeLater(() -> {
      SelectAreaDialog dialog = new SelectAreaDialog(area);
      dialog.setVisible(true);
      if (dialog.isConfirmed()) {
        area = dialog.getArea();
      }
    });

  }//GEN-LAST:event_selectAreaBtnActionPerformed

  private void showAnchorCellsStateChanged(ChangeEvent evt) {//GEN-FIRST:event_showAnchorCellsStateChanged
    anchorLayer.setVisible(showAnchorCells.isSelected());
  }//GEN-LAST:event_showAnchorCellsStateChanged

  private void saveImageBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_saveImageBtnActionPerformed
    if (JFileChooser.APPROVE_OPTION == jFileChooser1.showSaveDialog(null)) {
      File outputfile = jFileChooser1.getSelectedFile();
      if (!outputfile.getName().endsWith(".png")) {
        outputfile = new File(outputfile.getAbsolutePath() + ".png");
      }
      final BufferedImage img = new BufferedImage(jLayeredPane1.getWidth(), jLayeredPane1.getHeight(), BufferedImage.TYPE_INT_RGB);
      backgroundLayer.paint(img.getGraphics());
      if (hexLayer.isVisible()) {
        hexLayer.paint(img.getGraphics());
      }
      if (obstacleLayer.isVisible()) {
        obstacleLayer.paint(img.getGraphics());
      }
      if (passabilityLayer.isVisible()) {
        passabilityLayer.paint(img.getGraphics());
      }
      if (anchorLayer.isVisible()) {
        anchorLayer.paint(img.getGraphics());
      }
      boolean w = false;
      for (Component c : warmachines.getComponents()) {
        w |= (c.isVisible() && c.isEnabled());
      }
      if (w) {
        warmachines.paint(img.getGraphics());
      }
      imageTextLayer.setVisible(true);
      imageTextLayer.paint(img.getGraphics());
      imageTextLayer.setVisible(false);
      try {
        ImageIO.write(img, "png", outputfile);
      } catch (IOException ex) {

      }
    }
  }//GEN-LAST:event_saveImageBtnActionPerformed

  private void clipBoardBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_clipBoardBtnActionPerformed
    getDefaultToolkit()
            .getSystemClipboard()
            .setContents(new StringSelection(tm.toCSV()), null);
  }//GEN-LAST:event_clipBoardBtnActionPerformed

  private void alwaysOnTopStateChanged(ChangeEvent evt) {//GEN-FIRST:event_alwaysOnTopStateChanged
    this.setAlwaysOnTop(alwaysOnTop.isSelected());
  }//GEN-LAST:event_alwaysOnTopStateChanged

  //<editor-fold defaultstate="collapsed" desc=" Generated fields ">
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private JCheckBox alwaysOnTop;
  private JPanel amLselector;
  private JPanel amRselector;
  private JLabel ammocartL;
  private JLabel ammocartR;
  private JPanel anchorLayer;
  private JLabel background;
  private JPanel backgroundLayer;
  private JPanel balLselector;
  private JPanel balRselector;
  private JLabel ballistaL;
  private JLabel ballistaR;
  private JPanel catLselector;
  private JLabel catapultL;
  private JButton clearButton;
  private JButton clipBoardBtn;
  private JPanel faLselector;
  private JPanel faRselector;
  private JLabel firstaidL;
  private JLabel firstaidR;
  private JCheckBox fixedShape;
  private JButton flipHbtn;
  private JButton flipVbtn;
  private JScrollPane helpTextPanel;
  private JPanel hexLayer;
  private JTextPane imageInfo;
  private JPanel imageTextLayer;
  private JFileChooser jFileChooser1;
  private JLabel jLabel1;
  private JLabel jLabel2;
  private JLayeredPane jLayeredPane1;
  private JPanel jPanel1;
  private JPanel jPanel2;
  private JPanel jPanel3;
  private JScrollPane jScrollPane1;
  private JTable jTable1;
  private JTextPane jTextPane1;
  private JPanel loadingIndicator;
  private JPanel obstacleLayer;
  private JPanel passabilityLayer;
  private JLabel resultsLabel;
  private JButton saveImageBtn;
  private JButton searchButton;
  private JCheckBox searchMirrorH;
  private JCheckBox searchMirrorV;
  private JButton selectAreaBtn;
  private JCheckBox showAnchorCells;
  private JCheckBox showBlocked;
  private JCheckBox showHex;
  private JCheckBox showObst;
  private JCheckBox showWarmachines;
  private JScrollPane tablePanel;
  private JPanel tacticsLayer;
  private JList<String> terrainList;
  private JPanel warmachines;
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
    try {
      getLookAndFeelDefaults().put("defaultFont", new Font("Andale", Font.BOLD, 12));
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      ex.printStackTrace();
    }
    //</editor-fold>
    invokeLater(() -> new Gui().setVisible(true));
  }

}
