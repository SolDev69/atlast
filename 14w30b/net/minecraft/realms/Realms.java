package net.minecraft.realms;

import com.mojang.authlib.GameProfile;
import java.net.Proxy;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Session;
import net.minecraft.world.WorldSettings;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class Realms {
   public static boolean isTouchScreen() {
      return MinecraftClient.getInstance().options.touchscreen;
   }

   public static Proxy getProxy() {
      return MinecraftClient.getInstance().getNetworkProxy();
   }

   public static String sessionId() {
      Session var0 = MinecraftClient.getInstance().getSession();
      return var0 == null ? null : var0.getSessionId();
   }

   public static String userName() {
      Session var0 = MinecraftClient.getInstance().getSession();
      return var0 == null ? null : var0.getUsername();
   }

   public static long currentTimeMillis() {
      return MinecraftClient.getTime();
   }

   public static String getSessionId() {
      return MinecraftClient.getInstance().getSession().getSessionId();
   }

   public static String getName() {
      return MinecraftClient.getInstance().getSession().getUsername();
   }

   public static String uuidToName(String string) {
      return MinecraftClient.getInstance()
         .createAuthenticationService()
         .fillProfileProperties(new GameProfile(UUID.fromString(string.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5")), null), false)
         .getName();
   }

   public static void setScreen(RealmsScreen realmsScreen) {
      MinecraftClient.getInstance().openScreen(realmsScreen.getProxy());
   }

   public static String getGameDirectoryPath() {
      return MinecraftClient.getInstance().runDir.getAbsolutePath();
   }

   public static int survivalId() {
      return WorldSettings.GameMode.SURVIVAL.getIndex();
   }

   public static int creativeId() {
      return WorldSettings.GameMode.CREATIVE.getIndex();
   }

   public static int adventureId() {
      return WorldSettings.GameMode.ADVENTURE.getIndex();
   }
}
