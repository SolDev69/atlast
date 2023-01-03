package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.group.ItemGroup;

public class ClayBlock extends Block {
   public ClayBlock() {
      super(Material.CLAY);
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Items.CLAY_BALL;
   }

   @Override
   public int getBaseDropCount(Random random) {
      return 4;
   }
}
