package battlefieldexplorer.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.TransferHandler;

public class FileDropHandler extends TransferHandler {

  private static final long serialVersionUID = 1L;
  private final Consumer<File> c;

  public FileDropHandler(Consumer<File> c) {
    this.c = c;
  }

  @Override
  public boolean canImport(TransferHandler.TransferSupport support) {
    for (DataFlavor flavor : support.getDataFlavors()) {
      if (flavor.isFlavorJavaFileListType()) {
        return true;
      }
    }
    return false;
  }

  @Override
  @SuppressWarnings (value = "unchecked")
  public boolean importData(TransferHandler.TransferSupport support) {
    if (this.canImport(support)) {
      try {
        final List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
        for (File file : files) {
          c.accept(file);
          break;
        }
        return true;
      } catch (UnsupportedFlavorException | IOException ex) {
        // should never happen (or JDK is buggy)
      }
    }
    return false;
  }

}
