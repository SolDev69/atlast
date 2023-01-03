package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.block.state.property.IntegerProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class RedstoneWireBlock extends Block {
   public static final EnumProperty NORTH = EnumProperty.of("north", RedstoneWireBlock.ConnectionSide.class);
   public static final EnumProperty EAST = EnumProperty.of("east", RedstoneWireBlock.ConnectionSide.class);
   public static final EnumProperty SOUTH = EnumProperty.of("south", RedstoneWireBlock.ConnectionSide.class);
   public static final EnumProperty WEST = EnumProperty.of("west", RedstoneWireBlock.ConnectionSide.class);
   public static final IntegerProperty POWER = IntegerProperty.of("power", 0, 15);
   private boolean emitsRedstonePower = true;
   private final Set neighborsToUpdate = Sets.newHashSet();

   public RedstoneWireBlock() {
      super(Material.DECORATION);
      this.setDefaultState(
         this.stateDefinition
            .any()
            .set(NORTH, RedstoneWireBlock.ConnectionSide.NONE)
            .set(EAST, RedstoneWireBlock.ConnectionSide.NONE)
            .set(SOUTH, RedstoneWireBlock.ConnectionSide.NONE)
            .set(WEST, RedstoneWireBlock.ConnectionSide.NONE)
            .set(POWER, 0)
      );
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
   }

   @Override
   public BlockState updateShape(BlockState state, IWorld world, BlockPos pos) {
      state = state.set(WEST, this.getConnection(world, pos, Direction.WEST));
      state = state.set(EAST, this.getConnection(world, pos, Direction.EAST));
      state = state.set(NORTH, this.getConnection(world, pos, Direction.NORTH));
      return state.set(SOUTH, this.getConnection(world, pos, Direction.SOUTH));
   }

   private RedstoneWireBlock.ConnectionSide getConnection(IWorld world, BlockPos pos, Direction dir) {
      BlockPos var4 = pos.offset(dir);
      Block var5 = world.getBlockState(pos.offset(dir)).getBlock();
      if (!shouldConnectTo(world.getBlockState(var4), dir) && (var5.blocksAmbientLight() || !shouldConnectTo(world.getBlockState(var4.down())))) {
         Block var6 = world.getBlockState(pos.up()).getBlock();
         return !var6.blocksAmbientLight() && var5.blocksAmbientLight() && shouldConnectTo(world.getBlockState(var4.up()))
            ? RedstoneWireBlock.ConnectionSide.UP
            : RedstoneWireBlock.ConnectionSide.NONE;
      } else {
         return RedstoneWireBlock.ConnectionSide.SIDE;
      }
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      return null;
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
   public int getColor(IWorld world, BlockPos pos, int tint) {
      return this.getColorForPower(world.getBlockState(pos).get(POWER));
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      return World.hasSolidTop(world, pos.down()) || world.getBlockState(pos.down()).getBlock() == Blocks.GLOWSTONE;
   }

   private BlockState updatePower(World world, BlockPos pos, BlockState state) {
      state = this.updatePower(world, pos, pos, state);
      ArrayList var4 = Lists.newArrayList(this.neighborsToUpdate);
      this.neighborsToUpdate.clear();

      for(BlockPos var6 : var4) {
         world.updateNeighbors(var6, this);
      }

      return state;
   }

   private BlockState updatePower(World world, BlockPos pos, BlockPos pos_, BlockState state) {
      BlockState var5 = state;
      int var6 = state.get(POWER);
      int var7 = 0;
      var7 = this.increaseWirePower(world, pos_, var7);
      this.emitsRedstonePower = false;
      int var8 = world.getReceivedPower(pos);
      this.emitsRedstonePower = true;
      if (var8 > 0 && var8 > var7 - 1) {
         var7 = var8;
      }

      int var9 = 0;

      for(Direction var11 : Direction.Plane.HORIZONTAL) {
         BlockPos var12 = pos.offset(var11);
         boolean var13 = var12.getX() != pos_.getX() || var12.getZ() != pos_.getZ();
         if (var13) {
            var9 = this.increaseWirePower(world, var12, var9);
         }

         if (world.getBlockState(var12).getBlock().isConductor() && !world.getBlockState(pos.up()).getBlock().isConductor()) {
            if (var13 && pos.getY() >= pos_.getY()) {
               var9 = this.increaseWirePower(world, var12.up(), var9);
            }
         } else if (!world.getBlockState(var12).getBlock().isConductor() && var13 && pos.getY() <= pos_.getY()) {
            var9 = this.increaseWirePower(world, var12.down(), var9);
         }
      }

      if (var9 > var7) {
         var7 = var9 - 1;
      } else if (var7 > 0) {
         --var7;
      } else {
         var7 = 0;
      }

      if (var8 > var7 - 1) {
         var7 = var8;
      }

      if (var6 != var7) {
         state = state.set(POWER, var7);
         if (world.getBlockState(pos) == var5) {
            world.setBlockState(pos, state, 2);
         }

         this.neighborsToUpdate.add(pos);

         for(Direction var19 : Direction.values()) {
            this.neighborsToUpdate.add(pos.offset(var19));
         }
      }

      return state;
   }

   private void updateNeighborsOfWire(World world, BlockPos pos) {
      if (world.getBlockState(pos).getBlock() == this) {
         world.updateNeighbors(pos, this);

         for(Direction var6 : Direction.values()) {
            world.updateNeighbors(pos.offset(var6), this);
         }
      }
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      if (!world.isClient) {
         this.updatePower(world, pos, state);

         for(Direction var5 : Direction.Plane.VERTICAL) {
            world.updateNeighbors(pos.offset(var5), this);
         }

         for(Direction var9 : Direction.Plane.HORIZONTAL) {
            this.updateNeighborsOfWire(world, pos.offset(var9));
         }

         for(Direction var10 : Direction.Plane.HORIZONTAL) {
            BlockPos var6 = pos.offset(var10);
            if (world.getBlockState(var6).getBlock().isConductor()) {
               this.updateNeighborsOfWire(world, var6.up());
            } else {
               this.updateNeighborsOfWire(world, var6.down());
            }
         }
      }
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      super.onRemoved(world, pos, state);
      if (!world.isClient) {
         for(Direction var7 : Direction.values()) {
            world.updateNeighbors(pos.offset(var7), this);
         }

         this.updatePower(world, pos, state);

         for(Direction var10 : Direction.Plane.HORIZONTAL) {
            this.updateNeighborsOfWire(world, pos.offset(var10));
         }

         for(Direction var11 : Direction.Plane.HORIZONTAL) {
            BlockPos var12 = pos.offset(var11);
            if (world.getBlockState(var12).getBlock().isConductor()) {
               this.updateNeighborsOfWire(world, var12.up());
            } else {
               this.updateNeighborsOfWire(world, var12.down());
            }
         }
      }
   }

   private int increaseWirePower(World world, BlockPos pos, int power) {
      if (world.getBlockState(pos).getBlock() != this) {
         return power;
      } else {
         int var4 = world.getBlockState(pos).get(POWER);
         return var4 > power ? var4 : power;
      }
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (!world.isClient) {
         if (this.canSurvive(world, pos)) {
            this.updatePower(world, pos, state);
         } else {
            this.dropItems(world, pos, state, 0);
            world.removeBlock(pos);
         }
      }
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Items.REDSTONE;
   }

   @Override
   public int getEmittedStrongPower(IWorld world, BlockPos pos, BlockState state, Direction dir) {
      return !this.emitsRedstonePower ? 0 : this.getEmittedWeakPower(world, pos, state, dir);
   }

   @Override
   public int getEmittedWeakPower(IWorld world, BlockPos pos, BlockState state, Direction dir) {
      if (!this.emitsRedstonePower) {
         return 0;
      } else {
         int var5 = state.get(POWER);
         if (var5 == 0) {
            return 0;
         } else if (dir == Direction.UP) {
            return var5;
         } else {
            EnumSet var6 = EnumSet.noneOf(Direction.class);

            for(Direction var8 : Direction.Plane.HORIZONTAL) {
               if (this.connectsTo(world, pos, var8)) {
                  var6.add(var8);
               }
            }

            if (dir.getAxis().isHorizontal() && var6.isEmpty()) {
               return var5;
            } else {
               return var6.contains(dir) && !var6.contains(dir.counterClockwiseY()) && !var6.contains(dir.clockwiseY()) ? var5 : 0;
            }
         }
      }
   }

   private boolean connectsTo(IWorld world, BlockPos pos, Direction dir) {
      BlockPos var4 = pos.offset(dir);
      BlockState var5 = world.getBlockState(var4);
      Block var6 = var5.getBlock();
      boolean var7 = var6.isConductor();
      boolean var8 = world.getBlockState(pos.up()).getBlock().isConductor();
      if (!var8 && var7 && shouldConnectTo(world, var4.up())) {
         return true;
      } else if (shouldConnectTo(var5, dir)) {
         return true;
      } else if (var6 == Blocks.POWERED_REPEATER && var5.get(RedstoneDiodeBlock.FACING) == dir) {
         return true;
      } else {
         return !var7 && shouldConnectTo(world, var4.down());
      }
   }

   protected static boolean shouldConnectTo(IWorld world, BlockPos pos) {
      return shouldConnectTo(world.getBlockState(pos));
   }

   protected static boolean shouldConnectTo(BlockState state) {
      return shouldConnectTo(state, null);
   }

   protected static boolean shouldConnectTo(BlockState state, Direction dir) {
      Block var2 = state.getBlock();
      if (var2 == Blocks.REDSTONE_WIRE) {
         return true;
      } else if (Blocks.REPEATER.isSameDiode(var2)) {
         Direction var3 = (Direction)state.get(RepeaterBlock.FACING);
         return var3 == dir || var3.getOpposite() == dir;
      } else {
         return var2.isPowerSource() && dir != null;
      }
   }

   @Override
   public boolean isPowerSource() {
      return this.emitsRedstonePower;
   }

   @Environment(EnvType.CLIENT)
   private int getColorForPower(int power) {
      float var2 = (float)power / 15.0F;
      float var3 = var2 * 0.6F + 0.4F;
      if (power == 0) {
         var3 = 0.3F;
      }

      float var4 = var2 * var2 * 0.7F - 0.5F;
      float var5 = var2 * var2 * 0.6F - 0.7F;
      if (var4 < 0.0F) {
         var4 = 0.0F;
      }

      if (var5 < 0.0F) {
         var5 = 0.0F;
      }

      int var6 = MathHelper.clamp((int)(var3 * 255.0F), 0, 255);
      int var7 = MathHelper.clamp((int)(var4 * 255.0F), 0, 255);
      int var8 = MathHelper.clamp((int)(var5 * 255.0F), 0, 255);
      return 0xFF000000 | var6 << 16 | var7 << 8 | var8;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random random) {
      int var5 = state.get(POWER);
      if (var5 != 0) {
         double var6 = (double)pos.getX() + 0.5 + ((double)random.nextFloat() - 0.5) * 0.2;
         double var8 = (double)((float)pos.getY() + 0.0625F);
         double var10 = (double)pos.getZ() + 0.5 + ((double)random.nextFloat() - 0.5) * 0.2;
         float var12 = (float)var5 / 15.0F;
         float var13 = var12 * 0.6F + 0.4F;
         float var14 = Math.max(0.0F, var12 * var12 * 0.7F - 0.5F);
         float var15 = Math.max(0.0F, var12 * var12 * 0.6F - 0.7F);
         world.addParticle(ParticleType.REDSTONE, var6, var8, var10, (double)var13, (double)var14, (double)var15);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Items.REDSTONE;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(POWER, metadata);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return state.get(POWER);
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, NORTH, EAST, SOUTH, WEST, POWER);
   }

   static enum ConnectionSide implements StringRepresentable {
      UP("up"),
      SIDE("side"),
      NONE("none");

      private final String name;

      private ConnectionSide(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.getStringRepresentation();
      }

      @Override
      public String getStringRepresentation() {
         return this.name;
      }
   }
}
