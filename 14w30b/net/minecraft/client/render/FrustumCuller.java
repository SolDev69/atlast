package net.minecraft.client.render;

import net.minecraft.util.math.Box;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class FrustumCuller implements Culler {
   private FrustumData frustum;
   private double x;
   private double y;
   private double z;

   public FrustumCuller() {
      this(Frustum.getInstance());
   }

   public FrustumCuller(FrustumData frustum) {
      this.frustum = frustum;
   }

   @Override
   public void set(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public boolean isVisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      return this.frustum.contains(minX - this.x, minY - this.y, minZ - this.z, maxX - this.x, maxY - this.y, maxZ - this.z);
   }

   @Override
   public boolean isVisible(Box box) {
      return this.isVisible(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
   }
}
