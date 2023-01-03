package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.block.state.property.IntegerProperty;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.AcaciaTreeFeature;
import net.minecraft.world.gen.feature.BirchTreeFeature;
import net.minecraft.world.gen.feature.DarkOakTreeFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.GiantJungleTreeFeature;
import net.minecraft.world.gen.feature.GiantSpruceTreeFeature;
import net.minecraft.world.gen.feature.LargeOakTreeFeature;
import net.minecraft.world.gen.feature.SpruceTreeFeature;
import net.minecraft.world.gen.feature.TreeFeature;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SaplingBlock extends PlantBlock implements Fertilizable {
   public static final EnumProperty TYPE = EnumProperty.of("type", PlanksBlock.Variant.class);
   public static final IntegerProperty STAGE = IntegerProperty.of("stage", 0, 1);

   protected SaplingBlock() {
      this.setDefaultState(this.stateDefinition.any().set(TYPE, PlanksBlock.Variant.OAK).set(STAGE, 0));
      float var1 = 0.4F;
      this.setShape(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, var1 * 2.0F, 0.5F + var1);
      this.setItemGroup(ItemGroup.DECORATIONS);
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (!world.isClient) {
         super.tick(world, pos, state, random);
         if (world.getRawBrightness(pos.up()) >= 9 && random.nextInt(7) == 0) {
            this.tryGrow(world, pos, state, random);
         }
      }
   }

   public void tryGrow(World world, BlockPos pos, BlockState state, Random random) {
      if (state.get(STAGE) == 0) {
         world.setBlockState(pos, state.next(STAGE), 4);
      } else {
         this.grow(world, pos, state, random);
      }
   }

   public void grow(World world, BlockPos pos, BlockState state, Random random) {
      Object var5 = random.nextInt(10) == 0 ? new LargeOakTreeFeature(true) : new TreeFeature(true);
      int var6 = 0;
      int var7 = 0;
      boolean var8 = false;
      switch((PlanksBlock.Variant)state.get(TYPE)) {
         case SPRUCE:
            label80:
            for(var6 = 0; var6 >= -1; --var6) {
               for(var7 = 0; var7 >= -1; --var7) {
                  if (this.isSapling(world, pos.add(var6, 0, var7), PlanksBlock.Variant.SPRUCE)
                     && this.isSapling(world, pos.add(var6 + 1, 0, var7), PlanksBlock.Variant.SPRUCE)
                     && this.isSapling(world, pos.add(var6, 0, var7 + 1), PlanksBlock.Variant.SPRUCE)
                     && this.isSapling(world, pos.add(var6 + 1, 0, var7 + 1), PlanksBlock.Variant.SPRUCE)) {
                     var5 = new GiantSpruceTreeFeature(false, random.nextBoolean());
                     var8 = true;
                     break label80;
                  }
               }
            }

            if (!var8) {
               var7 = 0;
               var6 = 0;
               var5 = new SpruceTreeFeature(true);
            }
            break;
         case BIRCH:
            var5 = new BirchTreeFeature(true, false);
            break;
         case JUNGLE:
            label97:
            for(var6 = 0; var6 >= -1; --var6) {
               for(var7 = 0; var7 >= -1; --var7) {
                  if (this.isSapling(world, pos.add(var6, 0, var7), PlanksBlock.Variant.JUNGLE)
                     && this.isSapling(world, pos.add(var6 + 1, 0, var7), PlanksBlock.Variant.JUNGLE)
                     && this.isSapling(world, pos.add(var6, 0, var7 + 1), PlanksBlock.Variant.JUNGLE)
                     && this.isSapling(world, pos.add(var6 + 1, 0, var7 + 1), PlanksBlock.Variant.JUNGLE)) {
                     var5 = new GiantJungleTreeFeature(true, 10, 20, PlanksBlock.Variant.JUNGLE.getIndex(), PlanksBlock.Variant.JUNGLE.getIndex());
                     var8 = true;
                     break label97;
                  }
               }
            }

            if (!var8) {
               var7 = 0;
               var6 = 0;
               var5 = new TreeFeature(true, 4 + random.nextInt(7), PlanksBlock.Variant.JUNGLE.getIndex(), PlanksBlock.Variant.JUNGLE.getIndex(), false);
            }
            break;
         case ACACIA:
            var5 = new AcaciaTreeFeature(true);
            break;
         case DARK_OAK:
            label114:
            for(var6 = 0; var6 >= -1; --var6) {
               for(var7 = 0; var7 >= -1; --var7) {
                  if (this.isSapling(world, pos.add(var6, 0, var7), PlanksBlock.Variant.DARK_OAK)
                     && this.isSapling(world, pos.add(var6 + 1, 0, var7), PlanksBlock.Variant.DARK_OAK)
                     && this.isSapling(world, pos.add(var6, 0, var7 + 1), PlanksBlock.Variant.DARK_OAK)
                     && this.isSapling(world, pos.add(var6 + 1, 0, var7 + 1), PlanksBlock.Variant.DARK_OAK)) {
                     var5 = new DarkOakTreeFeature(true);
                     var8 = true;
                     break label114;
                  }
               }
            }

            if (!var8) {
               return;
            }
         case OAK:
      }

      BlockState var9 = Blocks.AIR.defaultState();
      if (var8) {
         world.setBlockState(pos.add(var6, 0, var7), var9, 4);
         world.setBlockState(pos.add(var6 + 1, 0, var7), var9, 4);
         world.setBlockState(pos.add(var6, 0, var7 + 1), var9, 4);
         world.setBlockState(pos.add(var6 + 1, 0, var7 + 1), var9, 4);
      } else {
         world.setBlockState(pos, var9, 4);
      }

      if (!((Feature)var5).place(world, random, pos.add(var6, 0, var7))) {
         if (var8) {
            world.setBlockState(pos.add(var6, 0, var7), state, 4);
            world.setBlockState(pos.add(var6 + 1, 0, var7), state, 4);
            world.setBlockState(pos.add(var6, 0, var7 + 1), state, 4);
            world.setBlockState(pos.add(var6 + 1, 0, var7 + 1), state, 4);
         } else {
            world.setBlockState(pos, state, 4);
         }
      }
   }

   public boolean isSapling(World world, BlockPos pos, PlanksBlock.Variant variant) {
      BlockState var4 = world.getBlockState(pos);
      return var4.getBlock() == this && var4.get(TYPE) == variant;
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      return ((PlanksBlock.Variant)state.get(TYPE)).getIndex();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      for(PlanksBlock.Variant var7 : PlanksBlock.Variant.values()) {
         stacks.add(new ItemStack(item, 1, var7.getIndex()));
      }
   }

   @Override
   public boolean canGrow(World world, BlockPos pos, BlockState state, boolean bl) {
      return true;
   }

   @Override
   public boolean canBeFertilized(World world, Random rand, BlockPos pos, BlockState state) {
      return (double)world.random.nextFloat() < 0.45;
   }

   @Override
   public void grow(World world, Random rand, BlockPos pos, BlockState state) {
      this.tryGrow(world, pos, state, rand);
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(TYPE, PlanksBlock.Variant.byIndex(metadata & 7)).set(STAGE, (metadata & 8) >> 3);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((PlanksBlock.Variant)state.get(TYPE)).getIndex();
      return var2 | state.get(STAGE) << 3;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, TYPE, STAGE);
   }
}
