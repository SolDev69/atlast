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

public class PrismarineBlock extends Block {
   public static final EnumProperty VARIANT = EnumProperty.of("variant", PrismarineBlock.Variant.class);
   public static final int ROUGH_VARIANT = PrismarineBlock.Variant.ROUGH.getIndex();
   public static final int BRICKS_VARIANT = PrismarineBlock.Variant.BRICKS.getIndex();
   public static final int DARK_VARIANT = PrismarineBlock.Variant.DARK.getIndex();

   public PrismarineBlock() {
      super(Material.STONE);
      this.setDefaultState(this.stateDefinition.any().set(VARIANT, PrismarineBlock.Variant.ROUGH));
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      return ((PrismarineBlock.Variant)state.get(VARIANT)).getIndex();
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return ((PrismarineBlock.Variant)state.get(VARIANT)).getIndex();
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, VARIANT);
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(VARIANT, PrismarineBlock.Variant.byIndex(metadata));
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      stacks.add(new ItemStack(item, 1, ROUGH_VARIANT));
      stacks.add(new ItemStack(item, 1, BRICKS_VARIANT));
      stacks.add(new ItemStack(item, 1, DARK_VARIANT));
   }

   public static enum Variant implements StringRepresentable {
      ROUGH(0, "prismarine", "rough"),
      BRICKS(1, "prismarine_bricks", "bricks"),
      DARK(2, "dark_prismarine", "dark");

      private static final PrismarineBlock.Variant[] ALL = new PrismarineBlock.Variant[values().length];
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

      public static PrismarineBlock.Variant byIndex(int index) {
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
         for(PrismarineBlock.Variant var3 : values()) {
            ALL[var3.getIndex()] = var3;
         }
      }
   }
}
