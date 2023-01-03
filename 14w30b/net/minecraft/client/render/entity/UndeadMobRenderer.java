package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.layer.HeldItemLayer;
import net.minecraft.client.render.entity.layer.WornSkullLayer;
import net.minecraft.client.render.model.entity.HumanoidModel;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class UndeadMobRenderer extends MobRenderer {
   private static final Identifier STEVE_TEXTURE = new Identifier("textures/entity/steve.png");
   protected HumanoidModel f_35kzpvqyv;
   protected float f_08ujyjsqj;

   public UndeadMobRenderer(EntityRenderDispatcher c_28wsgstbh, HumanoidModel c_85hwjrnsi, float f) {
      this(c_28wsgstbh, c_85hwjrnsi, f, 1.0F);
      this.addLayer(new HeldItemLayer(this));
   }

   public UndeadMobRenderer(EntityRenderDispatcher c_28wsgstbh, HumanoidModel c_85hwjrnsi, float f, float g) {
      super(c_28wsgstbh, c_85hwjrnsi, f);
      this.f_35kzpvqyv = c_85hwjrnsi;
      this.f_08ujyjsqj = g;
      this.addLayer(new WornSkullLayer(c_85hwjrnsi.head));
   }

   protected Identifier getTexture(MobEntity c_81psrrogw) {
      return STEVE_TEXTURE;
   }

   @Override
   public void m_81npivqro() {
      GlStateManager.translatef(0.0F, 0.1875F, 0.0F);
   }
}
