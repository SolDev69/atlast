package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.living.mob.PathAwareEntity;

public class AvoidSunlightGoal extends Goal {
   private PathAwareEntity entity;

   public AvoidSunlightGoal(PathAwareEntity entity) {
      this.entity = entity;
   }

   @Override
   public boolean canStart() {
      return this.entity.world.isSunny();
   }

   @Override
   public void start() {
      ((MobEntityNavigation)this.entity.getNavigation()).setAvoidSunLight(true);
   }

   @Override
   public void stop() {
      ((MobEntityNavigation)this.entity.getNavigation()).setAvoidSunLight(false);
   }
}
