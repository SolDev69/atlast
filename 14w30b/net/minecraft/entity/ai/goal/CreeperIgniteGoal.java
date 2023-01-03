package net.minecraft.entity.ai.goal;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.hostile.CreeperEntity;

public class CreeperIgniteGoal extends Goal {
   CreeperEntity creeper;
   LivingEntity target;

   public CreeperIgniteGoal(CreeperEntity creeper) {
      this.creeper = creeper;
      this.setControls(1);
   }

   @Override
   public boolean canStart() {
      LivingEntity var1 = this.creeper.getTargetEntity();
      return this.creeper.getFuseSpeed() > 0 || var1 != null && this.creeper.getSquaredDistanceTo(var1) < 9.0;
   }

   @Override
   public void start() {
      this.creeper.getNavigation().stopCurrentNavigation();
      this.target = this.creeper.getTargetEntity();
   }

   @Override
   public void stop() {
      this.target = null;
   }

   @Override
   public void tick() {
      if (this.target == null) {
         this.creeper.setFuseSpeed(-1);
      } else if (this.creeper.getSquaredDistanceTo(this.target) > 49.0) {
         this.creeper.setFuseSpeed(-1);
      } else if (!this.creeper.getMobVisibilityCache().canSee(this.target)) {
         this.creeper.setFuseSpeed(-1);
      } else {
         this.creeper.setFuseSpeed(1);
      }
   }
}
