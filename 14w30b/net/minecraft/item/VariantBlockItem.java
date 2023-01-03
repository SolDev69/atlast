package net.minecraft.item;

import com.google.common.base.Function;
import net.minecraft.block.Block;

public class VariantBlockItem extends BlockItem {
   protected final Block spriteProvider;
   protected final Function idProvider;

   public VariantBlockItem(Block block, Block spriteProvider, Function idProvider) {
      super(block);
      this.spriteProvider = spriteProvider;
      this.idProvider = idProvider;
      this.setMaxDamage(0);
      this.setStackable(true);
   }

   public VariantBlockItem(Block block, Block spriteProvider, String[] variants) {
      this(block, spriteProvider, new Function() {
         public String apply(ItemStack c_72owraavl) {
            int var2 = c_72owraavl.getMetadata();
            if (var2 < 0 || var2 >= variants.length) {
               var2 = 0;
            }

            return variants[var2];
         }
      });
   }

   @Override
   public int getBlockMetadata(int metadata) {
      return metadata;
   }

   @Override
   public String getTranslationKey(ItemStack stack) {
      return super.getTranslationKey() + "." + (String)this.idProvider.apply(stack);
   }
}
