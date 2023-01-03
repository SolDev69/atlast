package net.minecraft.client.entity.particle;

import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class WakeParticle extends Particle {
   protected WakeParticle(World c_54ruxjwzt, double d, double e, double f, double g, double h, double i) {
      super(c_54ruxjwzt, d, e, f, 0.0, 0.0, 0.0);
      this.velocityX *= 0.3F;
      this.velocityY = Math.random() * 0.2F + 0.1F;
      this.velocityZ *= 0.3F;
      this.red = 1.0F;
      this.green = 1.0F;
      this.blue = 1.0F;
      this.setMiscTexture(19);
      this.setDimensions(0.01F, 0.01F);
      this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
      this.gravity = 0.0F;
      this.velocityX = g;
      this.velocityY = h;
      this.velocityZ = i;
   }

   @Override
   public void tick() {
      this.prevX = this.x;
      this.prevY = this.y;
      this.prevZ = this.z;
      this.velocityY -= (double)this.gravity;
      this.move(this.velocityX, this.velocityY, this.velocityZ);
      this.velocityX *= 0.98F;
      this.velocityY *= 0.98F;
      this.velocityZ *= 0.98F;
      int var1 = 60 - this.maxAge;
      float var2 = (float)var1 * 0.001F;
      this.setDimensions(var2, var2);
      this.setMiscTexture(19 + var1 % 4);
      if (this.maxAge-- <= 0) {
         this.remove();
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         return new WakeParticle(world, x, y, z, velocityX, velocityY, velocityZ);
      }
   }
}
