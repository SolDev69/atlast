package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.IntegerProperty;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class WheatBlock extends PlantBlock implements Fertilizable {
   public static final IntegerProperty AGE = IntegerProperty.of("age", 0, 7);

   protected WheatBlock() {
      this.setDefaultState(this.stateDefinition.any().set(AGE, 0));
      this.setTicksRandomly(true);
      float var1 = 0.5F;
      this.setShape(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 0.25F, 0.5F + var1);
      this.setItemGroup(null);
      this.setStrength(0.0F);
      this.setSound(GRASS_SOUND);
      this.disableStats();
   }

   @Override
   protected boolean canPlantOn(Block block) {
      return block == Blocks.FARMLAND;
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      super.tick(world, pos, state, random);
      if (world.getRawBrightness(pos.up()) >= 9) {
         int var5 = state.get(AGE);
         if (var5 < 7) {
            float var6 = getMoisture(this, world, pos);
            if (random.nextInt((int)(25.0F / var6) + 1) == 0) {
               world.setBlockState(pos, state.set(AGE, var5 + 1), 2);
            }
         }
      }
   }

   public void grow(World world, BlockPos pos, BlockState state) {
      int var4 = state.get(AGE) + MathHelper.nextInt(world.random, 2, 5);
      if (var4 > 7) {
         var4 = 7;
      }

      world.setBlockState(pos, state.set(AGE, var4), 2);
   }

   protected static float getMoisture(Block block, World world, BlockPos pos) {
      float var3 = 1.0F;
      BlockPos var4 = pos.down();

      for(int var5 = -1; var5 <= 1; ++var5) {
         for(int var6 = -1; var6 <= 1; ++var6) {
            float var7 = 0.0F;
            BlockState var8 = world.getBlockState(var4.add(var5, 0, var6));
            if (var8.getBlock() == Blocks.FARMLAND) {
               var7 = 1.0F;
               if (var8.get(FarmlandBlock.MOISTURE) > 0) {
                  var7 = 3.0F;
               }
            }

            if (var5 != 0 || var6 != 0) {
               var7 /= 4.0F;
            }

            var3 += var7;
         }
      }

      BlockPos var12 = pos.north();
      BlockPos var13 = pos.south();
      BlockPos var14 = pos.west();
      BlockPos var15 = pos.east();
      boolean var9 = block == world.getBlockState(var14).getBlock() || block == world.getBlockState(var15).getBlock();
      boolean var10 = block == world.getBlockState(var12).getBlock() || block == world.getBlockState(var13).getBlock();
      if (var9 && var10) {
         var3 /= 2.0F;
      } else {
         boolean var11 = block == world.getBlockState(var14.north()).getBlock()
            || block == world.getBlockState(var15.north()).getBlock()
            || block == world.getBlockState(var15.south()).getBlock()
            || block == world.getBlockState(var14.south()).getBlock();
         if (var11) {
            var3 /= 2.0F;
         }
      }

      return var3;
   }

   @Override
   public boolean isSupported(World world, BlockPos pos, BlockState state) {
      return (world.getLight(pos) >= 8 || world.hasSkyAccess(pos)) && this.canPlantOn(world.getBlockState(pos.down()).getBlock());
   }

   protected Item getSeedItem() {
      return Items.WHEAT_SEEDS;
   }

   protected Item getPlantItem() {
      return Items.WHEAT;
   }

   @Override
   public void dropItems(World world, BlockPos pos, BlockState state, float luck, int fortuneLevel) {
      super.dropItems(world, pos, state, luck, 0);
      if (!world.isClient) {
         int var6 = state.get(AGE);
         if (var6 >= 7) {
            int var7 = 3 + fortuneLevel;

            for(int var8 = 0; var8 < var7; ++var8) {
               if (world.random.nextInt(15) <= var6) {
                  this.dropItems(world, pos, new ItemStack(this.getSeedItem(), 1, 0));
               }
            }
         }
      }
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return state.get(AGE) == 7 ? this.getPlantItem() : this.getSeedItem();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return this.getSeedItem();
   }

   @Override
   public boolean canGrow(World world, BlockPos pos, BlockState state, boolean bl) {
      return state.get(AGE) <= 7;
   }

   @Override
   public boolean canBeFertilized(World world, Random rand, BlockPos pos, BlockState state) {
      return true;
   }

   @Override
   public void grow(World world, Random rand, BlockPos pos, BlockState state) {
      this.grow(world, pos, state);
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
