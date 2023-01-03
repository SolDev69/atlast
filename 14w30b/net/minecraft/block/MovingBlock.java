package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MovingBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class MovingBlock extends BlockWithBlockEntity {
   public static final DirectionProperty FACING = PistonHeadBlock.FACING;
   public static final EnumProperty TYPE = PistonHeadBlock.TYPE;

   public MovingBlock() {
      super(Material.PISTON);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH).set(TYPE, PistonHeadBlock.Type.DEFAULT));
      this.setStrength(-1.0F);
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      return null;
   }

   public static BlockEntity createMovingBlockEntity(BlockState movedState, Direction facing, boolean extending, boolean source) {
      return new MovingBlockEntity(movedState, facing, extending, source);
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      BlockEntity var4 = world.getBlockEntity(pos);
      if (var4 instanceof MovingBlockEntity) {
         ((MovingBlockEntity)var4).finish();
      } else {
         super.onRemoved(world, pos, state);
      }
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      return false;
   }

   @Override
   public boolean canPlace(World world, BlockPos pos, Direction dir) {
      return false;
   }

   @Override
   public void onBroken(World world, BlockPos pos, BlockState state) {
      BlockPos var4 = pos.offset(((Direction)state.get(FACING)).getOpposite());
      BlockState var5 = world.getBlockState(var4);
      if (var5.getBlock() instanceof PistonBaseBlock && var5.get(PistonBaseBlock.EXTENDED)) {
         world.removeBlock(var4);
      }
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
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (!world.isClient && world.getBlockEntity(pos) == null) {
         world.removeBlock(pos);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return null;
   }

   @Override
   public void dropItems(World world, BlockPos pos, BlockState state, float luck, int fortuneLevel) {
      if (!world.isClient) {
         MovingBlockEntity var6 = this.getMovingBlockEntity(world, pos);
         if (var6 != null) {
            BlockState var7 = var6.getMovedState();
            var7.getBlock().dropItems(world, pos, var7, 0);
         }
      }
   }

   @Override
   public HitResult rayTrace(World world, BlockPos pos, Vec3d start, Vec3d end) {
      return null;
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (!world.isClient) {
         world.getBlockEntity(pos);
      }
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      MovingBlockEntity var4 = this.getMovingBlockEntity(world, pos);
      if (var4 == null) {
         return null;
      } else {
         float var5 = var4.getProgress(0.0F);
         if (var4.isExtending()) {
            var5 = 1.0F - var5;
         }

         return this.getCollisionShape(world, pos, var4.getMovedState(), var5, var4.getFacing());
      }
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      MovingBlockEntity var3 = this.getMovingBlockEntity(world, pos);
      if (var3 != null) {
         BlockState var4 = var3.getMovedState();
         Block var5 = var4.getBlock();
         if (var5 == this || var5.getMaterial() == Material.AIR) {
            return;
         }

         float var6 = var3.getProgress(0.0F);
         if (var3.isExtending()) {
            var6 = 1.0F - var6;
         }

         var5.updateShape(world, pos);
         if (var5 == Blocks.PISTON || var5 == Blocks.STICKY_PISTON) {
            var6 = 0.0F;
         }

         Direction var7 = var3.getFacing();
         this.minX = var5.getMinX() - (double)((float)var7.getOffsetX() * var6);
         this.minY = var5.getMinY() - (double)((float)var7.getOffsetY() * var6);
         this.minZ = var5.getMinZ() - (double)((float)var7.getOffsetZ() * var6);
         this.maxX = var5.getMaxX() - (double)((float)var7.getOffsetX() * var6);
         this.maxY = var5.getMaxY() - (double)((float)var7.getOffsetY() * var6);
         this.maxZ = var5.getMaxZ() - (double)((float)var7.getOffsetZ() * var6);
      }
   }

   public Box getCollisionShape(World world, BlockPos pos, BlockState movedState, float progress, Direction facing) {
      if (movedState.getBlock() != this && movedState.getBlock().getMaterial() != Material.AIR) {
         Box var6 = movedState.getBlock().getCollisionShape(world, pos, movedState);
         if (var6 == null) {
            return null;
         } else {
            double var7 = var6.minX;
            double var9 = var6.minY;
            double var11 = var6.minZ;
            double var13 = var6.maxX;
            double var15 = var6.maxY;
            double var17 = var6.maxZ;
            if (facing.getOffsetX() < 0) {
               var7 -= (double)((float)facing.getOffsetX() * progress);
            } else {
               var13 -= (double)((float)facing.getOffsetX() * progress);
            }

            if (facing.getOffsetY() < 0) {
               var9 -= (double)((float)facing.getOffsetY() * progress);
            } else {
               var15 -= (double)((float)facing.getOffsetY() * progress);
            }

            if (facing.getOffsetZ() < 0) {
               var11 -= (double)((float)facing.getOffsetZ() * progress);
            } else {
               var17 -= (double)((float)facing.getOffsetZ() * progress);
            }

            return new Box(var7, var9, var11, var13, var15, var17);
         }
      } else {
         return null;
      }
   }

   private MovingBlockEntity getMovingBlockEntity(IWorld world, BlockPos pos) {
      BlockEntity var3 = world.getBlockEntity(pos);
      return var3 instanceof MovingBlockEntity ? (MovingBlockEntity)var3 : null;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Item.byRawId(0);
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState()
         .set(FACING, PistonHeadBlock.getFacingFromMetadata(metadata))
         .set(TYPE, (metadata & 8) > 0 ? PistonHeadBlock.Type.STICKY : PistonHeadBlock.Type.DEFAULT);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((Direction)state.get(FACING)).getId();
      if (state.get(TYPE) == PistonHeadBlock.Type.STICKY) {
         var2 |= 8;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING, TYPE);
   }
}
