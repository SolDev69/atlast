package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.entity.living.mob.passive.animal.AnimalEntity;

public class FollowParentGoal extends Goal {
   AnimalEntity childAnimal;
   AnimalEntity parentAnimalEntity;
   double speed;
   private int delay;

   public FollowParentGoal(AnimalEntity childAnimal, double speed) {
      this.childAnimal = childAnimal;
      this.speed = speed;
   }

   @Override
   public boolean canStart() {
      if (this.childAnimal.getBreedingAge() >= 0) {
         return false;
      } else {
         List var1 = this.childAnimal.world.getEntities(this.childAnimal.getClass(), this.childAnimal.getBoundingBox().expand(8.0, 4.0, 8.0));
         AnimalEntity var2 = null;
         double var3 = Double.MAX_VALUE;

         for(AnimalEntity var6 : var1) {
            if (var6.getBreedingAge() >= 0) {
               double var7 = this.childAnimal.getSquaredDistanceTo(var6);
               if (!(var7 > var3)) {
                  var3 = var7;
                  var2 = var6;
               }
            }
         }

         if (var2 == null) {
            return false;
         } else if (var3 < 9.0) {
            return false;
         } else {
            this.parentAnimalEntity = var2;
            return true;
         }
      }
   }

   @Override
   public boolean shouldContinue() {
      if (this.childAnimal.getBreedingAge() >= 0) {
         return false;
      } else if (!this.parentAnimalEntity.isAlive()) {
         return false;
      } else {
         double var1 = this.childAnimal.getSquaredDistanceTo(this.parentAnimalEntity);
         return !(var1 < 9.0) && !(var1 > 256.0);
      }
   }

   @Override
   public void start() {
      this.delay = 0;
   }

   @Override
   public void stop() {
      this.parentAnimalEntity = null;
   }

   @Override
   public void tick() {
      if (--this.delay <= 0) {
         this.delay = 10;
         this.childAnimal.getNavigation().startMovingTo(this.parentAnimalEntity, this.speed);
      }
   }
}
