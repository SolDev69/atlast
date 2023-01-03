package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.source.CommandExecutor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CommandBlock extends BlockWithBlockEntity {
   public static final BooleanProperty TRIGGERED = BooleanProperty.of("triggered");

   public CommandBlock() {
      super(Material.IRON);
      this.setDefaultState(this.stateDefinition.any().set(TRIGGERED, false));
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      return new CommandBlockBlockEntity();
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (!world.isClient) {
         boolean var5 = world.isReceivingPower(pos);
         boolean var6 = state.get(TRIGGERED);
         if (var5 && !var6) {
            world.setBlockState(pos, state.set(TRIGGERED, true), 4);
            world.scheduleTick(pos, this, this.getTickRate(world));
         } else if (!var5 && var6) {
            world.setBlockState(pos, state.set(TRIGGERED, false), 4);
         }
      }
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      BlockEntity var5 = world.getBlockEntity(pos);
      if (var5 instanceof CommandBlockBlockEntity) {
         ((CommandBlockBlockEntity)var5).getCommandExecutor().run(world);
         world.updateComparators(pos, this);
      }
   }

   @Override
   public int getTickRate(World world) {
      return 1;
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      BlockEntity var9 = world.getBlockEntity(pos);
      return var9 instanceof CommandBlockBlockEntity ? ((CommandBlockBlockEntity)var9).getCommandExecutor().openScreen(player) : false;
   }

   @Override
   public boolean hasAnalogOutput() {
      return true;
   }

   @Override
   public int getAnalogOutput(World world, BlockPos pos) {
      BlockEntity var3 = world.getBlockEntity(pos);
      return var3 instanceof CommandBlockBlockEntity ? ((CommandBlockBlockEntity)var3).getCommandExecutor().getSuccessCount() : 0;
   }

   @Override
   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
      BlockEntity var6 = world.getBlockEntity(pos);
      if (var6 instanceof CommandBlockBlockEntity) {
         CommandExecutor var7 = ((CommandBlockBlockEntity)var6).getCommandExecutor();
         if (stack.hasCustomHoverName()) {
            var7.setName(stack.getHoverName());
         }

         if (!world.isClient) {
            var7.setTrackOutput(world.getGameRules().getBoolean("sendCommandFeedback"));
         }
      }
   }

   @Override
   public int getBaseDropCount(Random random) {
      return 0;
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(TRIGGERED, (metadata & 1) > 0);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      if (state.get(TRIGGERED)) {
         var2 |= 1;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, TRIGGERED);
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      return this.defaultState().set(TRIGGERED, false);
   }
}
