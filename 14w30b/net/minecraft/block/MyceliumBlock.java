package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class MyceliumBlock extends Block {
   public static final BooleanProperty SNOWY = BooleanProperty.of("snowy");

   protected MyceliumBlock() {
      super(Material.GRASS);
      this.setDefaultState(this.stateDefinition.any().set(SNOWY, false));
      this.setTicksRandomly(true);
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public BlockState updateShape(BlockState state, IWorld world, BlockPos pos) {
      Block var4 = world.getBlockState(pos.up()).getBlock();
      return state.set(SNOWY, var4 == Blocks.SNOW || var4 == Blocks.SNOW_LAYER);
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (!world.isClient) {
         if (world.getRawBrightness(pos.up()) < 4 && world.getBlockState(pos.up()).getBlock().getOpacity() > 2) {
            world.setBlockState(pos, Blocks.DIRT.defaultState().set(DirtBlock.VARIANT, DirtBlock.Variant.DIRT));
         } else {
            if (world.getRawBrightness(pos.up()) >= 9) {
               for(int var5 = 0; var5 < 4; ++var5) {
                  BlockPos var6 = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                  BlockState var7 = world.getBlockState(var6);
                  Block var8 = world.getBlockState(var6.up()).getBlock();
                  if (var7.getBlock() == Blocks.DIRT
                     && var7.get(DirtBlock.VARIANT) == DirtBlock.Variant.DIRT
                     && world.getRawBrightness(var6.up()) >= 4
                     && var8.getOpacity() <= 2) {
                     world.setBlockState(var6, this.defaultState());
                  }
               }
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random random) {
      super.randomDisplayTick(world, pos, state, random);
      if (random.nextInt(10) == 0) {
         world.addParticle(
            ParticleType.TOWN_AURA,
            (double)((float)pos.getX() + random.nextFloat()),
            (double)((float)pos.getY() + 1.1F),
            (double)((float)pos.getZ() + random.nextFloat()),
            0.0,
            0.0,
            0.0
         );
      }
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Blocks.DIRT.getDropItem(Blocks.DIRT.defaultState().set(DirtBlock.VARIANT, DirtBlock.Variant.DIRT), random, fortuneLevel);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return 0;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, SNOWY);
   }
}
