package net.minecraft.item;

import net.minecraft.block.Block;

public class StickyPistonBlockItem extends BlockItem {
   public StickyPistonBlockItem(Block c_68zcrzyxg) {
      super(c_68zcrzyxg);
   }

   @Override
   public int getBlockMetadata(int metadata) {
      return 7;
   }
}
