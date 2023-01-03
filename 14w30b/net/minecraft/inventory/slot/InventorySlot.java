package net.minecraft.inventory.slot;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class InventorySlot {
   private final int slot;
   public final Inventory inventory;
   public int id;
   public int x;
   public int y;

   public InventorySlot(Inventory inventory, int slot, int x, int y) {
      this.inventory = inventory;
      this.slot = slot;
      this.x = x;
      this.y = y;
   }

   public void onQuickMoved(ItemStack oldStack, ItemStack newStack) {
      if (oldStack != null && newStack != null) {
         if (oldStack.getItem() == newStack.getItem()) {
            int var3 = newStack.size - oldStack.size;
            if (var3 > 0) {
               this.onQuickMoved(oldStack, var3);
            }
         }
      }
   }

   protected void onQuickMoved(ItemStack stack, int amount) {
   }

   protected void checkAchievements(ItemStack stack) {
   }

   public void onStackRemovedByPlayer(PlayerEntity player, ItemStack stack) {
      this.markDirty();
   }

   public boolean canSetStack(ItemStack stack) {
      return true;
   }

   public ItemStack getStack() {
      return this.inventory.getStack(this.slot);
   }

   public boolean hasStack() {
      return this.getStack() != null;
   }

   public void setStack(ItemStack stack) {
      this.inventory.setStack(this.slot, stack);
      this.markDirty();
   }

   public void markDirty() {
      this.inventory.markDirty();
   }

   public int getMaxStackSize() {
      return this.inventory.getMaxStackSize();
   }

   public int getMaxStackSize(ItemStack stack) {
      return this.getMaxStackSize();
   }

   @Environment(EnvType.CLIENT)
   public String getTexture() {
      return null;
   }

   public ItemStack removeStack(int amount) {
      return this.inventory.removeStack(this.slot, amount);
   }

   public boolean equals(Inventory inventory, int slot) {
      return inventory == this.inventory && slot == this.slot;
   }

   public boolean canPickUp(PlayerEntity playerEntity) {
      return true;
   }

   @Environment(EnvType.CLIENT)
   public boolean isActive() {
      return true;
   }
}
