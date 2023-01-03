package net.minecraft.client.render.entity;

import net.minecraft.client.render.model.entity.SilverfishModel;
import net.minecraft.entity.living.mob.hostile.SliverfishEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SilverfishRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/silverfish.png");

   public SilverfishRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh, new SilverfishModel(), 0.3F);
   }

   protected float getYawWhileDead(SliverfishEntity c_12vzxufzf) {
      return 180.0F;
   }

   protected Identifier getTexture(SliverfishEntity c_12vzxufzf) {
      return TEXTURE;
   }
}
