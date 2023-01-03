package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.IntegerProperty;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class NetherWartBlock extends PlantBlock {
   public static final IntegerProperty AGE = IntegerProperty.of("age", 0, 3);

   protected NetherWartBlock() {
      this.setDefaultState(this.stateDefinition.any().set(AGE, 0));
      this.setTicksRandomly(true);
      float var1 = 0.5F;
      this.setShape(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 0.25F, 0.5F + var1);
      this.setItemGroup(null);
   }

   @Override
   protected boolean canPlantOn(Block block) {
      return block == Blocks.SOUL_SAND;
   }

   @Override
   public boolean isSupported(World world, BlockPos pos, BlockState state) {
      return this.canPlantOn(world.getBlockState(pos.down()).getBlock());
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      int var5 = state.get(AGE);
      if (var5 < 3 && random.nextInt(10) == 0) {
         state = state.set(AGE, var5 + 1);
         world.setBlockState(pos, state, 2);
      }

      super.tick(world, pos, state, random);
   }

   @Override
   public void dropItems(World world, BlockPos pos, BlockState state, float luck, int fortuneLevel) {
      if (!world.isClient) {
         int var6 = 1;
         if (state.get(AGE) >= 3) {
            var6 = 2 + world.random.nextInt(3);
            if (fortuneLevel > 0) {
               var6 += world.random.nextInt(fortuneLevel + 1);
            }
         }

         for(int var7 = 0; var7 < var6; ++var7) {
            this.dropItems(world, pos, new ItemStack(Items.NETHER_WART));
         }
      }
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return null;
   }

   @Override
   public int getBaseDropCount(Random random) {
      return 0;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Items.NETHER_WART;
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
