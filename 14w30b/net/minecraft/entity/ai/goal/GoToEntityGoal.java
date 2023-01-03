package net.minecraft.entity.ai.goal;

import net.minecraft.entity.living.mob.MobEntity;

public class GoToEntityGoal extends LookAtEntityGoal {
   public GoToEntityGoal(MobEntity c_81psrrogw, Class class_, float f, float g) {
      super(c_81psrrogw, class_, f, g);
      this.setControls(3);
   }
}
