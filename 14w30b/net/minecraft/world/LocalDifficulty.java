package net.minecraft.world;

import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class LocalDifficulty {
   private final Difficulty base;
   private final float difficulty;

   public LocalDifficulty(Difficulty base, long timeOfDay, long inhabitedTime, float moonSize) {
      this.base = base;
      this.difficulty = this.calculate(base, timeOfDay, inhabitedTime, moonSize);
   }

   @Environment(EnvType.CLIENT)
   public float get() {
      return this.difficulty;
   }

   public float getMultiplier() {
      if (this.difficulty < 2.0F) {
         return 0.0F;
      } else {
         return this.difficulty > 4.0F ? 1.0F : (this.difficulty - 2.0F) / 2.0F;
      }
   }

   private float calculate(Difficulty base, long timeOfDay, long inhabitedTime, float moonSize) {
      if (base == Difficulty.PEACEFUL) {
         return 0.0F;
      } else {
         boolean var7 = base == Difficulty.HARD;
         float var8 = 0.75F;
         float var9 = MathHelper.clamp(((float)timeOfDay + -72000.0F) / 1440000.0F, 0.0F, 1.0F) * 0.25F;
         var8 += var9;
         float var10 = 0.0F;
         var10 += MathHelper.clamp((float)inhabitedTime / 3600000.0F, 0.0F, 1.0F) * (var7 ? 1.0F : 0.75F);
         var10 += MathHelper.clamp(moonSize * 0.25F, 0.0F, var9);
         if (base == Difficulty.EASY) {
            var10 *= 0.5F;
         }

         var8 += var10;
         return (float)base.getIndex() * var8;
      }
   }
}
