package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.player.PlayerEntity;

public class LookAtEntityGoal extends Goal {
   protected MobEntity entity;
   protected Entity targetEntity;
   protected float range;
   private int lookTime;
   private float chance;
   protected Class tagetType;

   public LookAtEntityGoal(MobEntity mob, Class targetType, float range) {
      this.entity = mob;
      this.tagetType = targetType;
      this.range = range;
      this.chance = 0.02F;
      this.setControls(2);
   }

   public LookAtEntityGoal(MobEntity c_81psrrogw, Class class_, float f, float g) {
      this.entity = c_81psrrogw;
      this.tagetType = class_;
      this.range = f;
      this.chance = g;
      this.setControls(2);
   }

   @Override
   public boolean canStart() {
      if (this.entity.getRandom().nextFloat() >= this.chance) {
         return false;
      } else {
         if (this.entity.getTargetEntity() != null) {
            this.targetEntity = this.entity.getTargetEntity();
         }

         if (this.tagetType == PlayerEntity.class) {
            this.targetEntity = this.entity.world.getClosestPlayer(this.entity, (double)this.range);
         } else {
            this.targetEntity = this.entity
               .world
               .getClosestEntity(this.tagetType, this.entity.getBoundingBox().expand((double)this.range, 3.0, (double)this.range), this.entity);
         }

         return this.targetEntity != null;
      }
   }

   @Override
   public boolean shouldContinue() {
      if (!this.targetEntity.isAlive()) {
         return false;
      } else if (this.entity.getSquaredDistanceTo(this.targetEntity) > (double)(this.range * this.range)) {
         return false;
      } else {
         return this.lookTime > 0;
      }
   }

   @Override
   public void start() {
      this.lookTime = 40 + this.entity.getRandom().nextInt(40);
   }

   @Override
   public void stop() {
      this.targetEntity = null;
   }

   @Override
   public void tick() {
      this.entity
         .getLookControl()
         .lookAt(
            this.targetEntity.x,
            this.targetEntity.y + (double)this.targetEntity.getEyeHeight(),
            this.targetEntity.z,
            10.0F,
            (float)this.entity.getLookPitchSpeed()
         );
      --this.lookTime;
   }
}
