package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.LeadItem;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class FenceBlock extends Block {
   public static final BooleanProperty NORTH = BooleanProperty.of("north");
   public static final BooleanProperty EAST = BooleanProperty.of("east");
   public static final BooleanProperty SOUTH = BooleanProperty.of("south");
   public static final BooleanProperty WEST = BooleanProperty.of("west");

   public FenceBlock(Material c_57ywipuwq) {
      super(c_57ywipuwq);
      this.setDefaultState(this.stateDefinition.any().set(NORTH, false).set(EAST, false).set(SOUTH, false).set(WEST, false));
      this.setItemGroup(ItemGroup.DECORATIONS);
   }

   @Override
   public void getCollisionBoxes(World world, BlockPos pos, BlockState state, Box entityBox, List boxes, Entity entity) {
      boolean var7 = this.shouldConnectTo(world, pos.north());
      boolean var8 = this.shouldConnectTo(world, pos.south());
      boolean var9 = this.shouldConnectTo(world, pos.west());
      boolean var10 = this.shouldConnectTo(world, pos.east());
      float var11 = 0.375F;
      float var12 = 0.625F;
      float var13 = 0.375F;
      float var14 = 0.625F;
      if (var7) {
         var13 = 0.0F;
      }

      if (var8) {
         var14 = 1.0F;
      }

      if (var7 || var8) {
         this.setShape(var11, 0.0F, var13, var12, 1.5F, var14);
         super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      }

      var13 = 0.375F;
      var14 = 0.625F;
      if (var9) {
         var11 = 0.0F;
      }

      if (var10) {
         var12 = 1.0F;
      }

      if (var9 || var10 || !var7 && !var8) {
         this.setShape(var11, 0.0F, var13, var12, 1.5F, var14);
         super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      }

      if (var7) {
         var13 = 0.0F;
      }

      if (var8) {
         var14 = 1.0F;
      }

      this.setShape(var11, 0.0F, var13, var12, 1.0F, var14);
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      boolean var3 = this.shouldConnectTo(world, pos.north());
      boolean var4 = this.shouldConnectTo(world, pos.south());
      boolean var5 = this.shouldConnectTo(world, pos.west());
      boolean var6 = this.shouldConnectTo(world, pos.east());
      float var7 = 0.375F;
      float var8 = 0.625F;
      float var9 = 0.375F;
      float var10 = 0.625F;
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

      this.setShape(var7, 0.0F, var9, var8, 1.0F, var10);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean canWalkThrough(IWorld world, BlockPos pos) {
      return false;
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
   public boolean shouldRenderFace(IWorld world, BlockPos pos, Direction face) {
      return true;
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      return world.isClient ? true : LeadItem.attachLead(player, world, pos);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return 0;
   }

   @Override
   public BlockState updateShape(BlockState state, IWorld world, BlockPos pos) {
      return state.set(NORTH, this.shouldConnectTo(world, pos.north()))
         .set(EAST, this.shouldConnectTo(world, pos.east()))
         .set(SOUTH, this.shouldConnectTo(world, pos.south()))
         .set(WEST, this.shouldConnectTo(world, pos.west()));
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, NORTH, EAST, WEST, SOUTH);
   }
}
