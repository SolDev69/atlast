package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class TargetFinder {
   private static Vec3d distanceFromStart = new Vec3d(0.0, 0.0, 0.0);

   public static Vec3d getTarget(PathAwareEntity pathAwareEntity, int horizontalRange, int verticalRange) {
      return getTarget(pathAwareEntity, horizontalRange, verticalRange, null);
   }

   public static Vec3d getTargetAwayFromPosition(PathAwareEntity pathAwareEntity, int horizontalRange, int verticalRange, Vec3d currentPos) {
      distanceFromStart = currentPos.subtract(pathAwareEntity.x, pathAwareEntity.y, pathAwareEntity.z);
      return getTarget(pathAwareEntity, horizontalRange, verticalRange, distanceFromStart);
   }

   public static Vec3d getTargetAwayFromEntity(PathAwareEntity pathAwareEntity, int horizontalRange, int verticalRange, Vec3d attackerPos) {
      distanceFromStart = new Vec3d(pathAwareEntity.x, pathAwareEntity.y, pathAwareEntity.z).subtract(attackerPos);
      return getTarget(pathAwareEntity, horizontalRange, verticalRange, distanceFromStart);
   }

   private static Vec3d getTarget(PathAwareEntity pathAwareEntity, int horizontalRange, int verticalRange, Vec3d distanceFromStart) {
      Random var4 = pathAwareEntity.getRandom();
      boolean var5 = false;
      int var6 = 0;
      int var7 = 0;
      int var8 = 0;
      float var9 = -99999.0F;
      boolean var10;
      if (pathAwareEntity.inVillage()) {
         double var11 = pathAwareEntity.getPos()
               .squaredDistanceTo(
                  (double)MathHelper.floor(pathAwareEntity.x), (double)MathHelper.floor(pathAwareEntity.y), (double)MathHelper.floor(pathAwareEntity.z)
               )
            + 4.0;
         double var13 = (double)(pathAwareEntity.getVillageRadius() + (float)horizontalRange);
         var10 = var11 < var13 * var13;
      } else {
         var10 = false;
      }

      for(int var17 = 0; var17 < 10; ++var17) {
         int var12 = var4.nextInt(2 * horizontalRange + 1) - horizontalRange;
         int var19 = var4.nextInt(2 * verticalRange + 1) - verticalRange;
         int var14 = var4.nextInt(2 * horizontalRange + 1) - horizontalRange;
         if (distanceFromStart == null || !((double)var12 * distanceFromStart.x + (double)var14 * distanceFromStart.z < 0.0)) {
            if (pathAwareEntity.inVillage() && horizontalRange > 1) {
               BlockPos var15 = pathAwareEntity.getPos();
               if (pathAwareEntity.x > (double)var15.getX()) {
                  var12 -= var4.nextInt(horizontalRange / 2);
               } else {
                  var12 += var4.nextInt(horizontalRange / 2);
               }

               if (pathAwareEntity.z > (double)var15.getZ()) {
                  var14 -= var4.nextInt(horizontalRange / 2);
               } else {
                  var14 += var4.nextInt(horizontalRange / 2);
               }
            }

            var12 += MathHelper.floor(pathAwareEntity.x);
            var19 += MathHelper.floor(pathAwareEntity.y);
            var14 += MathHelper.floor(pathAwareEntity.z);
            BlockPos var22 = new BlockPos(var12, var19, var14);
            if (!var10 || pathAwareEntity.isPosInVillage(var22)) {
               float var16 = pathAwareEntity.getPathfindingFavor(var22);
               if (var16 > var9) {
                  var9 = var16;
                  var6 = var12;
                  var7 = var19;
                  var8 = var14;
                  var5 = true;
               }
            }
         }
      }

      return var5 ? new Vec3d((double)var6, (double)var7, (double)var8) : null;
   }
}
