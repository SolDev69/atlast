package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class TripwireBlock extends Block {
   public static final BooleanProperty POWERED = BooleanProperty.of("powered");
   public static final BooleanProperty SUSPENDED = BooleanProperty.of("suspended");
   public static final BooleanProperty ATTACHED = BooleanProperty.of("attached");
   public static final BooleanProperty DISARMED = BooleanProperty.of("disarmed");
   public static final BooleanProperty NORTH = BooleanProperty.of("north");
   public static final BooleanProperty EAST = BooleanProperty.of("east");
   public static final BooleanProperty SOUTH = BooleanProperty.of("south");
   public static final BooleanProperty WEST = BooleanProperty.of("west");

   public TripwireBlock() {
      super(Material.DECORATION);
      this.setDefaultState(
         this.stateDefinition
            .any()
            .set(POWERED, false)
            .set(SUSPENDED, false)
            .set(ATTACHED, false)
            .set(DISARMED, false)
            .set(NORTH, false)
            .set(EAST, false)
            .set(SOUTH, false)
            .set(WEST, false)
      );
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.15625F, 1.0F);
      this.setTicksRandomly(true);
   }

   @Override
   public BlockState updateShape(BlockState state, IWorld world, BlockPos pos) {
      return state.set(NORTH, shouldConnectTo(world, pos, state, Direction.NORTH))
         .set(EAST, shouldConnectTo(world, pos, state, Direction.EAST))
         .set(SOUTH, shouldConnectTo(world, pos, state, Direction.SOUTH))
         .set(WEST, shouldConnectTo(world, pos, state, Direction.WEST));
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
   public BlockLayer getRenderLayer() {
      return BlockLayer.TRANSLUCENT;
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Items.STRING;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Items.STRING;
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      boolean var5 = state.get(SUSPENDED);
      boolean var6 = !World.hasSolidTop(world, pos.down());
      if (var5 != var6) {
         this.dropItems(world, pos, state, 0);
         world.removeBlock(pos);
      }
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      BlockState var3 = world.getBlockState(pos);
      boolean var4 = var3.get(ATTACHED);
      boolean var5 = var3.get(SUSPENDED);
      if (!var5) {
         this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.09375F, 1.0F);
      } else if (!var4) {
         this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      } else {
         this.setShape(0.0F, 0.0625F, 0.0F, 1.0F, 0.15625F, 1.0F);
      }
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      state = state.set(SUSPENDED, !World.hasSolidTop(world, pos.down()));
      world.setBlockState(pos, state, 3);
      this.updateTripwireHooks(world, pos, state);
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      this.updateTripwireHooks(world, pos, state.set(POWERED, true));
   }

   @Override
   public void beforeMinedByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      if (!world.isClient) {
         if (player.getMainHandStack() != null && player.getMainHandStack().getItem() == Items.SHEARS) {
            world.setBlockState(pos, state.set(DISARMED, true), 4);
         }
      }
   }

   private void updateTripwireHooks(World world, BlockPos pos, BlockState state) {
      for(Direction var7 : new Direction[]{Direction.SOUTH, Direction.WEST}) {
         for(int var8 = 1; var8 < 42; ++var8) {
            BlockPos var9 = pos.offset(var7, var8);
            BlockState var10 = world.getBlockState(var9);
            if (var10.getBlock() == Blocks.TRIPWIRE_HOOK) {
               if (var10.get(TripwireHookBlock.FACING) == var7.getOpposite()) {
                  Blocks.TRIPWIRE_HOOK.updatePowered(world, var9, var10, false, true, var8, state);
               }
               break;
            }

            if (var10.getBlock() != Blocks.TRIPWIRE) {
               break;
            }
         }
      }
   }

   @Override
   public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
      if (!world.isClient) {
         if (!state.get(POWERED)) {
            this.updatePowered(world, pos);
         }
      }
   }

   @Override
   public void randomTick(World world, BlockPos pos, BlockState state, Random random) {
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (!world.isClient) {
         if (world.getBlockState(pos).get(POWERED)) {
            this.updatePowered(world, pos);
         }
      }
   }

   private void updatePowered(World world, BlockPos pos) {
      BlockState var3 = world.getBlockState(pos);
      boolean var4 = var3.get(POWERED);
      boolean var5 = false;
      List var6 = world.getEntities(
         null,
         new Box(
            (double)pos.getX() + this.minX,
            (double)pos.getY() + this.minY,
            (double)pos.getZ() + this.minZ,
            (double)pos.getX() + this.maxX,
            (double)pos.getY() + this.maxY,
            (double)pos.getZ() + this.maxZ
         )
      );
      if (!var6.isEmpty()) {
         for(Entity var8 : var6) {
            if (!var8.canAvoidTraps()) {
               var5 = true;
               break;
            }
         }
      }

      if (var5 != var4) {
         var3 = var3.set(POWERED, var5);
         world.setBlockState(pos, var3, 3);
         this.updateTripwireHooks(world, pos, var3);
      }

      if (var5) {
         world.scheduleTick(pos, this, this.getTickRate(world));
      }
   }

   public static boolean shouldConnectTo(IWorld world, BlockPos pos, BlockState state, Direction dir) {
      BlockPos var4 = pos.offset(dir);
      BlockState var5 = world.getBlockState(var4);
      Block var6 = var5.getBlock();
      if (var6 == Blocks.TRIPWIRE_HOOK) {
         Direction var9 = dir.getOpposite();
         return var5.get(TripwireHookBlock.FACING) == var9;
      } else if (var6 == Blocks.TRIPWIRE) {
         boolean var7 = state.get(SUSPENDED);
         boolean var8 = var5.get(SUSPENDED);
         return var7 == var8;
      } else {
         return false;
      }
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState()
         .set(POWERED, (metadata & 1) > 0)
         .set(SUSPENDED, (metadata & 2) > 0)
         .set(ATTACHED, (metadata & 4) > 0)
         .set(DISARMED, (metadata & 8) > 0);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      if (state.get(POWERED)) {
         var2 |= 1;
      }

      if (state.get(SUSPENDED)) {
         var2 |= 2;
      }

      if (state.get(ATTACHED)) {
         var2 |= 4;
      }

      if (state.get(DISARMED)) {
         var2 |= 8;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, POWERED, SUSPENDED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
   }
}
