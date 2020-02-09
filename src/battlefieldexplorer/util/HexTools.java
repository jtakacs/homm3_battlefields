package battlefieldexplorer.util;

import static battlefieldexplorer.util.Constants.*;
import battlefieldexplorer.generator.BitVector;
import java.util.*;

public class HexTools {

  public static final BitVector calcBitMask(final Set<Integer> obstacle) {
    BitVector mask = new BitVector();
    final List<Integer> blocked = new ArrayList<>();
    for (final int hex : obstacle) {
      blocked.add(distort(hex));
    }
    Collections.sort(blocked);
    for (int i = BFIELD_SIZE - 1; i >= 0; i--) {
      if (blocked.contains(i)) {
        mask.set(0);
      }
      mask.shiftLeft(1);
    }
    return mask;
  }

  public static int distort(final int o) {
    if (o > 153) {
      return (o - 5);
    } else if (o > 119) {
      return (o - 4);
    } else if (o > 85) {
      return (o - 3);
    } else if (o > 51) {
      return (o - 2);
    } else if (o > 17) {
      return (o - 1);
    }
    return o;
  }

  public static int relocateHex(final int hex, final int offset) {
    final int pos = hex + offset;
    if (isOddRow(getY(hex))) {
      if (!isOddRow(getY(pos))) {
        return pos - 1;
      }
    }
    return pos;
  }

  public static int getX(final int hex) {
    if (hex < 0 || BFIELD_SIZE <= hex) {
      throw new IllegalArgumentException("value must be between 0 and " + BFIELD_SIZE);
    }
    return hex % BFIELD_WIDTH;
  }

  public static int getY(final int hex) {
    if (hex < 0 || BFIELD_SIZE <= hex) {
      throw new IllegalArgumentException("value must be between 0 and " + BFIELD_SIZE);
    }
    return hex / BFIELD_WIDTH;
  }

  public static boolean hexIsValid(final int hex) {
    return (0 <= hex) && (hex < BFIELD_SIZE);
  }

  public static boolean hexIsVisible(final int hex) {
    final int x = getX(hex);
    return (0 < x) && (x < BFIELD_WIDTH - 1);
  }

  public static int posToHex(final int x, final int y) {
    return x + y * BFIELD_WIDTH;
  }

  public static boolean isOddRow(final int y) {
    return 0 < (y & 0x01);
  }

  public static void hexdump(final Set<Integer> blocked) {
    final StringBuilder sb = new StringBuilder();
    for (int y = 0; y < BFIELD_HEIGHT; y++) {
      final boolean ODD = isOddRow(y);
      if (!ODD) {
        sb.append(" /  \\  ");
        for (int top = 0; top < BFIELD_WIDTH; top++) {
          final int i = top + y * BFIELD_WIDTH;
          final int k = top + 1 + (y - 1) * BFIELD_WIDTH;
          if (blocked.contains(i)) {
            sb.append("/##\\");
          } else {
            sb.append("/  \\");
          }
          if (blocked.contains(k)) {
            sb.append("##");
          } else {
            sb.append("  ");
          }
        }
        sb.append(System.lineSeparator())
                .append("|    ");
      } else {
        sb.append("  ");
      }
      for (int x = 0; x < BFIELD_WIDTH; x++) {
        final int i = x + y * BFIELD_WIDTH;
        if (blocked.contains(i)) {
          sb.append(String.format("|#%03d#", i));
        } else {
          sb.append(String.format("| %03d ", i));
        }
      }
      sb.append("|").append(System.lineSeparator());
      if (!ODD) {
        sb.append(" \\  /  ");
        for (int bottom = 0; bottom < BFIELD_WIDTH; bottom++) {
          final int i = bottom + y * BFIELD_WIDTH;
          final int k = bottom + 1 + (y + 1) * BFIELD_WIDTH;
          if (blocked.contains(i)) {
            sb.append("\\##/");
          } else {
            sb.append("\\  /");
          }
          if (blocked.contains(k)) {
            sb.append("##");
          } else {
            sb.append("  ");
          }
        }
        sb.append(System.lineSeparator());
      }
    }
    sb.append(System.lineSeparator());
    System.out.println(sb);
  }

  public static <K, V> Map.Entry<K, V> pair(final K key, final V value) {
    return new AbstractMap.SimpleImmutableEntry<>(key, value);
  }

  private HexTools() {
  }

}
