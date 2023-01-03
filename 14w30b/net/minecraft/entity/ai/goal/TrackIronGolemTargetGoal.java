package net.minecraft.entity.ai.goal;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.IronGolemEntity;
import net.minecraft.world.village.Village;

public class TrackIronGolemTargetGoal extends TrackTargetGoal {
   IronGolemEntity golem;
   LivingEntity target;

   public TrackIronGolemTargetGoal(IronGolemEntity golem) {
      super(golem, false, true);
      this.golem = golem;
      this.setControls(1);
   }

   @Override
   public boolean canStart() {
      Village var1 = this.golem.getVillage();
      if (var1 == null) {
         return false;
      } else {
         this.target = var1.getClosestAttacker(this.golem);
         if (!this.canTarget(this.target, false)) {
            if (this.entity.getRandom().nextInt(20) == 0) {
               this.target = var1.getClosestPlayer(this.golem);
               return this.canTarget(this.target, false);
            } else {
               return false;
            }
         } else {
            return true;
         }
      }
   }

   @Override
   public void start() {
      this.golem.setAttackTarget(this.target);
      super.start();
   }
}
