package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class DirtBlock extends Block {
   public static final EnumProperty VARIANT = EnumProperty.of("variant", DirtBlock.Variant.class);
   public static final BooleanProperty SNOWY = BooleanProperty.of("snowy");

   protected DirtBlock() {
      super(Material.DIRT);
      this.setDefaultState(this.stateDefinition.any().set(VARIANT, DirtBlock.Variant.DIRT).set(SNOWY, false));
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public BlockState updateShape(BlockState state, IWorld world, BlockPos pos) {
      if (state.get(VARIANT) == DirtBlock.Variant.PODZOL) {
         Block var4 = world.getBlockState(pos.up()).getBlock();
         state = state.set(SNOWY, var4 == Blocks.SNOW || var4 == Blocks.SNOW_LAYER);
      }

      return state;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      stacks.add(new ItemStack(this, 1, DirtBlock.Variant.DIRT.getIndex()));
      stacks.add(new ItemStack(this, 1, DirtBlock.Variant.DOARSE_DIRT.getIndex()));
      stacks.add(new ItemStack(this, 1, DirtBlock.Variant.PODZOL.getIndex()));
   }

   @Override
   public int getPickItemMetadata(World world, BlockPos pos) {
      BlockState var3 = world.getBlockState(pos);
      return var3.getBlock() != this ? 0 : ((DirtBlock.Variant)var3.get(VARIANT)).getIndex();
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(VARIANT, DirtBlock.Variant.byIndex(metadata));
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return ((DirtBlock.Variant)state.get(VARIANT)).getIndex();
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, VARIANT, SNOWY);
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      DirtBlock.Variant var2 = (DirtBlock.Variant)state.get(VARIANT);
      if (var2 == DirtBlock.Variant.PODZOL) {
         var2 = DirtBlock.Variant.DIRT;
      }

      return var2.getIndex();
   }

   public static enum Variant implements StringRepresentable {
      DIRT(0, "dirt", "default"),
      DOARSE_DIRT(1, "coarse_dirt", "coarse"),
      PODZOL(2, "podzol");

      private static final DirtBlock.Variant[] ALL = new DirtBlock.Variant[values().length];
      private final int index;
      private final String id;
      private final String name;

      private Variant(int index, String id) {
         this(index, id, id);
      }

      private Variant(int index, String id, String name) {
         this.index = index;
         this.id = id;
         this.name = name;
      }

      public int getIndex() {
         return this.index;
      }

      public String getName() {
         return this.name;
      }

      @Override
      public String toString() {
         return this.id;
      }

      public static DirtBlock.Variant byIndex(int index) {
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
         for(DirtBlock.Variant var3 : values()) {
            ALL[var3.getIndex()] = var3;
         }
      }
   }
}
