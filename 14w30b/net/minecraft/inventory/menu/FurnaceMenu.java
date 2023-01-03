package net.minecraft.inventory.menu;

import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.FurnaceFuelSlot;
import net.minecraft.inventory.slot.FurnaceResultSlot;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.ItemStack;
import net.minecraft.smelting.SmeltingManager;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class FurnaceMenu extends InventoryMenu {
   private final Inventory furnace;
   private int cookTime;
   private int totalCookTime;
   private int fuelTime;
   private int totalFuelTime;

   public FurnaceMenu(PlayerInventory playerInventory, Inventory furnace) {
      this.furnace = furnace;
      this.addSlot(new InventorySlot(furnace, 0, 56, 17));
      this.addSlot(new FurnaceFuelSlot(furnace, 1, 56, 53));
      this.addSlot(new FurnaceResultSlot(playerInventory.player, furnace, 2, 116, 35));

      for(int var3 = 0; var3 < 3; ++var3) {
         for(int var4 = 0; var4 < 9; ++var4) {
            this.addSlot(new InventorySlot(playerInventory, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
         }
      }

      for(int var5 = 0; var5 < 9; ++var5) {
         this.addSlot(new InventorySlot(playerInventory, var5, 8 + var5 * 18, 142));
      }
   }

   @Override
   public void addListener(InventoryMenuListener listener) {
      super.addListener(listener);
      listener.updateData(this, this.furnace);
   }

   @Override
   public void updateListeners() {
      super.updateListeners();

      for(int var1 = 0; var1 < this.listeners.size(); ++var1) {
         InventoryMenuListener var2 = (InventoryMenuListener)this.listeners.get(var1);
         if (this.cookTime != this.furnace.getData(2)) {
            var2.onDataChanged(this, 2, this.furnace.getData(2));
         }

         if (this.fuelTime != this.furnace.getData(0)) {
            var2.onDataChanged(this, 0, this.furnace.getData(0));
         }

         if (this.totalFuelTime != this.furnace.getData(1)) {
            var2.onDataChanged(this, 1, this.furnace.getData(1));
         }

         if (this.totalCookTime != this.furnace.getData(3)) {
            var2.onDataChanged(this, 3, this.furnace.getData(3));
         }
      }

      this.cookTime = this.furnace.getData(2);
      this.fuelTime = this.furnace.getData(0);
      this.totalFuelTime = this.furnace.getData(1);
      this.totalCookTime = this.furnace.getData(3);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void setData(int id, int value) {
      this.furnace.setData(id, value);
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      return this.furnace.isValid(player);
   }

   @Override
   public ItemStack quickMoveStack(PlayerEntity player, int id) {
      ItemStack var3 = null;
      InventorySlot var4 = (InventorySlot)this.slots.get(id);
      if (var4 != null && var4.hasStack()) {
         ItemStack var5 = var4.getStack();
         var3 = var5.copy();
         if (id == 2) {
            if (!this.moveStack(var5, 3, 39, true)) {
               return null;
            }

            var4.onQuickMoved(var5, var3);
         } else if (id != 1 && id != 0) {
            if (SmeltingManager.getInstance().getResult(var5) != null) {
               if (!this.moveStack(var5, 0, 1, false)) {
                  return null;
               }
            } else if (FurnaceBlockEntity.isFuel(var5)) {
               if (!this.moveStack(var5, 1, 2, false)) {
                  return null;
               }
            } else if (id >= 3 && id < 30) {
               if (!this.moveStack(var5, 30, 39, false)) {
                  return null;
               }
            } else if (id >= 30 && id < 39 && !this.moveStack(var5, 3, 30, false)) {
               return null;
            }
         } else if (!this.moveStack(var5, 3, 39, false)) {
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
