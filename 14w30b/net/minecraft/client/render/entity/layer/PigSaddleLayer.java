package net.minecraft.client.render.entity.layer;

import net.minecraft.client.render.entity.PigRenderer;
import net.minecraft.client.render.model.entity.PigModel;
import net.minecraft.entity.living.mob.passive.animal.PigEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class PigSaddleLayer implements EntityRenderLayer {
   private static final Identifier SADDLE_TEXTURE = new Identifier("textures/entity/pig/pig_saddle.png");
   private final PigRenderer parent;
   private final PigModel model = new PigModel(0.5F);

   public PigSaddleLayer(PigRenderer parent) {
      this.parent = parent;
   }

   public void render(PigEntity c_23orywdnd, float f, float g, float h, float i, float j, float k, float l) {
      if (c_23orywdnd.isSaddled()) {
         this.parent.bindTexture(SADDLE_TEXTURE);
         this.model.copyPropertiesFrom(this.parent.getModel());
         this.model.render(c_23orywdnd, f, g, i, j, k, l);
      }
   }

   @Override
   public boolean colorsWhenDamaged() {
      return false;
   }
}
