package battlefieldexplorer.gui;

import javax.swing.SpinnerNumberModel;

public class SpinnerIntModel extends SpinnerNumberModel {

  private static final long serialVersionUID = 1L;

  public SpinnerIntModel(final int value, final int minimum, final int maximum, final int stepSize) {
    super(value, minimum, maximum, stepSize);
  }

  public int value() {
    return super.getNumber().intValue();
  }

  public void increment() {
    final Object nextValue = super.getNextValue();
    if (nextValue != null) {
      super.setValue(nextValue);
    }
  }

  public void decrement() {
    final Object prevValue = super.getPreviousValue();
    if (prevValue != null) {
      super.setValue(prevValue);
    }
  }

}
