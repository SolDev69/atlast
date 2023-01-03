package net.minecraft.client.render.entity.layer;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.entity.EnderDragonRenderer;
import net.minecraft.entity.living.mob.hostile.boss.EnderDragonEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EnderDragonEyesLayer implements EntityRenderLayer {
   private static final Identifier EYES_TEXTURE = new Identifier("textures/entity/enderdragon/dragon_eyes.png");
   private final EnderDragonRenderer parent;

   public EnderDragonEyesLayer(EnderDragonRenderer parent) {
      this.parent = parent;
   }

   public void render(EnderDragonEntity c_36agmpvyu, float f, float g, float h, float i, float j, float k, float l) {
      this.parent.bindTexture(EYES_TEXTURE);
      GlStateManager.disableBlend();
      GlStateManager.disableAlphaTest();
      GlStateManager.blendFunc(1, 1);
      GlStateManager.disableLighting();
      GlStateManager.depthFunc(514);
      char var9 = '\uf0f0';
      int var10 = var9 % 65536;
      int var11 = var9 / 65536;
      GLX.multiTexCoord2f(GLX.GL_TEXTURE1, (float)var10 / 1.0F, (float)var11 / 1.0F);
      GlStateManager.enableLighting();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.parent.getModel().render(c_36agmpvyu, f, g, i, j, k, l);
      GlStateManager.enableBlend();
      GlStateManager.enableAlphaTest();
      GlStateManager.depthFunc(515);
   }

   @Override
   public boolean colorsWhenDamaged() {
      return false;
   }
}
