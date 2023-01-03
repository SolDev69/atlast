package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ItemParticle extends Particle {
   protected ItemParticle(World world, double x, double y, double z, Item item) {
      this(world, x, y, z, item, 0);
   }

   protected ItemParticle(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Item item, int metadata) {
      this(world, x, y, z, item, metadata);
      this.velocityX *= 0.1F;
      this.velocityY *= 0.1F;
      this.velocityZ *= 0.1F;
      this.velocityX += velocityX;
      this.velocityY += velocityY;
      this.velocityZ += velocityZ;
   }

   protected ItemParticle(World world, double x, double y, double z, Item item, int metadata) {
      super(world, x, y, z, 0.0, 0.0, 0.0);
      this.setTexture(MinecraftClient.getInstance().getItemRenderer().getModelShaper().getParticleIcon(item, metadata));
      this.red = this.green = this.blue = 1.0F;
      this.gravity = Blocks.SNOW.gravity;
      this.scale /= 2.0F;
   }

   @Override
   public int getTextureType() {
      return 1;
   }

   @Override
   public void render(BufferBuilder bufferBuilder, Entity camera, float tickDelta, float dx, float dy, float dz, float forwards, float sideways) {
      float var9 = ((float)this.miscTexRow + this.randomUOffset / 4.0F) / 16.0F;
      float var10 = var9 + 0.015609375F;
      float var11 = ((float)this.miscTexColumn + this.randomVOffset / 4.0F) / 16.0F;
      float var12 = var11 + 0.015609375F;
      float var13 = 0.1F * this.scale;
      if (this.sprite != null) {
         var9 = this.sprite.getU((double)(this.randomUOffset / 4.0F * 16.0F));
         var10 = this.sprite.getU((double)((this.randomUOffset + 1.0F) / 4.0F * 16.0F));
         var11 = this.sprite.getV((double)(this.randomVOffset / 4.0F * 16.0F));
         var12 = this.sprite.getV((double)((this.randomVOffset + 1.0F) / 4.0F * 16.0F));
      }

      float var14 = (float)(this.prevX + (this.x - this.prevX) * (double)tickDelta - currentX);
      float var15 = (float)(this.prevY + (this.y - this.prevY) * (double)tickDelta - currentY);
      float var16 = (float)(this.prevZ + (this.z - this.prevZ) * (double)tickDelta - currentZ);
      bufferBuilder.color(this.red, this.green, this.blue);
      bufferBuilder.vertex(
         (double)(var14 - dx * var13 - forwards * var13),
         (double)(var15 - dy * var13),
         (double)(var16 - dz * var13 - sideways * var13),
         (double)var9,
         (double)var12
      );
      bufferBuilder.vertex(
         (double)(var14 - dx * var13 + forwards * var13),
         (double)(var15 + dy * var13),
         (double)(var16 - dz * var13 + sideways * var13),
         (double)var9,
         (double)var11
      );
      bufferBuilder.vertex(
         (double)(var14 + dx * var13 + forwards * var13),
         (double)(var15 + dy * var13),
         (double)(var16 + dz * var13 + sideways * var13),
         (double)var10,
         (double)var11
      );
      bufferBuilder.vertex(
         (double)(var14 + dx * var13 - forwards * var13),
         (double)(var15 - dy * var13),
         (double)(var16 + dz * var13 - sideways * var13),
         (double)var10,
         (double)var12
      );
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         int var16 = parameters.length > 1 ? parameters[1] : 0;
         return new ItemParticle(world, x, y, z, velocityX, velocityY, velocityZ, Item.byRawId(parameters[0]), var16);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class SlimeBallFactory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         return new ItemParticle(world, x, y, z, Items.SLIME_BALL);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class SnowballFactory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         return new ItemParticle(world, x, y, z, Items.SNOWBALL);
      }
   }
}
