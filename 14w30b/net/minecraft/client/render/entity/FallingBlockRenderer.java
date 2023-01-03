package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderDispatcher;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class FallingBlockRenderer extends EntityRenderer {
   public FallingBlockRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh);
      this.shadowSize = 0.5F;
   }

   public void render(FallingBlockEntity c_51wlzxfql, double d, double e, double f, float g, float h) {
      if (c_51wlzxfql.getBlock() != null) {
         this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
         BlockState var10 = c_51wlzxfql.getBlock();
         Block var11 = var10.getBlock();
         BlockPos var12 = new BlockPos(c_51wlzxfql);
         World var13 = c_51wlzxfql.getWorld();
         if (var10 != var13.getBlockState(var12) && var11.getRenderType() != -1) {
            if (var11.getRenderType() == 3) {
               GlStateManager.pushMatrix();
               GlStateManager.translatef((float)d, (float)e, (float)f);
               GlStateManager.disableLighting();
               Tessellator var14 = Tessellator.getInstance();
               BufferBuilder var15 = var14.getBufferBuilder();
               var15.start();
               var15.format(DefaultVertexFormat.BLOCK);
               int var16 = var12.getX();
               int var17 = var12.getY();
               int var18 = var12.getZ();
               var15.offset((double)((float)(-var16) - 0.5F), (double)(-var17), (double)((float)(-var18) - 0.5F));
               BlockRenderDispatcher var19 = MinecraftClient.getInstance().getBlockRenderDispatcher();
               BakedModel var20 = var19.getModel(var10, var13, null);
               var19.getModelRenderer().render(var13, var20, var10, var12, var15, false);
               var15.offset(0.0, 0.0, 0.0);
               var14.end();
               GlStateManager.enableLighting();
               GlStateManager.popMatrix();
               super.render(c_51wlzxfql, d, e, f, g, h);
            }
         }
      }
   }

   protected Identifier getTexture(FallingBlockEntity c_51wlzxfql) {
      return SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS;
   }
}
