package net.minecraft.entity.ai.goal;

import java.util.Random;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EscapeSunlightGoal extends Goal {
   private PathAwareEntity entity;
   private double targetX;
   private double targetY;
   private double targetZ;
   private double speed;
   private World world;

   public EscapeSunlightGoal(PathAwareEntity entity, double speed) {
      this.entity = entity;
      this.speed = speed;
      this.world = entity.world;
      this.setControls(1);
   }

   @Override
   public boolean canStart() {
      if (!this.world.isSunny()) {
         return false;
      } else if (!this.entity.isOnFire()) {
         return false;
      } else if (!this.world.hasSkyAccess(new BlockPos(this.entity.x, this.entity.getBoundingBox().minY, this.entity.z))) {
         return false;
      } else {
         Vec3d var1 = this.getShadedLocationPos();
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
      return !this.entity.getNavigation().isIdle();
   }

   @Override
   public void start() {
      this.entity.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
   }

   private Vec3d getShadedLocationPos() {
      Random var1 = this.entity.getRandom();
      BlockPos var2 = new BlockPos(this.entity.x, this.entity.getBoundingBox().minY, this.entity.z);

      for(int var3 = 0; var3 < 10; ++var3) {
         BlockPos var4 = var2.add(var1.nextInt(20) - 10, var1.nextInt(6) - 3, var1.nextInt(20) - 10);
         if (!this.world.hasSkyAccess(var4) && this.entity.getPathfindingFavor(var4) < 0.0F) {
            return new Vec3d((double)var4.getX(), (double)var4.getY(), (double)var4.getZ());
         }
      }

      return null;
   }
}
