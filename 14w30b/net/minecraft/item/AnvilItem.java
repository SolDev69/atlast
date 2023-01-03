package net.minecraft.item;

import net.minecraft.block.Block;

public class AnvilItem extends VariantBlockItem {
   public AnvilItem(Block c_68zcrzyxg) {
      super(c_68zcrzyxg, c_68zcrzyxg, new String[]{"intact", "slightlyDamaged", "veryDamaged"});
   }

   @Override
   public int getBlockMetadata(int metadata) {
      return metadata << 2;
   }
}
