package battlefieldexplorer.gui;

import static battlefieldexplorer.gui.Gui.spinnerFixNumericInput;
import static battlefieldexplorer.util.Constants.MAP_SIZE;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.UIManager.getInstalledLookAndFeels;
import static javax.swing.UIManager.setLookAndFeel;
import battlefieldexplorer.util.FileDropHandler;
import battlefieldexplorer.util.FileUtil;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager.LookAndFeelInfo;

public class SelectAreaDialog extends JDialog {

  private static final long serialVersionUID = 1L;
  public static final String ALTMAPBG = "altmapbg.png";

  private final SpinnerIntModel mapLeft = new SpinnerIntModel(0, 0, MAP_SIZE - 1);
  private final SpinnerIntModel mapTop = new SpinnerIntModel(0, 0, MAP_SIZE - 1);
  private final SpinnerIntModel mapRight = new SpinnerIntModel(0, 0, MAP_SIZE - 1);
  private final SpinnerIntModel mapBtm = new SpinnerIntModel(0, 0, MAP_SIZE - 1);
  private int clickX = 0;
  private int clickY = 0;
  private boolean dragging = false;
  private boolean confirmed = false;
  private boolean fullmap = true;

  public SelectAreaDialog(final Rectangle area) {
    super(new JFrame(), true);
    initComponents();
    spinnerFixNumericInput(btmSpinner);
    spinnerFixNumericInput(leftSpinner);
    spinnerFixNumericInput(rightSpinner);
    spinnerFixNumericInput(topSpinner);
    final Rectangle r;
    if (area.x > -1 && area.y > -1 && area.width > -1 && area.height > -1) {
      r = new Rectangle(5 * area.x, 5 * area.y, 5 * (area.width - area.x), 5 * (area.height - area.y));
    } else {
      r = new Rectangle(120, 340, 75, 80);
    }
    setSpinner(r);
    jPanel3.setBounds(r);
    setAlternativeImage();
    jPanel2.setTransferHandler(new FileDropHandler(this::processFileDrop));
  }

  private void setAlternativeImage() {
    FileUtil.getLocation().ifPresent((File l) -> {
      final File file = new File(l, ALTMAPBG);
      if (file.exists()) {
        invokeLater(() -> {
          /*
              The underlying toolkit attempts to resolve multiple requests
              with the same URL to the same returned Image.
              Therefore, we create a new URL from the same filename every time we change the image file.
           */
          try {
            final URL url = new URL(file.toURI().toString() + "?" + System.currentTimeMillis());
            jLabel1.setIcon(new ImageIcon(url));
          } catch (MalformedURLException ex) {
            ex.printStackTrace();
          }
        });
      }
    });
  }

