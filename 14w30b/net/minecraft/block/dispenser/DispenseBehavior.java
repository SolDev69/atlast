package net.minecraft.block.dispenser;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockSource;

public interface DispenseBehavior {
   DispenseBehavior NONE = new DispenseBehavior() {
      @Override
      public ItemStack dispense(IBlockSource source, ItemStack stack) {
         return stack;
      }
   };

   ItemStack dispense(IBlockSource source, ItemStack stack);
}
