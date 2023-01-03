package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.HitResult;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class StairsBlock extends Block {
   public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.Plane.HORIZONTAL);
   public static final EnumProperty HALF = EnumProperty.of("half", StairsBlock.Half.class);
   public static final EnumProperty SHAPE = EnumProperty.of("shape", StairsBlock.Shape.class);
   private static final int[][] ROTATIONS = new int[][]{{2, 6}, {3, 7}, {2, 3}, {6, 7}, {0, 4}, {1, 5}, {0, 1}, {4, 5}};
   private final Block baseBlock;
   private final BlockState baseState;
   private boolean isCorner;
   private int stairType;

   protected StairsBlock(BlockState baseState) {
      super(baseState.getBlock().material);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH).set(HALF, StairsBlock.Half.BOTTOM).set(SHAPE, StairsBlock.Shape.STRAIGHT));
      this.baseBlock = baseState.getBlock();
      this.baseState = baseState;
      this.setStrength(this.baseBlock.miningSpeed);
      this.setResistance(this.baseBlock.resistance / 3.0F);
      this.setSound(this.baseBlock.sound);
      this.setOpacity(255);
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      if (this.isCorner) {
         this.setShape(
            0.5F * (float)(this.stairType % 2),
            0.5F * (float)(this.stairType / 2 % 2),
            0.5F * (float)(this.stairType / 4 % 2),
            0.5F + 0.5F * (float)(this.stairType % 2),
            0.5F + 0.5F * (float)(this.stairType / 2 % 2),
            0.5F + 0.5F * (float)(this.stairType / 4 % 2)
         );
      } else {
         this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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

   public void setBoundingBox(IWorld world, BlockPos pos) {
      if (world.getBlockState(pos).get(HALF) == StairsBlock.Half.TOP) {
         this.setShape(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      }
   }

   public static boolean isStair(Block block) {
      return block instanceof StairsBlock;
   }

   public static boolean isStairSameHalfSameFacing(IWorld world, BlockPos pos, BlockState state) {
      BlockState var3 = world.getBlockState(pos);
      Block var4 = var3.getBlock();
      return isStair(var4) && var3.get(HALF) == state.get(HALF) && var3.get(FACING) == state.get(FACING);
   }

   public int getMetadataForInnerStair(IWorld world, BlockPos pos) {
      BlockState var3 = world.getBlockState(pos);
      Direction var4 = (Direction)var3.get(FACING);
      StairsBlock.Half var5 = (StairsBlock.Half)var3.get(HALF);
      boolean var6 = var5 == StairsBlock.Half.TOP;
      if (var4 == Direction.EAST) {
         BlockState var7 = world.getBlockState(pos.east());
         Block var8 = var7.getBlock();
         if (isStair(var8) && var5 == var7.get(HALF)) {
            Direction var9 = (Direction)var7.get(FACING);
            if (var9 == Direction.NORTH && !isStairSameHalfSameFacing(world, pos.south(), var3)) {
               return var6 ? 1 : 2;
            }

            if (var9 == Direction.SOUTH && !isStairSameHalfSameFacing(world, pos.north(), var3)) {
               return var6 ? 2 : 1;
            }
         }
      } else if (var4 == Direction.WEST) {
         BlockState var10 = world.getBlockState(pos.west());
         Block var13 = var10.getBlock();
         if (isStair(var13) && var5 == var10.get(HALF)) {
            Direction var16 = (Direction)var10.get(FACING);
            if (var16 == Direction.NORTH && !isStairSameHalfSameFacing(world, pos.south(), var3)) {
               return var6 ? 2 : 1;
            }

            if (var16 == Direction.SOUTH && !isStairSameHalfSameFacing(world, pos.north(), var3)) {
               return var6 ? 1 : 2;
            }
         }
      } else if (var4 == Direction.SOUTH) {
         BlockState var11 = world.getBlockState(pos.south());
         Block var14 = var11.getBlock();
         if (isStair(var14) && var5 == var11.get(HALF)) {
            Direction var17 = (Direction)var11.get(FACING);
            if (var17 == Direction.WEST && !isStairSameHalfSameFacing(world, pos.east(), var3)) {
               return var6 ? 2 : 1;
            }

            if (var17 == Direction.EAST && !isStairSameHalfSameFacing(world, pos.west(), var3)) {
               return var6 ? 1 : 2;
            }
         }
      } else if (var4 == Direction.NORTH) {
         BlockState var12 = world.getBlockState(pos.north());
         Block var15 = var12.getBlock();
         if (isStair(var15) && var5 == var12.get(HALF)) {
            Direction var18 = (Direction)var12.get(FACING);
            if (var18 == Direction.WEST && !isStairSameHalfSameFacing(world, pos.east(), var3)) {
               return var6 ? 1 : 2;
            }

            if (var18 == Direction.EAST && !isStairSameHalfSameFacing(world, pos.west(), var3)) {
               return var6 ? 2 : 1;
            }
         }
      }

      return 0;
   }

   public int getMetadataForOuterStair(IWorld world, BlockPos pos) {
      BlockState var3 = world.getBlockState(pos);
      Direction var4 = (Direction)var3.get(FACING);
      StairsBlock.Half var5 = (StairsBlock.Half)var3.get(HALF);
      boolean var6 = var5 == StairsBlock.Half.TOP;
      if (var4 == Direction.EAST) {
         BlockState var7 = world.getBlockState(pos.west());
         Block var8 = var7.getBlock();
         if (isStair(var8) && var5 == var7.get(HALF)) {
            Direction var9 = (Direction)var7.get(FACING);
            if (var9 == Direction.NORTH && !isStairSameHalfSameFacing(world, pos.north(), var3)) {
               return var6 ? 1 : 2;
            }

            if (var9 == Direction.SOUTH && !isStairSameHalfSameFacing(world, pos.south(), var3)) {
               return var6 ? 2 : 1;
            }
         }
      } else if (var4 == Direction.WEST) {
         BlockState var10 = world.getBlockState(pos.east());
         Block var13 = var10.getBlock();
         if (isStair(var13) && var5 == var10.get(HALF)) {
            Direction var16 = (Direction)var10.get(FACING);
            if (var16 == Direction.NORTH && !isStairSameHalfSameFacing(world, pos.north(), var3)) {
               return var6 ? 2 : 1;
            }

            if (var16 == Direction.SOUTH && !isStairSameHalfSameFacing(world, pos.south(), var3)) {
               return var6 ? 1 : 2;
            }
         }
      } else if (var4 == Direction.SOUTH) {
         BlockState var11 = world.getBlockState(pos.north());
         Block var14 = var11.getBlock();
         if (isStair(var14) && var5 == var11.get(HALF)) {
            Direction var17 = (Direction)var11.get(FACING);
            if (var17 == Direction.WEST && !isStairSameHalfSameFacing(world, pos.west(), var3)) {
               return var6 ? 2 : 1;
            }

            if (var17 == Direction.EAST && !isStairSameHalfSameFacing(world, pos.east(), var3)) {
               return var6 ? 1 : 2;
            }
         }
      } else if (var4 == Direction.NORTH) {
         BlockState var12 = world.getBlockState(pos.south());
         Block var15 = var12.getBlock();
         if (isStair(var15) && var5 == var12.get(HALF)) {
            Direction var18 = (Direction)var12.get(FACING);
            if (var18 == Direction.WEST && !isStairSameHalfSameFacing(world, pos.west(), var3)) {
               return var6 ? 1 : 2;
            }

            if (var18 == Direction.EAST && !isStairSameHalfSameFacing(world, pos.east(), var3)) {
               return var6 ? 2 : 1;
            }
         }
      }

      return 0;
   }

   public boolean updateOuterCornerBoundingBox(IWorld world, BlockPos pos) {
      BlockState var3 = world.getBlockState(pos);
      Direction var4 = (Direction)var3.get(FACING);
      StairsBlock.Half var5 = (StairsBlock.Half)var3.get(HALF);
      boolean var6 = var5 == StairsBlock.Half.TOP;
      float var7 = 0.5F;
      float var8 = 1.0F;
      if (var6) {
         var7 = 0.0F;
         var8 = 0.5F;
      }

      float var9 = 0.0F;
      float var10 = 1.0F;
      float var11 = 0.0F;
      float var12 = 0.5F;
      boolean var13 = true;
      if (var4 == Direction.EAST) {
         var9 = 0.5F;
         var12 = 1.0F;
         BlockState var14 = world.getBlockState(pos.east());
         Block var15 = var14.getBlock();
         if (isStair(var15) && var5 == var14.get(HALF)) {
            Direction var16 = (Direction)var14.get(FACING);
            if (var16 == Direction.NORTH && !isStairSameHalfSameFacing(world, pos.south(), var3)) {
               var12 = 0.5F;
               var13 = false;
            } else if (var16 == Direction.SOUTH && !isStairSameHalfSameFacing(world, pos.north(), var3)) {
               var11 = 0.5F;
               var13 = false;
            }
         }
      } else if (var4 == Direction.WEST) {
         var10 = 0.5F;
         var12 = 1.0F;
         BlockState var17 = world.getBlockState(pos.west());
         Block var20 = var17.getBlock();
         if (isStair(var20) && var5 == var17.get(HALF)) {
            Direction var23 = (Direction)var17.get(FACING);
            if (var23 == Direction.NORTH && !isStairSameHalfSameFacing(world, pos.south(), var3)) {
               var12 = 0.5F;
               var13 = false;
            } else if (var23 == Direction.SOUTH && !isStairSameHalfSameFacing(world, pos.north(), var3)) {
               var11 = 0.5F;
               var13 = false;
            }
         }
      } else if (var4 == Direction.SOUTH) {
         var11 = 0.5F;
         var12 = 1.0F;
         BlockState var18 = world.getBlockState(pos.south());
         Block var21 = var18.getBlock();
         if (isStair(var21) && var5 == var18.get(HALF)) {
            Direction var24 = (Direction)var18.get(FACING);
            if (var24 == Direction.WEST && !isStairSameHalfSameFacing(world, pos.east(), var3)) {
               var10 = 0.5F;
               var13 = false;
            } else if (var24 == Direction.EAST && !isStairSameHalfSameFacing(world, pos.west(), var3)) {
               var9 = 0.5F;
               var13 = false;
            }
         }
      } else if (var4 == Direction.NORTH) {
         BlockState var19 = world.getBlockState(pos.north());
         Block var22 = var19.getBlock();
         if (isStair(var22) && var5 == var19.get(HALF)) {
            Direction var25 = (Direction)var19.get(FACING);
            if (var25 == Direction.WEST && !isStairSameHalfSameFacing(world, pos.east(), var3)) {
               var10 = 0.5F;
               var13 = false;
            } else if (var25 == Direction.EAST && !isStairSameHalfSameFacing(world, pos.west(), var3)) {
               var9 = 0.5F;
               var13 = false;
            }
         }
      }

      this.setShape(var9, var7, var11, var10, var8, var12);
      return var13;
   }

   public boolean updateInnerCornerBoundingBox(IWorld world, BlockPos pos) {
      BlockState var3 = world.getBlockState(pos);
      Direction var4 = (Direction)var3.get(FACING);
      StairsBlock.Half var5 = (StairsBlock.Half)var3.get(HALF);
      boolean var6 = var5 == StairsBlock.Half.TOP;
      float var7 = 0.5F;
      float var8 = 1.0F;
      if (var6) {
         var7 = 0.0F;
         var8 = 0.5F;
      }

      float var9 = 0.0F;
      float var10 = 0.5F;
      float var11 = 0.5F;
      float var12 = 1.0F;
      boolean var13 = false;
      if (var4 == Direction.EAST) {
         BlockState var14 = world.getBlockState(pos.west());
         Block var15 = var14.getBlock();
         if (isStair(var15) && var5 == var14.get(HALF)) {
            Direction var16 = (Direction)var14.get(FACING);
            if (var16 == Direction.NORTH && !isStairSameHalfSameFacing(world, pos.north(), var3)) {
               var11 = 0.0F;
               var12 = 0.5F;
               var13 = true;
            } else if (var16 == Direction.SOUTH && !isStairSameHalfSameFacing(world, pos.south(), var3)) {
               var11 = 0.5F;
               var12 = 1.0F;
               var13 = true;
            }
         }
      } else if (var4 == Direction.WEST) {
         BlockState var17 = world.getBlockState(pos.east());
         Block var20 = var17.getBlock();
         if (isStair(var20) && var5 == var17.get(HALF)) {
            var9 = 0.5F;
            var10 = 1.0F;
            Direction var23 = (Direction)var17.get(FACING);
            if (var23 == Direction.NORTH && !isStairSameHalfSameFacing(world, pos.north(), var3)) {
               var11 = 0.0F;
               var12 = 0.5F;
               var13 = true;
            } else if (var23 == Direction.SOUTH && !isStairSameHalfSameFacing(world, pos.south(), var3)) {
               var11 = 0.5F;
               var12 = 1.0F;
               var13 = true;
            }
         }
      } else if (var4 == Direction.SOUTH) {
         BlockState var18 = world.getBlockState(pos.north());
         Block var21 = var18.getBlock();
         if (isStair(var21) && var5 == var18.get(HALF)) {
            var11 = 0.0F;
            var12 = 0.5F;
            Direction var24 = (Direction)var18.get(FACING);
            if (var24 == Direction.WEST && !isStairSameHalfSameFacing(world, pos.west(), var3)) {
               var13 = true;
            } else if (var24 == Direction.EAST && !isStairSameHalfSameFacing(world, pos.east(), var3)) {
               var9 = 0.5F;
               var10 = 1.0F;
               var13 = true;
            }
         }
      } else if (var4 == Direction.NORTH) {
         BlockState var19 = world.getBlockState(pos.south());
         Block var22 = var19.getBlock();
         if (isStair(var22) && var5 == var19.get(HALF)) {
            Direction var25 = (Direction)var19.get(FACING);
            if (var25 == Direction.WEST && !isStairSameHalfSameFacing(world, pos.west(), var3)) {
               var13 = true;
            } else if (var25 == Direction.EAST && !isStairSameHalfSameFacing(world, pos.east(), var3)) {
               var9 = 0.5F;
               var10 = 1.0F;
               var13 = true;
            }
         }
      }

      if (var13) {
         this.setShape(var9, var7, var11, var10, var8, var12);
      }

      return var13;
   }

   @Override
   public void getCollisionBoxes(World world, BlockPos pos, BlockState state, Box entityBox, List boxes, Entity entity) {
      this.setBoundingBox(world, pos);
      super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      boolean var7 = this.updateOuterCornerBoundingBox(world, pos);
      super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      if (var7 && this.updateInnerCornerBoundingBox(world, pos)) {
         super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      }

      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random random) {
      this.baseBlock.randomDisplayTick(world, pos, state, random);
   }

   @Override
   public void startMining(World world, BlockPos pos, PlayerEntity player) {
      this.baseBlock.startMining(world, pos, player);
   }

   @Override
   public void onBroken(World world, BlockPos pos, BlockState state) {
      this.baseBlock.onBroken(world, pos, state);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getLightColor(IWorld world, BlockPos pos) {
      return this.baseBlock.getLightColor(world, pos);
   }

   @Override
   public float getBlastResistance(Entity entity) {
      return this.baseBlock.getBlastResistance(entity);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return this.baseBlock.getRenderLayer();
   }

   @Override
   public int getTickRate(World world) {
      return this.baseBlock.getTickRate(world);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Box getOutlineShape(World world, BlockPos pos) {
      return this.baseBlock.getOutlineShape(world, pos);
   }

   @Override
   public Vec3d applyMaterialDrag(World world, BlockPos pos, Entity entity, Vec3d velocity) {
      return this.baseBlock.applyMaterialDrag(world, pos, entity, velocity);
   }

   @Override
   public boolean hasCollision() {
      return this.baseBlock.hasCollision();
   }

   @Override
   public boolean hasCollision(BlockState state, boolean allowFluids) {
      return this.baseBlock.hasCollision(state, allowFluids);
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      return this.baseBlock.canSurvive(world, pos);
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      this.update(world, pos, this.baseState, Blocks.AIR);
      this.baseBlock.onAdded(world, pos, this.baseState);
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      this.baseBlock.onRemoved(world, pos, this.baseState);
   }

   @Override
   public void onSteppedOn(World world, BlockPos pos, Entity entity) {
      this.baseBlock.onSteppedOn(world, pos, entity);
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      this.baseBlock.tick(world, pos, state, random);
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      return this.baseBlock.use(world, pos, this.baseState, player, Direction.DOWN, 0.0F, 0.0F, 0.0F);
   }

   @Override
   public void onExploded(World world, BlockPos pos, Explosion explosion) {
      this.baseBlock.onExploded(world, pos, explosion);
   }

   @Override
   public MaterialColor getMaterialColor(BlockState state) {
      return this.baseBlock.getMaterialColor(this.baseState);
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      BlockState var9 = super.getPlacementState(world, pos, dir, dx, dy, dz, metadata, entity);
      var9 = var9.set(FACING, entity.getDirection()).set(SHAPE, StairsBlock.Shape.STRAIGHT);
      return dir != Direction.DOWN && (dir == Direction.UP || !((double)dy > 0.5))
         ? var9.set(HALF, StairsBlock.Half.BOTTOM)
         : var9.set(HALF, StairsBlock.Half.TOP);
   }

   @Override
   public HitResult rayTrace(World world, BlockPos pos, Vec3d start, Vec3d end) {
      HitResult[] var5 = new HitResult[8];
      BlockState var6 = world.getBlockState(pos);
      int var7 = ((Direction)var6.get(FACING)).getIdHorizontal();
      boolean var8 = var6.get(HALF) == StairsBlock.Half.TOP;
      int[] var9 = ROTATIONS[var7 + (var8 ? 4 : 0)];
      this.isCorner = true;

      for(int var10 = 0; var10 < 8; ++var10) {
         this.stairType = var10;

         for(int var14 : var9) {
            if (var14 == var10) {
            }
         }

         var5[var10] = super.rayTrace(world, pos, start, end);
      }

      for(int var24 : var9) {
         var5[var24] = null;
      }

      HitResult var20 = null;
      double var22 = 0.0;

      for(HitResult var16 : var5) {
         if (var16 != null) {
            double var17 = var16.pos.squaredDistanceTo(end);
            if (var17 > var22) {
               var20 = var16;
               var22 = var17;
            }
         }
      }

      return var20;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      BlockState var2 = this.defaultState().set(HALF, (metadata & 4) > 0 ? StairsBlock.Half.TOP : StairsBlock.Half.BOTTOM);
      return var2.set(FACING, Direction.byId(5 - (metadata & 3)));
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      if (state.get(HALF) == StairsBlock.Half.TOP) {
         var2 |= 4;
      }

      return var2 | 5 - ((Direction)state.get(FACING)).getId();
   }

   @Override
   public BlockState updateShape(BlockState state, IWorld world, BlockPos pos) {
      if (this.updateOuterCornerBoundingBox(world, pos)) {
         switch(this.getMetadataForOuterStair(world, pos)) {
            case 0:
               state = state.set(SHAPE, StairsBlock.Shape.STRAIGHT);
               break;
            case 1:
               state = state.set(SHAPE, StairsBlock.Shape.INNER_RIGHT);
               break;
            case 2:
               state = state.set(SHAPE, StairsBlock.Shape.INNER_LEFT);
         }
      } else {
         switch(this.getMetadataForInnerStair(world, pos)) {
            case 0:
               state = state.set(SHAPE, StairsBlock.Shape.STRAIGHT);
               break;
            case 1:
               state = state.set(SHAPE, StairsBlock.Shape.OUTER_RIGHT);
               break;
            case 2:
               state = state.set(SHAPE, StairsBlock.Shape.OUTER_LEFT);
         }
      }

      return state;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING, HALF, SHAPE);
   }

   public static enum Half implements StringRepresentable {
      TOP("top"),
      BOTTOM("bottom");

      private final String id;

      private Half(String id) {
         this.id = id;
      }

      @Override
      public String toString() {
         return this.id;
      }

      @Override
      public String getStringRepresentation() {
         return this.id;
      }
   }

   public static enum Shape implements StringRepresentable {
      STRAIGHT("straight"),
      INNER_LEFT("inner_left"),
      INNER_RIGHT("inner_right"),
      OUTER_LEFT("outer_left"),
      OUTER_RIGHT("outer_right");

      private final String id;

      private Shape(String id) {
         this.id = id;
      }

      @Override
      public String toString() {
         return this.id;
      }

      @Override
      public String getStringRepresentation() {
         return this.id;
      }
   }
}
