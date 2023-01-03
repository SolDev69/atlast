package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class FlameParticle extends Particle {
   private float baseScale;

   protected FlameParticle(World c_54ruxjwzt, double d, double e, double f, double g, double h, double i) {
      super(c_54ruxjwzt, d, e, f, g, h, i);
      this.velocityX = this.velocityX * 0.01F + g;
      this.velocityY = this.velocityY * 0.01F + h;
      this.velocityZ = this.velocityZ * 0.01F + i;
      d += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      e += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      f += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.baseScale = this.scale;
      this.red = this.green = this.blue = 1.0F;
      this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
      this.noClip = true;
      this.setMiscTexture(48);
   }

   @Override
   public void render(BufferBuilder bufferBuilder, Entity camera, float tickDelta, float dx, float dy, float dz, float forwards, float sideways) {
      float var9 = ((float)this.age + tickDelta) / (float)this.maxAge;
      this.scale = this.baseScale * (1.0F - var9 * var9 * 0.5F);
      super.render(bufferBuilder, camera, tickDelta, dx, dy, dz, forwards, sideways);
   }

   @Override
   public int getLightLevel(float tickDelta) {
      float var2 = ((float)this.age + tickDelta) / (float)this.maxAge;
      var2 = MathHelper.clamp(var2, 0.0F, 1.0F);
      int var3 = super.getLightLevel(tickDelta);
      int var4 = var3 & 0xFF;
      int var5 = var3 >> 16 & 0xFF;
      var4 += (int)(var2 * 15.0F * 16.0F);
      if (var4 > 240) {
         var4 = 240;
      }

      return var4 | var5 << 16;
   }

   @Override
   public float getBrightness(float tickDelta) {
      float var2 = ((float)this.age + tickDelta) / (float)this.maxAge;
      var2 = MathHelper.clamp(var2, 0.0F, 1.0F);
      float var3 = super.getBrightness(tickDelta);
      return var3 * var2 + (1.0F - var2);
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
         return new FlameParticle(world, x, y, z, velocityX, velocityY, velocityZ);
      }
   }
}
