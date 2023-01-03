package net.minecraft.entity.ai.goal;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.passive.animal.tamable.TameableEntity;

public class OwnerHurtGoal extends TrackTargetGoal {
   TameableEntity pet;
   LivingEntity target;
   private int ownerLastAttackedTime;

   public OwnerHurtGoal(TameableEntity pet) {
      super(pet, false);
      this.pet = pet;
      this.setControls(1);
   }

   @Override
   public boolean canStart() {
      if (!this.pet.isTamed()) {
         return false;
      } else {
         LivingEntity var1 = this.pet.getOwner();
         if (var1 == null) {
            return false;
         } else {
            this.target = var1.getAttackTarget();
            int var2 = var1.getLastAttackTime();
            return var2 != this.ownerLastAttackedTime && this.canTarget(this.target, false) && this.pet.shouldAttack(this.target, var1);
         }
      }
   }

   @Override
   public void start() {
      this.entity.setAttackTarget(this.target);
      LivingEntity var1 = this.pet.getOwner();
      if (var1 != null) {
         this.ownerLastAttackedTime = var1.getLastAttackTime();
      }

      super.start();
   }
}
