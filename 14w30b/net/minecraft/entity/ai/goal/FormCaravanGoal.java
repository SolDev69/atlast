package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.passive.VillagerEntity;
import net.minecraft.util.math.Vec3d;

public class FormCaravanGoal extends Goal {
   private VillagerEntity villager;
   private LivingEntity target;
   private double speed;
   private int actionCountdown;

   public FormCaravanGoal(VillagerEntity villager, double speed) {
      this.villager = villager;
      this.speed = speed;
      this.setControls(1);
   }

   @Override
   public boolean canStart() {
      if (this.villager.getBreedingAge() >= 0) {
         return false;
      } else if (this.villager.getRandom().nextInt(400) != 0) {
         return false;
      } else {
         List var1 = this.villager.world.getEntities(VillagerEntity.class, this.villager.getBoundingBox().expand(6.0, 3.0, 6.0));
         double var2 = Double.MAX_VALUE;

         for(VillagerEntity var5 : var1) {
            if (var5 != this.villager && !var5.getInCaravan() && var5.getBreedingAge() < 0) {
               double var6 = var5.getSquaredDistanceTo(this.villager);
               if (!(var6 > var2)) {
                  var2 = var6;
                  this.target = var5;
               }
            }
         }

         if (this.target == null) {
            Vec3d var8 = TargetFinder.getTarget(this.villager, 16, 3);
            if (var8 == null) {
               return false;
            }
         }

         return true;
      }
   }

   @Override
   public boolean shouldContinue() {
      return this.actionCountdown > 0;
   }

   @Override
   public void start() {
      if (this.target != null) {
         this.villager.setInCaravan(true);
      }

      this.actionCountdown = 1000;
   }

   @Override
   public void stop() {
      this.villager.setInCaravan(false);
      this.target = null;
   }

   @Override
   public void tick() {
      --this.actionCountdown;
      if (this.target != null) {
         if (this.villager.getSquaredDistanceTo(this.target) > 4.0) {
            this.villager.getNavigation().startMovingTo(this.target, this.speed);
         }
      } else if (this.villager.getNavigation().isIdle()) {
         Vec3d var1 = TargetFinder.getTarget(this.villager, 16, 3);
         if (var1 == null) {
            return;
         }

         this.villager.getNavigation().startMovingTo(var1.x, var1.y, var1.z, this.speed);
      }
   }
}
