package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderDispatcher;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class TntRenderer extends EntityRenderer {
   public TntRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh);
      this.shadowSize = 0.5F;
   }

   public void render(PrimedTntEntity c_89vvlrlyn, double d, double e, double f, float g, float h) {
      BlockRenderDispatcher var10 = MinecraftClient.getInstance().getBlockRenderDispatcher();
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)d, (float)e + 0.5F, (float)f);
      if ((float)c_89vvlrlyn.fuseTimer - h + 1.0F < 10.0F) {
         float var11 = 1.0F - ((float)c_89vvlrlyn.fuseTimer - h + 1.0F) / 10.0F;
         var11 = MathHelper.clamp(var11, 0.0F, 1.0F);
         var11 *= var11;
         var11 *= var11;
         float var12 = 1.0F + var11 * 0.3F;
         GlStateManager.scalef(var12, var12, var12);
      }

      float var16 = (1.0F - ((float)c_89vvlrlyn.fuseTimer - h + 1.0F) / 100.0F) * 0.8F;
      this.bindTexture(c_89vvlrlyn);
      GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
      var10.renderDynamic(Blocks.TNT, 0, c_89vvlrlyn.getBrightness(h));
      GlStateManager.translatef(0.0F, 0.0F, 1.0F);
      if (c_89vvlrlyn.fuseTimer / 5 % 2 == 0) {
         GlStateManager.disableTexture();
         GlStateManager.disableLighting();
         GlStateManager.disableBlend();
         GlStateManager.blendFunc(770, 772);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, var16);
         GlStateManager.polygonOffset(-3.0F, -3.0F);
         GlStateManager.enablePolygonOffset();
         var10.renderDynamic(Blocks.TNT, 0, 1.0F);
         GlStateManager.polygonOffset(0.0F, 0.0F);
         GlStateManager.disablePolygonOffset();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableBlend();
         GlStateManager.enableLighting();
         GlStateManager.enableTexture();
      }

      GlStateManager.popMatrix();
      super.render(c_89vvlrlyn, d, e, f, g, h);
   }

   protected Identifier getTexture(PrimedTntEntity c_89vvlrlyn) {
      return SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS;
   }
}
