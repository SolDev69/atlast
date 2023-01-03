package net.minecraft.util;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class Utils {
   public static Utils.OS getOS() {
      String var0 = System.getProperty("os.name").toLowerCase();
      if (var0.contains("win")) {
         return Utils.OS.WINDOWS;
      } else if (var0.contains("mac")) {
         return Utils.OS.MACOS;
      } else if (var0.contains("solaris")) {
         return Utils.OS.SOLARIS;
      } else if (var0.contains("sunos")) {
         return Utils.OS.SOLARIS;
      } else if (var0.contains("linux")) {
         return Utils.OS.LINUX;
      } else {
         return var0.contains("unix") ? Utils.OS.LINUX : Utils.OS.UNKNOWN;
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum OS {
      LINUX,
      SOLARIS,
      WINDOWS,
      MACOS,
      UNKNOWN;
   }
}
