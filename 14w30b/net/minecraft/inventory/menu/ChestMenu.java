package net.minecraft.inventory.menu;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.ItemStack;

public class ChestMenu extends InventoryMenu {
   private Inventory chest;
   private int rows;

   public ChestMenu(Inventory chest, Inventory playerInventory, PlayerEntity player) {
      this.chest = playerInventory;
      this.rows = playerInventory.getSize() / 9;
      playerInventory.onOpen(player);
      int var4 = (this.rows - 4) * 18;

      for(int var5 = 0; var5 < this.rows; ++var5) {
         for(int var6 = 0; var6 < 9; ++var6) {
            this.addSlot(new InventorySlot(playerInventory, var6 + var5 * 9, 8 + var6 * 18, 18 + var5 * 18));
         }
      }

      for(int var7 = 0; var7 < 3; ++var7) {
         for(int var9 = 0; var9 < 9; ++var9) {
            this.addSlot(new InventorySlot(chest, var9 + var7 * 9 + 9, 8 + var9 * 18, 103 + var7 * 18 + var4));
         }
      }

      for(int var8 = 0; var8 < 9; ++var8) {
         this.addSlot(new InventorySlot(chest, var8, 8 + var8 * 18, 161 + var4));
      }
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      return this.chest.isValid(player);
   }

   @Override
   public ItemStack quickMoveStack(PlayerEntity player, int id) {
      ItemStack var3 = null;
      InventorySlot var4 = (InventorySlot)this.slots.get(id);
      if (var4 != null && var4.hasStack()) {
         ItemStack var5 = var4.getStack();
         var3 = var5.copy();
         if (id < this.rows * 9) {
            if (!this.moveStack(var5, this.rows * 9, this.slots.size(), true)) {
               return null;
            }
         } else if (!this.moveStack(var5, 0, this.rows * 9, false)) {
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
      this.chest.onClose(player);
   }

   public Inventory getChest() {
      return this.chest;
   }
}
