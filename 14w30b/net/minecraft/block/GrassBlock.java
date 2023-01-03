package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.client.world.color.BiomeColors;
import net.minecraft.client.world.color.GrassColors;
import net.minecraft.item.Item;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class GrassBlock extends Block implements Fertilizable {
   public static final BooleanProperty SNOWY = BooleanProperty.of("snowy");

   protected GrassBlock() {
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

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor() {
      return GrassColors.getColor(0.5, 1.0);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor(int tint) {
      return this.getColor();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor(IWorld world, BlockPos pos, int tint) {
      return BiomeColors.getGrassColor(world, pos);
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (!world.isClient) {
         if (world.getRawBrightness(pos.up()) < 4 && world.getBlockState(pos.up()).getBlock().getOpacity() > 2) {
            world.setBlockState(pos, Blocks.DIRT.defaultState());
         } else {
            if (world.getRawBrightness(pos.up()) >= 9) {
               for(int var5 = 0; var5 < 4; ++var5) {
                  BlockPos var6 = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                  Block var7 = world.getBlockState(var6.up()).getBlock();
                  BlockState var8 = world.getBlockState(var6);
                  if (var8.getBlock() == Blocks.DIRT
                     && var8.get(DirtBlock.VARIANT) == DirtBlock.Variant.DIRT
                     && world.getRawBrightness(var6.up()) >= 4
                     && var7.getOpacity() <= 2) {
                     world.setBlockState(var6, Blocks.GRASS.defaultState());
                  }
               }
            }
         }
      }
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Blocks.DIRT.getDropItem(Blocks.DIRT.defaultState().set(DirtBlock.VARIANT, DirtBlock.Variant.DIRT), random, fortuneLevel);
   }

   @Override
   public boolean canGrow(World world, BlockPos pos, BlockState state, boolean bl) {
      return true;
   }

   @Override
   public boolean canBeFertilized(World world, Random rand, BlockPos pos, BlockState state) {
      return true;
   }

   @Override
   public void grow(World world, Random rand, BlockPos pos, BlockState state) {
      BlockPos var5 = pos.up();

      label38:
      for(int var6 = 0; var6 < 128; ++var6) {
         BlockPos var7 = var5;

         for(int var8 = 0; var8 < var6 / 16; ++var8) {
            var7 = var7.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);
            if (world.getBlockState(var7.down()).getBlock() != Blocks.GRASS || world.getBlockState(var7).getBlock().isConductor()) {
               continue label38;
            }
         }

         if (world.getBlockState(var7).getBlock().material == Material.AIR) {
            if (rand.nextInt(8) == 0) {
               FlowerBlock.Type var11 = world.getBiome(var7).getRandomFlower(rand, var7);
               FlowerBlock var9 = var11.getGroup().getBlock();
               BlockState var10 = var9.defaultState().set(var9.getTypeProperty(), var11);
               if (var9.isSupported(world, var7, var10)) {
                  world.setBlockState(var7, var10, 3);
               }
            } else {
               BlockState var12 = Blocks.TALLGRASS.defaultState().set(TallPlantBlock.TYPE, TallPlantBlock.Type.GRASS);
               if (Blocks.TALLGRASS.isSupported(world, var7, var12)) {
                  world.setBlockState(var7, var12, 3);
               }
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT_MIPPED;
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
