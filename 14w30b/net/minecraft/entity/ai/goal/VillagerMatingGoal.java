package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.mob.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.village.Village;

public class VillagerMatingGoal extends Goal {
   private VillagerEntity villagerOne;
   private VillagerEntity villagerTwo;
   private World world;
   private int breedTimer;
   Village village;

   public VillagerMatingGoal(VillagerEntity villager) {
      this.villagerOne = villager;
      this.world = villager.world;
      this.setControls(3);
   }

   @Override
   public boolean canStart() {
      if (this.villagerOne.getBreedingAge() != 0) {
         return false;
      } else if (this.villagerOne.getRandom().nextInt(500) != 0) {
         return false;
      } else {
         this.village = this.world.getVillageData().getClosestVillage(new BlockPos(this.villagerOne), 0);
         if (this.village == null) {
            return false;
         } else if (this.checkVillageValidity() && this.villagerOne.m_59wrmvuvt(true)) {
            Entity var1 = this.world.getClosestEntity(VillagerEntity.class, this.villagerOne.getBoundingBox().expand(8.0, 3.0, 8.0), this.villagerOne);
            if (var1 == null) {
               return false;
            } else {
               this.villagerTwo = (VillagerEntity)var1;
               return this.villagerTwo.getBreedingAge() == 0 && this.villagerTwo.m_59wrmvuvt(true);
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public void start() {
      this.breedTimer = 300;
      this.villagerOne.setMating(true);
   }

   @Override
   public void stop() {
      this.village = null;
      this.villagerTwo = null;
      this.villagerOne.setMating(false);
   }

   @Override
   public boolean shouldContinue() {
      return this.breedTimer >= 0 && this.checkVillageValidity() && this.villagerOne.getBreedingAge() == 0 && this.villagerOne.m_59wrmvuvt(false);
   }

   @Override
   public void tick() {
      --this.breedTimer;
      this.villagerOne.getLookControl().setLookatValues(this.villagerTwo, 10.0F, 30.0F);
      if (this.villagerOne.getSquaredDistanceTo(this.villagerTwo) > 2.25) {
         this.villagerOne.getNavigation().startMovingTo(this.villagerTwo, 0.25);
      } else if (this.breedTimer == 0 && this.villagerTwo.getMating()) {
         this.breed();
      }

      if (this.villagerOne.getRandom().nextInt(35) == 0) {
         this.world.doEntityEvent(this.villagerOne, (byte)12);
      }
   }

   private boolean checkVillageValidity() {
      if (!this.village.canMate()) {
         return false;
      } else {
         int var1 = (int)((double)((float)this.village.getDoorCount()) * 0.35);
         return this.village.getPopulationSize() < var1;
      }
   }

   private void breed() {
      VillagerEntity var1 = this.villagerOne.makeChild(this.villagerTwo);
      this.villagerTwo.setBreedingAge(6000);
      this.villagerOne.setBreedingAge(6000);
      this.villagerTwo.m_07mjfzjua(false);
      this.villagerOne.m_07mjfzjua(false);
      var1.setBreedingAge(-24000);
      var1.refreshPositionAndAngles(this.villagerOne.x, this.villagerOne.y, this.villagerOne.z, 0.0F, 0.0F);
      this.world.addEntity(var1);
      this.world.doEntityEvent(var1, (byte)12);
   }
}
