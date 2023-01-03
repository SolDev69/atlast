package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SimpleInventory implements Inventory {
   private String name;
   private int size;
   private ItemStack[] stacks;
   private List listeners;
   private boolean hasCustomName;

   public SimpleInventory(String name, boolean hasCustomName, int size) {
      this.name = name;
      this.hasCustomName = hasCustomName;
      this.size = size;
      this.stacks = new ItemStack[size];
   }

   @Environment(EnvType.CLIENT)
   public SimpleInventory(Text name, int size) {
      this(name.buildString(), true, size);
   }

   public void addListener(InventoryListener listener) {
      if (this.listeners == null) {
         this.listeners = Lists.newArrayList();
      }

      this.listeners.add(listener);
   }

   public void removeListener(InventoryListener listener) {
      this.listeners.remove(listener);
   }

   @Override
   public ItemStack getStack(int slot) {
      return slot >= 0 && slot < this.stacks.length ? this.stacks[slot] : null;
   }

   @Override
   public ItemStack removeStack(int slot, int amount) {
      if (this.stacks[slot] != null) {
         if (this.stacks[slot].size <= amount) {
            ItemStack var4 = this.stacks[slot];
            this.stacks[slot] = null;
            this.markDirty();
            return var4;
         } else {
            ItemStack var3 = this.stacks[slot].split(amount);
            if (this.stacks[slot].size == 0) {
               this.stacks[slot] = null;
            }

            this.markDirty();
            return var3;
         }
      } else {
         return null;
      }
   }

   public ItemStack addStack(ItemStack stack) {
      ItemStack var2 = stack.copy();

      for(int var3 = 0; var3 < this.size; ++var3) {
         ItemStack var4 = this.getStack(var3);
         if (var4 == null) {
            this.setStack(var3, var2);
            this.markDirty();
            return null;
         }

         if (ItemStack.matchesItem(var4, var2)) {
            int var5 = Math.min(this.getMaxStackSize(), var4.getMaxSize());
            int var6 = Math.min(var2.size, var5 - var4.size);
            if (var6 > 0) {
               var4.size += var6;
               var2.size -= var6;
               if (var2.size <= 0) {
                  this.markDirty();
                  return null;
               }
            }
         }
      }

      if (var2.size != stack.size) {
         this.markDirty();
      }

      return var2;
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

      this.markDirty();
   }

   @Override
   public int getSize() {
      return this.size;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public boolean hasCustomName() {
      return this.hasCustomName;
   }

   public void setCustomName(String name) {
      this.hasCustomName = true;
      this.name = name;
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
   public void markDirty() {
      if (this.listeners != null) {
         for(int var1 = 0; var1 < this.listeners.size(); ++var1) {
            ((InventoryListener)this.listeners.get(var1)).onInventoryChanged(this);
         }
      }
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
