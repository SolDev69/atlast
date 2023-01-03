package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class ColoredBlock extends Block {
   public static final EnumProperty COLOR = EnumProperty.of("color", DyeColor.class);

   public ColoredBlock(Material c_57ywipuwq) {
      super(c_57ywipuwq);
      this.setDefaultState(this.stateDefinition.any().set(COLOR, DyeColor.WHITE));
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      return ((DyeColor)state.get(COLOR)).getIndex();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      for(DyeColor var7 : DyeColor.values()) {
         stacks.add(new ItemStack(item, 1, var7.getIndex()));
      }
   }

   @Override
   public MaterialColor getMaterialColor(BlockState state) {
      return ((DyeColor)state.get(COLOR)).getMaterialColor();
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
      return new StateDefinition(this, COLOR);
   }
}
