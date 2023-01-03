package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EmotionParticle extends Particle {
   float baseScale;

   protected EmotionParticle(World c_54ruxjwzt, double d, double e, double f, double g, double h, double i) {
      this(c_54ruxjwzt, d, e, f, g, h, i, 2.0F);
   }

   protected EmotionParticle(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scaleFactor) {
      super(world, x, y, z, 0.0, 0.0, 0.0);
      this.velocityX *= 0.01F;
      this.velocityY *= 0.01F;
      this.velocityZ *= 0.01F;
      this.velocityY += 0.1;
      this.scale *= 0.75F;
      this.scale *= scaleFactor;
      this.baseScale = this.scale;
      this.maxAge = 16;
      this.noClip = false;
      this.setMiscTexture(80);
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
      if (this.y == this.prevY) {
         this.velocityX *= 1.1;
         this.velocityZ *= 1.1;
      }

      this.velocityX *= 0.86F;
      this.velocityY *= 0.86F;
      this.velocityZ *= 0.86F;
      if (this.onGround) {
         this.velocityX *= 0.7F;
         this.velocityZ *= 0.7F;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class AngryFactory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         EmotionParticle var16 = new EmotionParticle(world, x, y + 0.5, z, velocityX, velocityY, velocityZ);
         var16.setMiscTexture(81);
         var16.setColor(1.0F, 1.0F, 1.0F);
         return var16;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         return new EmotionParticle(world, x, y, z, velocityX, velocityY, velocityZ);
      }
   }
}
