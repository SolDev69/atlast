package net.minecraft.client.world.color;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class FoliageColors {
   private static int[] colors = new int[65536];

   public static void set(int[] colors) {
      FoliageColors.colors = colors;
   }

   public static int get(double temperature, double humidity) {
      humidity *= temperature;
      int var4 = (int)((1.0 - temperature) * 255.0);
      int var5 = (int)((1.0 - humidity) * 255.0);
      return colors[var5 << 8 | var4];
   }

   public static int getSpruceColor() {
      return 6396257;
   }

   public static int getBirchColor() {
      return 8431445;
   }

   public static int getDefaultColor() {
      return 4764952;
   }
}
