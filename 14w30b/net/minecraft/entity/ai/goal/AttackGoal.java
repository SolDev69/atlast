package net.minecraft.entity.ai.goal;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.world.World;

public class AttackGoal extends Goal {
   World world;
   MobEntity mob;
   LivingEntity target;
   int cooldown;

   public AttackGoal(MobEntity mob) {
      this.mob = mob;
      this.world = mob.world;
      this.setControls(3);
   }

   @Override
   public boolean canStart() {
      LivingEntity var1 = this.mob.getTargetEntity();
      if (var1 == null) {
         return false;
      } else {
         this.target = var1;
         return true;
      }
   }

   @Override
   public boolean shouldContinue() {
      if (!this.target.isAlive()) {
         return false;
      } else if (this.mob.getSquaredDistanceTo(this.target) > 225.0) {
         return false;
      } else {
         return !this.mob.getNavigation().isIdle() || this.canStart();
      }
   }

   @Override
   public void stop() {
      this.target = null;
      this.mob.getNavigation().stopCurrentNavigation();
   }

   @Override
   public void tick() {
      this.mob.getLookControl().setLookatValues(this.target, 30.0F, 30.0F);
      double var1 = (double)(this.mob.width * 2.0F * this.mob.width * 2.0F);
      double var3 = this.mob.getSquaredDistanceTo(this.target.x, this.target.getBoundingBox().minY, this.target.z);
      double var5 = 0.8;
      if (var3 > var1 && var3 < 16.0) {
         var5 = 1.33;
      } else if (var3 < 225.0) {
         var5 = 0.6;
      }

      this.mob.getNavigation().startMovingTo(this.target, var5);
      this.cooldown = Math.max(this.cooldown - 1, 0);
      if (!(var3 > var1)) {
         if (this.cooldown <= 0) {
            this.cooldown = 20;
            this.mob.attack(this.target);
         }
      }
   }
}
