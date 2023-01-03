package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.IntegerProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SugarCaneBlock extends Block {
   public static final IntegerProperty AGE = IntegerProperty.of("age", 0, 15);

   protected SugarCaneBlock() {
      super(Material.PLANT);
      this.setDefaultState(this.stateDefinition.any().set(AGE, 0));
      float var1 = 0.375F;
      this.setShape(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 1.0F, 0.5F + var1);
      this.setTicksRandomly(true);
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (world.getBlockState(pos.down()).getBlock() == Blocks.REEDS || this.canSurviveOrBreak(world, pos, state)) {
         if (world.isAir(pos.up())) {
            int var5 = 1;

            while(world.getBlockState(pos.down(var5)).getBlock() == this) {
               ++var5;
            }

            if (var5 < 3) {
               int var6 = state.get(AGE);
               if (var6 == 15) {
                  world.setBlockState(pos.up(), this.defaultState());
                  world.setBlockState(pos, state.set(AGE, 0), 4);
               } else {
                  world.setBlockState(pos, state.set(AGE, var6 + 1), 4);
               }
            }
         }
      }
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      Block var3 = world.getBlockState(pos.down()).getBlock();
      if (var3 == this) {
         return true;
      } else if (var3 != Blocks.GRASS && var3 != Blocks.DIRT && var3 != Blocks.SAND) {
         return false;
      } else {
         for(Direction var5 : Direction.Plane.HORIZONTAL) {
            if (world.getBlockState(pos.offset(var5).down()).getBlock().getMaterial() == Material.WATER) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      this.canSurviveOrBreak(world, pos, state);
   }

   protected final boolean canSurviveOrBreak(World world, BlockPos pos, BlockState state) {
      if (this.isSupported(world, pos)) {
         return true;
      } else {
         this.dropItems(world, pos, state, 0);
         world.removeBlock(pos);
         return false;
      }
   }

   public boolean isSupported(World world, BlockPos pos) {
      return this.canSurvive(world, pos);
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      return null;
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Items.REEDS;
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
   public Item getPickItem(World world, BlockPos pos) {
      return Items.REEDS;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor(IWorld world, BlockPos pos, int tint) {
      return world.getBiome(pos).getGrassColor(pos);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(AGE, metadata);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return state.get(AGE);
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, AGE);
   }
}
