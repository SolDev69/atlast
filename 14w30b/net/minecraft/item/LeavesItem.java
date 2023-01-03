package net.minecraft.item;

import net.minecraft.block.AbstractLeavesBlock;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class LeavesItem extends BlockItem {
   private final AbstractLeavesBlock block;

   public LeavesItem(AbstractLeavesBlock block) {
      super(block);
      this.block = block;
      this.setMaxDamage(0);
      this.setStackable(true);
   }

   @Override
   public int getBlockMetadata(int metadata) {
      return metadata | 4;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getDisplayColor(ItemStack stack, int color) {
      return this.block.getColor(stack.getMetadata());
   }

   @Override
   public String getTranslationKey(ItemStack stack) {
      return super.getTranslationKey() + "." + this.block.getVariant(stack.getMetadata()).getName();
   }
}
