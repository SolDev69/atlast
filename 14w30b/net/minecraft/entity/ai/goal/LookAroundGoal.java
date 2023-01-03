package net.minecraft.entity.ai.goal;

import net.minecraft.entity.living.mob.MobEntity;

public class LookAroundGoal extends Goal {
   private MobEntity entity;
   private double deltaX;
   private double deltaZ;
   private int lookTime;

   public LookAroundGoal(MobEntity mob) {
      this.entity = mob;
      this.setControls(3);
   }

   @Override
   public boolean canStart() {
      return this.entity.getRandom().nextFloat() < 0.02F;
   }

   @Override
   public boolean shouldContinue() {
      return this.lookTime >= 0;
   }

   @Override
   public void start() {
      double var1 = (Math.PI * 2) * this.entity.getRandom().nextDouble();
      this.deltaX = Math.cos(var1);
      this.deltaZ = Math.sin(var1);
      this.lookTime = 20 + this.entity.getRandom().nextInt(20);
   }

   @Override
   public void tick() {
      --this.lookTime;
      this.entity
         .getLookControl()
         .lookAt(
            this.entity.x + this.deltaX,
            this.entity.y + (double)this.entity.getEyeHeight(),
            this.entity.z + this.deltaZ,
            10.0F,
            (float)this.entity.getLookPitchSpeed()
         );
   }
}
