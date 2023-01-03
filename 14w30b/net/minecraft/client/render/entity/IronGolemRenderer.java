package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.layer.IronGolemHeldFlowerLayer;
import net.minecraft.client.render.model.entity.IronGolemModel;
import net.minecraft.entity.living.mob.IronGolemEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class IronGolemRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/iron_golem.png");

   public IronGolemRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh, new IronGolemModel(), 0.5F);
      this.addLayer(new IronGolemHeldFlowerLayer(this));
   }

   protected Identifier getTexture(IronGolemEntity c_91ginzfwf) {
      return TEXTURE;
   }

   protected void applyRotation(IronGolemEntity c_91ginzfwf, float f, float g, float h) {
      super.applyRotation(c_91ginzfwf, f, g, h);
      if (!((double)c_91ginzfwf.handSwingAmount < 0.01)) {
         float var5 = 13.0F;
         float var6 = c_91ginzfwf.handSwing - c_91ginzfwf.handSwingAmount * (1.0F - h) + 6.0F;
         float var7 = (Math.abs(var6 % var5 - var5 * 0.5F) - var5 * 0.25F) / (var5 * 0.25F);
         GlStateManager.rotatef(6.5F * var7, 0.0F, 0.0F, 1.0F);
      }
   }
}
