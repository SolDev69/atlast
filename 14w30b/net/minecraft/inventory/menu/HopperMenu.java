package net.minecraft.inventory.menu;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.ItemStack;

public class HopperMenu extends InventoryMenu {
   private final Inventory hopper;

   public HopperMenu(PlayerInventory playerInventory, Inventory hopper, PlayerEntity player) {
      this.hopper = hopper;
      hopper.onOpen(player);
      byte var4 = 51;

      for(int var5 = 0; var5 < hopper.getSize(); ++var5) {
         this.addSlot(new InventorySlot(hopper, var5, 44 + var5 * 18, 20));
      }

      for(int var7 = 0; var7 < 3; ++var7) {
         for(int var6 = 0; var6 < 9; ++var6) {
            this.addSlot(new InventorySlot(playerInventory, var6 + var7 * 9 + 9, 8 + var6 * 18, var7 * 18 + var4));
         }
      }

      for(int var8 = 0; var8 < 9; ++var8) {
         this.addSlot(new InventorySlot(playerInventory, var8, 8 + var8 * 18, 58 + var4));
      }
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      return this.hopper.isValid(player);
   }

   @Override
   public ItemStack quickMoveStack(PlayerEntity player, int id) {
      ItemStack var3 = null;
      InventorySlot var4 = (InventorySlot)this.slots.get(id);
      if (var4 != null && var4.hasStack()) {
         ItemStack var5 = var4.getStack();
         var3 = var5.copy();
         if (id < this.hopper.getSize()) {
            if (!this.moveStack(var5, this.hopper.getSize(), this.slots.size(), true)) {
               return null;
            }
         } else if (!this.moveStack(var5, 0, this.hopper.getSize(), false)) {
            return null;
         }

         if (var5.size == 0) {
            var4.setStack(null);
         } else {
            var4.markDirty();
         }
      }

      return var3;
   }

   @Override
   public void close(PlayerEntity player) {
      super.close(player);
      this.hopper.onClose(player);
   }
}
