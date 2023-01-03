package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SpellParticle extends Particle {
   private int miscTexOffset = 128;

   protected SpellParticle(World c_54ruxjwzt, double d, double e, double f, double g, double h, double i) {
      super(c_54ruxjwzt, d, e, f, g, h, i);
      this.velocityY *= 0.2F;
      if (g == 0.0 && i == 0.0) {
         this.velocityX *= 0.1F;
         this.velocityZ *= 0.1F;
      }

      this.scale *= 0.75F;
      this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
      this.noClip = false;
   }

   @Override
   public void render(BufferBuilder bufferBuilder, Entity camera, float tickDelta, float dx, float dy, float dz, float forwards, float sideways) {
      float var9 = ((float)this.age + tickDelta) / (float)this.maxAge * 32.0F;
      var9 = MathHelper.clamp(var9, 0.0F, 1.0F);
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

      this.setMiscTexture(this.miscTexOffset + (7 - this.age * 8 / this.maxAge));
      this.velocityY += 0.004;
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

   public void setMiscTexOffset(int offset) {
      this.miscTexOffset = offset;
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         return new SpellParticle(world, x, y, z, velocityX, velocityY, velocityZ);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class InstantFactory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         SpellParticle var16 = new SpellParticle(world, x, y, z, velocityX, velocityY, velocityZ);
         var16.setMiscTexOffset(144);
         return var16;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class MobAmbientFactory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         SpellParticle var16 = new SpellParticle(world, x, y, z, velocityX, velocityY, velocityZ);
         var16.setAlpha(0.15F);
         var16.setColor((float)velocityX, (float)velocityY, (float)velocityZ);
         return var16;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class MobFactory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         SpellParticle var16 = new SpellParticle(world, x, y, z, velocityX, velocityY, velocityZ);
         var16.setColor((float)velocityX, (float)velocityY, (float)velocityZ);
         return var16;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class WitchFactory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         SpellParticle var16 = new SpellParticle(world, x, y, z, velocityX, velocityY, velocityZ);
         var16.setMiscTexOffset(144);
         float var17 = world.random.nextFloat() * 0.5F + 0.35F;
         var16.setColor(1.0F * var17, 0.0F * var17, 1.0F * var17);
         return var16;
      }
   }
}
