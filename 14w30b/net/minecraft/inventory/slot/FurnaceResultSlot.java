package net.minecraft.inventory.slot;

import net.minecraft.entity.XpOrbEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.smelting.SmeltingManager;
import net.minecraft.stat.achievement.Achievements;
import net.minecraft.util.math.MathHelper;

public class FurnaceResultSlot extends InventorySlot {
   private PlayerEntity player;
   private int removeAmount;

   public FurnaceResultSlot(PlayerEntity player, Inventory resultInventory, int slot, int x, int y) {
      super(resultInventory, slot, x, y);
      this.player = player;
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
   public void onStackRemovedByPlayer(PlayerEntity player, ItemStack stack) {
      this.checkAchievements(stack);
      super.onStackRemovedByPlayer(player, stack);
   }

   @Override
   protected void onQuickMoved(ItemStack stack, int amount) {
      this.removeAmount += amount;
      this.checkAchievements(stack);
   }

   @Override
   protected void checkAchievements(ItemStack stack) {
      stack.onResult(this.player.world, this.player, this.removeAmount);
      if (!this.player.world.isClient) {
         int var2 = this.removeAmount;
         float var3 = SmeltingManager.getInstance().getXp(stack);
         if (var3 == 0.0F) {
            var2 = 0;
         } else if (var3 < 1.0F) {
            int var4 = MathHelper.floor((float)var2 * var3);
            if (var4 < MathHelper.ceil((float)var2 * var3) && Math.random() < (double)((float)var2 * var3 - (float)var4)) {
               ++var4;
            }

            var2 = var4;
         }

         while(var2 > 0) {
            int var5 = XpOrbEntity.roundSize(var2);
            var2 -= var5;
            this.player.world.addEntity(new XpOrbEntity(this.player.world, this.player.x, this.player.y + 0.5, this.player.z + 0.5, var5));
         }
      }

      this.removeAmount = 0;
      if (stack.getItem() == Items.IRON_INGOT) {
         this.player.incrementStat(Achievements.GET_IRON_INGOT);
      }

      if (stack.getItem() == Items.COOKED_FISH) {
         this.player.incrementStat(Achievements.COOK_FISH);
      }
   }
}
