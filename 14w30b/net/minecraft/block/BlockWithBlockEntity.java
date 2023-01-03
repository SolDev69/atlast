package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockWithBlockEntity extends Block implements BlockEntityProvider {
   protected BlockWithBlockEntity(Material c_57ywipuwq) {
      super(c_57ywipuwq);
      this.hasBlockEntity = true;
   }

   @Override
   public int getRenderType() {
      return -1;
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      super.onRemoved(world, pos, state);
      world.removeBlockEntity(pos);
   }

   @Override
   public boolean doEvent(World world, BlockPos pos, BlockState state, int type, int data) {
      super.doEvent(world, pos, state, type, data);
      BlockEntity var6 = world.getBlockEntity(pos);
      return var6 == null ? false : var6.doEvent(type, data);
   }
}
