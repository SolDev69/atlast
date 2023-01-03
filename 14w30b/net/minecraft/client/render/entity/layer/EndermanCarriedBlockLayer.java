package net.minecraft.client.render.entity.layer;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderDispatcher;
import net.minecraft.client.render.model.entity.EndermanRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.living.mob.hostile.EndermanEntity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EndermanCarriedBlockLayer implements EntityRenderLayer {
   private final EndermanRenderer parent;

   public EndermanCarriedBlockLayer(EndermanRenderer parent) {
      this.parent = parent;
   }

   public void render(EndermanEntity c_12ysvaaev, float f, float g, float h, float i, float j, float k, float l) {
      BlockState var9 = c_12ysvaaev.getCarriedBlock();
      Block var10 = var9.getBlock();
      if (var10.getMaterial() != Material.AIR) {
         BlockRenderDispatcher var11 = MinecraftClient.getInstance().getBlockRenderDispatcher();
         GlStateManager.enableRescaleNormal();
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, 0.6875F, -0.75F);
         GlStateManager.rotatef(20.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(45.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translatef(0.25F, 0.1875F, 0.25F);
         float var12 = 0.5F;
         GlStateManager.scalef(-var12, -var12, var12);
         int var13 = c_12ysvaaev.getLightLevel(h);
         int var14 = var13 % 65536;
         int var15 = var13 / 65536;
         GLX.multiTexCoord2f(GLX.GL_TEXTURE1, (float)var14 / 1.0F, (float)var15 / 1.0F);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.parent.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
         var11.renderDynamic(var10, var10.getMetadataFromState(var9), 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.disableRescaleNormal();
      }
   }

   @Override
   public boolean colorsWhenDamaged() {
      return false;
   }
}
