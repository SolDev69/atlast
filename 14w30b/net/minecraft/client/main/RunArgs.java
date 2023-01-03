package net.minecraft.client.main;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import net.minecraft.client.Session;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class RunArgs {
   public final RunArgs.User user;
   public final RunArgs.Display display;
   public final RunArgs.Directories location;
   public final RunArgs.Game game;
   public final RunArgs.Server server;

   public RunArgs(RunArgs.User user, RunArgs.Display display, RunArgs.Directories location, RunArgs.Game game, RunArgs.Server server) {
      this.user = user;
      this.display = display;
      this.location = location;
      this.game = game;
      this.server = server;
   }

   @Environment(EnvType.CLIENT)
   public static class Directories {
      public final File gameDir;
      public final File resourcePacksDir;
      public final File assetsDir;
      public final String assetIndex;

      public Directories(File gameDir, File resourcePacksDir, File assetsDir, String assetIndex) {
         this.gameDir = gameDir;
         this.resourcePacksDir = resourcePacksDir;
         this.assetsDir = assetsDir;
         this.assetIndex = assetIndex;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Display {
      public final int width;
      public final int height;
      public final boolean fullscreen;
      public final boolean checkGlErrors;

      public Display(int width, int height, boolean fullscreen, boolean checkGlErrors) {
         this.width = width;
         this.height = height;
         this.fullscreen = fullscreen;
         this.checkGlErrors = checkGlErrors;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Game {
      public final boolean demo;
      public final String version;

      public Game(boolean demo, String version) {
         this.demo = demo;
         this.version = version;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Server {
      public final String ip;
      public final int port;

      public Server(String ip, int port) {
         this.ip = ip;
         this.port = port;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class User {
      public final Session session;
      public final PropertyMap userProperties;
      public final Proxy proxy;

      public User(Session c_33rxbrxcu, PropertyMap propertyMap, Proxy proxy) {
         this.session = c_33rxbrxcu;
         this.userProperties = propertyMap;
         this.proxy = proxy;
      }
   }
}
