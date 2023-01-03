package net.minecraft.entity.ai.goal;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.passive.animal.tamable.TameableEntity;

public class AttackWithOwnerGoal extends TrackTargetGoal {
   TameableEntity pet;
   LivingEntity targetEntity;
   private int lasAttackTime;

   public AttackWithOwnerGoal(TameableEntity pet) {
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
            this.targetEntity = var1.getAttacker();
            int var2 = var1.getLastAttackedTime();
            return var2 != this.lasAttackTime && this.canTarget(this.targetEntity, false) && this.pet.shouldAttack(this.targetEntity, var1);
         }
      }
   }

   @Override
   public void start() {
      this.entity.setAttackTarget(this.targetEntity);
      LivingEntity var1 = this.pet.getOwner();
      if (var1 != null) {
         this.lasAttackTime = var1.getLastAttackedTime();
      }

      super.start();
   }
}
