package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EntityPickupParticle extends Particle {
   private Entity entity;
   private Entity collector;
   private int age;
   private int maxAge;
   private float yOffset;
   private EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();

   public EntityPickupParticle(World world, Entity entity, Entity collector, float yOffset) {
      super(world, entity.x, entity.y, entity.z, entity.velocityX, entity.velocityY, entity.velocityZ);
      this.entity = entity;
      this.collector = collector;
      this.maxAge = 3;
      this.yOffset = yOffset;
   }

   @Override
   public void render(BufferBuilder bufferBuilder, Entity camera, float tickDelta, float dx, float dy, float dz, float forwards, float sideways) {
      float var9 = ((float)this.age + tickDelta) / (float)this.maxAge;
      var9 *= var9;
      double var10 = this.entity.x;
      double var12 = this.entity.y;
      double var14 = this.entity.z;
      double var16 = this.collector.prevTickX + (this.collector.x - this.collector.prevTickX) * (double)tickDelta;
      double var18 = this.collector.prevTickY + (this.collector.y - this.collector.prevTickY) * (double)tickDelta + (double)this.yOffset;
      double var20 = this.collector.prevTickZ + (this.collector.z - this.collector.prevTickZ) * (double)tickDelta;
      double var22 = var10 + (var16 - var10) * (double)var9;
      double var24 = var12 + (var18 - var12) * (double)var9;
      double var26 = var14 + (var20 - var14) * (double)var9;
      int var28 = this.getLightLevel(tickDelta);
      int var29 = var28 % 65536;
      int var30 = var28 / 65536;
      GLX.multiTexCoord2f(GLX.GL_TEXTURE1, (float)var29 / 1.0F, (float)var30 / 1.0F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      var22 -= currentX;
      var24 -= currentY;
      var26 -= currentZ;
      this.entityRenderDispatcher.render(this.entity, (double)((float)var22), (double)((float)var24), (double)((float)var26), this.entity.yaw, tickDelta);
   }

   @Override
   public void tick() {
      ++this.age;
      if (this.age == this.maxAge) {
         this.remove();
      }
   }

   @Override
   public int getTextureType() {
      return 3;
   }
}
