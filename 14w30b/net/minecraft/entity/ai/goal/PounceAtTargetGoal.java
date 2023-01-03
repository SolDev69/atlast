package net.minecraft.entity.ai.goal;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class PounceAtTargetGoal extends Goal {
   MobEntity mob;
   LivingEntity targetEntity;
   float speed;

   public PounceAtTargetGoal(MobEntity mob, float speed) {
      this.mob = mob;
      this.speed = speed;
      this.setControls(5);
   }

   @Override
   public boolean canStart() {
      this.targetEntity = this.mob.getTargetEntity();
      if (this.targetEntity == null) {
         return false;
      } else {
         double var1 = this.mob.getSquaredDistanceTo(this.targetEntity);
         if (var1 < 4.0 || var1 > 16.0) {
            return false;
         } else if (!this.mob.onGround) {
            return false;
         } else {
            return this.mob.getRandom().nextInt(5) == 0;
         }
      }
   }

   @Override
   public boolean shouldContinue() {
      return !this.mob.onGround;
   }

   @Override
   public void start() {
      double var1 = this.targetEntity.x - this.mob.x;
      double var3 = this.targetEntity.z - this.mob.z;
      float var5 = MathHelper.sqrt(var1 * var1 + var3 * var3);
      this.mob.velocityX += var1 / (double)var5 * 0.5 * 0.8F + this.mob.velocityX * 0.2F;
      this.mob.velocityZ += var3 / (double)var5 * 0.5 * 0.8F + this.mob.velocityZ * 0.2F;
      this.mob.velocityY = (double)this.speed;
   }
}
