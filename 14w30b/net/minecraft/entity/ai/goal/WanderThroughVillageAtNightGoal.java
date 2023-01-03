package net.minecraft.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.village.Village;
import net.minecraft.world.village.VillageDoor;

public class WanderThroughVillageAtNightGoal extends Goal {
   private PathAwareEntity entity;
   private double speed;
   private Path path;
   private VillageDoor villageDoor;
   private boolean friendly;
   private List doors = Lists.newArrayList();

   public WanderThroughVillageAtNightGoal(PathAwareEntity entity, double speed, boolean friendly) {
      this.entity = entity;
      this.speed = speed;
      this.friendly = friendly;
      this.setControls(1);
      if (!(entity.getNavigation() instanceof MobEntityNavigation)) {
         throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
      }
   }

   @Override
   public boolean canStart() {
      this.clampDoorsListSize();
      if (this.friendly && this.entity.world.isSunny()) {
         return false;
      } else {
         Village var1 = this.entity.world.getVillageData().getClosestVillage(new BlockPos(this.entity), 0);
         if (var1 == null) {
            return false;
         } else {
            this.villageDoor = this.findDoor(var1);
            if (this.villageDoor == null) {
               return false;
            } else {
               MobEntityNavigation var2 = (MobEntityNavigation)this.entity.getNavigation();
               boolean var3 = var2.canInteractWithDoors();
               var2.m_54onmfdow(false);
               this.path = var2.m_79ixqqkkt(this.villageDoor.getPos());
               var2.m_54onmfdow(var3);
               if (this.path != null) {
                  return true;
               } else {
                  Vec3d var4 = TargetFinder.getTargetAwayFromPosition(
                     this.entity,
                     10,
                     7,
                     new Vec3d((double)this.villageDoor.getPos().getX(), (double)this.villageDoor.getPos().getY(), (double)this.villageDoor.getPos().getZ())
                  );
                  if (var4 == null) {
                     return false;
                  } else {
                     var2.m_54onmfdow(false);
                     this.path = this.entity.getNavigation().findPathTo(var4.x, var4.y, var4.z);
                     var2.m_54onmfdow(var3);
                     return this.path != null;
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean shouldContinue() {
      if (this.entity.getNavigation().isIdle()) {
         return false;
      } else {
         float var1 = this.entity.width + 4.0F;
         return this.entity.getSquaredDistanceTo(this.villageDoor.getPos()) > (double)(var1 * var1);
      }
   }

   @Override
   public void start() {
      this.entity.getNavigation().startMovingAlong(this.path, this.speed);
   }

   @Override
   public void stop() {
      if (this.entity.getNavigation().isIdle() || this.entity.getSquaredDistanceTo(this.villageDoor.getPos()) < 16.0) {
         this.doors.add(this.villageDoor);
      }
   }

   private VillageDoor findDoor(Village village) {
      VillageDoor var2 = null;
      int var3 = Integer.MAX_VALUE;

      for(VillageDoor var6 : village.getDoors()) {
         int var7 = var6.getSquaredDistanceTo(MathHelper.floor(this.entity.x), MathHelper.floor(this.entity.y), MathHelper.floor(this.entity.z));
         if (var7 < var3 && !this.isDoorInRange(var6)) {
            var2 = var6;
            var3 = var7;
         }
      }

      return var2;
   }

   private boolean isDoorInRange(VillageDoor door) {
      for(VillageDoor var3 : this.doors) {
         if (door.getPos().equals(var3.getPos())) {
            return true;
         }
      }

      return false;
   }

   private void clampDoorsListSize() {
      if (this.doors.size() > 15) {
         this.doors.remove(0);
      }
   }
}
