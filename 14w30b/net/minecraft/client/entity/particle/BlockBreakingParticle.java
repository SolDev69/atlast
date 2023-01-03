package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockBreakingParticle extends Particle {
   private BlockState state;

   protected BlockBreakingParticle(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, BlockState state) {
      super(world, x, y, z, velocityX, velocityY, velocityZ);
      this.state = state;
      this.setTexture(MinecraftClient.getInstance().getBlockRenderDispatcher().getModelShaper().getParticleIcon(state));
      this.gravity = state.getBlock().gravity;
      this.red = this.green = this.blue = 0.6F;
      this.scale /= 2.0F;
   }

   public BlockBreakingParticle updateColor(BlockPos pos) {
      if (this.state.getBlock() == Blocks.GRASS) {
         return this;
      } else {
         int var2 = this.state.getBlock().getColor(this.world, pos);
         this.red *= (float)(var2 >> 16 & 0xFF) / 255.0F;
         this.green *= (float)(var2 >> 8 & 0xFF) / 255.0F;
         this.blue *= (float)(var2 & 0xFF) / 255.0F;
         return this;
      }
   }

   public BlockBreakingParticle updateColor() {
      Block var1 = this.state.getBlock();
      if (var1 == Blocks.GRASS) {
         return this;
      } else {
         int var2 = var1.getColor(var1.getMetadataFromState(this.state));
         this.red *= (float)(var2 >> 16 & 0xFF) / 255.0F;
         this.green *= (float)(var2 >> 8 & 0xFF) / 255.0F;
         this.blue *= (float)(var2 & 0xFF) / 255.0F;
         return this;
      }
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
         return new BlockBreakingParticle(world, x, y, z, velocityX, velocityY, velocityZ, Block.deserialize(parameters[0])).updateColor();
      }
   }
}
