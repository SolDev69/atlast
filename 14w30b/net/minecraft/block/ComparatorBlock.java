package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityProvider;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class ComparatorBlock extends RedstoneDiodeBlock implements BlockEntityProvider {
   public static final BooleanProperty POWERED = BooleanProperty.of("powered");
   public static final EnumProperty MODE = EnumProperty.of("mode", ComparatorBlock.Mode.class);

   public ComparatorBlock(boolean bl) {
      super(bl);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH).set(POWERED, false).set(MODE, ComparatorBlock.Mode.COMPARE));
      this.hasBlockEntity = true;
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Items.COMPARATOR;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Items.COMPARATOR;
   }

   @Override
   protected int getDelay(BlockState state) {
      return 2;
   }

   @Override
   protected BlockState setUnpowered(BlockState state) {
      Boolean var2 = (Boolean)state.get(POWERED);
      ComparatorBlock.Mode var3 = (ComparatorBlock.Mode)state.get(MODE);
      Direction var4 = (Direction)state.get(FACING);
      return Blocks.POWERED_COMPARATOR.defaultState().set(FACING, var4).set(POWERED, var2).set(MODE, var3);
   }

   @Override
   protected BlockState setPowered(BlockState state) {
      Boolean var2 = (Boolean)state.get(POWERED);
      ComparatorBlock.Mode var3 = (ComparatorBlock.Mode)state.get(MODE);
      Direction var4 = (Direction)state.get(FACING);
      return Blocks.COMPARATOR.defaultState().set(FACING, var4).set(POWERED, var2).set(MODE, var3);
   }

   @Override
   protected boolean isPowered(BlockState state) {
      return this.powered || state.get(POWERED);
   }

   @Override
   protected int getPowerLevel(IWorld world, BlockPos pos, BlockState state) {
      BlockEntity var4 = world.getBlockEntity(pos);
      return var4 instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)var4).getPowerLevel() : 0;
   }

   private int calculatePowerLevel(World world, BlockPos pos, BlockState state) {
      return state.get(MODE) == ComparatorBlock.Mode.SUBTRACT
         ? Math.max(this.getReceivedPower(world, pos, state) - this.getReceivedSidePower(world, pos, state), 0)
         : this.getReceivedPower(world, pos, state);
   }

   @Override
   protected boolean shouldBePowered(World world, BlockPos pos, BlockState state) {
      int var4 = this.getReceivedPower(world, pos, state);
      if (var4 >= 15) {
         return true;
      } else if (var4 == 0) {
         return false;
      } else {
         int var5 = this.getReceivedSidePower(world, pos, state);
         if (var5 == 0) {
            return true;
         } else {
            return var4 >= var5;
         }
      }
   }

   @Override
   protected int getReceivedPower(World world, BlockPos pos, BlockState state) {
      int var4 = super.getReceivedPower(world, pos, state);
      Direction var5 = (Direction)state.get(FACING);
      BlockPos var6 = pos.offset(var5);
      Block var7 = world.getBlockState(var6).getBlock();
      if (var7.hasAnalogOutput()) {
         var4 = var7.getAnalogOutput(world, var6);
      } else if (var4 < 15 && var7.isConductor()) {
         var6 = var6.offset(var5);
         var7 = world.getBlockState(var6).getBlock();
         if (var7.hasAnalogOutput()) {
            var4 = var7.getAnalogOutput(world, var6);
         } else if (var7.getMaterial() == Material.AIR) {
            ItemFrameEntity var8 = this.findItemFrameForPower(world, var5, var6);
            if (var8 != null) {
               var4 = var8.m_45dhrnlgy();
            }
         }
      }

      return var4;
   }

   private ItemFrameEntity findItemFrameForPower(World world, Direction dir, BlockPos pos) {
      List var4 = world.getEntities(
         ItemFrameEntity.class,
         new Box((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)),
         new Predicate() {
            public boolean apply(Entity c_47ldwddrb) {
               return c_47ldwddrb != null && c_47ldwddrb.getDirection() == dir;
            }
         }
      );
      return var4.size() == 1 ? (ItemFrameEntity)var4.get(0) : null;
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (!player.abilities.canModifyWorld) {
         return false;
      } else {
         state = state.next(MODE);
         world.playSound(
            (double)pos.getX() + 0.5,
            (double)pos.getY() + 0.5,
            (double)pos.getZ() + 0.5,
            "random.click",
            0.3F,
            state.get(MODE) == ComparatorBlock.Mode.SUBTRACT ? 0.55F : 0.5F
         );
         world.setBlockState(pos, state, 2);
         this.updatePowerLevel(world, pos, state);
         return true;
      }
   }

   @Override
   protected void updatePowered(World world, BlockPos pos, BlockState state) {
      if (!world.willTickThisTick(pos, this)) {
         int var4 = this.calculatePowerLevel(world, pos, state);
         BlockEntity var5 = world.getBlockEntity(pos);
         int var6 = var5 instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)var5).getPowerLevel() : 0;
         if (var4 != var6 || this.isPowered(state) != this.shouldBePowered(world, pos, state)) {
            if (this.shouldPrioritize(world, pos, state)) {
               world.scheduleTick(pos, this, 2, -1);
            } else {
               world.scheduleTick(pos, this, 2, 0);
            }
         }
      }
   }

   private void updatePowerLevel(World world, BlockPos pos, BlockState state) {
      int var4 = this.calculatePowerLevel(world, pos, state);
      BlockEntity var5 = world.getBlockEntity(pos);
      int var6 = 0;
      if (var5 instanceof ComparatorBlockEntity) {
         ComparatorBlockEntity var7 = (ComparatorBlockEntity)var5;
         var6 = var7.getPowerLevel();
         var7.setPowerLevel(var4);
      }

      if (var6 != var4 || state.get(MODE) == ComparatorBlock.Mode.COMPARE) {
         boolean var9 = this.shouldBePowered(world, pos, state);
         boolean var8 = this.isPowered(state);
         if (var8 && !var9) {
            world.setBlockState(pos, state.set(POWERED, false), 2);
         } else if (!var8 && var9) {
            world.setBlockState(pos, state.set(POWERED, true), 2);
         }

         this.updateNeighbors(world, pos, state);
      }
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (this.powered) {
         world.setBlockState(pos, this.setPowered(state).set(POWERED, true), 4);
      }

      this.updatePowerLevel(world, pos, state);
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      super.onAdded(world, pos, state);
      world.setBlockEntity(pos, this.createBlockEntity(world, 0));
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      super.onRemoved(world, pos, state);
      world.removeBlockEntity(pos);
      this.updateNeighbors(world, pos, state);
   }

   @Override
   public boolean doEvent(World world, BlockPos pos, BlockState state, int type, int data) {
      super.doEvent(world, pos, state, type, data);
      BlockEntity var6 = world.getBlockEntity(pos);
      return var6 == null ? false : var6.doEvent(type, data);
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      return new ComparatorBlockEntity();
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState()
         .set(FACING, Direction.byIdHorizontal(metadata))
         .set(POWERED, (metadata & 8) > 0)
         .set(MODE, (metadata & 4) > 0 ? ComparatorBlock.Mode.SUBTRACT : ComparatorBlock.Mode.COMPARE);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((Direction)state.get(FACING)).getIdHorizontal();
      if (state.get(POWERED)) {
         var2 |= 8;
      }

      if (state.get(MODE) == ComparatorBlock.Mode.SUBTRACT) {
         var2 |= 4;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING, MODE, POWERED);
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      return this.defaultState().set(FACING, entity.getDirection().getOpposite()).set(POWERED, false).set(MODE, ComparatorBlock.Mode.COMPARE);
   }

   public static enum Mode implements StringRepresentable {
      COMPARE("compare"),
      SUBTRACT("subtract");

      private final String id;

      private Mode(String id) {
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
