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

public class SandstoneBlock extends Block {
   public static final EnumProperty TYPE = EnumProperty.of("type", SandstoneBlock.Type.class);

   public SandstoneBlock() {
      super(Material.STONE);
      this.setDefaultState(this.stateDefinition.any().set(TYPE, SandstoneBlock.Type.DEFAULT));
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      return ((SandstoneBlock.Type)state.get(TYPE)).getIndex();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      for(SandstoneBlock.Type var7 : SandstoneBlock.Type.values()) {
         stacks.add(new ItemStack(item, 1, var7.getIndex()));
      }
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(TYPE, SandstoneBlock.Type.byIndex(metadata));
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return ((SandstoneBlock.Type)state.get(TYPE)).getIndex();
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, TYPE);
   }

   public static enum Type implements StringRepresentable {
      DEFAULT(0, "sandstone", "default"),
      CHISELED(1, "chiseled_sandstone", "chiseled"),
      SMOOTH(2, "smooth_sandstone", "smooth");

      private static final SandstoneBlock.Type[] ALL = new SandstoneBlock.Type[values().length];
      private final int index;
      private final String id;
      private final String name;

      private Type(int index, String id, String name) {
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

      public static SandstoneBlock.Type byIndex(int index) {
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
         for(SandstoneBlock.Type var3 : values()) {
            ALL[var3.getIndex()] = var3;
         }
      }
   }
}
