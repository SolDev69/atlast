package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.block.state.property.IntegerProperty;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class StemBlock extends PlantBlock implements Fertilizable {
   public static final IntegerProperty AGE = IntegerProperty.of("age", 0, 7);
   public static final DirectionProperty FACING = DirectionProperty.of("facing", new Predicate() {
      public boolean apply(Direction c_69garkogr) {
         return c_69garkogr != Direction.DOWN;
      }
   });
   private final Block plant;

   protected StemBlock(Block plant) {
      this.setDefaultState(this.stateDefinition.any().set(AGE, 0).set(FACING, Direction.UP));
      this.plant = plant;
      this.setTicksRandomly(true);
      float var2 = 0.125F;
      this.setShape(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, 0.25F, 0.5F + var2);
      this.setItemGroup(null);
   }

   @Override
   public BlockState updateShape(BlockState state, IWorld world, BlockPos pos) {
      state = state.set(FACING, Direction.UP);

      for(Direction var5 : Direction.Plane.HORIZONTAL) {
         if (world.getBlockState(pos.offset(var5)).getBlock() == this.plant) {
            state = state.set(FACING, var5);
            break;
         }
      }

      return state;
   }

   @Override
   protected boolean canPlantOn(Block block) {
      return block == Blocks.FARMLAND;
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      super.tick(world, pos, state, random);
      if (world.getRawBrightness(pos.up()) >= 9) {
         float var5 = WheatBlock.getMoisture(this, world, pos);
         if (random.nextInt((int)(25.0F / var5) + 1) == 0) {
            int var6 = state.get(AGE);
            if (var6 < 7) {
               state = state.set(AGE, var6 + 1);
               world.setBlockState(pos, state, 2);
            } else {
               for(Direction var8 : Direction.Plane.HORIZONTAL) {
                  if (world.getBlockState(pos.offset(var8)).getBlock() == this.plant) {
                     return;
                  }
               }

               pos = pos.offset(Direction.Plane.HORIZONTAL.pick(random));
               Block var11 = world.getBlockState(pos.down()).getBlock();
               if (world.getBlockState(pos).getBlock().material == Material.AIR && (var11 == Blocks.FARMLAND || var11 == Blocks.DIRT || var11 == Blocks.GRASS)) {
                  world.setBlockState(pos, this.plant.defaultState());
               }
            }
         }
      }
   }

   public void grow(World world, BlockPos pos, BlockState state) {
      int var4 = state.get(AGE) + MathHelper.nextInt(world.random, 2, 5);
      world.setBlockState(pos, state.set(AGE, Math.min(7, var4)), 2);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor(int tint) {
      int var2 = tint * 32;
      int var3 = 255 - tint * 8;
      int var4 = tint * 4;
      return var2 << 16 | var3 << 8 | var4;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor(IWorld world, BlockPos pos, int tint) {
      return this.getColor(world.getBlockState(pos).get(AGE));
   }

   @Override
   public void setBlockItemBounds() {
      float var1 = 0.125F;
      this.setShape(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 0.25F, 0.5F + var1);
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      this.maxY = (double)((float)(world.getBlockState(pos).get(AGE) * 2 + 2) / 16.0F);
      float var3 = 0.125F;
      this.setShape(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, (float)this.maxY, 0.5F + var3);
   }

   @Override
   public void dropItems(World world, BlockPos pos, BlockState state, float luck, int fortuneLevel) {
      super.dropItems(world, pos, state, luck, fortuneLevel);
      if (!world.isClient) {
         Item var6 = this.getSeedsItem();
         if (var6 != null) {
            int var7 = state.get(AGE);

            for(int var8 = 0; var8 < 3; ++var8) {
               if (world.random.nextInt(15) <= var7) {
                  this.dropItems(world, pos, new ItemStack(var6));
               }
            }
         }
      }
   }

   protected Item getSeedsItem() {
      if (this.plant == Blocks.PUMPKIN) {
         return Items.PUMPKIN_SEEDS;
      } else {
         return this.plant == Blocks.MELON_BLOCK ? Items.MELON_SEEDS : null;
      }
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return null;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      Item var3 = this.getSeedsItem();
      return var3 != null ? var3 : Item.byRawId(0);
   }

   @Override
   public boolean canGrow(World world, BlockPos pos, BlockState state, boolean bl) {
      return state.get(AGE) != 7;
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
      return new StateDefinition(this, AGE, FACING);
   }
}
