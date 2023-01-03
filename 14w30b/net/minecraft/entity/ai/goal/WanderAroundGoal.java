package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

public class WanderAroundGoal extends Goal {
   private PathAwareEntity entity;
   private double targetX;
   private double targetY;
   private double targetZ;
   private double speed;
   private int goalStartRngRange;
   private boolean shouldUpdateGoal;

   public WanderAroundGoal(PathAwareEntity entity, double speed) {
      this(entity, speed, 120);
   }

   public WanderAroundGoal(PathAwareEntity entity, double speed, int rngRange) {
      this.entity = entity;
      this.speed = speed;
      this.goalStartRngRange = rngRange;
      this.setControls(1);
   }

   @Override
   public boolean canStart() {
      if (!this.shouldUpdateGoal) {
         if (this.entity.getDespawnTimer() >= 100) {
            return false;
         }

         if (this.entity.getRandom().nextInt(this.goalStartRngRange) != 0) {
            return false;
         }
      }

      Vec3d var1 = TargetFinder.getTarget(this.entity, 10, 7);
      if (var1 == null) {
         return false;
      } else {
         this.targetX = var1.x;
         this.targetY = var1.y;
         this.targetZ = var1.z;
         this.shouldUpdateGoal = false;
         return true;
      }
   }

   @Override
   public boolean shouldContinue() {
      return !this.entity.getNavigation().isIdle();
   }

   @Override
   public void start() {
      this.entity.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
   }

   public void updateGoal() {
      this.shouldUpdateGoal = true;
   }

   public void setGoalStartRngRange(int rngRange) {
      this.goalStartRngRange = rngRange;
   }
}
