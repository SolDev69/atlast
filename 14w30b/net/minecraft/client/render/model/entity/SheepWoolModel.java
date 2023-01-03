package net.minecraft.client.render.model.entity;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.passive.animal.SheepEntity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SheepWoolModel extends QuadrupedModel {
   private float headAngle;

   public SheepWoolModel() {
      super(12, 0.0F);
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-3.0F, -4.0F, -4.0F, 6, 6, 6, 0.6F);
      this.head.setPivot(0.0F, 6.0F, -8.0F);
      this.body = new ModelPart(this, 28, 8);
      this.body.addBox(-4.0F, -10.0F, -7.0F, 8, 16, 6, 1.75F);
      this.body.setPivot(0.0F, 5.0F, 2.0F);
      float var1 = 0.5F;
      this.backRightLeg = new ModelPart(this, 0, 16);
      this.backRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, var1);
      this.backRightLeg.setPivot(-3.0F, 12.0F, 7.0F);
      this.backLeftLeg = new ModelPart(this, 0, 16);
      this.backLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, var1);
      this.backLeftLeg.setPivot(3.0F, 12.0F, 7.0F);
      this.frontRightLeg = new ModelPart(this, 0, 16);
      this.frontRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, var1);
      this.frontRightLeg.setPivot(-3.0F, 12.0F, -5.0F);
      this.frontLeftLeg = new ModelPart(this, 0, 16);
      this.frontLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, var1);
      this.frontLeftLeg.setPivot(3.0F, 12.0F, -5.0F);
   }

   @Override
   public void renderMobAnimation(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta) {
      super.renderMobAnimation(entity, handSwing, handSwingAmount, tickDelta);
      this.head.pivotY = 6.0F + ((SheepEntity)entity).getNeckAngle(tickDelta) * 9.0F;
      this.headAngle = ((SheepEntity)entity).getHeadAngle(tickDelta);
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      super.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      this.head.rotationX = this.headAngle;
   }
}
