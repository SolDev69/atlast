package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.village.Village;
import net.minecraft.world.village.VillageDoor;

public class StayIndoorsGoal extends Goal {
   private PathAwareEntity entity;
   private VillageDoor door;
   private int x = -1;
   private int y = -1;

   public StayIndoorsGoal(PathAwareEntity entity) {
      this.entity = entity;
      this.setControls(1);
   }

   @Override
   public boolean canStart() {
      BlockPos var1 = new BlockPos(this.entity);
      if ((!this.entity.world.isSunny() || this.entity.world.isRaining() && !this.entity.world.getBiome(var1).canRain())
         && !this.entity.world.dimension.isDark()) {
         if (this.entity.getRandom().nextInt(50) != 0) {
            return false;
         } else if (this.x != -1 && this.entity.getSquaredDistanceTo((double)this.x, this.entity.y, (double)this.y) < 4.0) {
            return false;
         } else {
            Village var2 = this.entity.world.getVillageData().getClosestVillage(var1, 14);
            if (var2 == null) {
               return false;
            } else {
               this.door = var2.getClosestTickingDoor(var1);
               return this.door != null;
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean shouldContinue() {
      return !this.entity.getNavigation().isIdle();
   }

   @Override
   public void start() {
      this.x = -1;
      BlockPos var1 = this.door.getIndoorsPos();
      int var2 = var1.getX();
      int var3 = var1.getY();
      int var4 = var1.getZ();
      if (this.entity.getSquaredDistanceTo(var1) > 256.0) {
         Vec3d var5 = TargetFinder.getTargetAwayFromPosition(this.entity, 14, 3, new Vec3d((double)var2 + 0.5, (double)var3, (double)var4 + 0.5));
         if (var5 != null) {
            this.entity.getNavigation().startMovingTo(var5.x, var5.y, var5.z, 1.0);
         }
      } else {
         this.entity.getNavigation().startMovingTo((double)var2 + 0.5, (double)var3, (double)var4 + 0.5, 1.0);
      }
   }

   @Override
   public void stop() {
      this.x = this.door.getIndoorsPos().getX();
      this.y = this.door.getIndoorsPos().getZ();
      this.door = null;
   }
}
