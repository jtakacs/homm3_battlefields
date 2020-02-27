package battlefieldexplorer.network;

import static java.nio.charset.StandardCharsets.UTF_8;
import battlefieldexplorer.generator.Battlefield;
import battlefieldexplorer.generator.Terrain;
import battlefieldexplorer.gui.Gui;
import battlefieldexplorer.search.BattleFieldInfo;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocketListener {

  private static final byte OK[] = "OK".getBytes(UTF_8);
  private static final byte ERR[] = "ERR".getBytes(UTF_8);
  private static final Pattern pattern = Pattern.compile("^<([A-Z_]{1,20});([0-9]{1,3});([0-9]{1,3})>");
  private final HttpServer server;
  private final Gui gui;
  private final boolean printException;

  public SocketListener(final Gui gui) {
    this(gui, false);
  }

  public SocketListener(final Gui gui, final boolean printException) {
    this.printException = printException;
    this.gui = gui;
    try {
      server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 7777), 0);
      server.createContext("/setfield", this::listener);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public void start() {
    server.start();
  }

  public void stop() {
    server.stop(0);
  }

  private void listener(final HttpExchange httpExchange) {
    try {
      final StringBuilder textBuilder = new StringBuilder();
      try (Reader reader = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), UTF_8))) {
        int c = 0;
        while ((c = reader.read()) != -1) {
          textBuilder.append((char) c);
        }
      }
      if (setField(textBuilder)) {
        httpExchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        httpExchange.sendResponseHeaders(200, OK.length);
        OutputStream out = httpExchange.getResponseBody();
        out.write(OK);
        out.close();
        httpExchange.close();
      } else {
        httpExchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        httpExchange.sendResponseHeaders(400, ERR.length);
        OutputStream out = httpExchange.getResponseBody();
        out.write(ERR);
        out.close();
        httpExchange.close();
      }
    } catch (Exception e) {
      if (printException) {
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
    }
  }

  private boolean setField(final StringBuilder textBuilder) {
    try {
      final Matcher m = pattern.matcher(textBuilder);
      if (m.matches()) {
        final String name = m.group(1).trim();
        final int x = Integer.parseInt(m.group(2).trim(), 10);
        final int y = Integer.parseInt(m.group(3).trim(), 10);
        if (0 <= x && x <= 143 && 0 <= y && y <= 143) {
          final Terrain terrain = Terrain.valueOf(name);
          final Battlefield bf = BattleFieldInfo.load().get(x, y, terrain);
          gui.setControlState(bf);
          return true;
        }
      }
    } catch (Exception e) {
      if (printException) {
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
    }
    return false;
  }

  public static void main(String[] args) throws InterruptedException {
    int x = 1;
    int y = 100;
    for (Terrain t : Terrain.values()) {
      Matcher m = pattern.matcher("<" + t.name() + ";" + x + ";" + y + ">");
      if (!m.matches()) {
        throw new RuntimeException(t.name());
      } else {
        System.out.println(m.group(1) + " , " + m.group(2) + " , " + m.group(3));
      }
      x++;
      y++;
    }
    SocketListener s = new SocketListener(new Gui() {
      @Override
      public void setControlState(Battlefield bf) {
        System.out.println(bf);
      }
    }, true);
    s.start();
    Thread.sleep(120_000L);
    s.stop();
  }
}
