package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.resource.Identifier;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class LargeExplosionParticle extends Particle {
   private static final Identifier EXPLOSION_TEXTURE = new Identifier("textures/entity/explosion.png");
   private int age;
   private int maxAge;
   private TextureManager textureManager;
   private float scaleFactor;

   protected LargeExplosionParticle(
      TextureManager textureManager, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ
   ) {
      super(world, x, y, z, 0.0, 0.0, 0.0);
      this.textureManager = textureManager;
      this.maxAge = 6 + this.random.nextInt(4);
      this.red = this.green = this.blue = this.random.nextFloat() * 0.6F + 0.4F;
      this.scaleFactor = 1.0F - (float)velocityX * 0.5F;
   }

   @Override
   public void render(BufferBuilder bufferBuilder, Entity camera, float tickDelta, float dx, float dy, float dz, float forwards, float sideways) {
      int var9 = (int)(((float)this.age + tickDelta) * 15.0F / (float)this.maxAge);
      if (var9 <= 15) {
         this.textureManager.bind(EXPLOSION_TEXTURE);
         float var10 = (float)(var9 % 4) / 4.0F;
         float var11 = var10 + 0.24975F;
         float var12 = (float)(var9 / 4) / 4.0F;
         float var13 = var12 + 0.24975F;
         float var14 = 2.0F * this.scaleFactor;
         float var15 = (float)(this.prevX + (this.x - this.prevX) * (double)tickDelta - currentX);
         float var16 = (float)(this.prevY + (this.y - this.prevY) * (double)tickDelta - currentY);
         float var17 = (float)(this.prevZ + (this.z - this.prevZ) * (double)tickDelta - currentZ);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableLighting();
         Lighting.turnOff();
         bufferBuilder.start();
         bufferBuilder.color(this.red, this.green, this.blue, 1.0F);
         bufferBuilder.normal(0.0F, 1.0F, 0.0F);
         bufferBuilder.brightness(240);
         bufferBuilder.vertex(
            (double)(var15 - dx * var14 - forwards * var14),
            (double)(var16 - dy * var14),
            (double)(var17 - dz * var14 - sideways * var14),
            (double)var11,
            (double)var13
         );
         bufferBuilder.vertex(
            (double)(var15 - dx * var14 + forwards * var14),
            (double)(var16 + dy * var14),
            (double)(var17 - dz * var14 + sideways * var14),
            (double)var11,
            (double)var12
         );
         bufferBuilder.vertex(
            (double)(var15 + dx * var14 + forwards * var14),
            (double)(var16 + dy * var14),
            (double)(var17 + dz * var14 + sideways * var14),
            (double)var10,
            (double)var12
         );
         bufferBuilder.vertex(
            (double)(var15 + dx * var14 - forwards * var14),
            (double)(var16 - dy * var14),
            (double)(var17 + dz * var14 - sideways * var14),
            (double)var10,
            (double)var13
         );
         Tessellator.getInstance().end();
         GlStateManager.polygonOffset(0.0F, 0.0F);
         GlStateManager.enableLighting();
      }
   }

   @Override
   public int getLightLevel(float tickDelta) {
      return 61680;
   }

   @Override
   public void tick() {
      this.prevX = this.x;
      this.prevY = this.y;
      this.prevZ = this.z;
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
         return new LargeExplosionParticle(MinecraftClient.getInstance().getTextureManager(), world, x, y, z, velocityX, velocityY, velocityZ);
      }
   }
}
