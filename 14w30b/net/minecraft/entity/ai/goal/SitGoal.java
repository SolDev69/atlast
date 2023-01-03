package net.minecraft.entity.ai.goal;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.passive.animal.tamable.TameableEntity;

public class SitGoal extends Goal {
   private TameableEntity pet;
   private boolean enabledWithOwner;

   public SitGoal(TameableEntity pet) {
      this.pet = pet;
      this.setControls(5);
   }

   @Override
   public boolean canStart() {
      if (!this.pet.isTamed()) {
         return false;
      } else if (this.pet.isInWater()) {
         return false;
      } else if (!this.pet.onGround) {
         return false;
      } else {
         LivingEntity var1 = this.pet.getOwner();
         if (var1 == null) {
            return true;
         } else {
            return this.pet.getSquaredDistanceTo(var1) < 144.0 && var1.getAttacker() != null ? false : this.enabledWithOwner;
         }
      }
   }

   @Override
   public void start() {
      this.pet.getNavigation().stopCurrentNavigation();
      this.pet.setSitting(true);
   }

   @Override
   public void stop() {
      this.pet.setSitting(false);
   }

   public void setEnabledWithOwner(boolean enabledWithOwner) {
      this.enabledWithOwner = enabledWithOwner;
   }
}
