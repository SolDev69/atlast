package net.minecraft.client.render.entity.layer;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.entity.EndermanRenderer;
import net.minecraft.entity.living.mob.hostile.EndermanEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EndermanEyesLayer implements EntityRenderLayer {
   private static final Identifier EYES_TEXTURE = new Identifier("textures/entity/enderman/enderman_eyes.png");
   private final EndermanRenderer parent;

   public EndermanEyesLayer(EndermanRenderer parent) {
      this.parent = parent;
   }

   public void render(EndermanEntity c_12ysvaaev, float f, float g, float h, float i, float j, float k, float l) {
      this.parent.bindTexture(EYES_TEXTURE);
      GlStateManager.disableBlend();
      GlStateManager.disableAlphaTest();
      GlStateManager.blendFunc(1, 1);
      GlStateManager.disableLighting();
      if (c_12ysvaaev.isInvisible()) {
         GlStateManager.depthMask(false);
      } else {
         GlStateManager.depthMask(true);
      }

      char var9 = '\uf0f0';
      int var10 = var9 % 65536;
      int var11 = var9 / 65536;
      GLX.multiTexCoord2f(GLX.GL_TEXTURE1, (float)var10 / 1.0F, (float)var11 / 1.0F);
      GlStateManager.enableLighting();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.parent.getModel().render(c_12ysvaaev, f, g, i, j, k, l);
      GlStateManager.enableBlend();
      GlStateManager.enableAlphaTest();
   }

   @Override
   public boolean colorsWhenDamaged() {
      return false;
   }
}
