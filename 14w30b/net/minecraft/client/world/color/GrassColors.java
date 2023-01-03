package net.minecraft.client.world.color;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class GrassColors {
   private static int[] colorMap = new int[65536];

   public static void setColorMap(int[] map) {
      colorMap = map;
   }

   public static int getColor(double temperature, double humidity) {
      humidity *= temperature;
      int var4 = (int)((1.0 - temperature) * 255.0);
      int var5 = (int)((1.0 - humidity) * 255.0);
      int var6 = var5 << 8 | var4;
      return var6 > colorMap.length ? -65281 : colorMap[var6];
   }
}
