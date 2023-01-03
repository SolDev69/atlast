package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBaseBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.entity.MovingBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderDispatcher;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class MovingBlockRenderer extends BlockEntityRenderer {
   private final BlockRenderDispatcher blockRenderer = MinecraftClient.getInstance().getBlockRenderDispatcher();

   public void render(MovingBlockEntity c_50szfpixs, double d, double e, double f, float g, int i) {
      BlockPos var10 = c_50szfpixs.getPos();
      BlockState var11 = c_50szfpixs.getMovedState();
      Block var12 = var11.getBlock();
      if (var12.getMaterial() != Material.AIR && !(c_50szfpixs.getProgress(g) >= 1.0F)) {
         Tessellator var13 = Tessellator.getInstance();
         BufferBuilder var14 = var13.getBufferBuilder();
         this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
         Lighting.turnOff();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.disableBlend();
         GlStateManager.disableCull();
         if (MinecraftClient.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(7425);
         } else {
            GlStateManager.shadeModel(7424);
         }

         var14.start();
         var14.format(DefaultVertexFormat.BLOCK);
         var14.offset(
            (double)((float)d - (float)var10.getX() + c_50szfpixs.getRenderOffsetX(g)),
            (double)((float)e - (float)var10.getY() + c_50szfpixs.getRenderOffsetY(g)),
            (double)((float)f - (float)var10.getZ() + c_50szfpixs.getRenderOffsetZ(g))
         );
         var14.color(1.0F, 1.0F, 1.0F);
         World var15 = this.getWorld();
         if (var12 == Blocks.PISTON_HEAD && c_50szfpixs.getProgress(g) < 0.5F) {
            var11 = var11.set(PistonHeadBlock.SHORT, true);
            this.blockRenderer.getModelRenderer().render(var15, this.blockRenderer.getModel(var11, var15, var10), var11, var10, var14, true);
         } else if (c_50szfpixs.isSource() && !c_50szfpixs.isExtending()) {
            PistonHeadBlock.Type var16 = var12 == Blocks.STICKY_PISTON ? PistonHeadBlock.Type.STICKY : PistonHeadBlock.Type.DEFAULT;
            BlockState var17 = Blocks.PISTON_HEAD
               .defaultState()
               .set(PistonHeadBlock.TYPE, var16)
               .set(PistonHeadBlock.FACING, var11.get(PistonBaseBlock.FACING));
            var17 = var17.set(PistonHeadBlock.SHORT, c_50szfpixs.getProgress(g) >= 0.5F);
            this.blockRenderer.getModelRenderer().render(var15, this.blockRenderer.getModel(var17, var15, var10), var17, var10, var14, true);
            var14.offset((double)((float)d - (float)var10.getX()), (double)((float)e - (float)var10.getY()), (double)((float)f - (float)var10.getZ()));
            var11.set(PistonBaseBlock.EXTENDED, true);
            this.blockRenderer.getModelRenderer().render(var15, this.blockRenderer.getModel(var11, var15, var10), var11, var10, var14, true);
         } else {
            this.blockRenderer.getModelRenderer().render(var15, this.blockRenderer.getModel(var11, var15, var10), var11, var10, var14, false);
         }

         var14.offset(0.0, 0.0, 0.0);
         var13.end();
         Lighting.turnOn();
      }
   }
}
