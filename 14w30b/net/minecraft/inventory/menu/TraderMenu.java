package net.minecraft.inventory.menu;

import net.minecraft.entity.living.mob.passive.Trader;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.inventory.slot.TradeResultSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.village.trade.TraderInventory;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class TraderMenu extends InventoryMenu {
   private Trader trader;
   private TraderInventory traderInventory;
   private final World world;

   public TraderMenu(PlayerInventory playerInventory, Trader trader, World world) {
      this.trader = trader;
      this.world = world;
      this.traderInventory = new TraderInventory(playerInventory.player, trader);
      this.addSlot(new InventorySlot(this.traderInventory, 0, 36, 53));
      this.addSlot(new InventorySlot(this.traderInventory, 1, 62, 53));
      this.addSlot(new TradeResultSlot(playerInventory.player, trader, this.traderInventory, 2, 120, 53));

      for(int var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 9; ++var5) {
            this.addSlot(new InventorySlot(playerInventory, var5 + var4 * 9 + 9, 8 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(int var6 = 0; var6 < 9; ++var6) {
         this.addSlot(new InventorySlot(playerInventory, var6, 8 + var6 * 18, 142));
      }
   }

   public TraderInventory getTraderInventory() {
      return this.traderInventory;
   }

   @Override
   public void addListener(InventoryMenuListener listener) {
      super.addListener(listener);
   }

   @Override
   public void updateListeners() {
      super.updateListeners();
   }

   @Override
   public void onContentChanged(Inventory inventory) {
      this.traderInventory.updateOffer();
      super.onContentChanged(inventory);
   }

   public void setRecipeIndex(int index) {
      this.traderInventory.setOffer(index);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void setData(int id, int value) {
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      return this.trader.getCustomer() == player;
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
         } else if (id != 0 && id != 1) {
            if (id >= 3 && id < 30) {
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

   @Override
   public void close(PlayerEntity player) {
      super.close(player);
      this.trader.setCustomer(null);
      super.close(player);
      if (!this.world.isClient) {
         ItemStack var2 = this.traderInventory.removeStackQuietly(0);
         if (var2 != null) {
            player.dropItem(var2, false);
         }

         var2 = this.traderInventory.removeStackQuietly(1);
         if (var2 != null) {
            player.dropItem(var2, false);
         }
      }
   }
}
