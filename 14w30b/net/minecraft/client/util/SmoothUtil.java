package net.minecraft.client.util;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SmoothUtil {
   private float actualSum;
   private float smoothedSum;
   private float movementLatency;

   public float smooth(float original, float smoother) {
      this.actualSum += original;
      original = (this.actualSum - this.smoothedSum) * smoother;
      this.movementLatency += (original - this.movementLatency) * 0.5F;
      if (original > 0.0F && original > this.movementLatency || original < 0.0F && original < this.movementLatency) {
         original = this.movementLatency;
      }

      this.smoothedSum += original;
      return original;
   }
}
