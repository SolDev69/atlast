package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockState;
import net.minecraft.item.group.ItemGroup;

public class MineralBlock extends Block {
   private final MaterialColor color;

   public MineralBlock(MaterialColor color) {
      super(Material.IRON);
      this.color = color;
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public MaterialColor getMaterialColor(BlockState state) {
      return this.color;
   }
}
