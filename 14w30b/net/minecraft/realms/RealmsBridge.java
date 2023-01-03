package net.minecraft.realms;

import java.lang.reflect.Constructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsBridge extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private Screen previousScreen;

   public void switchToRealms(Screen c_31rdgoemj) {
      this.previousScreen = c_31rdgoemj;

      try {
         Class var2 = Class.forName("com.mojang.realmsclient.RealmsMainScreen");
         Constructor var3 = var2.getDeclaredConstructor(RealmsScreen.class);
         var3.setAccessible(true);
         Object var4 = var3.newInstance(this);
         MinecraftClient.getInstance().openScreen(((RealmsScreen)var4).getProxy());
      } catch (Exception var5) {
         LOGGER.error("Realms module missing", var5);
      }
   }

   @Override
   public void init() {
      MinecraftClient.getInstance().openScreen(this.previousScreen);
   }
}
