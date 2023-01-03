package net.minecraft.client.render.model.entity;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class WitchModel extends VillagerModel {
   public boolean heldItemId;
   private ModelPart mole = new ModelPart(this).setTextureSize(64, 128);
   private ModelPart hat;

   public WitchModel(float f) {
      super(f, 0.0F, 64, 128);
      this.mole.setPivot(0.0F, -2.0F, 0.0F);
      this.mole.setTextureCoords(0, 0).addBox(0.0F, 3.0F, -6.75F, 1, 1, 1, -0.25F);
      this.nose.addChild(this.mole);
      this.hat = new ModelPart(this).setTextureSize(64, 128);
      this.hat.setPivot(-5.0F, -10.03125F, -5.0F);
      this.hat.setTextureCoords(0, 64).addBox(0.0F, 0.0F, 0.0F, 10, 2, 10);
      this.head.addChild(this.hat);
      ModelPart var2 = new ModelPart(this).setTextureSize(64, 128);
      var2.setPivot(1.75F, -4.0F, 2.0F);
      var2.setTextureCoords(0, 76).addBox(0.0F, 0.0F, 0.0F, 7, 4, 7);
      var2.rotationX = -0.05235988F;
      var2.rotationZ = 0.02617994F;
      this.hat.addChild(var2);
      ModelPart var3 = new ModelPart(this).setTextureSize(64, 128);
      var3.setPivot(1.75F, -4.0F, 2.0F);
      var3.setTextureCoords(0, 87).addBox(0.0F, 0.0F, 0.0F, 4, 4, 4);
      var3.rotationX = -0.10471976F;
      var3.rotationZ = 0.05235988F;
      var2.addChild(var3);
      ModelPart var4 = new ModelPart(this).setTextureSize(64, 128);
      var4.setPivot(1.75F, -2.0F, 2.0F);
      var4.setTextureCoords(0, 95).addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.25F);
      var4.rotationX = (float) (-Math.PI / 15);
      var4.rotationZ = 0.10471976F;
      var3.addChild(var4);
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      super.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      this.nose.translateX = this.nose.translateY = this.nose.translateZ = 0.0F;
      float var8 = 0.01F * (float)(entity.getNetworkId() % 10);
      this.nose.rotationX = MathHelper.sin((float)entity.time * var8) * 4.5F * (float) Math.PI / 180.0F;
      this.nose.rotationY = 0.0F;
      this.nose.rotationZ = MathHelper.cos((float)entity.time * var8) * 2.5F * (float) Math.PI / 180.0F;
      if (this.heldItemId) {
         this.nose.rotationX = -0.9F;
         this.nose.translateZ = -0.09375F;
         this.nose.translateY = 0.1875F;
      }
   }
}
