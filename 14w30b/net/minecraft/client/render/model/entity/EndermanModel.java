package net.minecraft.client.render.model.entity;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EndermanModel extends HumanoidModel {
   public boolean carryingBlock;
   public boolean angry;

   public EndermanModel(float f) {
      super(0.0F, -14.0F, 64, 32);
      float var2 = -14.0F;
      this.hat = new ModelPart(this, 0, 16);
      this.hat.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f - 0.5F);
      this.hat.setPivot(0.0F, 0.0F + var2, 0.0F);
      this.body = new ModelPart(this, 32, 16);
      this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, f);
      this.body.setPivot(0.0F, 0.0F + var2, 0.0F);
      this.rightArm = new ModelPart(this, 56, 0);
      this.rightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, f);
      this.rightArm.setPivot(-3.0F, 2.0F + var2, 0.0F);
      this.leftArm = new ModelPart(this, 56, 0);
      this.leftArm.flipped = true;
      this.leftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, f);
      this.leftArm.setPivot(5.0F, 2.0F + var2, 0.0F);
      this.rightLeg = new ModelPart(this, 56, 0);
      this.rightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, f);
      this.rightLeg.setPivot(-2.0F, 12.0F + var2, 0.0F);
      this.leftLeg = new ModelPart(this, 56, 0);
      this.leftLeg.flipped = true;
      this.leftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, f);
      this.leftLeg.setPivot(2.0F, 12.0F + var2, 0.0F);
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      super.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      this.head.visible = true;
      float var8 = -14.0F;
      this.body.rotationX = 0.0F;
      this.body.pivotY = var8;
      this.body.pivotZ = -0.0F;
      this.rightLeg.rotationX -= 0.0F;
      this.leftLeg.rotationX -= 0.0F;
      this.rightArm.rotationX = (float)((double)this.rightArm.rotationX * 0.5);
      this.leftArm.rotationX = (float)((double)this.leftArm.rotationX * 0.5);
      this.rightLeg.rotationX = (float)((double)this.rightLeg.rotationX * 0.5);
      this.leftLeg.rotationX = (float)((double)this.leftLeg.rotationX * 0.5);
      float var9 = 0.4F;
      if (this.rightArm.rotationX > var9) {
         this.rightArm.rotationX = var9;
      }

      if (this.leftArm.rotationX > var9) {
         this.leftArm.rotationX = var9;
      }

      if (this.rightArm.rotationX < -var9) {
         this.rightArm.rotationX = -var9;
      }

      if (this.leftArm.rotationX < -var9) {
         this.leftArm.rotationX = -var9;
      }

      if (this.rightLeg.rotationX > var9) {
         this.rightLeg.rotationX = var9;
      }

      if (this.leftLeg.rotationX > var9) {
         this.leftLeg.rotationX = var9;
      }

      if (this.rightLeg.rotationX < -var9) {
         this.rightLeg.rotationX = -var9;
      }

      if (this.leftLeg.rotationX < -var9) {
         this.leftLeg.rotationX = -var9;
      }

      if (this.carryingBlock) {
         this.rightArm.rotationX = -0.5F;
         this.leftArm.rotationX = -0.5F;
         this.rightArm.rotationZ = 0.05F;
         this.leftArm.rotationZ = -0.05F;
      }

      this.rightArm.pivotZ = 0.0F;
      this.leftArm.pivotZ = 0.0F;
      this.rightLeg.pivotZ = 0.0F;
      this.leftLeg.pivotZ = 0.0F;
      this.rightLeg.pivotY = 9.0F + var8;
      this.leftLeg.pivotY = 9.0F + var8;
      this.head.pivotZ = -0.0F;
      this.head.pivotY = var8 + 1.0F;
      this.hat.pivotX = this.head.pivotX;
      this.hat.pivotY = this.head.pivotY;
      this.hat.pivotZ = this.head.pivotZ;
      this.hat.rotationX = this.head.rotationX;
      this.hat.rotationY = this.head.rotationY;
      this.hat.rotationZ = this.head.rotationZ;
      if (this.angry) {
         float var10 = 1.0F;
         this.head.pivotY -= var10 * 5.0F;
      }
   }
}
