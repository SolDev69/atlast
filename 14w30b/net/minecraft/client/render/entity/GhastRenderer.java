package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.entity.GhastModel;
import net.minecraft.entity.living.mob.GhastEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class GhastRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/ghast/ghast.png");
   private static final Identifier SHOOTING_TEXTURE = new Identifier("textures/entity/ghast/ghast_shooting.png");

   public GhastRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh, new GhastModel(), 0.5F);
   }

   protected Identifier getTexture(GhastEntity c_65lktxchf) {
      return c_65lktxchf.isShooting() ? SHOOTING_TEXTURE : TEXTURE;
   }

   protected void scale(GhastEntity c_65lktxchf, float f) {
      float var3 = 1.0F;
      float var4 = (8.0F + var3) / 2.0F;
      float var5 = (8.0F + 1.0F / var3) / 2.0F;
      GlStateManager.scalef(var5, var4, var5);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
