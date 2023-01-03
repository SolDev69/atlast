package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.group.ItemGroup;

public class ObsidianBlock extends Block {
   public ObsidianBlock() {
      super(Material.STONE);
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Item.byBlock(Blocks.OBSIDIAN);
   }

   @Override
   public MaterialColor getMaterialColor(BlockState state) {
      return MaterialColor.SPRUCE;
   }
}
