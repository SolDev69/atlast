package net.minecraft.client.render;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class FrustumData {
   public float[][] frustum = new float[16][16];
   public float[] projectionMatrix = new float[16];
   public float[] modelMatrix = new float[16];
   public float[] clipMatrix = new float[16];

   public boolean contains(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      for(int var13 = 0; var13 < 6; ++var13) {
         if (!(
               (double)this.frustum[var13][0] * minX
                     + (double)this.frustum[var13][1] * minY
                     + (double)this.frustum[var13][2] * minZ
                     + (double)this.frustum[var13][3]
                  > 0.0
            )
            && !(
               (double)this.frustum[var13][0] * maxX
                     + (double)this.frustum[var13][1] * minY
                     + (double)this.frustum[var13][2] * minZ
                     + (double)this.frustum[var13][3]
                  > 0.0
            )
            && !(
               (double)this.frustum[var13][0] * minX
                     + (double)this.frustum[var13][1] * maxY
                     + (double)this.frustum[var13][2] * minZ
                     + (double)this.frustum[var13][3]
                  > 0.0
            )
            && !(
               (double)this.frustum[var13][0] * maxX
                     + (double)this.frustum[var13][1] * maxY
                     + (double)this.frustum[var13][2] * minZ
                     + (double)this.frustum[var13][3]
                  > 0.0
            )
            && !(
               (double)this.frustum[var13][0] * minX
                     + (double)this.frustum[var13][1] * minY
                     + (double)this.frustum[var13][2] * maxZ
                     + (double)this.frustum[var13][3]
                  > 0.0
            )
            && !(
               (double)this.frustum[var13][0] * maxX
                     + (double)this.frustum[var13][1] * minY
                     + (double)this.frustum[var13][2] * maxZ
                     + (double)this.frustum[var13][3]
                  > 0.0
            )
            && !(
               (double)this.frustum[var13][0] * minX
                     + (double)this.frustum[var13][1] * maxY
                     + (double)this.frustum[var13][2] * maxZ
                     + (double)this.frustum[var13][3]
                  > 0.0
            )
            && !(
               (double)this.frustum[var13][0] * maxX
                     + (double)this.frustum[var13][1] * maxY
                     + (double)this.frustum[var13][2] * maxZ
                     + (double)this.frustum[var13][3]
                  > 0.0
            )) {
            return false;
         }
      }

      return true;
   }
}
