package net.minecraft.client.render.model.entity;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.passive.animal.SheepEntity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SheepModel extends QuadrupedModel {
   private float tickdelta;

   public SheepModel() {
      super(12, 0.0F);
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-3.0F, -4.0F, -6.0F, 6, 6, 8, 0.0F);
      this.head.setPivot(0.0F, 6.0F, -8.0F);
      this.body = new ModelPart(this, 28, 8);
      this.body.addBox(-4.0F, -10.0F, -7.0F, 8, 16, 6, 0.0F);
      this.body.setPivot(0.0F, 5.0F, 2.0F);
   }

   @Override
   public void renderMobAnimation(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta) {
      super.renderMobAnimation(entity, handSwing, handSwingAmount, tickDelta);
      this.head.pivotY = 6.0F + ((SheepEntity)entity).getNeckAngle(tickDelta) * 9.0F;
      this.tickdelta = ((SheepEntity)entity).getHeadAngle(tickDelta);
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      super.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      this.head.rotationX = this.tickdelta;
   }
}
