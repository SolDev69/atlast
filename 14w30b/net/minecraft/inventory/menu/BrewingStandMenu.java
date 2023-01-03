package net.minecraft.inventory.menu;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.achievement.Achievements;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BrewingStandMenu extends InventoryMenu {
   private Inventory brewingStand;
   private final InventorySlot ingredientSlot;
   private int timer;

   public BrewingStandMenu(PlayerInventory playerInventory, Inventory brewingStand) {
      this.brewingStand = brewingStand;
      this.addSlot(new BrewingStandMenu.PotionSlot(playerInventory.player, brewingStand, 0, 56, 46));
      this.addSlot(new BrewingStandMenu.PotionSlot(playerInventory.player, brewingStand, 1, 79, 53));
      this.addSlot(new BrewingStandMenu.PotionSlot(playerInventory.player, brewingStand, 2, 102, 46));
      this.ingredientSlot = this.addSlot(new BrewingStandMenu.IngredientSlot(brewingStand, 3, 79, 17));

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
      listener.updateData(this, this.brewingStand);
   }

   @Override
   public void updateListeners() {
      super.updateListeners();

      for(int var1 = 0; var1 < this.listeners.size(); ++var1) {
         InventoryMenuListener var2 = (InventoryMenuListener)this.listeners.get(var1);
         if (this.timer != this.brewingStand.getData(0)) {
            var2.onDataChanged(this, 0, this.brewingStand.getData(0));
         }
      }

      this.timer = this.brewingStand.getData(0);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void setData(int id, int value) {
      this.brewingStand.setData(id, value);
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      return this.brewingStand.isValid(player);
   }

   @Override
   public ItemStack quickMoveStack(PlayerEntity player, int id) {
      ItemStack var3 = null;
      InventorySlot var4 = (InventorySlot)this.slots.get(id);
      if (var4 != null && var4.hasStack()) {
         ItemStack var5 = var4.getStack();
         var3 = var5.copy();
         if ((id < 0 || id > 2) && id != 3) {
            if (!this.ingredientSlot.hasStack() && this.ingredientSlot.canSetStack(var5)) {
               if (!this.moveStack(var5, 3, 4, false)) {
                  return null;
               }
            } else if (BrewingStandMenu.PotionSlot.matches(var3)) {
               if (!this.moveStack(var5, 0, 3, false)) {
                  return null;
               }
            } else if (id >= 4 && id < 31) {
               if (!this.moveStack(var5, 31, 40, false)) {
                  return null;
               }
            } else if (id >= 31 && id < 40) {
               if (!this.moveStack(var5, 4, 31, false)) {
                  return null;
               }
            } else if (!this.moveStack(var5, 4, 40, false)) {
               return null;
            }
         } else {
            if (!this.moveStack(var5, 4, 40, true)) {
               return null;
            }

            var4.onQuickMoved(var5, var3);
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

   class IngredientSlot extends InventorySlot {
      public IngredientSlot(Inventory inventory, int slot, int x, int y) {
         super(inventory, slot, x, y);
      }

      @Override
      public boolean canSetStack(ItemStack stack) {
         return stack != null ? stack.getItem().hasBrewingRecipe(stack) : false;
      }

      @Override
      public int getMaxStackSize() {
         return 64;
      }
   }

   static class PotionSlot extends InventorySlot {
      private PlayerEntity player;

      public PotionSlot(PlayerEntity player, Inventory inventory, int slot, int x, int y) {
         super(inventory, slot, x, y);
         this.player = player;
      }

      @Override
      public boolean canSetStack(ItemStack stack) {
         return matches(stack);
      }

      @Override
      public int getMaxStackSize() {
         return 1;
      }

      @Override
      public void onStackRemovedByPlayer(PlayerEntity player, ItemStack stack) {
         if (stack.getItem() == Items.POTION && stack.getMetadata() > 0) {
            this.player.incrementStat(Achievements.BREW_POTION);
         }

         super.onStackRemovedByPlayer(player, stack);
      }

      public static boolean matches(ItemStack stack) {
         return stack != null && (stack.getItem() == Items.POTION || stack.getItem() == Items.GLASS_BOTTLE);
      }
   }
}
