package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.Model;
import net.minecraft.entity.living.mob.water.SquidEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SquidRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/squid.png");

   public SquidRenderer(EntityRenderDispatcher c_28wsgstbh, Model c_56prnndub, float f) {
      super(c_28wsgstbh, c_56prnndub, f);
   }

   protected Identifier getTexture(SquidEntity c_36ywfeeve) {
      return TEXTURE;
   }

   protected void applyRotation(SquidEntity c_36ywfeeve, float f, float g, float h) {
      float var5 = c_36ywfeeve.lastSquidPitch + (c_36ywfeeve.squidPitch - c_36ywfeeve.lastSquidPitch) * h;
      float var6 = c_36ywfeeve.lastSquisYaw + (c_36ywfeeve.squidYaw - c_36ywfeeve.lastSquisYaw) * h;
      GlStateManager.translatef(0.0F, 0.5F, 0.0F);
      GlStateManager.rotatef(180.0F - g, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(var5, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(var6, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(0.0F, -1.2F, 0.0F);
   }

   protected float getEntityAge(SquidEntity c_36ywfeeve, float f) {
      return c_36ywfeeve.lastTentacleRotation + (c_36ywfeeve.tentacleRotation - c_36ywfeeve.lastTentacleRotation) * f;
   }
}
