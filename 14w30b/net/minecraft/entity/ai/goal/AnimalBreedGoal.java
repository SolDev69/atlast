package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.XpOrbEntity;
import net.minecraft.entity.living.mob.passive.PassiveEntity;
import net.minecraft.entity.living.mob.passive.animal.AnimalEntity;
import net.minecraft.entity.living.mob.passive.animal.CowEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.stat.Stats;
import net.minecraft.stat.achievement.Achievements;
import net.minecraft.world.World;

public class AnimalBreedGoal extends Goal {
   private AnimalEntity animal;
   World world;
   private AnimalEntity animalTwo;
   int breedTimer;
   double speedModifier;

   public AnimalBreedGoal(AnimalEntity animal, double speedModifier) {
      this.animal = animal;
      this.world = animal.world;
      this.speedModifier = speedModifier;
      this.setControls(3);
   }

   @Override
   public boolean canStart() {
      if (!this.animal.isInLove()) {
         return false;
      } else {
         this.animalTwo = this.findMate();
         return this.animalTwo != null;
      }
   }

   @Override
   public boolean shouldContinue() {
      return this.animalTwo.isAlive() && this.animalTwo.isInLove() && this.breedTimer < 60;
   }

   @Override
   public void stop() {
      this.animalTwo = null;
      this.breedTimer = 0;
   }

   @Override
   public void tick() {
      this.animal.getLookControl().setLookatValues(this.animalTwo, 10.0F, (float)this.animal.getLookPitchSpeed());
      this.animal.getNavigation().startMovingTo(this.animalTwo, this.speedModifier);
      ++this.breedTimer;
      if (this.breedTimer >= 60 && this.animal.getSquaredDistanceTo(this.animalTwo) < 9.0) {
         this.breed();
      }
   }

   private AnimalEntity findMate() {
      float var1 = 8.0F;
      List var2 = this.world.getEntities(this.animal.getClass(), this.animal.getBoundingBox().expand((double)var1, (double)var1, (double)var1));
      double var3 = Double.MAX_VALUE;
      AnimalEntity var5 = null;

      for(AnimalEntity var7 : var2) {
         if (this.animal.canBreedWith(var7) && this.animal.getSquaredDistanceTo(var7) < var3) {
            var5 = var7;
            var3 = this.animal.getSquaredDistanceTo(var7);
         }
      }

      return var5;
   }

   private void breed() {
      PassiveEntity var1 = this.animal.makeChild(this.animalTwo);
      if (var1 != null) {
         PlayerEntity var2 = this.animal.getLoveCausingPlayer();
         if (var2 == null && this.animalTwo.getLoveCausingPlayer() != null) {
            var2 = this.animalTwo.getLoveCausingPlayer();
         }

         if (var2 != null) {
            var2.incrementStat(Stats.ANIMALS_BRED);
            if (this.animal instanceof CowEntity) {
               var2.incrementStat(Achievements.BREED_COW);
            }
         }

         this.animal.setBreedingAge(6000);
         this.animalTwo.setBreedingAge(6000);
         this.animal.resetLoveTicks();
         this.animalTwo.resetLoveTicks();
         var1.setBreedingAge(-24000);
         var1.refreshPositionAndAngles(this.animal.x, this.animal.y, this.animal.z, 0.0F, 0.0F);
         this.world.addEntity(var1);
         Random var3 = this.animal.getRandom();

         for(int var4 = 0; var4 < 7; ++var4) {
            double var5 = var3.nextGaussian() * 0.02;
            double var7 = var3.nextGaussian() * 0.02;
            double var9 = var3.nextGaussian() * 0.02;
            this.world
               .addParticle(
                  ParticleType.HEART,
                  this.animal.x + (double)(var3.nextFloat() * this.animal.width * 2.0F) - (double)this.animal.width,
                  this.animal.y + 0.5 + (double)(var3.nextFloat() * this.animal.height),
                  this.animal.z + (double)(var3.nextFloat() * this.animal.width * 2.0F) - (double)this.animal.width,
                  var5,
                  var7,
                  var9
               );
         }

         if (this.world.getGameRules().getBoolean("doMobLoot")) {
            this.world.addEntity(new XpOrbEntity(this.world, this.animal.x, this.animal.y, this.animal.z, var3.nextInt(7) + 1));
         }
      }
   }
}
