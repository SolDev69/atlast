package net.minecraft.world.border;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public enum BorderStatus {
   GROWING(4259712),
   SHRINKING(16724016),
   STATIONARY(2138367);

   private final int color;

   private BorderStatus(int color) {
      this.color = color;
   }

   @Environment(EnvType.CLIENT)
   public int getColor() {
      return this.color;
   }
}
