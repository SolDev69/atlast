package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockState;
import net.minecraft.item.group.ItemGroup;

public class HardenedClayBlock extends Block {
   public HardenedClayBlock() {
      super(Material.STONE);
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public MaterialColor getMaterialColor(BlockState state) {
      return MaterialColor.ORANGE;
   }
}
