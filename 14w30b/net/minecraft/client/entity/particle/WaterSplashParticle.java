package net.minecraft.client.entity.particle;

import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class WaterSplashParticle extends RainSplashParticle {
   protected WaterSplashParticle(World c_54ruxjwzt, double d, double e, double f, double g, double h, double i) {
      super(c_54ruxjwzt, d, e, f);
      this.gravity = 0.04F;
      this.incrMiscTexRow();
      if (h == 0.0 && (g != 0.0 || i != 0.0)) {
         this.velocityX = g;
         this.velocityY = h + 0.1;
         this.velocityZ = i;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         return new WaterSplashParticle(world, x, y, z, velocityX, velocityY, velocityZ);
      }
   }
}
