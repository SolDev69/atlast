package net.minecraft.block;

import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockState;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class RedstoneBlock extends MineralBlock {
   public RedstoneBlock(MaterialColor c_71wxkaaxh) {
      super(c_71wxkaaxh);
      this.setItemGroup(ItemGroup.REDSTONE);
   }

   @Override
   public boolean isPowerSource() {
      return true;
   }

   @Override
   public int getEmittedWeakPower(IWorld world, BlockPos pos, BlockState state, Direction dir) {
      return 15;
   }
}
