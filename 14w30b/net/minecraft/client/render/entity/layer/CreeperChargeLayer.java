package net.minecraft.client.render.entity.layer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.CreeperRenderer;
import net.minecraft.client.render.model.entity.CreeperModel;
import net.minecraft.entity.living.mob.hostile.CreeperEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class CreeperChargeLayer implements EntityRenderLayer {
   private static final Identifier CHARGE_TEXTURE = new Identifier("textures/entity/creeper/creeper_armor.png");
   private final CreeperRenderer parent;
   private final CreeperModel model = new CreeperModel(2.0F);

   public CreeperChargeLayer(CreeperRenderer parent) {
      this.parent = parent;
   }

   public void render(CreeperEntity c_24qhtkqyz, float f, float g, float h, float i, float j, float k, float l) {
      if (c_24qhtkqyz.isCharged()) {
         GlStateManager.depthMask(!c_24qhtkqyz.isInvisible());
         this.parent.bindTexture(CHARGE_TEXTURE);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         float var9 = (float)c_24qhtkqyz.time + h;
         GlStateManager.translatef(var9 * 0.01F, var9 * 0.01F, 0.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.disableBlend();
         float var10 = 0.5F;
         GlStateManager.color4f(var10, var10, var10, 1.0F);
         GlStateManager.disableLighting();
         GlStateManager.blendFunc(1, 1);
         this.model.copyPropertiesFrom(this.parent.getModel());
         this.model.render(c_24qhtkqyz, f, g, i, j, k, l);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         GlStateManager.enableBlend();
      }
   }

   @Override
   public boolean colorsWhenDamaged() {
      return false;
   }
}
