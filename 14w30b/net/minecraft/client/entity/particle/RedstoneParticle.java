package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class RedstoneParticle extends Particle {
   float baseScale;

   protected RedstoneParticle(World world, double x, double y, double z, float r, float g, float b) {
      this(world, x, y, z, 1.0F, r, g, b);
   }

   protected RedstoneParticle(World world, double x, double y, double z, float scaleFactor, float r, float g, float b) {
      super(world, x, y, z, 0.0, 0.0, 0.0);
      this.velocityX *= 0.1F;
      this.velocityY *= 0.1F;
      this.velocityZ *= 0.1F;
      if (r == 0.0F) {
         r = 1.0F;
      }

      float var12 = (float)Math.random() * 0.4F + 0.6F;
      this.red = ((float)(Math.random() * 0.2F) + 0.8F) * r * var12;
      this.green = ((float)(Math.random() * 0.2F) + 0.8F) * g * var12;
      this.blue = ((float)(Math.random() * 0.2F) + 0.8F) * b * var12;
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
      this.move(this.velocityX, this.velocityY, this.velocityZ);
      if (this.y == this.prevY) {
         this.velocityX *= 1.1;
         this.velocityZ *= 1.1;
      }

      this.velocityX *= 0.96F;
      this.velocityY *= 0.96F;
      this.velocityZ *= 0.96F;
      if (this.onGround) {
         this.velocityX *= 0.7F;
         this.velocityZ *= 0.7F;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         return new RedstoneParticle(world, x, y, z, (float)velocityX, (float)velocityY, (float)velocityZ);
      }
   }
}
