package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.StringRepresentable;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class StonebrickBlock extends Block {
   public static final EnumProperty VARIANT = EnumProperty.of("variant", StonebrickBlock.Variant.class);
   public static final int DEFAULT_INDEX = StonebrickBlock.Variant.DEFAULT.getIndex();
   public static final int MOSSY_INDEX = StonebrickBlock.Variant.MOSSY.getIndex();
   public static final int CRACKED_INDEX = StonebrickBlock.Variant.CRACKED.getIndex();
   public static final int CHISELED_INDEX = StonebrickBlock.Variant.CHISELED.getIndex();

   public StonebrickBlock() {
      super(Material.STONE);
      this.setDefaultState(this.stateDefinition.any().set(VARIANT, StonebrickBlock.Variant.DEFAULT));
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      return ((StonebrickBlock.Variant)state.get(VARIANT)).getIndex();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      for(StonebrickBlock.Variant var7 : StonebrickBlock.Variant.values()) {
         stacks.add(new ItemStack(item, 1, var7.getIndex()));
      }
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(VARIANT, StonebrickBlock.Variant.byIndex(metadata));
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return ((StonebrickBlock.Variant)state.get(VARIANT)).getIndex();
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, VARIANT);
   }

   public static enum Variant implements StringRepresentable {
      DEFAULT(0, "stonebrick", "default"),
      MOSSY(1, "mossy_stonebrick", "mossy"),
      CRACKED(2, "cracked_stonebrick", "cracked"),
      CHISELED(3, "chiseled_stonebrick", "chiseled");

      private static final StonebrickBlock.Variant[] ALL = new StonebrickBlock.Variant[values().length];
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
         return this.id;
      }

      public static StonebrickBlock.Variant byIndex(int index) {
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
         for(StonebrickBlock.Variant var3 : values()) {
            ALL[var3.getIndex()] = var3;
         }
      }
   }
}
