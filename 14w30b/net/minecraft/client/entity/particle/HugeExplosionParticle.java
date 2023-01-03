package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class HugeExplosionParticle extends Particle {
   private int age;
   private int maxAge = 8;

   protected HugeExplosionParticle(World c_54ruxjwzt, double d, double e, double f, double g, double h, double i) {
      super(c_54ruxjwzt, d, e, f, 0.0, 0.0, 0.0);
   }

   @Override
   public void render(BufferBuilder bufferBuilder, Entity camera, float tickDelta, float dx, float dy, float dz, float forwards, float sideways) {
   }

   @Override
   public void tick() {
      for(int var1 = 0; var1 < 6; ++var1) {
         double var2 = this.x + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
         double var4 = this.y + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
         double var6 = this.z + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
         this.world.addParticle(ParticleType.EXPLOSION_LARGE, var2, var4, var6, (double)((float)this.age / (float)this.maxAge), 0.0, 0.0);
      }

      ++this.age;
      if (this.age == this.maxAge) {
         this.remove();
      }
   }

   @Override
   public int getTextureType() {
      return 1;
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         return new HugeExplosionParticle(world, x, y, z, velocityX, velocityY, velocityZ);
      }
   }
}
