package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class StainedGlassPaneBlock extends PaneBlock {
   public static final EnumProperty COLOR = EnumProperty.of("color", DyeColor.class);

   public StainedGlassPaneBlock() {
      super(Material.GLASS, false);
      this.setDefaultState(this.stateDefinition.any().set(NORTH, false).set(EAST, false).set(SOUTH, false).set(WEST, false).set(COLOR, DyeColor.WHITE));
      this.setItemGroup(ItemGroup.DECORATIONS);
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      return ((DyeColor)state.get(COLOR)).getIndex();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      for(int var4 = 0; var4 < DyeColor.values().length; ++var4) {
         stacks.add(new ItemStack(item, 1, var4));
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.TRANSLUCENT;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(COLOR, DyeColor.byIndex(metadata));
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return ((DyeColor)state.get(COLOR)).getIndex();
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, NORTH, EAST, WEST, SOUTH, COLOR);
   }
}
