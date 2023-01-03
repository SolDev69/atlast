package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.block.state.property.IntegerProperty;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class RepeaterBlock extends RedstoneDiodeBlock {
   public static final BooleanProperty LOCKED = BooleanProperty.of("locked");
   public static final IntegerProperty DELAY = IntegerProperty.of("delay", 1, 4);

   protected RepeaterBlock(boolean bl) {
      super(bl);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH).set(DELAY, 1).set(LOCKED, false));
   }

   @Override
   public BlockState updateShape(BlockState state, IWorld world, BlockPos pos) {
      return state.set(LOCKED, this.isLocked(world, pos, state));
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (!player.abilities.canModifyWorld) {
         return false;
      } else {
         world.setBlockState(pos, state.next(DELAY), 3);
         return true;
      }
   }

   @Override
   protected int getDelay(BlockState state) {
      return state.get(DELAY) * 2;
   }

   @Override
   protected BlockState setUnpowered(BlockState state) {
      Integer var2 = (Integer)state.get(DELAY);
      Boolean var3 = (Boolean)state.get(LOCKED);
      Direction var4 = (Direction)state.get(FACING);
      return Blocks.POWERED_REPEATER.defaultState().set(FACING, var4).set(DELAY, var2).set(LOCKED, var3);
   }

   @Override
   protected BlockState setPowered(BlockState state) {
      Integer var2 = (Integer)state.get(DELAY);
      Boolean var3 = (Boolean)state.get(LOCKED);
      Direction var4 = (Direction)state.get(FACING);
      return Blocks.REPEATER.defaultState().set(FACING, var4).set(DELAY, var2).set(LOCKED, var3);
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Items.REPEATER;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Items.REPEATER;
   }

   @Override
   public boolean isLocked(IWorld world, BlockPos pos, BlockState state) {
      return this.getReceivedSidePower(world, pos, state) > 0;
   }

   @Override
   protected boolean isValidSideInput(Block block) {
      return isDiode(block);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random random) {
      if (this.powered) {
         Direction var5 = (Direction)state.get(FACING);
         double var6 = (double)((float)pos.getX() + 0.5F) + (double)(random.nextFloat() - 0.5F) * 0.2;
         double var8 = (double)((float)pos.getY() + 0.4F) + (double)(random.nextFloat() - 0.5F) * 0.2;
         double var10 = (double)((float)pos.getZ() + 0.5F) + (double)(random.nextFloat() - 0.5F) * 0.2;
         float var12 = -5.0F;
         if (random.nextBoolean()) {
            var12 = (float)(state.get(DELAY) * 2 - 1);
         }

         var12 /= 16.0F;
         double var13 = (double)(var12 * (float)var5.getOffsetX());
         double var15 = (double)(var12 * (float)var5.getOffsetZ());
         world.addParticle(ParticleType.REDSTONE, var6 + var13, var8, var10 + var15, 0.0, 0.0, 0.0);
      }
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      super.onRemoved(world, pos, state);
      this.updateNeighbors(world, pos, state);
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(FACING, Direction.byIdHorizontal(metadata)).set(LOCKED, false).set(DELAY, 1 + (metadata >> 2));
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((Direction)state.get(FACING)).getIdHorizontal();
      return var2 | state.get(DELAY) - 1 << 2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING, DELAY, LOCKED);
   }
}
