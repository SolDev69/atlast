package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.HitResult;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class DoorBlock extends Block {
   public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.Plane.HORIZONTAL);
   public static final BooleanProperty OPEN = BooleanProperty.of("open");
   public static final EnumProperty HINGE = EnumProperty.of("hinge", DoorBlock.Hinge.class);
   public static final BooleanProperty POWERED = BooleanProperty.of("powered");
   public static final EnumProperty HALF = EnumProperty.of("half", DoorBlock.Half.class);

   protected DoorBlock(Material c_57ywipuwq) {
      super(c_57ywipuwq);
      this.setDefaultState(
         this.stateDefinition
            .any()
            .set(FACING, Direction.NORTH)
            .set(OPEN, false)
            .set(HINGE, DoorBlock.Hinge.LEFT)
            .set(POWERED, false)
            .set(HALF, DoorBlock.Half.LOWER)
      );
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean canWalkThrough(IWorld world, BlockPos pos) {
      return getOpenFromMetadata(getCombinedMetadata(world, pos));
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Box getOutlineShape(World world, BlockPos pos) {
      this.updateShape(world, pos);
      return super.getOutlineShape(world, pos);
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      this.updateShape(world, pos);
      return super.getCollisionShape(world, pos, state);
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      this.updateBoundingBox(getCombinedMetadata(world, pos));
   }

   private void updateBoundingBox(int metadata) {
      float var2 = 0.1875F;
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
      Direction var3 = getFacingFromMetadata(metadata);
      boolean var4 = getOpenFromMetadata(metadata);
      boolean var5 = getHingeFromMetadata(metadata);
      if (var4) {
         if (var3 == Direction.EAST) {
            if (!var5) {
               this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
            } else {
               this.setShape(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
            }
         } else if (var3 == Direction.SOUTH) {
            if (!var5) {
               this.setShape(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
               this.setShape(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
            }
         } else if (var3 == Direction.WEST) {
            if (!var5) {
               this.setShape(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
            } else {
               this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
            }
         } else if (var3 == Direction.NORTH) {
            if (!var5) {
               this.setShape(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
            } else {
               this.setShape(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      } else if (var3 == Direction.EAST) {
         this.setShape(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
      } else if (var3 == Direction.SOUTH) {
         this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
      } else if (var3 == Direction.WEST) {
         this.setShape(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else if (var3 == Direction.NORTH) {
         this.setShape(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
      }
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (this.material == Material.IRON) {
         return true;
      } else {
         BlockPos var9 = state.get(HALF) == DoorBlock.Half.LOWER ? pos : pos.down();
         BlockState var10 = pos.equals(var9) ? state : world.getBlockState(var9);
         if (var10.getBlock() != this) {
            return false;
         } else {
            world.setBlockState(var9, var10.next(OPEN), 2);
            world.onRegionChanged(var9, pos);
            world.doEvent(player, 1003, pos, 0);
            return true;
         }
      }
   }

   public void updateOpenState(World world, BlockPos pos, boolean open) {
      BlockState var4 = world.getBlockState(pos);
      BlockPos var5 = var4.get(HALF) == DoorBlock.Half.LOWER ? pos : pos.down();
      BlockState var6 = pos == var5 ? var4 : world.getBlockState(var5);
      if (var6.getBlock() == this && var6.get(OPEN) != open) {
         world.setBlockState(var5, var6.set(OPEN, open), 2);
         world.onRegionChanged(var5, pos);
         world.doEvent(null, 1003, pos, 0);
      }
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (state.get(HALF) == DoorBlock.Half.UPPER) {
         BlockPos var5 = pos.down();
         BlockState var6 = world.getBlockState(var5);
         if (var6.getBlock() != this) {
            world.removeBlock(pos);
         } else if (neighborBlock != this) {
            this.update(world, var5, var6, neighborBlock);
         }
      } else {
         boolean var9 = false;
         BlockPos var10 = pos.up();
         BlockState var7 = world.getBlockState(var10);
         if (var7.getBlock() != this) {
            world.removeBlock(pos);
            var9 = true;
         }

         if (!World.hasSolidTop(world, pos.down())) {
            world.removeBlock(pos);
            var9 = true;
            if (var7.getBlock() == this) {
               world.removeBlock(var10);
            }
         }

         if (var9) {
            if (!world.isClient) {
               this.dropItems(world, pos, state, 0);
            }
         } else {
            boolean var8 = world.isReceivingPower(pos) || world.isReceivingPower(var10);
            if ((var8 || neighborBlock.isPowerSource()) && neighborBlock != this && var8 != var7.get(POWERED)) {
               world.setBlockState(var10, var7.set(POWERED, var8), 2);
               if (var8 != state.get(OPEN)) {
                  world.setBlockState(pos, state.set(OPEN, var8), 2);
                  world.onRegionChanged(pos, pos);
                  world.doEvent(null, 1003, pos, 0);
               }
            }
         }
      }
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      if (state.get(HALF) == DoorBlock.Half.UPPER) {
         return null;
      } else {
         return this.material == Material.IRON ? Items.IRON_DOOR : Items.WOODEN_DOOR;
      }
   }

   @Override
   public HitResult rayTrace(World world, BlockPos pos, Vec3d start, Vec3d end) {
      this.updateShape(world, pos);
      return super.rayTrace(world, pos, start, end);
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      if (pos.getY() >= 255) {
         return false;
      } else {
         return World.hasSolidTop(world, pos.down()) && super.canSurvive(world, pos) && super.canSurvive(world, pos.up());
      }
   }

   @Override
   public int getPistonMoveBehavior() {
      return 1;
   }

   public static int getCombinedMetadata(IWorld world, BlockPos pos) {
      BlockState var2 = world.getBlockState(pos);
      int var3 = var2.getBlock().getMetadataFromState(var2);
      boolean var4 = getHalfFromMetadata(var3);
      BlockState var5 = world.getBlockState(pos.down());
      int var6 = var5.getBlock().getMetadataFromState(var5);
      int var7 = var4 ? var6 : var3;
      BlockState var8 = world.getBlockState(pos.up());
      int var9 = var8.getBlock().getMetadataFromState(var8);
      int var10 = var4 ? var3 : var9;
      boolean var11 = (var10 & 1) != 0;
      boolean var12 = (var10 & 2) != 0;
      return getLowerHalfMetadata(var7) | (var4 ? 8 : 0) | (var11 ? 16 : 0) | (var12 ? 32 : 0);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return this.material == Material.IRON ? Items.IRON_DOOR : Items.WOODEN_DOOR;
   }

   @Override
   public void beforeMinedByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      BlockPos var5 = pos.down();
      if (player.abilities.creativeMode && state.get(HALF) == DoorBlock.Half.UPPER && world.getBlockState(var5).getBlock() == this) {
         world.removeBlock(var5);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   @Override
   public BlockState updateShape(BlockState state, IWorld world, BlockPos pos) {
      if (state.get(HALF) == DoorBlock.Half.LOWER) {
         BlockState var4 = world.getBlockState(pos.up());
         if (var4.getBlock() == this) {
            state = state.set(HINGE, var4.get(HINGE)).set(POWERED, var4.get(POWERED));
         }
      } else {
         BlockState var5 = world.getBlockState(pos.down());
         if (var5.getBlock() == this) {
            state = state.set(FACING, var5.get(FACING)).set(OPEN, var5.get(OPEN));
         }
      }

      return state;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return (metadata & 8) > 0
         ? this.defaultState()
            .set(HALF, DoorBlock.Half.UPPER)
            .set(HINGE, (metadata & 1) > 0 ? DoorBlock.Hinge.RIGHT : DoorBlock.Hinge.LEFT)
            .set(POWERED, (metadata & 2) > 0)
         : this.defaultState()
            .set(HALF, DoorBlock.Half.LOWER)
            .set(FACING, Direction.byIdHorizontal(metadata & 3).counterClockwiseY())
            .set(OPEN, (metadata & 4) > 0);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      if (state.get(HALF) == DoorBlock.Half.UPPER) {
         var2 |= 8;
         if (state.get(HINGE) == DoorBlock.Hinge.RIGHT) {
            var2 |= 1;
         }

         if (state.get(POWERED)) {
            var2 |= 2;
         }
      } else {
         var2 |= ((Direction)state.get(FACING)).clockwiseY().getIdHorizontal();
         if (state.get(OPEN)) {
            var2 |= 4;
         }
      }

      return var2;
   }

   protected static int getLowerHalfMetadata(int metadata) {
      return metadata & 7;
   }

   public static boolean getOpenFromMetadata(IWorld world, BlockPos pos) {
      return getOpenFromMetadata(getCombinedMetadata(world, pos));
   }

   public static Direction getFacingFromMetadata(IWorld world, BlockPos pos) {
      return getFacingFromMetadata(getCombinedMetadata(world, pos));
   }

   public static Direction getFacingFromMetadata(int metadata) {
      return Direction.byIdHorizontal(metadata & 3).counterClockwiseY();
   }

   protected static boolean getOpenFromMetadata(int metadata) {
      return (metadata & 4) != 0;
   }

   protected static boolean getHalfFromMetadata(int metadata) {
      return (metadata & 8) != 0;
   }

   protected static boolean getHingeFromMetadata(int metadata) {
      return (metadata & 16) != 0;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, HALF, FACING, OPEN, HINGE, POWERED);
   }

   public static enum Half implements StringRepresentable {
      UPPER,
      LOWER;

      @Override
      public String toString() {
         return this.getStringRepresentation();
      }

      @Override
      public String getStringRepresentation() {
         return this == UPPER ? "upper" : "lower";
      }
   }

   public static enum Hinge implements StringRepresentable {
      LEFT,
      RIGHT;

      @Override
      public String toString() {
         return this.getStringRepresentation();
      }

      @Override
      public String getStringRepresentation() {
         return this == LEFT ? "left" : "right";
      }
   }
}
