package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class MushroomBlock extends Block {
   public static final EnumProperty VARIANT = EnumProperty.of("variant", MushroomBlock.Variant.class);
   private final int type;

   public MushroomBlock(Material material, int type) {
      super(material);
      this.setDefaultState(this.stateDefinition.any().set(VARIANT, MushroomBlock.Variant.ALL_OUTSIDE));
      this.type = type;
   }

   @Override
   public int getBaseDropCount(Random random) {
      return Math.max(0, random.nextInt(10) - 7);
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Item.byRawId(Block.getRawId(Blocks.BROWN_MUSHROOM) + this.type);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Item.byRawId(Block.getRawId(Blocks.BROWN_MUSHROOM) + this.type);
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      return this.defaultState();
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(VARIANT, MushroomBlock.Variant.byIndex(metadata));
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return ((MushroomBlock.Variant)state.get(VARIANT)).getIndex();
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, VARIANT);
   }

   public static enum Variant implements StringRepresentable {
      NORTH_WEST(1, "north_west"),
      NORTH(2, "north"),
      NORTH_EAST(3, "north_east"),
      WEST(4, "west"),
      CENTER(5, "center"),
      EAST(6, "east"),
      SOUTH_WEST(7, "south_west"),
      SOUTH(8, "south"),
      SOUTH_EAST(9, "south_east"),
      STEM(10, "stem"),
      ALL_INSIDE(0, "all_inside"),
      ALL_OUTSIDE(14, "all_outside"),
      ALL_STEM(15, "all_stem");

      private static final MushroomBlock.Variant[] ALL = new MushroomBlock.Variant[16];
      private final int index;
      private final String id;

      private Variant(int index, String id) {
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

      public static MushroomBlock.Variant byIndex(int index) {
         if (index < 0 || index >= ALL.length) {
            index = 0;
         }

         MushroomBlock.Variant var1 = ALL[index];
         return var1 == null ? ALL[0] : var1;
      }

      @Override
      public String getStringRepresentation() {
         return this.id;
      }

      static {
         for(MushroomBlock.Variant var3 : values()) {
            ALL[var3.getIndex()] = var3;
         }
      }
   }
}
