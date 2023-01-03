package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.hostile.GuardianEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class MobAppearanceParticle extends Particle {
   private LivingEntity mob;

   protected MobAppearanceParticle(World c_54ruxjwzt, double d, double e, double f) {
      super(c_54ruxjwzt, d, e, f, 0.0, 0.0, 0.0);
      this.red = this.green = this.blue = 1.0F;
      this.velocityX = this.velocityY = this.velocityZ = 0.0;
      this.gravity = 0.0F;
      this.maxAge = 30;
   }

   @Override
   public int getTextureType() {
      return 3;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.mob == null) {
         GuardianEntity var1 = new GuardianEntity(this.world);
         var1.m_28hbhdoxr();
         this.mob = var1;
      }
   }

   @Override
   public void render(BufferBuilder bufferBuilder, Entity camera, float tickDelta, float dx, float dy, float dz, float forwards, float sideways) {
      EntityRenderDispatcher var9 = MinecraftClient.getInstance().getEntityRenderDispatcher();
      var9.setCameraPos(Particle.currentX, Particle.currentY, Particle.currentZ);
      float var10 = 0.42553192F;
      float var11 = ((float)this.age + tickDelta) / (float)this.maxAge;
      GlStateManager.depthMask(true);
      GlStateManager.disableBlend();
      GlStateManager.disableDepth();
      GlStateManager.blendFunc(770, 771);
      float var12 = 240.0F;
      GLX.multiTexCoord2f(GLX.GL_TEXTURE1, var12, var12);
      GlStateManager.pushMatrix();
      float var13 = 0.05F + 0.5F * MathHelper.sin(var11 * (float) Math.PI);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, var13);
      GlStateManager.translatef(0.0F, 1.8F, 0.0F);
      GlStateManager.rotatef(180.0F - camera.yaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(60.0F - 150.0F * var11 - camera.pitch, 1.0F, 0.0F, 0.0F);
      GlStateManager.translatef(0.0F, -0.4F, -1.5F);
      GlStateManager.scalef(var10, var10, var10);
      this.mob.yaw = this.mob.prevYaw = 0.0F;
      this.mob.headYaw = this.mob.prevHeadYaw = 0.0F;
      var9.render(this.mob, 0.0, 0.0, 0.0, 0.0F, tickDelta);
      GlStateManager.popMatrix();
      GlStateManager.disableDepth();
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         return new MobAppearanceParticle(world, x, y, z);
      }
   }
}
