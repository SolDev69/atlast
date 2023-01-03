package net.minecraft.inventory.slot;

import net.minecraft.block.Blocks;
import net.minecraft.crafting.CraftingManager;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.stat.achievement.Achievements;

public class CraftingResultSlot extends InventorySlot {
   private final CraftingInventory craftingInventory;
   private final PlayerEntity player;
   private int removeAmount;

   public CraftingResultSlot(PlayerEntity player, CraftingInventory craftingInventory, Inventory resultInventory, int slot, int x, int y) {
      super(resultInventory, slot, x, y);
      this.player = player;
      this.craftingInventory = craftingInventory;
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
      if (this.removeAmount > 0) {
         stack.onResult(this.player.world, this.player, this.removeAmount);
      }

      this.removeAmount = 0;
      if (stack.getItem() == Item.byBlock(Blocks.CRAFTING_TABLE)) {
         this.player.incrementStat(Achievements.CRAFT_CRAFTING_TABLE);
      }

      if (stack.getItem() instanceof PickaxeItem) {
         this.player.incrementStat(Achievements.CRAFT_PICKAXE);
      }

      if (stack.getItem() == Item.byBlock(Blocks.FURNACE)) {
         this.player.incrementStat(Achievements.CRAFT_FURNACE);
      }

      if (stack.getItem() instanceof HoeItem) {
         this.player.incrementStat(Achievements.CRAFT_WOODEN_HOE);
      }

      if (stack.getItem() == Items.BREAD) {
         this.player.incrementStat(Achievements.CRAFT_BREAD);
      }

      if (stack.getItem() == Items.CAKE) {
         this.player.incrementStat(Achievements.CRAFT_CAKE);
      }

      if (stack.getItem() instanceof PickaxeItem && ((PickaxeItem)stack.getItem()).getMaterial() != Item.ToolMaterial.WOOD) {
         this.player.incrementStat(Achievements.CRAFT_BETTER_PICKAXE);
      }

      if (stack.getItem() instanceof SwordItem) {
         this.player.incrementStat(Achievements.CRAFT_SWORD);
      }

      if (stack.getItem() == Item.byBlock(Blocks.ENCHANTING_TABLE)) {
         this.player.incrementStat(Achievements.CRAFT_ENCHANTING_TABLE);
      }

      if (stack.getItem() == Item.byBlock(Blocks.BOOKSHELF)) {
         this.player.incrementStat(Achievements.CRAFT_BOOKSHELF);
      }

      if (stack.getItem() == Items.GOLDEN_APPLE && stack.getMetadata() == 1) {
         this.player.incrementStat(Achievements.EAT_ENCHANTED_GOLDEN_APPLE);
      }
   }

   @Override
   public void onStackRemovedByPlayer(PlayerEntity player, ItemStack stack) {
      this.checkAchievements(stack);
      ItemStack[] var3 = CraftingManager.getInstance().getRemainder(this.craftingInventory, player.world);

      for(int var4 = 0; var4 < var3.length; ++var4) {
         ItemStack var5 = this.craftingInventory.getStack(var4);
         ItemStack var6 = var3[var4];
         if (var5 != null) {
            this.craftingInventory.removeStack(var4, 1);
         }

         if (var6 != null) {
            if (this.craftingInventory.getStack(var4) == null) {
               this.craftingInventory.setStack(var4, var6);
            } else if (!this.player.inventory.insertStack(var6)) {
               this.player.dropItem(var6, false);
            }
         }
      }
   }
}
