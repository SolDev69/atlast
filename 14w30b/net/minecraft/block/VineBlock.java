package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.client.world.color.FoliageColors;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class VineBlock extends Block {
   public static final BooleanProperty UP = BooleanProperty.of("up");
   public static final BooleanProperty NORTH = BooleanProperty.of("north");
   public static final BooleanProperty EAST = BooleanProperty.of("east");
   public static final BooleanProperty SOUTH = BooleanProperty.of("south");
   public static final BooleanProperty WEST = BooleanProperty.of("west");
   public static final BooleanProperty[] PROPERTIES = new BooleanProperty[]{UP, NORTH, SOUTH, WEST, EAST};
   public static final int SOUTH_METADATA = getMetadataForConnection(Direction.SOUTH);
   public static final int NORTH_METADATA = getMetadataForConnection(Direction.NORTH);
   public static final int EAST_METADATA = getMetadataForConnection(Direction.EAST);
   public static final int WEST_METADATA = getMetadataForConnection(Direction.WEST);

   public VineBlock() {
      super(Material.REPLACEABLE_PLANT);
      this.setDefaultState(this.stateDefinition.any().set(UP, false).set(NORTH, false).set(EAST, false).set(SOUTH, false).set(WEST, false));
      this.setTicksRandomly(true);
      this.setItemGroup(ItemGroup.DECORATIONS);
   }

   @Override
   public BlockState updateShape(BlockState state, IWorld world, BlockPos pos) {
      return state.set(UP, world.getBlockState(pos.up()).getBlock().blocksAmbientLight());
   }

   @Override
   public void setBlockItemBounds() {
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
   public boolean canBeReplaced(World world, BlockPos pos) {
      return true;
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      float var3 = 0.0625F;
      float var4 = 1.0F;
      float var5 = 1.0F;
      float var6 = 1.0F;
      float var7 = 0.0F;
      float var8 = 0.0F;
      float var9 = 0.0F;
      boolean var10 = false;
      if (world.getBlockState(pos).get(WEST)) {
         var7 = Math.max(var7, 0.0625F);
         var4 = 0.0F;
         var5 = 0.0F;
         var8 = 1.0F;
         var6 = 0.0F;
         var9 = 1.0F;
         var10 = true;
      }

      if (world.getBlockState(pos).get(EAST)) {
         var4 = Math.min(var4, 0.9375F);
         var7 = 1.0F;
         var5 = 0.0F;
         var8 = 1.0F;
         var6 = 0.0F;
         var9 = 1.0F;
         var10 = true;
      }

      if (world.getBlockState(pos).get(NORTH)) {
         var9 = Math.max(var9, 0.0625F);
         var6 = 0.0F;
         var4 = 0.0F;
         var7 = 1.0F;
         var5 = 0.0F;
         var8 = 1.0F;
         var10 = true;
      }

      if (world.getBlockState(pos).get(SOUTH)) {
         var6 = Math.min(var6, 0.9375F);
         var9 = 1.0F;
         var4 = 0.0F;
         var7 = 1.0F;
         var5 = 0.0F;
         var8 = 1.0F;
         var10 = true;
      }

      if (!var10 && this.canGrowOn(world.getBlockState(pos.up()).getBlock())) {
         var5 = Math.min(var5, 0.9375F);
         var8 = 1.0F;
         var4 = 0.0F;
         var7 = 1.0F;
         var6 = 0.0F;
         var9 = 1.0F;
      }

      this.setShape(var4, var5, var6, var7, var8, var9);
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      return null;
   }

   @Override
   public boolean canPlace(World world, BlockPos pos, Direction dir) {
      switch(dir) {
         case UP:
            return this.canGrowOn(world.getBlockState(pos.up()).getBlock());
         case NORTH:
         case SOUTH:
         case EAST:
         case WEST:
            return this.canGrowOn(world.getBlockState(pos.offset(dir.getOpposite())).getBlock());
         default:
            return false;
      }
   }

   private boolean canGrowOn(Block block) {
      return block.isFullCube() && block.material.blocksMovement();
   }

   private boolean hasConnections(World world, BlockPos pos, BlockState state) {
      BlockState var4 = state;

      for(Direction var6 : Direction.Plane.HORIZONTAL) {
         BooleanProperty var7 = getPropertyForDirection(var6);
         if (state.get(var7) && !this.canGrowOn(world.getBlockState(pos.offset(var6)).getBlock())) {
            BlockState var8 = world.getBlockState(pos.up());
            if (var8.getBlock() != this || !var8.get(var7)) {
               state = state.set(var7, false);
            }
         }
      }

      if (getConnectionCount(state) == 0) {
         return false;
      } else {
         if (var4 != state) {
            world.setBlockState(pos, state, 2);
         }

         return true;
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor() {
      return FoliageColors.getDefaultColor();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor(int tint) {
      return FoliageColors.getDefaultColor();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor(IWorld world, BlockPos pos, int tint) {
      return world.getBiome(pos).getFoliageColor(pos);
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (!world.isClient && !this.hasConnections(world, pos, state)) {
         this.dropItems(world, pos, state, 0);
         world.removeBlock(pos);
      }
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (!world.isClient) {
         if (world.random.nextInt(4) == 0) {
            byte var5 = 4;
            int var6 = 5;
            boolean var7 = false;

            label191:
            for(int var8 = -var5; var8 <= var5; ++var8) {
               for(int var9 = -var5; var9 <= var5; ++var9) {
                  for(int var10 = -1; var10 <= 1; ++var10) {
                     if (world.getBlockState(pos.add(var8, var10, var9)).getBlock() == this) {
                        if (--var6 <= 0) {
                           var7 = true;
                           break label191;
                        }
                     }
                  }
               }
            }

            Direction var17 = Direction.pick(random);
            if (var17 == Direction.UP && pos.getY() < 255 && world.isAir(pos.up())) {
               if (!var7) {
                  BlockState var20 = state;

                  for(Direction var25 : Direction.Plane.HORIZONTAL) {
                     if (random.nextBoolean() || !this.canGrowOn(world.getBlockState(pos.offset(var25).up()).getBlock())) {
                        var20 = var20.set(getPropertyForDirection(var25), false);
                     }
                  }

                  if (var20.get(NORTH) || var20.get(EAST) || var20.get(SOUTH) || var20.get(WEST)) {
                     world.setBlockState(pos.up(), var20, 2);
                  }
               }
            } else if (!var17.getAxis().isHorizontal() || state.get(getPropertyForDirection(var17))) {
               if (pos.getY() > 1) {
                  BlockPos var19 = pos.down();
                  BlockState var22 = world.getBlockState(var19);
                  Block var24 = var22.getBlock();
                  if (var24.material == Material.AIR) {
                     BlockState var26 = state;

                     for(Direction var30 : Direction.Plane.HORIZONTAL) {
                        if (random.nextBoolean()) {
                           var26 = var26.set(getPropertyForDirection(var30), false);
                        }
                     }

                     if (var26.get(NORTH) || var26.get(EAST) || var26.get(SOUTH) || var26.get(WEST)) {
                        world.setBlockState(var19, var26, 2);
                     }
                  } else if (var24 == this) {
                     BlockState var27 = var22;

                     for(Direction var31 : Direction.Plane.HORIZONTAL) {
                        BooleanProperty var32 = getPropertyForDirection(var31);
                        if (random.nextBoolean() || !state.get(var32)) {
                           var27 = var27.set(var32, false);
                        }
                     }

                     if (var27.get(NORTH) || var27.get(EAST) || var27.get(SOUTH) || var27.get(WEST)) {
                        world.setBlockState(var19, var27, 2);
                     }
                  }
               }
            } else if (!var7) {
               BlockPos var18 = pos.offset(var17);
               Block var21 = world.getBlockState(var18).getBlock();
               if (var21.material == Material.AIR) {
                  Direction var11 = var17.clockwiseY();
                  Direction var12 = var17.counterClockwiseY();
                  boolean var13 = state.get(getPropertyForDirection(var11));
                  boolean var14 = state.get(getPropertyForDirection(var12));
                  BlockPos var15 = var18.offset(var11);
                  BlockPos var16 = var18.offset(var12);
                  if (var13 && this.canGrowOn(world.getBlockState(var15).getBlock())) {
                     world.setBlockState(var18, this.defaultState().set(getPropertyForDirection(var11), true), 2);
                  } else if (var14 && this.canGrowOn(world.getBlockState(var16).getBlock())) {
                     world.setBlockState(var18, this.defaultState().set(getPropertyForDirection(var12), true), 2);
                  } else if (var13 && world.isAir(var15) && this.canGrowOn(world.getBlockState(pos.offset(var11)).getBlock())) {
                     world.setBlockState(var15, this.defaultState().set(getPropertyForDirection(var17.getOpposite()), true), 2);
                  } else if (var14 && world.isAir(var16) && this.canGrowOn(world.getBlockState(pos.offset(var12)).getBlock())) {
                     world.setBlockState(var16, this.defaultState().set(getPropertyForDirection(var17.getOpposite()), true), 2);
                  } else if (this.canGrowOn(world.getBlockState(var18.up()).getBlock())) {
                     world.setBlockState(var18, this.defaultState(), 2);
                  }
               } else if (var21.material.isSolidBlocking() && var21.isFullCube()) {
                  world.setBlockState(pos, state.set(getPropertyForDirection(var17), true), 2);
               }
            }
         }
      }
   }

   private static int getMetadataForConnection(Direction dir) {
      return 1 << dir.getIdHorizontal();
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      BlockState var9 = this.defaultState().set(UP, false).set(NORTH, false).set(EAST, false).set(SOUTH, false).set(WEST, false);
      return dir.getAxis().isHorizontal() ? var9.set(getPropertyForDirection(dir.getOpposite()), true) : var9;
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return null;
   }

   @Override
   public int getBaseDropCount(Random random) {
      return 0;
   }

   @Override
   public void afterMinedByPlayer(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
      if (!world.isClient && player.getMainHandStack() != null && player.getMainHandStack().getItem() == Items.SHEARS) {
         player.incrementStat(Stats.BLOCKS_MINED[Block.getRawId(this)]);
         this.dropItems(world, pos, new ItemStack(Blocks.VINE, 1, 0));
      } else {
         super.afterMinedByPlayer(world, player, pos, state, blockEntity);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState()
         .set(NORTH, (metadata & NORTH_METADATA) > 0)
         .set(EAST, (metadata & EAST_METADATA) > 0)
         .set(SOUTH, (metadata & SOUTH_METADATA) > 0)
         .set(WEST, (metadata & WEST_METADATA) > 0);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      if (state.get(NORTH)) {
         var2 |= NORTH_METADATA;
      }

      if (state.get(EAST)) {
         var2 |= EAST_METADATA;
      }

      if (state.get(SOUTH)) {
         var2 |= SOUTH_METADATA;
      }

      if (state.get(WEST)) {
         var2 |= WEST_METADATA;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, UP, NORTH, EAST, SOUTH, WEST);
   }

   public static BooleanProperty getPropertyForDirection(Direction dir) {
      switch(dir) {
         case UP:
            return UP;
         case NORTH:
            return NORTH;
         case SOUTH:
            return SOUTH;
         case EAST:
            return EAST;
         case WEST:
            return WEST;
         default:
            throw new IllegalArgumentException(dir + " is an invalid choice");
      }
   }

   public static int getConnectionCount(BlockState state) {
      int var1 = 0;

      for(BooleanProperty var5 : PROPERTIES) {
         if (state.get(var5)) {
            ++var1;
         }
      }

      return var1;
   }
}
