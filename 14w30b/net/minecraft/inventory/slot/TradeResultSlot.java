package net.minecraft.inventory.slot;

import net.minecraft.entity.living.mob.passive.Trader;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.world.village.trade.TradeOffer;
import net.minecraft.world.village.trade.TraderInventory;

public class TradeResultSlot extends InventorySlot {
   private final TraderInventory traderInventory;
   private PlayerEntity player;
   private int removeAmount;
   private final Trader trader;

   public TradeResultSlot(PlayerEntity player, Trader trader, TraderInventory traderInventory, int slot, int x, int y) {
      super(traderInventory, slot, x, y);
      this.player = player;
      this.trader = trader;
      this.traderInventory = traderInventory;
   }

   @Override
   public boolean canSetStack(ItemStack stack) {
      return false;
   }

   @Override
   public ItemStack removeStack(int amount) {
      if (this.hasStack()) {
         this.removeAmount += Math.min(amount, this.getStack().size);
      }

      return super.removeStack(amount);
   }

   @Override
   protected void onQuickMoved(ItemStack stack, int amount) {
      this.removeAmount += amount;
      this.checkAchievements(stack);
   }

   @Override
   protected void checkAchievements(ItemStack stack) {
      stack.onResult(this.player.world, this.player, this.removeAmount);
      this.removeAmount = 0;
   }

   @Override
   public void onStackRemovedByPlayer(PlayerEntity player, ItemStack stack) {
      this.checkAchievements(stack);
      TradeOffer var3 = this.traderInventory.getOffer();
      if (var3 != null) {
         ItemStack var4 = this.traderInventory.getStack(0);
         ItemStack var5 = this.traderInventory.getStack(1);
         if (this.acceptPayment(var3, var4, var5) || this.acceptPayment(var3, var5, var4)) {
            this.trader.trade(var3);
            player.incrementStat(Stats.TRADED_WITH_VILLAGER);
            if (var4 != null && var4.size <= 0) {
               var4 = null;
            }

            if (var5 != null && var5.size <= 0) {
               var5 = null;
            }

            this.traderInventory.setStack(0, var4);
            this.traderInventory.setStack(1, var5);
         }
      }
   }

   private boolean acceptPayment(TradeOffer offer, ItemStack primaryPayment, ItemStack secondaryPayment) {
      ItemStack var4 = offer.getPrimaryPayment();
      ItemStack var5 = offer.getSecondaryPayment();
      if (primaryPayment != null && primaryPayment.getItem() == var4.getItem()) {
         if (var5 != null && secondaryPayment != null && var5.getItem() == secondaryPayment.getItem()) {
            primaryPayment.size -= var4.size;
            secondaryPayment.size -= var5.size;
            return true;
         }

         if (var5 == null && secondaryPayment == null) {
            primaryPayment.size -= var4.size;
            return true;
         }
      }

      return false;
   }
}
