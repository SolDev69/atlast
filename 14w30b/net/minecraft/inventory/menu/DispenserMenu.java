package net.minecraft.inventory.menu;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.ItemStack;

public class DispenserMenu extends InventoryMenu {
   private Inventory dispenser;

   public DispenserMenu(Inventory playerInventory, Inventory dispenser) {
      this.dispenser = dispenser;

      for(int var3 = 0; var3 < 3; ++var3) {
         for(int var4 = 0; var4 < 3; ++var4) {
            this.addSlot(new InventorySlot(dispenser, var4 + var3 * 3, 62 + var4 * 18, 17 + var3 * 18));
         }
      }

      for(int var5 = 0; var5 < 3; ++var5) {
         for(int var7 = 0; var7 < 9; ++var7) {
            this.addSlot(new InventorySlot(playerInventory, var7 + var5 * 9 + 9, 8 + var7 * 18, 84 + var5 * 18));
         }
      }

      for(int var6 = 0; var6 < 9; ++var6) {
         this.addSlot(new InventorySlot(playerInventory, var6, 8 + var6 * 18, 142));
      }
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      return this.dispenser.isValid(player);
   }

   @Override
   public ItemStack quickMoveStack(PlayerEntity player, int id) {
      ItemStack var3 = null;
      InventorySlot var4 = (InventorySlot)this.slots.get(id);
      if (var4 != null && var4.hasStack()) {
         ItemStack var5 = var4.getStack();
         var3 = var5.copy();
         if (id < 9) {
            if (!this.moveStack(var5, 9, 45, true)) {
               return null;
            }
         } else if (!this.moveStack(var5, 0, 9, false)) {
            return null;
         }

         if (var5.size == 0) {
            var4.setStack(null);
         } else {
            var4.markDirty();
         }

         if (var5.size == var3.size) {
            return null;
         }

         var4.onStackRemovedByPlayer(player, var5);
      }

      return var3;
   }
}
