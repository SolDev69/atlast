package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.block.state.property.Property;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class WoodenSlabBlock extends SlabBlock {
   public static final EnumProperty VARIANT = EnumProperty.of("variant", PlanksBlock.Variant.class);

   public WoodenSlabBlock() {
      super(Material.WOOD);
      BlockState var1 = this.stateDefinition.any();
      if (!this.isDouble()) {
         var1 = var1.set(HALF, SlabBlock.Half.BOTTOM);
      }

      this.setDefaultState(var1.set(VARIANT, PlanksBlock.Variant.OAK));
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Item.byBlock(Blocks.WOODEN_SLAB);
   }

   @Override
   protected ItemStack getSilkTouchDrop(BlockState state) {
      return new ItemStack(Item.byBlock(Blocks.WOODEN_SLAB), 2, ((PlanksBlock.Variant)state.get(VARIANT)).getIndex());
   }

   @Override
   public String getName(int variant) {
      return super.getTranslationKey() + "." + PlanksBlock.Variant.byIndex(variant).getName();
   }

   @Override
   public Property getVariantProperty() {
      return VARIANT;
   }

   @Override
   public Object getVariant(ItemStack stack) {
      return PlanksBlock.Variant.byIndex(stack.getMetadata() & 7);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      if (item != Item.byBlock(Blocks.DOUBLE_WOODEN_SLAB)) {
         for(PlanksBlock.Variant var7 : PlanksBlock.Variant.values()) {
            stacks.add(new ItemStack(item, 1, var7.getIndex()));
         }
      }
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      BlockState var2 = this.defaultState().set(VARIANT, PlanksBlock.Variant.byIndex(metadata & 7));
      if (!this.isDouble()) {
         var2 = var2.set(HALF, (metadata & 8) == 0 ? SlabBlock.Half.BOTTOM : SlabBlock.Half.TOP);
      }

      return var2;
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((PlanksBlock.Variant)state.get(VARIANT)).getIndex();
      if (!this.isDouble() && state.get(HALF) == SlabBlock.Half.TOP) {
         var2 |= 8;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return this.isDouble() ? new StateDefinition(this, VARIANT) : new StateDefinition(this, HALF, VARIANT);
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      return ((PlanksBlock.Variant)state.get(VARIANT)).getIndex();
   }
}
