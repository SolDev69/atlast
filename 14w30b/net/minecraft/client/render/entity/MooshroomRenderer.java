package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.layer.MushroomLayer;
import net.minecraft.client.render.model.Model;
import net.minecraft.entity.living.mob.passive.animal.MooshroomEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class MooshroomRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/cow/mooshroom.png");

   public MooshroomRenderer(EntityRenderDispatcher c_28wsgstbh, Model c_56prnndub, float f) {
      super(c_28wsgstbh, c_56prnndub, f);
      this.addLayer(new MushroomLayer(this));
   }

   protected Identifier getTexture(MooshroomEntity c_74tbbodgs) {
      return TEXTURE;
   }
}
