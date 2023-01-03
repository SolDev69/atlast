package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.client.render.model.block.entity.ChestModel;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EnderChestRenderer extends BlockEntityRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/chest/ender.png");
   private ChestModel model = new ChestModel();

   public void render(EnderChestBlockEntity c_41zcazxmm, double d, double e, double f, float g, int i) {
      int var10 = 0;
      if (c_41zcazxmm.hasWorld()) {
         var10 = c_41zcazxmm.getCachedMetadata();
      }

      if (i >= 0) {
         this.bindTexture(MINING_PROGRESS_TEXTURES[i]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(4.0F, 4.0F, 1.0F);
         GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         this.bindTexture(TEXTURE);
      }

      GlStateManager.pushMatrix();
      GlStateManager.enableRescaleNormal();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.translatef((float)d, (float)e + 1.0F, (float)f + 1.0F);
      GlStateManager.scalef(1.0F, -1.0F, -1.0F);
      GlStateManager.translatef(0.5F, 0.5F, 0.5F);
      short var11 = 0;
      if (var10 == 2) {
         var11 = 180;
      }

      if (var10 == 3) {
         var11 = 0;
      }

      if (var10 == 4) {
         var11 = 90;
      }

      if (var10 == 5) {
         var11 = -90;
      }

      GlStateManager.rotatef((float)var11, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
      float var12 = c_41zcazxmm.lastAnimationProgress + (c_41zcazxmm.animationProgress - c_41zcazxmm.lastAnimationProgress) * g;
      var12 = 1.0F - var12;
      var12 = 1.0F - var12 * var12 * var12;
      this.model.lid.rotationX = -(var12 * (float) Math.PI / 2.0F);
      this.model.renderParts();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (i >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }
   }
}
