package net.minecraft.item;

import net.minecraft.block.Block;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class GrassBlockItem extends BlockItem {
   private final Block block;
   private String[] names;

   public GrassBlockItem(Block block, boolean unbreakable) {
      super(block);
      this.block = block;
      if (unbreakable) {
         this.setMaxDamage(0);
         this.setStackable(true);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getDisplayColor(ItemStack stack, int color) {
      return this.block.getColor(stack.getMetadata());
   }

   @Override
   public int getBlockMetadata(int metadata) {
      return metadata;
   }

   public GrassBlockItem setNames(String[] names) {
      this.names = names;
      return this;
   }

   @Override
   public String getTranslationKey(ItemStack stack) {
      if (this.names == null) {
         return super.getTranslationKey(stack);
      } else {
         int var2 = stack.getMetadata();
         return var2 >= 0 && var2 < this.names.length ? super.getTranslationKey(stack) + "." + this.names[var2] : super.getTranslationKey(stack);
      }
   }
}
