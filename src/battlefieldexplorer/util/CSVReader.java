package battlefieldexplorer.util;

import java.util.Scanner;
import java.util.function.Consumer;

public abstract class CSVReader<T> {

  public abstract T parseLine(final String line);

  public final void read(final String filename, final Consumer<T> fn) {
    try (final Scanner scanner = new Scanner(CSVReader.class.getResourceAsStream("/" + filename))) {
      if (scanner.hasNextLine()) {
        scanner.nextLine();
      }
      while (scanner.hasNextLine()) {
        fn.accept(parseLine(scanner.nextLine()));
      }
    }
  }
}
