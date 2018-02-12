package battlefieldexplorer.gui;

import javax.swing.SpinnerNumberModel;

public class SpinnerIntModel extends SpinnerNumberModel {

  private static final long serialVersionUID = 1L;
  private final int min_value;
  private final int max_value;

  public SpinnerIntModel(final int value, final int minimum, final int maximum) {
    super(value, minimum, maximum, 1);
    this.min_value = minimum;
    this.max_value = maximum;
  }

  public int value() {
    return super.getNumber().intValue();
  }

  public void setIntValue(final int v) {
    if (v < min_value) {
      super.setValue(min_value);
    } else if (v > max_value) {
      super.setValue(max_value);
    } else {
      super.setValue(v);
    }
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
