package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PaneBlock extends Block {
   public static final BooleanProperty NORTH = BooleanProperty.of("north");
   public static final BooleanProperty EAST = BooleanProperty.of("east");
   public static final BooleanProperty SOUTH = BooleanProperty.of("south");
   public static final BooleanProperty WEST = BooleanProperty.of("west");
   private final boolean hasDrops;

   protected PaneBlock(Material material, boolean hasDrops) {
      super(material);
      this.setDefaultState(this.stateDefinition.any().set(NORTH, false).set(EAST, false).set(SOUTH, false).set(WEST, false));
      this.hasDrops = hasDrops;
      this.setItemGroup(ItemGroup.DECORATIONS);
   }

   @Override
   public BlockState updateShape(BlockState state, IWorld world, BlockPos pos) {
      return state.set(NORTH, this.shouldConnectTo(world.getBlockState(pos.north()).getBlock()))
         .set(SOUTH, this.shouldConnectTo(world.getBlockState(pos.south()).getBlock()))
         .set(WEST, this.shouldConnectTo(world.getBlockState(pos.west()).getBlock()))
         .set(EAST, this.shouldConnectTo(world.getBlockState(pos.east()).getBlock()));
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return !this.hasDrops ? null : super.getDropItem(state, random, fortuneLevel);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldRenderFace(IWorld world, BlockPos pos, Direction face) {
      return world.getBlockState(pos).getBlock() == this ? false : super.shouldRenderFace(world, pos, face);
   }

   @Override
   public void getCollisionBoxes(World world, BlockPos pos, BlockState state, Box entityBox, List boxes, Entity entity) {
      boolean var7 = this.shouldConnectTo(world.getBlockState(pos.north()).getBlock());
      boolean var8 = this.shouldConnectTo(world.getBlockState(pos.south()).getBlock());
      boolean var9 = this.shouldConnectTo(world.getBlockState(pos.west()).getBlock());
      boolean var10 = this.shouldConnectTo(world.getBlockState(pos.east()).getBlock());
      if ((!var9 || !var10) && (var9 || var10 || var7 || var8)) {
         if (var9) {
            this.setShape(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F);
            super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
         } else if (var10) {
            this.setShape(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
            super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
         }
      } else {
         this.setShape(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
         super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      }

      if ((!var7 || !var8) && (var9 || var10 || var7 || var8)) {
         if (var7) {
            this.setShape(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F);
            super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
         } else if (var8) {
            this.setShape(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F);
            super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
         }
      } else {
         this.setShape(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F);
         super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      }
   }

   @Override
   public void setBlockItemBounds() {
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      float var3 = 0.4375F;
      float var4 = 0.5625F;
      float var5 = 0.4375F;
      float var6 = 0.5625F;
      boolean var7 = this.shouldConnectTo(world.getBlockState(pos.north()).getBlock());
      boolean var8 = this.shouldConnectTo(world.getBlockState(pos.south()).getBlock());
      boolean var9 = this.shouldConnectTo(world.getBlockState(pos.west()).getBlock());
      boolean var10 = this.shouldConnectTo(world.getBlockState(pos.east()).getBlock());
      if ((!var9 || !var10) && (var9 || var10 || var7 || var8)) {
         if (var9) {
            var3 = 0.0F;
         } else if (var10) {
            var4 = 1.0F;
         }
      } else {
         var3 = 0.0F;
         var4 = 1.0F;
      }

      if ((!var7 || !var8) && (var9 || var10 || var7 || var8)) {
         if (var7) {
            var5 = 0.0F;
         } else if (var8) {
            var6 = 1.0F;
         }
      } else {
         var5 = 0.0F;
         var6 = 1.0F;
      }

      this.setShape(var3, 0.0F, var5, var4, 1.0F, var6);
   }

   public final boolean shouldConnectTo(Block block) {
      return block.isOpaque()
         || block == this
         || block == Blocks.GLASS
         || block == Blocks.STAINED_GLASS
         || block == Blocks.STAINED_GLASS_PANE
         || block instanceof PaneBlock;
   }

   @Override
   protected boolean hasSilkTouchDrops() {
      return true;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT_MIPPED;
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return 0;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, NORTH, EAST, WEST, SOUTH);
   }
}
