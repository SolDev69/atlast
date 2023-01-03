package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.village.Village;
import net.minecraft.world.village.VillageDoor;

public class RestrictOpenDoorGoal extends Goal {
   private PathAwareEntity entity;
   private VillageDoor door;

   public RestrictOpenDoorGoal(PathAwareEntity entity) {
      this.entity = entity;
      if (!(entity.getNavigation() instanceof MobEntityNavigation)) {
         throw new IllegalArgumentException("Unsupported mob type for RestrictOpenDoorGoal");
      }
   }

   @Override
   public boolean canStart() {
      if (this.entity.world.isSunny()) {
         return false;
      } else {
         BlockPos var1 = new BlockPos(this.entity);
         Village var2 = this.entity.world.getVillageData().getClosestVillage(var1, 16);
         if (var2 == null) {
            return false;
         } else {
            this.door = var2.getClosestDoor(var1);
            if (this.door == null) {
               return false;
            } else {
               return (double)this.door.getSquaredDistanceToIndoors(var1) < 2.25;
            }
         }
      }
   }

   @Override
   public boolean shouldContinue() {
      if (this.entity.world.isSunny()) {
         return false;
      } else {
         return !this.door.isOutsideVillage() && this.door.isIndoors(new BlockPos(this.entity));
      }
   }

   @Override
   public void start() {
      ((MobEntityNavigation)this.entity.getNavigation()).m_54onmfdow(false);
      ((MobEntityNavigation)this.entity.getNavigation()).m_91qjgongu(false);
   }

   @Override
   public void stop() {
      ((MobEntityNavigation)this.entity.getNavigation()).m_54onmfdow(true);
      ((MobEntityNavigation)this.entity.getNavigation()).m_91qjgongu(true);
      this.door = null;
   }

   @Override
   public void tick() {
      this.door.restrictOpening();
   }
}
