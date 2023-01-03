package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class FootstepParticle extends Particle {
   private static final Identifier FOOTPRINT = new Identifier("textures/particle/footprint.png");
   private int age;
   private int maxAge;
   private TextureManager textureManager;

   protected FootstepParticle(TextureManager textureManager, World world, double x, double y, double z) {
      super(world, x, y, z, 0.0, 0.0, 0.0);
      this.textureManager = textureManager;
      this.velocityX = this.velocityY = this.velocityZ = 0.0;
      this.maxAge = 200;
   }

   @Override
   public void render(BufferBuilder bufferBuilder, Entity camera, float tickDelta, float dx, float dy, float dz, float forwards, float sideways) {
      float var9 = ((float)this.age + tickDelta) / (float)this.maxAge;
      var9 *= var9;
      float var10 = 2.0F - var9 * 2.0F;
      if (var10 > 1.0F) {
         var10 = 1.0F;
      }

      var10 *= 0.2F;
      GlStateManager.disableLighting();
      float var11 = 0.125F;
      float var12 = (float)(this.x - currentX);
      float var13 = (float)(this.y - currentY);
      float var14 = (float)(this.z - currentZ);
      float var15 = this.world.getBrightness(new BlockPos(this));
      this.textureManager.bind(FOOTPRINT);
      GlStateManager.disableBlend();
      GlStateManager.blendFunc(770, 771);
      bufferBuilder.start();
      bufferBuilder.color(var15, var15, var15, var10);
      bufferBuilder.vertex((double)(var12 - var11), (double)var13, (double)(var14 + var11), 0.0, 1.0);
      bufferBuilder.vertex((double)(var12 + var11), (double)var13, (double)(var14 + var11), 1.0, 1.0);
      bufferBuilder.vertex((double)(var12 + var11), (double)var13, (double)(var14 - var11), 1.0, 0.0);
      bufferBuilder.vertex((double)(var12 - var11), (double)var13, (double)(var14 - var11), 0.0, 0.0);
      Tessellator.getInstance().end();
      GlStateManager.enableBlend();
      GlStateManager.enableLighting();
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

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         return new FootstepParticle(MinecraftClient.getInstance().getTextureManager(), world, x, y, z);
      }
   }
}
