package net.minecraft.inventory;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.Nameable;

public interface Inventory extends Nameable {
   int getSize();

   ItemStack getStack(int slot);

   ItemStack removeStack(int slot, int amount);

   ItemStack removeStackQuietly(int slot);

   void setStack(int slot, ItemStack stack);

   int getMaxStackSize();

   void markDirty();

   boolean isValid(PlayerEntity player);

   void onOpen(PlayerEntity player);

   void onClose(PlayerEntity player);

   boolean canSetStack(int slot, ItemStack stack);

   int getData(int id);

   void setData(int id, int value);

   int getDataCount();

   void clear();
}
