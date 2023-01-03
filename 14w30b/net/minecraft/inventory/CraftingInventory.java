package net.minecraft.inventory;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class CraftingInventory implements Inventory {
   private final ItemStack[] stacks;
   private final int width;
   private final int height;
   private final InventoryMenu menu;

   public CraftingInventory(InventoryMenu menu, int width, int height) {
      int var4 = width * height;
      this.stacks = new ItemStack[var4];
      this.menu = menu;
      this.width = width;
      this.height = height;
   }

   @Override
   public int getSize() {
      return this.stacks.length;
   }

   @Override
   public ItemStack getStack(int slot) {
      return slot >= this.getSize() ? null : this.stacks[slot];
   }

   public ItemStack getStack(int column, int row) {
      return column >= 0 && column < this.width && row >= 0 && row <= this.height ? this.getStack(column + row * this.width) : null;
   }

   @Override
   public String getName() {
      return "container.crafting";
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
   public ItemStack removeStack(int slot, int amount) {
      if (this.stacks[slot] != null) {
         if (this.stacks[slot].size <= amount) {
            ItemStack var4 = this.stacks[slot];
            this.stacks[slot] = null;
            this.menu.onContentChanged(this);
            return var4;
         } else {
            ItemStack var3 = this.stacks[slot].split(amount);
            if (this.stacks[slot].size == 0) {
               this.stacks[slot] = null;
            }

            this.menu.onContentChanged(this);
            return var3;
         }
      } else {
         return null;
      }
   }

   @Override
   public void setStack(int slot, ItemStack stack) {
      this.stacks[slot] = stack;
      this.menu.onContentChanged(this);
   }

   @Override
   public int getMaxStackSize() {
      return 64;
   }

   @Override
   public void markDirty() {
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      return true;
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

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }
}
