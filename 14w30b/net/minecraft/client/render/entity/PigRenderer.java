package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.layer.PigSaddleLayer;
import net.minecraft.client.render.model.Model;
import net.minecraft.entity.living.mob.passive.animal.PigEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class PigRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/pig/pig.png");

   public PigRenderer(EntityRenderDispatcher c_28wsgstbh, Model c_56prnndub, float f) {
      super(c_28wsgstbh, c_56prnndub, f);
      this.addLayer(new PigSaddleLayer(this));
   }

   protected Identifier getTexture(PigEntity c_23orywdnd) {
      return TEXTURE;
   }
}
