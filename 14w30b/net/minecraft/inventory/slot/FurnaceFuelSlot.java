package net.minecraft.inventory.slot;

import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class FurnaceFuelSlot extends InventorySlot {
   public FurnaceFuelSlot(Inventory c_70ssqjwqk, int i, int j, int k) {
      super(c_70ssqjwqk, i, j, k);
   }

   @Override
   public boolean canSetStack(ItemStack stack) {
      return FurnaceBlockEntity.isFuel(stack) || isBucket(stack);
   }

   @Override
   public int getMaxStackSize(ItemStack stack) {
      return isBucket(stack) ? 1 : super.getMaxStackSize(stack);
   }

   public static boolean isBucket(ItemStack stack) {
      return stack != null && stack.getItem() != null && stack.getItem() == Items.BUCKET;
   }
}
