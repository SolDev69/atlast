package net.minecraft.item;

import net.minecraft.block.Block;

public class WoolItem extends BlockItem {
   public WoolItem(Block c_68zcrzyxg) {
      super(c_68zcrzyxg);
      this.setMaxDamage(0);
      this.setStackable(true);
   }

   @Override
   public int getBlockMetadata(int metadata) {
      return metadata;
   }

   @Override
   public String getTranslationKey(ItemStack stack) {
      return super.getTranslationKey() + "." + DyeColor.byIndex(stack.getMetadata()).getName();
   }
}
