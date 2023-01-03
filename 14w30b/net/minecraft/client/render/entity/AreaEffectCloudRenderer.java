package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class AreaEffectCloudRenderer extends EntityRenderer {
   public AreaEffectCloudRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh);
   }

   @Override
   public void render(Entity entity, double dx, double dy, double dz, float yaw, float tickDelta) {
      GlStateManager.pushMatrix();
      renderAreaEffectCloud(entity.getBoundingBox(), dx - entity.prevTickX, dy - entity.prevTickY, dz - entity.prevTickZ);
      GlStateManager.popMatrix();
      super.render(entity, dx, dy, dz, yaw, tickDelta);
   }

   @Override
   protected Identifier getTexture(Entity entity) {
      return null;
   }
}
