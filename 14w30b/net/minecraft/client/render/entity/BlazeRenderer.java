package net.minecraft.client.render.entity;

import net.minecraft.client.render.model.entity.BlazeModel;
import net.minecraft.entity.living.mob.hostile.BlazeEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlazeRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/blaze.png");

   public BlazeRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh, new BlazeModel(), 0.5F);
   }

   protected Identifier getTexture(BlazeEntity c_57tlcvvsi) {
      return TEXTURE;
   }
}
