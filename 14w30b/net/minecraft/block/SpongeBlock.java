package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SpongeBlock extends Block {
   public static final BooleanProperty WET = BooleanProperty.of("wet");

   protected SpongeBlock() {
      super(Material.SPONGE);
      this.setDefaultState(this.stateDefinition.any().set(WET, false));
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      return state.get(WET) ? 1 : 0;
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      this.getWet(world, pos, state);
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      this.getWet(world, pos, state);
      super.update(world, pos, state, neighborBlock);
   }

   protected void getWet(World world, BlockPos pos, BlockState state) {
      if (!state.get(WET) && this.removeWater(world, pos)) {
         world.setBlockState(pos, state.set(WET, true), 2);
         world.doEvent(2001, pos, Block.getRawId(Blocks.WATER));
      }
   }

   private boolean removeWater(World world, BlockPos pos) {
      LinkedList var3 = Lists.newLinkedList();
      var3.add(new Pair(pos, 0));
      int var4 = 0;

      while(!var3.isEmpty()) {
         Pair var5 = (Pair)var3.poll();
         BlockPos var6 = (BlockPos)var5.getLeft();
         int var7 = var5.getRight();

         for(Direction var11 : Direction.values()) {
            BlockPos var12 = var6.offset(var11);
            if (world.getBlockState(var12).getBlock().getMaterial() == Material.WATER) {
               world.removeBlock(var12);
               ++var4;
               if (var7 < 6) {
                  var3.add(new Pair(var12, var7 + 1));
               }
            }
         }

         if (var4 > 64) {
            break;
         }
      }

      return var4 > 0;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      stacks.add(new ItemStack(item, 1, 0));
      stacks.add(new ItemStack(item, 1, 1));
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(WET, (metadata & 1) == 1);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return state.get(WET) ? 1 : 0;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, WET);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random random) {
      if (state.get(WET)) {
         Direction var5 = Direction.pick(random);
         if (var5 != Direction.UP && !World.hasSolidTop(world, pos.offset(var5))) {
            double var6 = (double)pos.getX();
            double var8 = (double)pos.getY();
            double var10 = (double)pos.getZ();
            if (var5 == Direction.DOWN) {
               var8 -= 0.05;
               var6 += random.nextDouble();
               var10 += random.nextDouble();
            } else {
               var8 += random.nextDouble() * 0.8;
               if (var5.getAxis() == Direction.Axis.X) {
                  var10 += random.nextDouble();
                  if (var5 == Direction.EAST) {
                     ++var6;
                  } else {
                     var6 += 0.05;
                  }
               } else {
                  var6 += random.nextDouble();
                  if (var5 == Direction.SOUTH) {
                     ++var10;
                  } else {
                     var10 += 0.05;
                  }
               }
            }

            world.addParticle(ParticleType.DRIP_WATER, var6, var8, var10, 0.0, 0.0, 0.0);
         }
      }
   }
}
