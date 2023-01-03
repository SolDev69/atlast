package net.minecraft.client.render.block;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public enum BlockLayer {
   SOLID("Solid"),
   CUTOUT_MIPPED("Mipped Cutout"),
   CUTOUT("Cutout"),
   TRANSLUCENT("Translucent");

   private final String name;

   private BlockLayer(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return this.name;
   }
}
