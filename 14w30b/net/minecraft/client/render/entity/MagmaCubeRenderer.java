package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.entity.MagmaCubeModel;
import net.minecraft.entity.living.mob.MagmaCubeEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class MagmaCubeRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/slime/magmacube.png");

   public MagmaCubeRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh, new MagmaCubeModel(), 0.25F);
   }

   protected Identifier getTexture(MagmaCubeEntity c_28zvlsulp) {
      return TEXTURE;
   }

   protected void scale(MagmaCubeEntity c_28zvlsulp, float f) {
      int var3 = c_28zvlsulp.getSize();
      float var4 = (c_28zvlsulp.lastStretch + (c_28zvlsulp.stretch - c_28zvlsulp.lastStretch) * f) / ((float)var3 * 0.5F + 1.0F);
      float var5 = 1.0F / (var4 + 1.0F);
      float var6 = (float)var3;
      GlStateManager.scalef(var5 * var6, 1.0F / var5 * var6, var5 * var6);
   }
}