  public Rectangle getArea() {
    if (fullmap) {
      return new Rectangle(-1, -1, -1, -1);
    }
    return new Rectangle(
            mapLeft.value() > mapRight.value() ? mapRight.value() : mapLeft.value(),
            mapTop.value() > mapBtm.value() ? mapBtm.value() : mapTop.value(),
            mapLeft.value() > mapRight.value() ? mapLeft.value() : mapRight.value(),
            mapTop.value() > mapBtm.value() ? mapTop.value() : mapBtm.value()
    );
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  public boolean isFullmap() {
    return fullmap;
  }

  public void processFileDrop(final File source) {
    FileUtil.getLocation().ifPresent((File l) -> {
      try {
        Files.copy(source.toPath(), new File(l, ALTMAPBG).toPath(), COPY_ATTRIBUTES, REPLACE_EXISTING);
        setAlternativeImage();
      } catch (IOException ex) {
      }
    });
  }

  private void setSpinner(final Rectangle r) {
    mapLeft.setIntValue(r.x / 5);
    mapRight.setIntValue((r.x + r.width) / 5);
    mapTop.setIntValue(r.y / 5);
    mapBtm.setIntValue((r.y + r.height) / 5);
  }

  public static Rectangle box(final int startX, final int startY, final int x, final int y) {
    final int x1 = 5 * (startX / 5);
    final int y1 = 5 * (startY / 5);
    if (x >= x1 && y >= y1) {
      final int w = 5 * (x / 5) - x1;
      final int h = 5 * (y / 5) - y1;
      return new Rectangle(x1, y1, w, h);
    } else if (x < x1 && y >= y1) {
      final int w = 5 * ((startX - x) / 5);
      final int h = 5 * (y / 5) - y1;
      return new Rectangle(x1 - w, y1, w, h);
    } else if (x >= x1 && y < y1) {
      final int w = 5 * (x / 5) - x1;
      final int h = 5 * ((startY - y) / 5);
      return new Rectangle(x1, y1 - h, w, h);
    } else {
      final int w = 5 * ((startX - x) / 5);
      final int h = 5 * ((startY - y) / 5);
      return new Rectangle(x1 - w, y1 - h, w, h);
    }
  }

  // <editor-fold defaultstate="collapsed" desc="initComponents()">
  @SuppressWarnings ("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    confirmButton = new javax.swing.JButton();
    jLayeredPane1 = new javax.swing.JLayeredPane();
    jPanel1 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    jPanel2 = new javax.swing.JPanel();
    jPanel3 = new javax.swing.JPanel();
    leftSpinner = new javax.swing.JSpinner();
    topSpinner = new javax.swing.JSpinner();
    rightSpinner = new javax.swing.JSpinner();
    btmSpinner = new javax.swing.JSpinner();
    jLabel2 = new javax.swing.JLabel();
    cancelButton = new javax.swing.JButton();
    fullmapButton = new javax.swing.JButton();
    restoreButton = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Select area to search");
    setMaximumSize(new java.awt.Dimension(855, 855));
    setMinimumSize(new java.awt.Dimension(855, 855));
    setModal(true);
    setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
    setPreferredSize(new java.awt.Dimension(855, 855));
    setResizable(false);

    confirmButton.setText("Use selected area for search");
    confirmButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        confirmButtonActionPerformed(evt);
      }
    });

    jLayeredPane1.setMaximumSize(new java.awt.Dimension(715, 715));
    jLayeredPane1.setMinimumSize(new java.awt.Dimension(715, 715));
    jLayeredPane1.setLayout(new javax.swing.OverlayLayout(jLayeredPane1));

    jPanel1.setMaximumSize(new java.awt.Dimension(715, 715));
    jPanel1.setMinimumSize(new java.awt.Dimension(715, 715));
    jPanel1.setOpaque(false);
    jPanel1.setPreferredSize(new java.awt.Dimension(715, 715));
    jPanel1.setLayout(new java.awt.BorderLayout());

    jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mapbg.png"))); // NOI18N
    jPanel1.add(jLabel1, java.awt.BorderLayout.CENTER);

    jLayeredPane1.setLayer(jPanel1, 10);
    jLayeredPane1.add(jPanel1);

    jPanel2.setMaximumSize(new java.awt.Dimension(715, 715));
    jPanel2.setMinimumSize(new java.awt.Dimension(715, 715));
    jPanel2.setOpaque(false);
    jPanel2.setPreferredSize(new java.awt.Dimension(715, 715));
    jPanel2.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseDragged(java.awt.event.MouseEvent evt) {
        jPanel2MouseDragged(evt);
      }
    });
    jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(java.awt.event.MouseEvent evt) {
        jPanel2MousePressed(evt);
      }
      public void mouseReleased(java.awt.event.MouseEvent evt) {
        jPanel2MouseReleased(evt);
      }
    });
    jPanel2.setLayout(null);

    jPanel3.setBackground(new java.awt.Color(0, 255, 0, 30));
    jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 0, 60), 4));
    jPanel3.setForeground(new java.awt.Color(0, 255, 0, 30));
    jPanel3.setFocusable(false);
    jPanel3.setMaximumSize(new java.awt.Dimension(715, 715));
    jPanel3.setMinimumSize(new java.awt.Dimension(20, 20));
    jPanel3.setPreferredSize(new java.awt.Dimension(70, 70));
    jPanel3.setRequestFocusEnabled(false);
    jPanel3.setVerifyInputWhenFocusTarget(false);

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 68, Short.MAX_VALUE)
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 68, Short.MAX_VALUE)
    );

    jPanel2.add(jPanel3);
    jPanel3.setBounds(120, 340, 70, 70);

    jLayeredPane1.setLayer(jPanel2, 20);
    jLayeredPane1.add(jPanel2);

    leftSpinner.setModel(mapLeft);
    leftSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(leftSpinner, ""));
    leftSpinner.setMaximumSize(new java.awt.Dimension(60, 20));
    leftSpinner.setMinimumSize(new java.awt.Dimension(60, 20));
    leftSpinner.setPreferredSize(new java.awt.Dimension(60, 20));
    leftSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        leftSpinnerStateChanged(evt);
      }
    });

    topSpinner.setModel(mapTop);
    topSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(topSpinner, ""));
    topSpinner.setMaximumSize(new java.awt.Dimension(60, 20));
    topSpinner.setMinimumSize(new java.awt.Dimension(60, 20));
    topSpinner.setPreferredSize(new java.awt.Dimension(60, 20));
    topSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        topSpinnerStateChanged(evt);
      }
    });

    rightSpinner.setModel(mapRight);
    rightSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(rightSpinner, ""));
    rightSpinner.setMaximumSize(new java.awt.Dimension(60, 20));
    rightSpinner.setMinimumSize(new java.awt.Dimension(60, 20));
    rightSpinner.setPreferredSize(new java.awt.Dimension(60, 20));
    rightSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        rightSpinnerStateChanged(evt);
      }
    });

    btmSpinner.setModel(mapBtm);
    btmSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(btmSpinner, ""));
    btmSpinner.setMaximumSize(new java.awt.Dimension(60, 20));
    btmSpinner.setMinimumSize(new java.awt.Dimension(60, 20));
    btmSpinner.setPreferredSize(new java.awt.Dimension(60, 20));
    btmSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        btmSpinnerStateChanged(evt);
      }
    });

    jLabel2.setText("Drag&Drop your own map picture over the sample map! (715x715px PNG)");

    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelButtonActionPerformed(evt);
      }
    });

    fullmapButton.setText("Use the whole map for search");
    fullmapButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        fullmapButtonActionPerformed(evt);
      }
    });

    restoreButton.setText("Restore sample map image");
    restoreButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        restoreButtonActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(leftSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(topSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
              .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btmSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 715, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(rightSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addGap(0, 0, Short.MAX_VALUE))
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(cancelButton)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(fullmapButton)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(restoreButton)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(confirmButton)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addComponent(topSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 715, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(leftSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(rightSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(btmSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel2))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(cancelButton)
          .addComponent(fullmapButton)
          .addComponent(restoreButton)
          .addComponent(confirmButton))
        .addContainerGap(24, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents
  // </editor-fold>

  private void jPanel2MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseDragged
    int x = evt.getX();
    int y = evt.getY();
    Rectangle r = box(clickX, clickY, x, y);
    setSpinner(r);
    jPanel3.setBounds(r);
  }//GEN-LAST:event_jPanel2MouseDragged

  private void jPanel2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MousePressed
    dragging = true;
    clickX = evt.getX();
    clickY = evt.getY();
    final Rectangle r = box(clickX, clickY, clickX, clickY);
    setSpinner(r);
    jPanel3.setBounds(r);
  }//GEN-LAST:event_jPanel2MousePressed

  private void jPanel2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseReleased
    int x = evt.getX();
    int y = evt.getY();
    final Rectangle r = box(clickX, clickY, x, y);
    jPanel3.setBounds(r);
    dragging = false;
    setSpinner(r);
  }//GEN-LAST:event_jPanel2MouseReleased

  private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    confirmed = false;
    this.dispose();
  }//GEN-LAST:event_cancelButtonActionPerformed

  private void fullmapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fullmapButtonActionPerformed
    confirmed = true;
    fullmap = true;
    jPanel3.setBounds(0, 0, 143 * 5, 143 * 5);
    final SelectAreaDialog dialog = this;
    invokeLater(() -> {
      try {
        Thread.sleep(500L);
      } catch (InterruptedException ex) {
      }
      dialog.dispose();
    });
  }//GEN-LAST:event_fullmapButtonActionPerformed

  private void restoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreButtonActionPerformed
    FileUtil.getLocation().ifPresent((File l) -> {
      try {
        Files.deleteIfExists(new File(l, ALTMAPBG).toPath());
      } catch (IOException ex) {
      }
      jLabel1.setIcon(new ImageIcon(getClass().getResource("/mapbg.png")));
    });
  }//GEN-LAST:event_restoreButtonActionPerformed

  private void confirmButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmButtonActionPerformed
    fullmap = false;
    confirmed = true;
    this.dispose();
  }//GEN-LAST:event_confirmButtonActionPerformed

  private void rightSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rightSpinnerStateChanged
    if (!dragging) {
      spinnerChanged();
    }
  }//GEN-LAST:event_rightSpinnerStateChanged

  private void btmSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_btmSpinnerStateChanged
    if (!dragging) {
      spinnerChanged();
    }
  }//GEN-LAST:event_btmSpinnerStateChanged

  private void leftSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_leftSpinnerStateChanged
    if (!dragging) {
      spinnerChanged();
    }
  }//GEN-LAST:event_leftSpinnerStateChanged

  private void topSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_topSpinnerStateChanged
    if (!dragging) {
      spinnerChanged();
    }
  }//GEN-LAST:event_topSpinnerStateChanged

  private void spinnerChanged() {
    clickX = mapLeft.value() * 5;
    clickY = mapTop.value() * 5;
    final Rectangle r = box(clickX, clickY, mapRight.value() * 5, mapBtm.value() * 5);
    jPanel3.setBounds(r);
  }

  //<editor-fold defaultstate="collapsed" desc=" main() ">
  public static void main(String args[]) {
    try {
      for (LookAndFeelInfo info : getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (Exception ex) {
    }
    invokeLater(() -> {
      SelectAreaDialog dialog = new SelectAreaDialog(new Rectangle(-1, -1, -1, -1));
      dialog.setVisible(true);
      System.out.println("CONFIRM: " + dialog.isConfirmed());
      System.out.println("FULL: " + dialog.isFullmap());
      System.out.println("rect: " + dialog.getArea());
      System.exit(0);
    });
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Variables declaration ">
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JSpinner btmSpinner;
  private javax.swing.JButton cancelButton;
  private javax.swing.JButton confirmButton;
  private javax.swing.JButton fullmapButton;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLayeredPane jLayeredPane1;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JSpinner leftSpinner;
  private javax.swing.JButton restoreButton;
  private javax.swing.JSpinner rightSpinner;
  private javax.swing.JSpinner topSpinner;
  // End of variables declaration//GEN-END:variables
  //</editor-fold>
}
