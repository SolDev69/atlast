package net.minecraft.client.render.model.entity;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class PigModel extends QuadrupedModel {
   public PigModel() {
      this(0.0F);
   }

   public PigModel(float f) {
      super(6, f);
      this.head.setTextureCoords(16, 16).addBox(-2.0F, 0.0F, -9.0F, 4, 3, 1, f);
      this.babyHeadHeightOffset = 4.0F;
   }
}
