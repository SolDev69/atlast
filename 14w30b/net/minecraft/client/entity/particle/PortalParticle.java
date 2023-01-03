package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class PortalParticle extends Particle {
   private float baseScale;
   private double startX;
   private double startY;
   private double startZ;

   protected PortalParticle(World c_54ruxjwzt, double d, double e, double f, double g, double h, double i) {
      super(c_54ruxjwzt, d, e, f, g, h, i);
      this.velocityX = g;
      this.velocityY = h;
      this.velocityZ = i;
      this.startX = this.x = d;
      this.startY = this.y = e;
      this.startZ = this.z = f;
      float var14 = this.random.nextFloat() * 0.6F + 0.4F;
      this.baseScale = this.scale = this.random.nextFloat() * 0.2F + 0.5F;
      this.red = this.green = this.blue = 1.0F * var14;
      this.green *= 0.3F;
      this.red *= 0.9F;
      this.maxAge = (int)(Math.random() * 10.0) + 40;
      this.noClip = true;
      this.setMiscTexture((int)(Math.random() * 8.0));
   }

   @Override
   public void render(BufferBuilder bufferBuilder, Entity camera, float tickDelta, float dx, float dy, float dz, float forwards, float sideways) {
      float var9 = ((float)this.age + tickDelta) / (float)this.maxAge;
      var9 = 1.0F - var9;
      var9 *= var9;
      var9 = 1.0F - var9;
      this.scale = this.baseScale * var9;
      super.render(bufferBuilder, camera, tickDelta, dx, dy, dz, forwards, sideways);
   }

   @Override
   public int getLightLevel(float tickDelta) {
      int var2 = super.getLightLevel(tickDelta);
      float var3 = (float)this.age / (float)this.maxAge;
      var3 *= var3;
      var3 *= var3;
      int var4 = var2 & 0xFF;
      int var5 = var2 >> 16 & 0xFF;
      var5 += (int)(var3 * 15.0F * 16.0F);
      if (var5 > 240) {
         var5 = 240;
      }

      return var4 | var5 << 16;
   }

   @Override
   public float getBrightness(float tickDelta) {
      float var2 = super.getBrightness(tickDelta);
      float var3 = (float)this.age / (float)this.maxAge;
      var3 = var3 * var3 * var3 * var3;
      return var2 * (1.0F - var3) + var3;
   }

   @Override
   public void tick() {
      this.prevX = this.x;
      this.prevY = this.y;
      this.prevZ = this.z;
      float var1 = (float)this.age / (float)this.maxAge;
      float var3 = -var1 + var1 * var1 * 2.0F;
      float var4 = 1.0F - var3;
      this.x = this.startX + this.velocityX * (double)var4;
      this.y = this.startY + this.velocityY * (double)var4 + (double)(1.0F - var1);
      this.z = this.startZ + this.velocityZ * (double)var4;
      if (this.age++ >= this.maxAge) {
         this.remove();
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         return new PortalParticle(world, x, y, z, velocityX, velocityY, velocityZ);
      }
   }
}
