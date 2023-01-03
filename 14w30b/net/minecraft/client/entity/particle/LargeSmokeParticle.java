package net.minecraft.client.entity.particle;

import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class LargeSmokeParticle extends SmokeParticle {
   protected LargeSmokeParticle(World c_54ruxjwzt, double d, double e, double f, double g, double h, double i) {
      super(c_54ruxjwzt, d, e, f, g, h, i, 2.5F);
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         return new LargeSmokeParticle(world, x, y, z, velocityX, velocityY, velocityZ);
      }
   }
}
