package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class GoToWalkTargetGoal extends Goal {
   private PathAwareEntity entity;
   private double targetX;
   private double targetY;
   private double targetZ;
   private double speed;

   public GoToWalkTargetGoal(PathAwareEntity entity, double speed) {
      this.entity = entity;
      this.speed = speed;
      this.setControls(1);
   }

   @Override
   public boolean canStart() {
      if (this.entity.isInVillage()) {
         return false;
      } else {
         BlockPos var1 = this.entity.getPos();
         Vec3d var2 = TargetFinder.getTargetAwayFromPosition(this.entity, 16, 7, new Vec3d((double)var1.getX(), (double)var1.getY(), (double)var1.getZ()));
         if (var2 == null) {
            return false;
         } else {
            this.targetX = var2.x;
            this.targetY = var2.y;
            this.targetZ = var2.z;
            return true;
         }
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
}
