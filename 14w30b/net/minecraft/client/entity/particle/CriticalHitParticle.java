package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class CriticalHitParticle extends Particle {
   float baseScale;

   protected CriticalHitParticle(World c_54ruxjwzt, double d, double e, double f, double g, double h, double i) {
      this(c_54ruxjwzt, d, e, f, g, h, i, 1.0F);
   }

   protected CriticalHitParticle(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scaleFactor) {
      super(world, x, y, z, 0.0, 0.0, 0.0);
      this.velocityX *= 0.1F;
      this.velocityY *= 0.1F;
      this.velocityZ *= 0.1F;
      this.velocityX += velocityX * 0.4;
      this.velocityY += velocityY * 0.4;
      this.velocityZ += velocityZ * 0.4;
      this.red = this.green = this.blue = (float)(Math.random() * 0.3F + 0.6F);
      this.scale *= 0.75F;
      this.scale *= scaleFactor;
      this.baseScale = this.scale;
      this.maxAge = (int)(6.0 / (Math.random() * 0.8 + 0.6));
      this.maxAge = (int)((float)this.maxAge * scaleFactor);
      this.noClip = false;
      this.setMiscTexture(65);
      this.tick();
   }

   @Override
   public void render(BufferBuilder bufferBuilder, Entity camera, float tickDelta, float dx, float dy, float dz, float forwards, float sideways) {
      float var9 = ((float)this.age + tickDelta) / (float)this.maxAge * 32.0F;
      var9 = MathHelper.clamp(var9, 0.0F, 1.0F);
      this.scale = this.baseScale * var9;
      super.render(bufferBuilder, camera, tickDelta, dx, dy, dz, forwards, sideways);
   }

   @Override
   public void tick() {
      this.prevX = this.x;
      this.prevY = this.y;
      this.prevZ = this.z;
      if (this.age++ >= this.maxAge) {
         this.remove();
      }

      this.move(this.velocityX, this.velocityY, this.velocityZ);
      this.green = (float)((double)this.green * 0.96);
      this.blue = (float)((double)this.blue * 0.9);
      this.velocityX *= 0.7F;
      this.velocityY *= 0.7F;
      this.velocityZ *= 0.7F;
      this.velocityY -= 0.02F;
      if (this.onGround) {
         this.velocityX *= 0.7F;
         this.velocityZ *= 0.7F;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         return new CriticalHitParticle(world, x, y, z, velocityX, velocityY, velocityZ);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class MagicFactory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         CriticalHitParticle var16 = new CriticalHitParticle(world, x, y, z, velocityX, velocityY, velocityZ);
         var16.setColor(var16.getRed() * 0.3F, var16.getGreen() * 0.8F, var16.getBlue());
         var16.incrMiscTexRow();
         return var16;
      }
   }
}
