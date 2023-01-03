package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.block.state.property.Property;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.StringRepresentable;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class StoneSlabBlock extends SlabBlock {
   public static final BooleanProperty SEAMLESS = BooleanProperty.of("seamless");
   public static final EnumProperty VARIANT = EnumProperty.of("variant", StoneSlabBlock.Variant.class);

   public StoneSlabBlock() {
      super(Material.STONE);
      BlockState var1 = this.stateDefinition.any();
      if (this.isDouble()) {
         var1 = var1.set(SEAMLESS, false);
      } else {
         var1 = var1.set(HALF, SlabBlock.Half.BOTTOM);
      }

      this.setDefaultState(var1.set(VARIANT, StoneSlabBlock.Variant.STONE));
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Item.byBlock(Blocks.STONE_SLAB);
   }

   @Override
   protected ItemStack getSilkTouchDrop(BlockState state) {
      return new ItemStack(Item.byBlock(Blocks.STONE_SLAB), 2, ((StoneSlabBlock.Variant)state.get(VARIANT)).getIndex());
   }

   @Override
   public String getName(int variant) {
      return super.getTranslationKey() + "." + StoneSlabBlock.Variant.byIndex(variant).getName();
   }

   @Override
   public Property getVariantProperty() {
      return VARIANT;
   }

   @Override
   public Object getVariant(ItemStack stack) {
      return StoneSlabBlock.Variant.byIndex(stack.getMetadata() & 7);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      if (item != Item.byBlock(Blocks.DOUBLE_STONE_SLAB)) {
         for(StoneSlabBlock.Variant var7 : StoneSlabBlock.Variant.values()) {
            if (var7 != StoneSlabBlock.Variant.WOOD) {
               stacks.add(new ItemStack(item, 1, var7.getIndex()));
            }
         }
      }
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      BlockState var2 = this.defaultState().set(VARIANT, StoneSlabBlock.Variant.byIndex(metadata & 7));
      if (this.isDouble()) {
         var2 = var2.set(SEAMLESS, (metadata & 8) != 0);
      } else {
         var2 = var2.set(HALF, (metadata & 8) == 0 ? SlabBlock.Half.BOTTOM : SlabBlock.Half.TOP);
      }

      return var2;
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((StoneSlabBlock.Variant)state.get(VARIANT)).getIndex();
      if (this.isDouble()) {
         if (state.get(SEAMLESS)) {
            var2 |= 8;
         }
      } else if (state.get(HALF) == SlabBlock.Half.TOP) {
         var2 |= 8;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return this.isDouble() ? new StateDefinition(this, SEAMLESS, VARIANT) : new StateDefinition(this, HALF, VARIANT);
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      return ((StoneSlabBlock.Variant)state.get(VARIANT)).getIndex();
   }

   public static enum Variant implements StringRepresentable {
      STONE(0, "stone"),
      SAND(1, "sandstone", "sand"),
      WOOD(2, "wood_old", "wood"),
      COBBLESTONE(3, "cobblestone", "cobble"),
      BRICK(4, "brick"),
      SMOOTHBRICK(5, "stone_brick", "smoothStoneBrick"),
      NETHERBRICK(6, "nether_brick", "netherBrick"),
      QUARTZ(7, "quartz");

      private static final StoneSlabBlock.Variant[] ALL = new StoneSlabBlock.Variant[values().length];
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

      public static StoneSlabBlock.Variant byIndex(int index) {
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
         for(StoneSlabBlock.Variant var3 : values()) {
            ALL[var3.getIndex()] = var3;
         }
      }
   }
}
