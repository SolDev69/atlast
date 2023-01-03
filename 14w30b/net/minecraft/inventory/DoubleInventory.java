package net.minecraft.inventory;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.menu.ChestMenu;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.menu.LockableMenuProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class DoubleInventory implements LockableMenuProvider {
   private String name;
   private LockableMenuProvider inventory1;
   private LockableMenuProvider inventory2;

   public DoubleInventory(String name, LockableMenuProvider inventory1, LockableMenuProvider inventory2) {
      this.name = name;
      if (inventory1 == null) {
         inventory1 = inventory2;
      }

      if (inventory2 == null) {
         inventory2 = inventory1;
      }

      this.inventory1 = inventory1;
      this.inventory2 = inventory2;
      if (inventory1.isLocked()) {
         inventory2.setLock(inventory1.getLock());
      } else if (inventory2.isLocked()) {
         inventory1.setLock(inventory2.getLock());
      }
   }

   @Override
   public int getSize() {
      return this.inventory1.getSize() + this.inventory2.getSize();
   }

   public boolean contains(Inventory inventory) {
      return this.inventory1 == inventory || this.inventory2 == inventory;
   }

   @Override
   public String getName() {
      if (this.inventory1.hasCustomName()) {
         return this.inventory1.getName();
      } else {
         return this.inventory2.hasCustomName() ? this.inventory2.getName() : this.name;
      }
   }

   @Override
   public boolean hasCustomName() {
      return this.inventory1.hasCustomName() || this.inventory2.hasCustomName();
   }

   @Override
   public Text getDisplayName() {
      return (Text)(this.hasCustomName() ? new LiteralText(this.getName()) : new TranslatableText(this.getName()));
   }

   @Override
   public ItemStack getStack(int slot) {
      return slot >= this.inventory1.getSize() ? this.inventory2.getStack(slot - this.inventory1.getSize()) : this.inventory1.getStack(slot);
   }

   @Override
   public ItemStack removeStack(int slot, int amount) {
      return slot >= this.inventory1.getSize()
         ? this.inventory2.removeStack(slot - this.inventory1.getSize(), amount)
         : this.inventory1.removeStack(slot, amount);
   }

   @Override
   public ItemStack removeStackQuietly(int slot) {
      return slot >= this.inventory1.getSize()
         ? this.inventory2.removeStackQuietly(slot - this.inventory1.getSize())
         : this.inventory1.removeStackQuietly(slot);
   }

   @Override
   public void setStack(int slot, ItemStack stack) {
      if (slot >= this.inventory1.getSize()) {
         this.inventory2.setStack(slot - this.inventory1.getSize(), stack);
      } else {
         this.inventory1.setStack(slot, stack);
      }
   }

   @Override
   public int getMaxStackSize() {
      return this.inventory1.getMaxStackSize();
   }

   @Override
   public void markDirty() {
      this.inventory1.markDirty();
      this.inventory2.markDirty();
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      return this.inventory1.isValid(player) && this.inventory2.isValid(player);
   }

   @Override
   public void onOpen(PlayerEntity player) {
      this.inventory1.onOpen(player);
      this.inventory2.onOpen(player);
   }

   @Override
   public void onClose(PlayerEntity player) {
      this.inventory1.onClose(player);
      this.inventory2.onClose(player);
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
   public boolean isLocked() {
      return this.inventory1.isLocked() || this.inventory2.isLocked();
   }

   @Override
   public void setLock(InventoryLock lock) {
      this.inventory1.setLock(lock);
      this.inventory2.setLock(lock);
   }

   @Override
   public InventoryLock getLock() {
      return this.inventory1.getLock();
   }

   @Override
   public String getMenuType() {
      return this.inventory1.getMenuType();
   }

   @Override
   public InventoryMenu createMenu(PlayerInventory playerInventory, PlayerEntity player) {
      return new ChestMenu(playerInventory, this, player);
   }

   @Override
   public void clear() {
      this.inventory1.clear();
      this.inventory2.clear();
   }
}
