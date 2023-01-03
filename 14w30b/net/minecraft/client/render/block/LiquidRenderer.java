package net.minecraft.client.render.block;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.block.LiquidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class LiquidRenderer {
   private TextureAtlasSprite[] lavaSprites = new TextureAtlasSprite[2];
   private TextureAtlasSprite[] waterSprites = new TextureAtlasSprite[2];

   public LiquidRenderer() {
      this.reload();
   }

   protected void reload() {
      SpriteAtlasTexture var1 = MinecraftClient.getInstance().getSpriteAtlasTexture();
      this.lavaSprites[0] = var1.getSprite("minecraft:blocks/lava_still");
      this.lavaSprites[1] = var1.getSprite("minecraft:blocks/lava_flow");
      this.waterSprites[0] = var1.getSprite("minecraft:blocks/water_still");
      this.waterSprites[1] = var1.getSprite("minecraft:blocks/water_flow");
   }

   public boolean render(IWorld world, BlockState state, BlockPos pos, BufferBuilder bufferBuilder) {
      LiquidBlock var5 = (LiquidBlock)state.getBlock();
      var5.updateShape(world, pos);
      TextureAtlasSprite[] var6 = var5.getMaterial() == Material.LAVA ? this.lavaSprites : this.waterSprites;
      int var7 = var5.getColor(world, pos);
      float var8 = (float)(var7 >> 16 & 0xFF) / 255.0F;
      float var9 = (float)(var7 >> 8 & 0xFF) / 255.0F;
      float var10 = (float)(var7 & 0xFF) / 255.0F;
      boolean var11 = var5.shouldRenderFace(world, pos.up(), Direction.UP);
      boolean var12 = var5.shouldRenderFace(world, pos.down(), Direction.DOWN);
      boolean[] var13 = new boolean[]{
         var5.shouldRenderFace(world, pos.north(), Direction.NORTH),
         var5.shouldRenderFace(world, pos.south(), Direction.SOUTH),
         var5.shouldRenderFace(world, pos.west(), Direction.WEST),
         var5.shouldRenderFace(world, pos.east(), Direction.EAST)
      };
      if (!var11 && !var12 && !var13[0] && !var13[1] && !var13[2] && !var13[3]) {
         return false;
      } else {
         boolean var14 = false;
         float var15 = 0.5F;
         float var16 = 1.0F;
         float var17 = 0.8F;
         float var18 = 0.6F;
         Material var19 = var5.getMaterial();
         float var20 = this.getLiquidHeight(world, pos, var19);
         float var21 = this.getLiquidHeight(world, pos.south(), var19);
         float var22 = this.getLiquidHeight(world, pos.east().south(), var19);
         float var23 = this.getLiquidHeight(world, pos.east(), var19);
         double var24 = (double)pos.getX();
         double var26 = (double)pos.getY();
         double var28 = (double)pos.getZ();
         float var30 = 0.001F;
         if (var11) {
            var14 = true;
            TextureAtlasSprite var31 = var6[0];
            float var32 = (float)LiquidBlock.getFlowAngle(world, pos, var19);
            if (var32 > -999.0F) {
               var31 = var6[1];
            }

            var20 -= var30;
            var21 -= var30;
            var22 -= var30;
            var23 -= var30;
            float var33;
            float var34;
            float var35;
            float var36;
            float var37;
            float var38;
            float var39;
            float var40;
            if (var32 < -999.0F) {
               var33 = var31.getU(0.0);
               var37 = var31.getV(0.0);
               var34 = var33;
               var38 = var31.getV(16.0);
               var35 = var31.getU(16.0);
               var39 = var38;
               var36 = var35;
               var40 = var37;
            } else {
               float var41 = MathHelper.sin(var32) * 0.25F;
               float var42 = MathHelper.cos(var32) * 0.25F;
               float var43 = 8.0F;
               var33 = var31.getU((double)(8.0F + (-var42 - var41) * 16.0F));
               var37 = var31.getV((double)(8.0F + (-var42 + var41) * 16.0F));
               var34 = var31.getU((double)(8.0F + (-var42 + var41) * 16.0F));
               var38 = var31.getV((double)(8.0F + (var42 + var41) * 16.0F));
               var35 = var31.getU((double)(8.0F + (var42 + var41) * 16.0F));
               var39 = var31.getV((double)(8.0F + (var42 - var41) * 16.0F));
               var36 = var31.getU((double)(8.0F + (var42 - var41) * 16.0F));
               var40 = var31.getV((double)(8.0F + (-var42 - var41) * 16.0F));
            }

            bufferBuilder.brightness(var5.getLightColor(world, pos));
            bufferBuilder.color(var16 * var8, var16 * var9, var16 * var10);
            bufferBuilder.vertex(var24 + 0.0, var26 + (double)var20, var28 + 0.0, (double)var33, (double)var37);
            bufferBuilder.vertex(var24 + 0.0, var26 + (double)var21, var28 + 1.0, (double)var34, (double)var38);
            bufferBuilder.vertex(var24 + 1.0, var26 + (double)var22, var28 + 1.0, (double)var35, (double)var39);
            bufferBuilder.vertex(var24 + 1.0, var26 + (double)var23, var28 + 0.0, (double)var36, (double)var40);
            if (var5.isNeighboringGap(world, pos.up())) {
               bufferBuilder.vertex(var24 + 0.0, var26 + (double)var20, var28 + 0.0, (double)var33, (double)var37);
               bufferBuilder.vertex(var24 + 1.0, var26 + (double)var23, var28 + 0.0, (double)var36, (double)var40);
               bufferBuilder.vertex(var24 + 1.0, var26 + (double)var22, var28 + 1.0, (double)var35, (double)var39);
               bufferBuilder.vertex(var24 + 0.0, var26 + (double)var21, var28 + 1.0, (double)var34, (double)var38);
            }
         }

         if (var12) {
            bufferBuilder.brightness(var5.getLightColor(world, pos.down()));
            bufferBuilder.color(var15, var15, var15);
            float var53 = var6[0].getUMin();
            float var55 = var6[0].getUMax();
            float var57 = var6[0].getVMin();
            float var59 = var6[0].getVMax();
            bufferBuilder.vertex(var24, var26, var28 + 1.0, (double)var53, (double)var59);
            bufferBuilder.vertex(var24, var26, var28, (double)var53, (double)var57);
            bufferBuilder.vertex(var24 + 1.0, var26, var28, (double)var55, (double)var57);
            bufferBuilder.vertex(var24 + 1.0, var26, var28 + 1.0, (double)var55, (double)var59);
            var14 = true;
         }

         for(int var54 = 0; var54 < 4; ++var54) {
            int var56 = 0;
            int var58 = 0;
            if (var54 == 0) {
               --var58;
            }

            if (var54 == 1) {
               ++var58;
            }

            if (var54 == 2) {
               --var56;
            }

            if (var54 == 3) {
               ++var56;
            }

            BlockPos var60 = pos.add(var56, 0, var58);
            TextureAtlasSprite var52 = var6[1];
            if (var13[var54]) {
               double var44;
               float var61;
               float var62;
               double var63;
               double var64;
               double var65;
               if (var54 == 0) {
                  var61 = var20;
                  var62 = var23;
                  var63 = var24;
                  var65 = var24 + 1.0;
                  var64 = var28 + (double)var30;
                  var44 = var28 + (double)var30;
               } else if (var54 == 1) {
                  var61 = var22;
                  var62 = var21;
                  var63 = var24 + 1.0;
                  var65 = var24;
                  var64 = var28 + 1.0 - (double)var30;
                  var44 = var28 + 1.0 - (double)var30;
               } else if (var54 == 2) {
                  var61 = var21;
                  var62 = var20;
                  var63 = var24 + (double)var30;
                  var65 = var24 + (double)var30;
                  var64 = var28 + 1.0;
                  var44 = var28;
               } else {
                  var61 = var23;
                  var62 = var22;
                  var63 = var24 + 1.0 - (double)var30;
                  var65 = var24 + 1.0 - (double)var30;
                  var64 = var28;
                  var44 = var28 + 1.0;
               }

               var14 = true;
               float var46 = var52.getU(0.0);
               float var47 = var52.getU(8.0);
               float var48 = var52.getV((double)((1.0F - var61) * 16.0F * 0.5F));
               float var49 = var52.getV((double)((1.0F - var62) * 16.0F * 0.5F));
               float var50 = var52.getV(8.0);
               bufferBuilder.brightness(var5.getLightColor(world, var60));
               float var51 = 1.0F;
               var51 *= var54 < 2 ? var17 : var18;
               bufferBuilder.color(var16 * var51 * var8, var16 * var51 * var9, var16 * var51 * var10);
               bufferBuilder.vertex(var63, var26 + (double)var61, var64, (double)var46, (double)var48);
               bufferBuilder.vertex(var65, var26 + (double)var62, var44, (double)var47, (double)var49);
               bufferBuilder.vertex(var65, var26 + 0.0, var44, (double)var47, (double)var50);
               bufferBuilder.vertex(var63, var26 + 0.0, var64, (double)var46, (double)var50);
               bufferBuilder.vertex(var63, var26 + 0.0, var64, (double)var46, (double)var50);
               bufferBuilder.vertex(var65, var26 + 0.0, var44, (double)var47, (double)var50);
               bufferBuilder.vertex(var65, var26 + (double)var62, var44, (double)var47, (double)var49);
               bufferBuilder.vertex(var63, var26 + (double)var61, var64, (double)var46, (double)var48);
            }
         }

         return var14;
      }
   }

   private float getLiquidHeight(IWorld world, BlockPos pos, Material material) {
      int var4 = 0;
      float var5 = 0.0F;

      for(int var6 = 0; var6 < 4; ++var6) {
         BlockPos var7 = pos.add(-(var6 & 1), 0, -(var6 >> 1 & 1));
         if (world.getBlockState(var7.up()).getBlock().getMaterial() == material) {
            return 1.0F;
         }

         BlockState var8 = world.getBlockState(var7);
         Material var9 = var8.getBlock().getMaterial();
         if (var9 == material) {
            int var10 = var8.get(LiquidBlock.LEVEL);
            if (var10 >= 8 || var10 == 0) {
               var5 += LiquidBlock.getHeightLoss(var10) * 10.0F;
               var4 += 10;
            }

            var5 += LiquidBlock.getHeightLoss(var10);
            ++var4;
         } else if (!var9.isSolid()) {
            ++var5;
            ++var4;
         }
      }

      return 1.0F - var5 / (float)var4;
   }
}
