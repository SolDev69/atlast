package net.minecraft.inventory.menu;

import net.minecraft.block.Blocks;
import net.minecraft.crafting.CraftingManager;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ResultInventory;
import net.minecraft.inventory.slot.CraftingResultSlot;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CraftingTableMenu extends InventoryMenu {
   public CraftingInventory craftingTable = new CraftingInventory(this, 3, 3);
   public Inventory resultInventory = new ResultInventory();
   private World world;
   private BlockPos pos;

   public CraftingTableMenu(PlayerInventory playerInventory, World world, BlockPos pos) {
      this.world = world;
      this.pos = pos;
      this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftingTable, this.resultInventory, 0, 124, 35));

      for(int var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 3; ++var5) {
            this.addSlot(new InventorySlot(this.craftingTable, var5 + var4 * 3, 30 + var5 * 18, 17 + var4 * 18));
         }
      }

      for(int var6 = 0; var6 < 3; ++var6) {
         for(int var8 = 0; var8 < 9; ++var8) {
            this.addSlot(new InventorySlot(playerInventory, var8 + var6 * 9 + 9, 8 + var8 * 18, 84 + var6 * 18));
         }
      }

      for(int var7 = 0; var7 < 9; ++var7) {
         this.addSlot(new InventorySlot(playerInventory, var7, 8 + var7 * 18, 142));
      }

      this.onContentChanged(this.craftingTable);
   }

   @Override
   public void onContentChanged(Inventory inventory) {
      this.resultInventory.setStack(0, CraftingManager.getInstance().getResult(this.craftingTable, this.world));
   }

   @Override
   public void close(PlayerEntity player) {
      super.close(player);
      if (!this.world.isClient) {
         for(int var2 = 0; var2 < 9; ++var2) {
            ItemStack var3 = this.craftingTable.removeStackQuietly(var2);
            if (var3 != null) {
               player.dropItem(var3, false);
            }
         }
      }
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      if (this.world.getBlockState(this.pos).getBlock() != Blocks.CRAFTING_TABLE) {
         return false;
      } else {
         return !(player.getSquaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
      }
   }

   @Override
   public ItemStack quickMoveStack(PlayerEntity player, int id) {
      ItemStack var3 = null;
      InventorySlot var4 = (InventorySlot)this.slots.get(id);
      if (var4 != null && var4.hasStack()) {
         ItemStack var5 = var4.getStack();
         var3 = var5.copy();
         if (id == 0) {
            if (!this.moveStack(var5, 10, 46, true)) {
               return null;
            }

            var4.onQuickMoved(var5, var3);
         } else if (id >= 10 && id < 37) {
            if (!this.moveStack(var5, 37, 46, false)) {
               return null;
            }
         } else if (id >= 37 && id < 46) {
            if (!this.moveStack(var5, 10, 37, false)) {
               return null;
            }
         } else if (!this.moveStack(var5, 10, 46, false)) {
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

   @Override
   public boolean canRemoveForPickupAll(ItemStack stack, InventorySlot invSlot) {
      return invSlot.inventory != this.resultInventory && super.canRemoveForPickupAll(stack, invSlot);
   }
}
