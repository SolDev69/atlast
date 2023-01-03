package net.minecraft.client.render.entity.layer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.model.PlayerModelPart;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class CapeLayer implements EntityRenderLayer {
   private final PlayerRenderer parent;

   public CapeLayer(PlayerRenderer parent) {
      this.parent = parent;
   }

   public void render(ClientPlayerEntity c_95zrfkavi, float f, float g, float h, float i, float j, float k, float l) {
      if (c_95zrfkavi.hasInfo() && !c_95zrfkavi.isInvisible() && c_95zrfkavi.hidesCape(PlayerModelPart.CAPE) && c_95zrfkavi.getCapeTexture() != null) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.parent.bindTexture(c_95zrfkavi.getCapeTexture());
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, 0.0F, 0.125F);
         double var9 = c_95zrfkavi.lastCapeX
            + (c_95zrfkavi.capeX - c_95zrfkavi.lastCapeX) * (double)h
            - (c_95zrfkavi.prevX + (c_95zrfkavi.x - c_95zrfkavi.prevX) * (double)h);
         double var11 = c_95zrfkavi.lastCapeY
            + (c_95zrfkavi.capeY - c_95zrfkavi.lastCapeY) * (double)h
            - (c_95zrfkavi.prevY + (c_95zrfkavi.y - c_95zrfkavi.prevY) * (double)h);
         double var13 = c_95zrfkavi.lastCapeZ
            + (c_95zrfkavi.capeZ - c_95zrfkavi.lastCapeZ) * (double)h
            - (c_95zrfkavi.prevZ + (c_95zrfkavi.z - c_95zrfkavi.prevZ) * (double)h);
         float var15 = c_95zrfkavi.prevBodyYaw + (c_95zrfkavi.bodyYaw - c_95zrfkavi.prevBodyYaw) * h;
         double var16 = (double)MathHelper.sin(var15 * (float) Math.PI / 180.0F);
         double var18 = (double)(-MathHelper.cos(var15 * (float) Math.PI / 180.0F));
         float var20 = (float)var11 * 10.0F;
         var20 = MathHelper.clamp(var20, -6.0F, 32.0F);
         float var21 = (float)(var9 * var16 + var13 * var18) * 100.0F;
         float var22 = (float)(var9 * var18 - var13 * var16) * 100.0F;
         if (var21 < 0.0F) {
            var21 = 0.0F;
         }

         float var23 = c_95zrfkavi.prevStrideDistance + (c_95zrfkavi.strideDistance - c_95zrfkavi.prevStrideDistance) * h;
         var20 += MathHelper.sin((c_95zrfkavi.prevHorizontalSpeed + (c_95zrfkavi.horizontalVelocity - c_95zrfkavi.prevHorizontalSpeed) * h) * 6.0F)
            * 32.0F
            * var23;
         if (c_95zrfkavi.isSneaking()) {
            var20 += 25.0F;
         }

         GlStateManager.rotatef(6.0F + var21 / 2.0F + var20, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(var22 / 2.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(-var22 / 2.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
         this.parent.getModel().renderCape(0.0625F);
         GlStateManager.popMatrix();
      }
   }

   @Override
   public boolean colorsWhenDamaged() {
      return false;
   }
}
