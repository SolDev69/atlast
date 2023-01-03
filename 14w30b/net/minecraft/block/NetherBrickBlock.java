package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockState;
import net.minecraft.item.group.ItemGroup;

public class NetherBrickBlock extends Block {
   public NetherBrickBlock() {
      super(Material.STONE);
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public MaterialColor getMaterialColor(BlockState state) {
      return MaterialColor.NETHER;
   }
}
