package net.minecraft.client.render.model.entity;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.hostile.SkeletonEntity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SkeletonModel extends ZombieModel {
   public SkeletonModel() {
      this(0.0F, false);
   }

   public SkeletonModel(float f, boolean bl) {
      super(f, 0.0F, 64, 32);
      if (!bl) {
         this.rightArm = new ModelPart(this, 40, 16);
         this.rightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, f);
         this.rightArm.setPivot(-5.0F, 2.0F, 0.0F);
         this.leftArm = new ModelPart(this, 40, 16);
         this.leftArm.flipped = true;
         this.leftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, f);
         this.leftArm.setPivot(5.0F, 2.0F, 0.0F);
         this.rightLeg = new ModelPart(this, 0, 16);
         this.rightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, f);
         this.rightLeg.setPivot(-2.0F, 12.0F, 0.0F);
         this.leftLeg = new ModelPart(this, 0, 16);
         this.leftLeg.flipped = true;
         this.leftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, f);
         this.leftLeg.setPivot(2.0F, 12.0F, 0.0F);
      }
   }

   @Override
   public void renderMobAnimation(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta) {
      this.aimingBow = ((SkeletonEntity)entity).getType() == 1;
      super.renderMobAnimation(entity, handSwing, handSwingAmount, tickDelta);
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      super.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
   }
}
