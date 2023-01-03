package net.minecraft.client.entity.particle;

import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class DepthSuspendParticle extends Particle {
   protected DepthSuspendParticle(World c_54ruxjwzt, double d, double e, double f, double g, double h, double i) {
      super(c_54ruxjwzt, d, e, f, g, h, i);
      float var14 = this.random.nextFloat() * 0.1F + 0.2F;
      this.red = var14;
      this.green = var14;
      this.blue = var14;
      this.setMiscTexture(0);
      this.setDimensions(0.02F, 0.02F);
      this.scale *= this.random.nextFloat() * 0.6F + 0.5F;
      this.velocityX *= 0.02F;
      this.velocityY *= 0.02F;
      this.velocityZ *= 0.02F;
      this.maxAge = (int)(20.0 / (Math.random() * 0.8 + 0.2));
      this.noClip = true;
   }

   @Override
   public void tick() {
      this.prevX = this.x;
      this.prevY = this.y;
      this.prevZ = this.z;
      this.move(this.velocityX, this.velocityY, this.velocityZ);
      this.velocityX *= 0.99;
      this.velocityY *= 0.99;
      this.velocityZ *= 0.99;
      if (this.maxAge-- <= 0) {
         this.remove();
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         return new DepthSuspendParticle(world, x, y, z, velocityX, velocityY, velocityZ);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class HappyFactory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         DepthSuspendParticle var16 = new DepthSuspendParticle(world, x, y, z, velocityX, velocityY, velocityZ);
         var16.setMiscTexture(82);
         var16.setColor(1.0F, 1.0F, 1.0F);
         return var16;
      }
   }
}
