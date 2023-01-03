package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class QuartzBlock extends Block {
   public static final EnumProperty VARIANT = EnumProperty.of("variant", QuartzBlock.Variant.class);

   public QuartzBlock() {
      super(Material.STONE);
      this.setDefaultState(this.stateDefinition.any().set(VARIANT, QuartzBlock.Variant.DEFAULT));
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      if (metadata == QuartzBlock.Variant.LINES_Y.getIndex()) {
         switch(dir.getAxis()) {
            case Z:
               return this.defaultState().set(VARIANT, QuartzBlock.Variant.LINES_Z);
            case X:
               return this.defaultState().set(VARIANT, QuartzBlock.Variant.LINES_X);
            case Y:
            default:
               return this.defaultState().set(VARIANT, QuartzBlock.Variant.LINES_Y);
         }
      } else {
         return metadata == QuartzBlock.Variant.CHISELED.getIndex()
            ? this.defaultState().set(VARIANT, QuartzBlock.Variant.CHISELED)
            : this.defaultState().set(VARIANT, QuartzBlock.Variant.DEFAULT);
      }
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      QuartzBlock.Variant var2 = (QuartzBlock.Variant)state.get(VARIANT);
      return var2 != QuartzBlock.Variant.LINES_X && var2 != QuartzBlock.Variant.LINES_Z ? var2.getIndex() : QuartzBlock.Variant.LINES_Y.getIndex();
   }

   @Override
   protected ItemStack getSilkTouchDrop(BlockState state) {
      QuartzBlock.Variant var2 = (QuartzBlock.Variant)state.get(VARIANT);
      return var2 != QuartzBlock.Variant.LINES_X && var2 != QuartzBlock.Variant.LINES_Z
         ? super.getSilkTouchDrop(state)
         : new ItemStack(Item.byBlock(this), 1, QuartzBlock.Variant.LINES_Y.getIndex());
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      stacks.add(new ItemStack(item, 1, QuartzBlock.Variant.DEFAULT.getIndex()));
      stacks.add(new ItemStack(item, 1, QuartzBlock.Variant.CHISELED.getIndex()));
      stacks.add(new ItemStack(item, 1, QuartzBlock.Variant.LINES_Y.getIndex()));
   }

   @Override
   public MaterialColor getMaterialColor(BlockState state) {
      return MaterialColor.QUARTZ;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(VARIANT, QuartzBlock.Variant.byIndex(metadata));
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return ((QuartzBlock.Variant)state.get(VARIANT)).getIndex();
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, VARIANT);
   }

   public static enum Variant implements StringRepresentable {
      DEFAULT(0, "default", "default"),
      CHISELED(1, "chiseled", "chiseled"),
      LINES_Y(2, "lines_y", "lines"),
      LINES_X(3, "lines_x", "lines"),
      LINES_Z(4, "lines_z", "lines");

      private static final QuartzBlock.Variant[] ALL = new QuartzBlock.Variant[values().length];
      private final int index;
      private final String id;
      private final String name;

      private Variant(int index, String id, String name) {
         this.index = index;
         this.id = id;
         this.name = name;
      }

      public int getIndex() {
         return this.index;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static QuartzBlock.Variant byIndex(int index) {
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
         for(QuartzBlock.Variant var3 : values()) {
            ALL[var3.getIndex()] = var3;
         }
      }
   }
}
