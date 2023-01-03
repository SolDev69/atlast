package net.minecraft.entity.living.mob.hostile.boss;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public final class BossBar {
   public static float health;
   public static int timer;
   public static String name;
   public static boolean modifiesSkyColor;

   public static void update(Boss boss, boolean modifiesSkyColor) {
      health = boss.getHealth() / boss.getMaxHealth();
      timer = 100;
      name = boss.getDisplayName().buildFormattedString();
      BossBar.modifiesSkyColor = modifiesSkyColor;
   }
}
