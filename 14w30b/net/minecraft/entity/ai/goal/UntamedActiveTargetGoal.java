package net.minecraft.entity.ai.goal;

import net.minecraft.entity.living.mob.passive.animal.tamable.TameableEntity;

public class UntamedActiveTargetGoal extends ActiveTargetGoal {
   private TameableEntity pet;

   public UntamedActiveTargetGoal(TameableEntity c_59mecpana, Class class_, boolean bl) {
      super(c_59mecpana, class_, bl);
      this.pet = c_59mecpana;
   }

   @Override
   public boolean canStart() {
      return !this.pet.isTamed() && super.canStart();
   }
}
