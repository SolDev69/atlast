package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PlantBlock extends Block {
   protected PlantBlock(Material c_57ywipuwq) {
      super(c_57ywipuwq);
      this.setTicksRandomly(true);
      float var2 = 0.2F;
      this.setShape(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, var2 * 3.0F, 0.5F + var2);
      this.setItemGroup(ItemGroup.DECORATIONS);
   }

   protected PlantBlock() {
      this(Material.PLANT);
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      return super.canSurvive(world, pos) && this.canPlantOn(world.getBlockState(pos.down()).getBlock());
   }

   protected boolean canPlantOn(Block block) {
      return block == Blocks.GRASS || block == Blocks.DIRT || block == Blocks.FARMLAND;
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      super.update(world, pos, state, neighborBlock);
      this.canSurviveOrBreak(world, pos, state);
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      this.canSurviveOrBreak(world, pos, state);
   }

   protected void canSurviveOrBreak(World world, BlockPos pos, BlockState state) {
      if (!this.isSupported(world, pos, state)) {
         this.dropItems(world, pos, state, 0);
         world.setBlockState(pos, Blocks.AIR.defaultState(), 3);
      }
   }

   public boolean isSupported(World world, BlockPos pos, BlockState state) {
      return this.canPlantOn(world.getBlockState(pos.down()).getBlock());
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      return null;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }
}
