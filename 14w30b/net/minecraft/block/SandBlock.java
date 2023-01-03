package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.StringRepresentable;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SandBlock extends FallingBlock {
   public static final EnumProperty VARIANT = EnumProperty.of("variant", SandBlock.Variant.class);

   public SandBlock() {
      this.setDefaultState(this.stateDefinition.any().set(VARIANT, SandBlock.Variant.SAND));
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      return ((SandBlock.Variant)state.get(VARIANT)).getIndex();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      for(SandBlock.Variant var7 : SandBlock.Variant.values()) {
         stacks.add(new ItemStack(item, 1, var7.getIndex()));
      }
   }

   @Override
   public MaterialColor getMaterialColor(BlockState state) {
      return ((SandBlock.Variant)state.get(VARIANT)).getColor();
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(VARIANT, SandBlock.Variant.byIndex(metadata));
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return ((SandBlock.Variant)state.get(VARIANT)).getIndex();
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, VARIANT);
   }

   public static enum Variant implements StringRepresentable {
      SAND(0, "sand", "default", MaterialColor.SAND),
      RED_SAND(1, "red_sand", "red", MaterialColor.DIRT);

      private static final SandBlock.Variant[] ALL = new SandBlock.Variant[values().length];
      private final int index;
      private final String id;
      private final MaterialColor color;
      private final String name;

      private Variant(int index, String id, String name, MaterialColor color) {
         this.index = index;
         this.id = id;
         this.color = color;
         this.name = name;
      }

      public int getIndex() {
         return this.index;
      }

      @Override
      public String toString() {
         return this.id;
      }

      public MaterialColor getColor() {
         return this.color;
      }

      public static SandBlock.Variant byIndex(int index) {
         if (index < 0 || index >= ALL.length) {
            index = 0;
         }

         return ALL[index];
      }

      @Override
      public String getStringRepresentation() {
         return this.id;
      }

      public String getName() {
         return this.name;
      }

      static {
         for(SandBlock.Variant var3 : values()) {
            ALL[var3.getIndex()] = var3;
         }
      }
   }
}
