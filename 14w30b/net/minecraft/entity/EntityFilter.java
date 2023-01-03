package net.minecraft.entity;

import com.google.common.base.Predicate;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public final class EntityFilter {
   public static final Predicate ALIVE = new Predicate() {
      public boolean apply(Entity c_47ldwddrb) {
         return c_47ldwddrb.isAlive();
      }
   };
   public static final Predicate NOT_RIDING = new Predicate() {
      public boolean apply(Entity c_47ldwddrb) {
         return c_47ldwddrb.isAlive() && c_47ldwddrb.rider == null && c_47ldwddrb.vehicle == null;
      }
   };
   public static final Predicate INVENTORY = new Predicate() {
      public boolean apply(Entity c_47ldwddrb) {
         return c_47ldwddrb instanceof Inventory && c_47ldwddrb.isAlive();
      }
   };
   public static final Predicate NOT_SPECTATOR = new Predicate() {
      public boolean apply(Entity c_47ldwddrb) {
         return !(c_47ldwddrb instanceof PlayerEntity) || !((PlayerEntity)c_47ldwddrb).isSpectator();
      }
   };

   public static class CanPickupItemsFilter implements Predicate {
      private final ItemStack itemStack;

      public CanPickupItemsFilter(ItemStack stack) {
         this.itemStack = stack;
      }

      public boolean apply(Entity c_47ldwddrb) {
         if (!c_47ldwddrb.isAlive()) {
            return false;
         } else if (!(c_47ldwddrb instanceof LivingEntity)) {
            return false;
         } else {
            LivingEntity var2 = (LivingEntity)c_47ldwddrb;
            if (var2.getStackInInventory(MobEntity.getSlotForEquipment(this.itemStack)) != null) {
               return false;
            } else if (var2 instanceof MobEntity) {
               return ((MobEntity)var2).canPickupLoot();
            } else {
               return var2 instanceof PlayerEntity;
            }
         }
      }
   }
}
