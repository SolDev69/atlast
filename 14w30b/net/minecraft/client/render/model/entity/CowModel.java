package net.minecraft.client.render.model.entity;

import net.minecraft.client.render.model.ModelPart;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class CowModel extends QuadrupedModel {
   public CowModel() {
      super(12, 0.0F);
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-4.0F, -4.0F, -6.0F, 8, 8, 6, 0.0F);
      this.head.setPivot(0.0F, 4.0F, -8.0F);
      this.head.setTextureCoords(22, 0).addBox(-5.0F, -5.0F, -4.0F, 1, 3, 1, 0.0F);
      this.head.setTextureCoords(22, 0).addBox(4.0F, -5.0F, -4.0F, 1, 3, 1, 0.0F);
      this.body = new ModelPart(this, 18, 4);
      this.body.addBox(-6.0F, -10.0F, -7.0F, 12, 18, 10, 0.0F);
      this.body.setPivot(0.0F, 5.0F, 2.0F);
      this.body.setTextureCoords(52, 0).addBox(-2.0F, 2.0F, -8.0F, 4, 6, 1);
      --this.backRightLeg.pivotX;
      ++this.backLeftLeg.pivotX;
      this.backRightLeg.pivotZ += 0.0F;
      this.backLeftLeg.pivotZ += 0.0F;
      --this.frontRightLeg.pivotX;
      ++this.frontLeftLeg.pivotX;
      --this.frontRightLeg.pivotZ;
      --this.frontLeftLeg.pivotZ;
      this.babyHeadOffset += 2.0F;
   }
}
