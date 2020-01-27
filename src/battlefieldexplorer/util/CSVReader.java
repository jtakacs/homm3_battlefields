package battlefieldexplorer.util;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;

public abstract class CSVReader<T> implements Iterable<T> {

  public final String filename;

  public CSVReader(String filename) {
    this.filename = "/" + filename;
  }

  public abstract T parseLine(final String line);

  public final void read(final Consumer<T> fn) {
    InputStream is = CSVReader.class.getResourceAsStream(filename);
    if (filename.endsWith(".gz")) {
      try {
        is = new GZIPInputStream(is);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
    try (final Scanner scanner = new Scanner(is, US_ASCII.name())) {
      if (scanner.hasNextLine()) {
        scanner.nextLine();
      }
      while (scanner.hasNextLine()) {
        fn.accept(parseLine(scanner.nextLine()));
      }
    }
  }

  public static <T extends Number> List<T> toNumberList(final String text, final String separator, final Function<String, T> fn) {
    return asList(text.split(separator))
            .stream()
            .map(fn)
            .collect(toList());
  }

  @Override
  public Iterator<T> iterator() {
    return new Iterator<T>() {
      private final Scanner scanner = new Scanner(CSVReader.class.getResourceAsStream(filename), US_ASCII.name());
      private boolean first = true;

      @Override
      public boolean hasNext() {
        return scanner.hasNextLine();
      }

      @Override
      public T next() {
        if (first) {
          first = false;
          if (hasNext()) {
            scanner.nextLine();
          }
        }
        if (hasNext()) {
          return parseLine(scanner.nextLine());
        }
        scanner.close();
        return null;
      }
    };
  }

}
