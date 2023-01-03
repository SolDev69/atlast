package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

public class GoToEntityTargetGoal extends Goal {
   private PathAwareEntity entity;
   private LivingEntity targetEntity;
   private double targetX;
   private double targetY;
   private double targetZ;
   private double speed;
   private float maxDistance;

   public GoToEntityTargetGoal(PathAwareEntity entity, double speed, float f) {
      this.entity = entity;
      this.speed = speed;
      this.maxDistance = f;
      this.setControls(1);
   }

   @Override
   public boolean canStart() {
      this.targetEntity = this.entity.getTargetEntity();
      if (this.targetEntity == null) {
         return false;
      } else if (this.targetEntity.getSquaredDistanceTo(this.entity) > (double)(this.maxDistance * this.maxDistance)) {
         return false;
      } else {
         Vec3d var1 = TargetFinder.getTargetAwayFromPosition(this.entity, 16, 7, new Vec3d(this.targetEntity.x, this.targetEntity.y, this.targetEntity.z));
         if (var1 == null) {
            return false;
         } else {
            this.targetX = var1.x;
            this.targetY = var1.y;
            this.targetZ = var1.z;
            return true;
         }
      }
   }

   @Override
   public boolean shouldContinue() {
      return !this.entity.getNavigation().isIdle()
         && this.targetEntity.isAlive()
         && this.targetEntity.getSquaredDistanceTo(this.entity) < (double)(this.maxDistance * this.maxDistance);
   }

   @Override
   public void stop() {
      this.targetEntity = null;
   }

   @Override
   public void start() {
      this.entity.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
   }
}
