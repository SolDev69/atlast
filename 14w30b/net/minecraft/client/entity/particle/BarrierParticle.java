package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BarrierParticle extends Particle {
   protected BarrierParticle(World world, double x, double y, double z, Item item) {
      super(world, x, y, z, 0.0, 0.0, 0.0);
      this.setTexture(MinecraftClient.getInstance().getItemRenderer().getModelShaper().getParticleIcon(item));
      this.red = this.green = this.blue = 1.0F;
      this.velocityX = this.velocityY = this.velocityZ = 0.0;
      this.gravity = 0.0F;
      this.maxAge = 80;
   }

   @Override
   public int getTextureType() {
      return 1;
   }

   @Override
   public void render(BufferBuilder bufferBuilder, Entity camera, float tickDelta, float dx, float dy, float dz, float forwards, float sideways) {
      float var9 = this.sprite.getUMin();
      float var10 = this.sprite.getUMax();
      float var11 = this.sprite.getVMin();
      float var12 = this.sprite.getVMax();
      float var13 = (float)(this.prevX + (this.x - this.prevX) * (double)tickDelta - currentX);
      float var14 = (float)(this.prevY + (this.y - this.prevY) * (double)tickDelta - currentY);
      float var15 = (float)(this.prevZ + (this.z - this.prevZ) * (double)tickDelta - currentZ);
      bufferBuilder.color(this.red, this.green, this.blue);
      float var16 = 0.5F;
      bufferBuilder.vertex(
         (double)(var13 - dx * var16 - forwards * var16),
         (double)(var14 - dy * var16),
         (double)(var15 - dz * var16 - sideways * var16),
         (double)var10,
         (double)var12
      );
      bufferBuilder.vertex(
         (double)(var13 - dx * var16 + forwards * var16),
         (double)(var14 + dy * var16),
         (double)(var15 - dz * var16 + sideways * var16),
         (double)var10,
         (double)var11
      );
      bufferBuilder.vertex(
         (double)(var13 + dx * var16 + forwards * var16),
         (double)(var14 + dy * var16),
         (double)(var15 + dz * var16 + sideways * var16),
         (double)var9,
         (double)var11
      );
      bufferBuilder.vertex(
         (double)(var13 + dx * var16 - forwards * var16),
         (double)(var14 - dy * var16),
         (double)(var15 + dz * var16 - sideways * var16),
         (double)var9,
         (double)var12
      );
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         return new BarrierParticle(world, x, y, z, Item.byBlock(Blocks.BARRIER));
      }
   }
}
