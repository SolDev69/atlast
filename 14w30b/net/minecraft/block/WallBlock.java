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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class WallBlock extends Block {
   public static final BooleanProperty UP = BooleanProperty.of("up");
   public static final BooleanProperty NORTH = BooleanProperty.of("north");
   public static final BooleanProperty EAST = BooleanProperty.of("east");
   public static final BooleanProperty SOUTH = BooleanProperty.of("south");
   public static final BooleanProperty WEST = BooleanProperty.of("west");
   public static final EnumProperty VARIANT = EnumProperty.of("variant", WallBlock.Variant.class);

   public WallBlock(Block block) {
      super(block.material);
      this.setDefaultState(
         this.stateDefinition.any().set(UP, false).set(NORTH, false).set(EAST, false).set(SOUTH, false).set(WEST, false).set(VARIANT, WallBlock.Variant.NORMAL)
      );
      this.setStrength(block.miningSpeed);
      this.setResistance(block.resistance / 3.0F);
      this.setSound(block.sound);
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean canWalkThrough(IWorld world, BlockPos pos) {
      return false;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      boolean var3 = this.shouldConnectTo(world, pos.north());
      boolean var4 = this.shouldConnectTo(world, pos.south());
      boolean var5 = this.shouldConnectTo(world, pos.west());
      boolean var6 = this.shouldConnectTo(world, pos.east());
      float var7 = 0.25F;
      float var8 = 0.75F;
      float var9 = 0.25F;
      float var10 = 0.75F;
      float var11 = 1.0F;
      if (var3) {
         var9 = 0.0F;
      }

      if (var4) {
         var10 = 1.0F;
      }

      if (var5) {
         var7 = 0.0F;
      }

      if (var6) {
         var8 = 1.0F;
      }

      if (var3 && var4 && !var5 && !var6) {
         var11 = 0.8125F;
         var7 = 0.3125F;
         var8 = 0.6875F;
      } else if (!var3 && !var4 && var5 && var6) {
         var11 = 0.8125F;
         var9 = 0.3125F;
         var10 = 0.6875F;
      }

      this.setShape(var7, 0.0F, var9, var8, var11, var10);
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      this.updateShape(world, pos);
      this.maxY = 1.5;
      return super.getCollisionShape(world, pos, state);
   }

   public boolean shouldConnectTo(IWorld world, BlockPos pos) {
      Block var3 = world.getBlockState(pos).getBlock();
      if (var3 == Blocks.BARRIER) {
         return false;
      } else if (var3 == this || var3 == Blocks.FENCE_GATE) {
         return true;
      } else if (var3.material.isSolidBlocking() && var3.isFullCube()) {
         return var3.material != Material.PUMPKIN;
      } else {
         return false;
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      for(WallBlock.Variant var7 : WallBlock.Variant.values()) {
         stacks.add(new ItemStack(item, 1, var7.getIndex()));
      }
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      return ((WallBlock.Variant)state.get(VARIANT)).getIndex();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldRenderFace(IWorld world, BlockPos pos, Direction face) {
      return face == Direction.DOWN ? super.shouldRenderFace(world, pos, face) : true;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(VARIANT, WallBlock.Variant.byIndex(metadata));
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return ((WallBlock.Variant)state.get(VARIANT)).getIndex();
   }

   @Override
   public BlockState updateShape(BlockState state, IWorld world, BlockPos pos) {
      return state.set(UP, !world.isAir(pos.up()))
         .set(NORTH, this.shouldConnectTo(world, pos.north()))
         .set(EAST, this.shouldConnectTo(world, pos.east()))
         .set(SOUTH, this.shouldConnectTo(world, pos.south()))
         .set(WEST, this.shouldConnectTo(world, pos.west()));
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, UP, NORTH, EAST, WEST, SOUTH, VARIANT);
   }

   public static enum Variant implements StringRepresentable {
      NORMAL(0, "cobblestone", "normal"),
      MOSSY(1, "mossy_cobblestone", "mossy");

      private static final WallBlock.Variant[] ALL = new WallBlock.Variant[values().length];
      private final int index;
      private final String id;
      private String name;

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

      public static WallBlock.Variant byIndex(int index) {
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
         for(WallBlock.Variant var3 : values()) {
            ALL[var3.getIndex()] = var3;
         }
      }
   }
}
