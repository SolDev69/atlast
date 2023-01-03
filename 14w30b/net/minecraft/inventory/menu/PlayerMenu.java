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
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PlayerMenu extends InventoryMenu {
   public CraftingInventory craftingInventory = new CraftingInventory(this, 2, 2);
   public Inventory resultInventory = new ResultInventory();
   public boolean isServer;
   private final PlayerEntity player;

   public PlayerMenu(PlayerInventory playerInventory, boolean isServer, PlayerEntity player) {
      this.isServer = isServer;
      this.player = player;
      this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftingInventory, this.resultInventory, 0, 144, 36));

      for(int var4 = 0; var4 < 2; ++var4) {
         for(int var5 = 0; var5 < 2; ++var5) {
            this.addSlot(new InventorySlot(this.craftingInventory, var5 + var4 * 2, 88 + var5 * 18, 26 + var4 * 18));
         }
      }

      for(final int var6 = 0; var6 < 4; ++var6) {
         this.addSlot(new InventorySlot(playerInventory, playerInventory.getSize() - 1 - var6, 8, 8 + var6 * 18) {
            @Override
            public int getMaxStackSize() {
               return 1;
            }

            @Override
            public boolean canSetStack(ItemStack stack) {
               if (stack == null) {
                  return false;
               } else if (stack.getItem() instanceof ArmorItem) {
                  return ((ArmorItem)stack.getItem()).slot == var6;
               } else if (stack.getItem() != Item.byBlock(Blocks.PUMPKIN) && stack.getItem() != Items.SKULL) {
                  return false;
               } else {
                  return var6 == 0;
               }
            }

            @Environment(EnvType.CLIENT)
            @Override
            public String getTexture() {
               return ArmorItem.EMPTY_SLOTS[var6];
            }
         });
      }

      for(int var7 = 0; var7 < 3; ++var7) {
         for(int var9 = 0; var9 < 9; ++var9) {
            this.addSlot(new InventorySlot(playerInventory, var9 + (var7 + 1) * 9, 8 + var9 * 18, 84 + var7 * 18));
         }
      }

      for(int var8 = 0; var8 < 9; ++var8) {
         this.addSlot(new InventorySlot(playerInventory, var8, 8 + var8 * 18, 142));
      }

      this.onContentChanged(this.craftingInventory);
   }

   @Override
   public void onContentChanged(Inventory inventory) {
      this.resultInventory.setStack(0, CraftingManager.getInstance().getResult(this.craftingInventory, this.player.world));
   }

   @Override
   public void close(PlayerEntity player) {
      super.close(player);

      for(int var2 = 0; var2 < 4; ++var2) {
         ItemStack var3 = this.craftingInventory.removeStackQuietly(var2);
         if (var3 != null) {
            player.dropItem(var3, false);
         }
      }

      this.resultInventory.setStack(0, null);
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      return true;
   }

   @Override
   public ItemStack quickMoveStack(PlayerEntity player, int id) {
      ItemStack var3 = null;
      InventorySlot var4 = (InventorySlot)this.slots.get(id);
      if (var4 != null && var4.hasStack()) {
         ItemStack var5 = var4.getStack();
         var3 = var5.copy();
         if (id == 0) {
            if (!this.moveStack(var5, 9, 45, true)) {
               return null;
            }

            var4.onQuickMoved(var5, var3);
         } else if (id >= 1 && id < 5) {
            if (!this.moveStack(var5, 9, 45, false)) {
               return null;
            }
         } else if (id >= 5 && id < 9) {
            if (!this.moveStack(var5, 9, 45, false)) {
               return null;
            }
         } else if (var3.getItem() instanceof ArmorItem && !((InventorySlot)this.slots.get(5 + ((ArmorItem)var3.getItem()).slot)).hasStack()) {
            int var6 = 5 + ((ArmorItem)var3.getItem()).slot;
            if (!this.moveStack(var5, var6, var6 + 1, false)) {
               return null;
            }
         } else if (id >= 9 && id < 36) {
            if (!this.moveStack(var5, 36, 45, false)) {
               return null;
            }
         } else if (id >= 36 && id < 45) {
            if (!this.moveStack(var5, 9, 36, false)) {
               return null;
            }
         } else if (!this.moveStack(var5, 9, 45, false)) {
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
