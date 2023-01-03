package net.minecraft.client.render.entity;

import net.minecraft.client.render.model.entity.EndermiteModel;
import net.minecraft.entity.living.mob.hostile.EndermiteEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EndermiteRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/endermite.png");

   public EndermiteRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh, new EndermiteModel(), 0.3F);
   }

   protected float getYawWhileDead(EndermiteEntity c_69mympzol) {
      return 180.0F;
   }

   protected Identifier getTexture(EndermiteEntity c_69mympzol) {
      return TEXTURE;
   }
}
