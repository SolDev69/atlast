package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SnowShovelParticle extends Particle {
   float baseScale;

   protected SnowShovelParticle(World c_54ruxjwzt, double d, double e, double f, double g, double h, double i) {
      this(c_54ruxjwzt, d, e, f, g, h, i, 1.0F);
   }

   protected SnowShovelParticle(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scaleFactor) {
      super(world, x, y, z, velocityX, velocityY, velocityZ);
      this.velocityX *= 0.1F;
      this.velocityY *= 0.1F;
      this.velocityZ *= 0.1F;
      this.velocityX += velocityX;
      this.velocityY += velocityY;
      this.velocityZ += velocityZ;
      this.red = this.green = this.blue = 1.0F - (float)(Math.random() * 0.3F);
      this.scale *= 0.75F;
      this.scale *= scaleFactor;
      this.baseScale = this.scale;
      this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
      this.maxAge = (int)((float)this.maxAge * scaleFactor);
      this.noClip = false;
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

      this.setMiscTexture(7 - this.age * 8 / this.maxAge);
      this.velocityY -= 0.03;
      this.move(this.velocityX, this.velocityY, this.velocityZ);
      this.velocityX *= 0.99F;
      this.velocityY *= 0.99F;
      this.velocityZ *= 0.99F;
      if (this.onGround) {
         this.velocityX *= 0.7F;
         this.velocityZ *= 0.7F;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         return new SnowShovelParticle(world, x, y, z, velocityX, velocityY, velocityZ);
      }
   }
}
