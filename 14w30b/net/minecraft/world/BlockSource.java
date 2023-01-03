package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;

public class BlockSource implements IBlockSource {
   private final World world;
   private final BlockPos pos;

   public BlockSource(World world, BlockPos pos) {
      this.world = world;
      this.pos = pos;
   }

   @Override
   public World getWorld() {
      return this.world;
   }

   @Override
   public double getX() {
      return (double)this.pos.getX() + 0.5;
   }

   @Override
   public double getY() {
      return (double)this.pos.getY() + 0.5;
   }

   @Override
   public double getZ() {
      return (double)this.pos.getZ() + 0.5;
   }

   @Override
   public BlockPos getPos() {
      return this.pos;
   }

   @Override
   public Block getBlock() {
      return this.world.getBlockState(this.pos).getBlock();
   }

   @Override
   public int getBlockMetadata() {
      BlockState var1 = this.world.getBlockState(this.pos);
      return var1.getBlock().getMetadataFromState(var1);
   }

   @Override
   public BlockEntity getBlockEntity() {
      return this.world.getBlockEntity(this.pos);
   }
}
