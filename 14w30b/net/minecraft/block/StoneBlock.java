package net.minecraft.block;

import java.util.List;
import java.util.Random;
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

public class StoneBlock extends Block {
   public static final EnumProperty VARIANT = EnumProperty.of("variant", StoneBlock.Variant.class);

   public StoneBlock() {
      super(Material.STONE);
      this.setDefaultState(this.stateDefinition.any().set(VARIANT, StoneBlock.Variant.STONE));
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return state.get(VARIANT) == StoneBlock.Variant.STONE ? Item.byBlock(Blocks.COBBLESTONE) : Item.byBlock(Blocks.STONE);
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      return ((StoneBlock.Variant)state.get(VARIANT)).getIndex();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      for(StoneBlock.Variant var7 : StoneBlock.Variant.values()) {
         stacks.add(new ItemStack(item, 1, var7.getIndex()));
      }
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(VARIANT, StoneBlock.Variant.byIndex(metadata));
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return ((StoneBlock.Variant)state.get(VARIANT)).getIndex();
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, VARIANT);
   }

   public static enum Variant implements StringRepresentable {
      STONE(0, "stone"),
      GRANITE(1, "granite"),
      GRANITE_SMOOTH(2, "smooth_granite", "graniteSmooth"),
      DIORITE(3, "diorite"),
      DIORITE_SMOOTH(4, "smooth_diorite", "dioriteSmooth"),
      ANDESITE(5, "andesite"),
      ANDESITE_SMOOTH(6, "smooth_andesite", "andesiteSmooth");

      private static final StoneBlock.Variant[] ALL = new StoneBlock.Variant[values().length];
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

      @Override
      public String toString() {
         return this.id;
      }

      public static StoneBlock.Variant byIndex(int index) {
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
         for(StoneBlock.Variant var3 : values()) {
            ALL[var3.getIndex()] = var3;
         }
      }
   }
}
