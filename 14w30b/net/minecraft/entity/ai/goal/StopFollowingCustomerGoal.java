package net.minecraft.entity.ai.goal;

import net.minecraft.entity.living.mob.passive.VillagerEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.menu.InventoryMenu;

public class StopFollowingCustomerGoal extends Goal {
   private VillagerEntity villager;

   public StopFollowingCustomerGoal(VillagerEntity villager) {
      this.villager = villager;
      this.setControls(5);
   }

   @Override
   public boolean canStart() {
      if (!this.villager.isAlive()) {
         return false;
      } else if (this.villager.isInWater()) {
         return false;
      } else if (!this.villager.onGround) {
         return false;
      } else if (this.villager.damaged) {
         return false;
      } else {
         PlayerEntity var1 = this.villager.getCustomer();
         if (var1 == null) {
            return false;
         } else if (this.villager.getSquaredDistanceTo(var1) > 16.0) {
            return false;
         } else {
            return var1.menu instanceof InventoryMenu;
         }
      }
   }

   @Override
   public void start() {
      this.villager.getNavigation().stopCurrentNavigation();
   }

   @Override
   public void stop() {
      this.villager.setCustomer(null);
   }
}
