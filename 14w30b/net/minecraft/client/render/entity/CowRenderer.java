package net.minecraft.client.render.entity;

import net.minecraft.client.render.model.Model;
import net.minecraft.entity.living.mob.passive.animal.CowEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class CowRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/cow/cow.png");

   public CowRenderer(EntityRenderDispatcher c_28wsgstbh, Model c_56prnndub, float f) {
      super(c_28wsgstbh, c_56prnndub, f);
   }

   protected Identifier getTexture(CowEntity c_96cethqbw) {
      return TEXTURE;
   }
}
