package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class ShovelItem extends ToolItem {
   private static final Set EFFECTIVE_BLOCKS = Sets.newHashSet(
      new Block[]{
         Blocks.CLAY, Blocks.DIRT, Blocks.FARMLAND, Blocks.GRASS, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.SNOW, Blocks.SNOW_LAYER, Blocks.SOUL_SAND
      }
   );

   public ShovelItem(Item.ToolMaterial material) {
      super(1.0F, material, EFFECTIVE_BLOCKS);
   }

   @Override
   public boolean canEffectivelyMine(Block block) {
      if (block == Blocks.SNOW_LAYER) {
         return true;
      } else {
         return block == Blocks.SNOW;
      }
   }
}
