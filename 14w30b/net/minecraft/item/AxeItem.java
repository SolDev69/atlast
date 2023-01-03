package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;

public class AxeItem extends ToolItem {
   private static final Set EFFECTIVE_BLOCKS = Sets.newHashSet(
      new Block[]{Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN}
   );

   protected AxeItem(Item.ToolMaterial material) {
      super(3.0F, material, EFFECTIVE_BLOCKS);
   }

   @Override
   public float getMiningSpeed(ItemStack stack, Block block) {
      return block.getMaterial() != Material.WOOD && block.getMaterial() != Material.PLANT && block.getMaterial() != Material.REPLACEABLE_PLANT
         ? super.getMiningSpeed(stack, block)
         : this.miningSpeed;
   }
}
