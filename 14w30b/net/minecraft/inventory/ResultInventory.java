package net.minecraft.inventory;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ResultInventory implements Inventory {
   private ItemStack[] stacks = new ItemStack[1];

   @Override
   public int getSize() {
      return 1;
   }

   @Override
   public ItemStack getStack(int slot) {
      return this.stacks[0];
   }

   @Override
   public String getName() {
      return "Result";
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
   public ItemStack removeStack(int slot, int amount) {
      if (this.stacks[0] != null) {
         ItemStack var3 = this.stacks[0];
         this.stacks[0] = null;
         return var3;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackQuietly(int slot) {
      if (this.stacks[0] != null) {
         ItemStack var2 = this.stacks[0];
         this.stacks[0] = null;
         return var2;
      } else {
         return null;
      }
   }

   @Override
   public void setStack(int slot, ItemStack stack) {
      this.stacks[0] = stack;
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
}
