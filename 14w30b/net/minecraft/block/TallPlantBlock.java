package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.client.world.color.GrassColors;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class TallPlantBlock extends PlantBlock implements Fertilizable {
   public static final EnumProperty TYPE = EnumProperty.of("type", TallPlantBlock.Type.class);

   protected TallPlantBlock() {
      super(Material.REPLACEABLE_PLANT);
      this.setDefaultState(this.stateDefinition.any().set(TYPE, TallPlantBlock.Type.DEAD_BUSH));
      float var1 = 0.4F;
      this.setShape(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 0.8F, 0.5F + var1);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor() {
      return GrassColors.getColor(0.5, 1.0);
   }

   @Override
   public boolean isSupported(World world, BlockPos pos, BlockState state) {
      return this.canPlantOn(world.getBlockState(pos.down()).getBlock());
   }

   @Override
   public boolean canBeReplaced(World world, BlockPos pos) {
      return true;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor(int tint) {
      return tint == TallPlantBlock.Type.DEAD_BUSH.getIndex() ? 16777215 : GrassColors.getColor(0.5, 1.0);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor(IWorld world, BlockPos pos, int tint) {
      return world.getBiome(pos).getGrassColor(pos);
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return random.nextInt(8) == 0 ? Items.WHEAT_SEEDS : null;
   }

   @Override
   public int getDropCount(int fortuneLevel, Random random) {
      return 1 + random.nextInt(fortuneLevel * 2 + 1);
   }

   @Override
   public void afterMinedByPlayer(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
      if (!world.isClient && player.getMainHandStack() != null && player.getMainHandStack().getItem() == Items.SHEARS) {
         player.incrementStat(Stats.BLOCKS_MINED[Block.getRawId(this)]);
         this.dropItems(world, pos, new ItemStack(Blocks.TALLGRASS, 1, ((TallPlantBlock.Type)state.get(TYPE)).getIndex()));
      } else {
         super.afterMinedByPlayer(world, player, pos, state, blockEntity);
      }
   }

   @Override
   public int getPickItemMetadata(World world, BlockPos pos) {
      BlockState var3 = world.getBlockState(pos);
      return var3.getBlock().getMetadataFromState(var3);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      for(int var4 = 1; var4 < 3; ++var4) {
         stacks.add(new ItemStack(item, 1, var4));
      }
   }

   @Override
   public boolean canGrow(World world, BlockPos pos, BlockState state, boolean bl) {
      return state.get(TYPE) != TallPlantBlock.Type.DEAD_BUSH;
   }

   @Override
   public boolean canBeFertilized(World world, Random rand, BlockPos pos, BlockState state) {
      return true;
   }

   @Override
   public void grow(World world, Random rand, BlockPos pos, BlockState state) {
      DoublePlantBlock.Variant var5 = DoublePlantBlock.Variant.GRASS;
      if (state.get(TYPE) == TallPlantBlock.Type.FERN) {
         var5 = DoublePlantBlock.Variant.FERN;
      }

      if (Blocks.DOUBLE_PLANT.canSurvive(world, pos)) {
         Blocks.DOUBLE_PLANT.setVariant(world, pos, var5, 2);
      }
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(TYPE, TallPlantBlock.Type.byIndex(metadata));
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return ((TallPlantBlock.Type)state.get(TYPE)).getIndex();
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, TYPE);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.XYZ;
   }

   public static enum Type implements StringRepresentable {
      DEAD_BUSH(0, "dead_bush"),
      GRASS(1, "tall_grass"),
      FERN(2, "fern");

      private static final TallPlantBlock.Type[] ALL = new TallPlantBlock.Type[values().length];
      private final int index;
      private final String id;

      private Type(int index, String id) {
         this.index = index;
         this.id = id;
      }

      public int getIndex() {
         return this.index;
      }

      @Override
      public String toString() {
         return this.id;
      }

      public static TallPlantBlock.Type byIndex(int index) {
         if (index < 0 || index >= ALL.length) {
            index = 0;
         }

         return ALL[index];
      }

      @Override
      public String getStringRepresentation() {
         return this.id;
      }

      static {
         for(TallPlantBlock.Type var3 : values()) {
            ALL[var3.getIndex()] = var3;
         }
      }
   }
}
