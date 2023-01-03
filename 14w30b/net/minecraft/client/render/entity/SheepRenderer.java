package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.layer.SheepWoolLayer;
import net.minecraft.client.render.model.Model;
import net.minecraft.entity.living.mob.passive.animal.SheepEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SheepRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/sheep/sheep.png");

   public SheepRenderer(EntityRenderDispatcher c_28wsgstbh, Model c_56prnndub, float f) {
      super(c_28wsgstbh, c_56prnndub, f);
      this.addLayer(new SheepWoolLayer(this));
   }

   protected Identifier getTexture(SheepEntity c_42soehjyi) {
      return TEXTURE;
   }
}
