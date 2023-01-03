package net.minecraft.client.render.entity.layer;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.SpiderRenderer;
import net.minecraft.entity.living.mob.hostile.SpiderEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SpiderEyesLayer implements EntityRenderLayer {
   private static final Identifier EYES_TEXTURE = new Identifier("textures/entity/spider_eyes.png");
   private final SpiderRenderer parent;

   public SpiderEyesLayer(SpiderRenderer parent) {
      this.parent = parent;
   }

   public void render(SpiderEntity c_83fmezsnj, float f, float g, float h, float i, float j, float k, float l) {
      this.parent.bindTexture(EYES_TEXTURE);
      GlStateManager.disableBlend();
      GlStateManager.disableAlphaTest();
      GlStateManager.blendFunc(1, 1);
      if (c_83fmezsnj.isInvisible()) {
         GlStateManager.depthMask(false);
      } else {
         GlStateManager.depthMask(true);
      }

      char var9 = '\uf0f0';
      int var10 = var9 % 65536;
      int var11 = var9 / 65536;
      GLX.multiTexCoord2f(GLX.GL_TEXTURE1, (float)var10 / 1.0F, (float)var11 / 1.0F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.parent.getModel().render(c_83fmezsnj, f, g, i, j, k, l);
      GlStateManager.enableBlend();
      GlStateManager.enableAlphaTest();
   }

   @Override
   public boolean colorsWhenDamaged() {
      return false;
   }
}
