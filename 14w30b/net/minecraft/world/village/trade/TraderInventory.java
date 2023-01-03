package net.minecraft.world.village.trade;

import net.minecraft.entity.living.mob.passive.Trader;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TraderInventory implements Inventory {
   private final Trader trader;
   private ItemStack[] stacks = new ItemStack[3];
   private final PlayerEntity player;
   private TradeOffer offer;
   private int offerIndex;

   public TraderInventory(PlayerEntity player, Trader trader) {
      this.player = player;
      this.trader = trader;
   }

   @Override
   public int getSize() {
      return this.stacks.length;
   }

   @Override
   public ItemStack getStack(int slot) {
      return this.stacks[slot];
   }

   @Override
   public ItemStack removeStack(int slot, int amount) {
      if (this.stacks[slot] != null) {
         if (slot == 2) {
            ItemStack var5 = this.stacks[slot];
            this.stacks[slot] = null;
            return var5;
         } else if (this.stacks[slot].size <= amount) {
            ItemStack var4 = this.stacks[slot];
            this.stacks[slot] = null;
            if (this.isInputSlot(slot)) {
               this.updateOffer();
            }

            return var4;
         } else {
            ItemStack var3 = this.stacks[slot].split(amount);
            if (this.stacks[slot].size == 0) {
               this.stacks[slot] = null;
            }

            if (this.isInputSlot(slot)) {
               this.updateOffer();
            }

            return var3;
         }
      } else {
         return null;
      }
   }

   private boolean isInputSlot(int slot) {
      return slot == 0 || slot == 1;
   }

   @Override
   public ItemStack removeStackQuietly(int slot) {
      if (this.stacks[slot] != null) {
         ItemStack var2 = this.stacks[slot];
         this.stacks[slot] = null;
         return var2;
      } else {
         return null;
      }
   }

   @Override
   public void setStack(int slot, ItemStack stack) {
      this.stacks[slot] = stack;
      if (stack != null && stack.size > this.getMaxStackSize()) {
         stack.size = this.getMaxStackSize();
      }

      if (this.isInputSlot(slot)) {
         this.updateOffer();
      }
   }

   @Override
   public String getName() {
      return "mob.villager";
   }

   @Override
   public boolean hasCustomName() {
      return false;
   }

   @Override
   public Text getDisplayName() {
      return (Text)(this.hasCustomName() ? new LiteralText(this.getName()) : new TranslatableText(this.getName()));
   }

   @Override
   public int getMaxStackSize() {
      return 64;
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      return this.trader.getCustomer() == player;
   }

   @Override
   public void onOpen(PlayerEntity player) {
   }

   @Override
   public void onClose(PlayerEntity player) {
   }

   @Override
   public boolean canSetStack(int slot, ItemStack stack) {
      return true;
   }

   @Override
   public void markDirty() {
      this.updateOffer();
   }

   public void updateOffer() {
      this.offer = null;
      ItemStack var1 = this.stacks[0];
      ItemStack var2 = this.stacks[1];
      if (var1 == null) {
         var1 = var2;
         var2 = null;
      }

      if (var1 == null) {
         this.setStack(2, null);
      } else {
         TradeOffers var3 = this.trader.getOffers(this.player);
         if (var3 != null) {
            TradeOffer var4 = var3.get(var1, var2, this.offerIndex);
            if (var4 != null && !var4.isDisabled()) {
               this.offer = var4;
               this.setStack(2, var4.getResult().copy());
            } else if (var2 != null) {
               var4 = var3.get(var2, var1, this.offerIndex);
               if (var4 != null && !var4.isDisabled()) {
                  this.offer = var4;
                  this.setStack(2, var4.getResult().copy());
               } else {
                  this.setStack(2, null);
               }
            } else {
               this.setStack(2, null);
            }
         }
      }

      this.trader.updateOffer(this.getStack(2));
   }

   public TradeOffer getOffer() {
      return this.offer;
   }

   public void setOffer(int index) {
      this.offerIndex = index;
      this.updateOffer();
   }

   @Override
   public int getData(int id) {
      return 0;
   }

   @Override
   public void setData(int id, int value) {
   }

   @Override
   public int getDataCount() {
      return 0;
   }

   @Override
   public void clear() {
      for(int var1 = 0; var1 < this.stacks.length; ++var1) {
         this.stacks[var1] = null;
      }
   }
}
