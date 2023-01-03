package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.entity.BatModel;
import net.minecraft.entity.living.mob.ambient.BatEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BatRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/bat.png");

   public BatRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh, new BatModel(), 0.25F);
   }

   protected Identifier getTexture(BatEntity c_99guwrhnc) {
      return TEXTURE;
   }

   protected void scale(BatEntity c_99guwrhnc, float f) {
      GlStateManager.scalef(0.35F, 0.35F, 0.35F);
   }

   protected void applyRotation(BatEntity c_99guwrhnc, float f, float g, float h) {
      if (!c_99guwrhnc.isRoosting()) {
         GlStateManager.translatef(0.0F, MathHelper.cos(f * 0.3F) * 0.1F, 0.0F);
      } else {
         GlStateManager.translatef(0.0F, -0.1F, 0.0F);
      }

      super.applyRotation(c_99guwrhnc, f, g, h);
   }
}
