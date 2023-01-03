package net.minecraft.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public interface SidedInventory extends Inventory {
   int[] getSlots(Direction side);

   boolean canHopperAddStack(int slot, ItemStack stack, Direction side);

   boolean canHopperRemoveStack(int slot, ItemStack stack, Direction side);
}
