package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class LavaParticle extends Particle {
   private float baseScale;

   protected LavaParticle(World c_54ruxjwzt, double d, double e, double f) {
      super(c_54ruxjwzt, d, e, f, 0.0, 0.0, 0.0);
      this.velocityX *= 0.8F;
      this.velocityY *= 0.8F;
      this.velocityZ *= 0.8F;
      this.velocityY = (double)(this.random.nextFloat() * 0.4F + 0.05F);
      this.red = this.green = this.blue = 1.0F;
      this.scale *= this.random.nextFloat() * 2.0F + 0.2F;
      this.baseScale = this.scale;
      this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
      this.noClip = false;
      this.setMiscTexture(49);
   }

   @Override
   public int getLightLevel(float tickDelta) {
      float var2 = ((float)this.age + tickDelta) / (float)this.maxAge;
      var2 = MathHelper.clamp(var2, 0.0F, 1.0F);
      int var3 = super.getLightLevel(tickDelta);
      short var4 = 240;
      int var5 = var3 >> 16 & 0xFF;
      return var4 | var5 << 16;
   }

   @Override
   public float getBrightness(float tickDelta) {
      return 1.0F;
   }

   @Override
   public void render(BufferBuilder bufferBuilder, Entity camera, float tickDelta, float dx, float dy, float dz, float forwards, float sideways) {
      float var9 = ((float)this.age + tickDelta) / (float)this.maxAge;
      this.scale = this.baseScale * (1.0F - var9 * var9);
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

      float var1 = (float)this.age / (float)this.maxAge;
      if (this.random.nextFloat() > var1) {
         this.world.addParticle(ParticleType.SMOKE_NORMAL, this.x, this.y, this.z, this.velocityX, this.velocityY, this.velocityZ);
      }

      this.velocityY -= 0.03;
      this.move(this.velocityX, this.velocityY, this.velocityZ);
      this.velocityX *= 0.999F;
      this.velocityY *= 0.999F;
      this.velocityZ *= 0.999F;
      if (this.onGround) {
         this.velocityX *= 0.7F;
         this.velocityZ *= 0.7F;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         return new LavaParticle(world, x, y, z);
      }
   }
}
