package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class CloudParticle extends Particle {
   float baseScale;

   protected CloudParticle(World c_54ruxjwzt, double d, double e, double f, double g, double h, double i) {
      super(c_54ruxjwzt, d, e, f, 0.0, 0.0, 0.0);
      float var14 = 2.5F;
      this.velocityX *= 0.1F;
      this.velocityY *= 0.1F;
      this.velocityZ *= 0.1F;
      this.velocityX += g;
      this.velocityY += h;
      this.velocityZ += i;
      this.red = this.green = this.blue = 1.0F - (float)(Math.random() * 0.3F);
      this.scale *= 0.75F;
      this.scale *= var14;
      this.baseScale = this.scale;
      this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.3));
      this.maxAge = (int)((float)this.maxAge * var14);
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
      this.velocityX *= 0.96F;
      this.velocityY *= 0.96F;
      this.velocityZ *= 0.96F;
      PlayerEntity var1 = this.world.getClosestPlayer(this, 2.0);
      if (var1 != null && this.y > var1.getBoundingBox().minY) {
         this.y += (var1.getBoundingBox().minY - this.y) * 0.2;
         this.velocityY += (var1.velocityY - this.velocityY) * 0.2;
         this.setPosition(this.x, this.y, this.z);
      }

      if (this.onGround) {
         this.velocityX *= 0.7F;
         this.velocityZ *= 0.7F;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         return new CloudParticle(world, x, y, z, velocityX, velocityY, velocityZ);
      }
   }
}
