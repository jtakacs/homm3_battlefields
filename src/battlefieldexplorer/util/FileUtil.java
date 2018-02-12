package battlefieldexplorer.util;

import java.io.File;
import java.net.*;
import java.util.Optional;

public class FileUtil {

  public static boolean isWindows() {
    return System.getProperty("os.name", "Unknown").startsWith("Win");
  }

  public static Optional<File> getLocation() {
    return urlToFile(Optional.ofNullable(a()).orElseGet(FileUtil::b));
  }

  private static URL a() {
    try {
      return FileUtil.class.getProtectionDomain().getCodeSource().getLocation();
    } catch (final SecurityException | NullPointerException e) {
    }
    return null;
  }

  private static URL b() {
    try {
      final Class<FileUtil> c = FileUtil.class;
      final String suffix = c.getCanonicalName().replace('.', '/') + ".class";
      return new URL(c.getResource(c.getSimpleName() + ".class")
              .toString()
              .replaceFirst(suffix, "")
              .replaceFirst("jar:", "")
              .replaceFirst("!/", ""));
    } catch (MalformedURLException ex) {
    }
    return null;
  }

  private static Optional<File> urlToFile(final URL url) {
    if (url != null) {
      String path = url.toString()
              .replaceFirst("jar:", "")
              .replaceFirst("!/", "");
      try {
        if (isWindows() && path.matches("file:[A-Za-z]:.*")) {
          path = "file:/" + path.substring(5);
        }
        return Optional.of(new File(new URL(path).toURI()));
      } catch (final MalformedURLException | URISyntaxException e) {
      }
      if (path.startsWith("file:")) {
        return Optional.of(new File(path.substring(5)));
      }
    }
    return Optional.empty();
  }

  public static void main(String[] args) {
    getLocation().ifPresent(l -> {
      System.out.println("LOCATION: " + l.getAbsolutePath());
    });
  }

  private FileUtil() {
  }

}
