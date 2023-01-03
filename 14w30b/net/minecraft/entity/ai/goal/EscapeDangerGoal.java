package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

public class EscapeDangerGoal extends Goal {
   private PathAwareEntity entity;
   private double speed;
   private double targetX;
   private double targetY;
   private double targetZ;

   public EscapeDangerGoal(PathAwareEntity entity, double speed) {
      this.entity = entity;
      this.speed = speed;
      this.setControls(1);
   }

   @Override
   public boolean canStart() {
      if (this.entity.getAttacker() == null && !this.entity.isOnFire()) {
         return false;
      } else {
         Vec3d var1 = TargetFinder.getTarget(this.entity, 5, 4);
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
   public void start() {
      this.entity.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
   }

   @Override
   public boolean shouldContinue() {
      return !this.entity.getNavigation().isIdle();
   }
}
