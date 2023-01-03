package net.minecraft.client.render.entity.layer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class Deadmou5Layer implements EntityRenderLayer {
   private final PlayerRenderer parent;

   public Deadmou5Layer(PlayerRenderer parent) {
      this.parent = parent;
   }

   public void render(ClientPlayerEntity c_95zrfkavi, float f, float g, float h, float i, float j, float k, float l) {
      if (c_95zrfkavi.getName().equals("deadmau5") && c_95zrfkavi.hasTextures() && !c_95zrfkavi.isInvisible()) {
         this.parent.bindTexture(c_95zrfkavi.getSkinTexture());

         for(int var9 = 0; var9 < 2; ++var9) {
            float var10 = c_95zrfkavi.prevYaw
               + (c_95zrfkavi.yaw - c_95zrfkavi.prevYaw) * h
               - (c_95zrfkavi.prevBodyYaw + (c_95zrfkavi.bodyYaw - c_95zrfkavi.prevBodyYaw) * h);
            float var11 = c_95zrfkavi.prevPitch + (c_95zrfkavi.pitch - c_95zrfkavi.prevPitch) * h;
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(var10, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(var11, 1.0F, 0.0F, 0.0F);
            GlStateManager.translatef(0.375F * (float)(var9 * 2 - 1), 0.0F, 0.0F);
            GlStateManager.translatef(0.0F, -0.375F, 0.0F);
            GlStateManager.rotatef(-var11, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(-var10, 0.0F, 1.0F, 0.0F);
            float var12 = 1.3333334F;
            GlStateManager.scalef(var12, var12, var12);
            this.parent.getModel().renderEars(0.0625F);
            GlStateManager.popMatrix();
         }
      }
   }

   @Override
   public boolean colorsWhenDamaged() {
      return true;
   }
}
