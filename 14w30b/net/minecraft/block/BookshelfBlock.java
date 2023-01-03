package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.group.ItemGroup;

public class BookshelfBlock extends Block {
   public BookshelfBlock() {
      super(Material.WOOD);
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public int getBaseDropCount(Random random) {
      return 3;
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Items.BOOK;
   }
}
