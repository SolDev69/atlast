package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.living.mob.MobEntity;

public class SwimGoal extends Goal {
   private MobEntity entity;

   public SwimGoal(MobEntity mob) {
      this.entity = mob;
      this.setControls(4);
      ((MobEntityNavigation)mob.getNavigation()).setCanSwim(true);
   }

   @Override
   public boolean canStart() {
      return this.entity.isInWater() || this.entity.isInLava();
   }

   @Override
   public void tick() {
      if (this.entity.getRandom().nextFloat() < 0.8F) {
         this.entity.getJumpControl().setActive();
      }
   }
}
