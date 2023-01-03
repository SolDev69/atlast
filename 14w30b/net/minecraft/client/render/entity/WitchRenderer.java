package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.layer.WitchHeldItemLayer;
import net.minecraft.client.render.model.entity.WitchModel;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.hostile.WitchEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class WitchRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/witch.png");

   public WitchRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh, new WitchModel(0.0F), 0.5F);
      this.addLayer(new WitchHeldItemLayer(this));
   }

   public void render(WitchEntity c_12xxtvzpn, double d, double e, double f, float g, float h) {
      ((WitchModel)this.model).heldItemId = c_12xxtvzpn.getStackInHand() != null;
      super.render((MobEntity)c_12xxtvzpn, d, e, f, g, h);
   }

   protected Identifier getTexture(WitchEntity c_12xxtvzpn) {
      return TEXTURE;
   }

   @Override
   public void m_81npivqro() {
      GlStateManager.translatef(0.0F, 0.1875F, 0.0F);
   }

   protected void scale(WitchEntity c_12xxtvzpn, float f) {
      float var3 = 0.9375F;
      GlStateManager.scalef(var3, var3, var3);
   }
}
