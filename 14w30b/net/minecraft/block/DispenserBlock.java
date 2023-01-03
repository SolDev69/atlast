package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.dispenser.DispenseBehavior;
import net.minecraft.block.dispenser.DispenseItemBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.InventoryUtils;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.IPosition;
import net.minecraft.util.math.Position;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.world.BlockSource;
import net.minecraft.world.IBlockSource;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class DispenserBlock extends BlockWithBlockEntity {
   public static final DirectionProperty FACING = DirectionProperty.of("facing");
   public static final BooleanProperty TRIGGERED = BooleanProperty.of("triggered");
   public static final DefaultedRegistry BEHAVIORS = new DefaultedRegistry(new DispenseItemBehavior());
   protected Random random = new Random();

   protected DispenserBlock() {
      super(Material.STONE);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH).set(TRIGGERED, false));
      this.setItemGroup(ItemGroup.REDSTONE);
   }

   @Override
   public int getTickRate(World world) {
      return 4;
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      super.onAdded(world, pos, state);
      this.updateFacing(world, pos, state);
   }

   private void updateFacing(World world, BlockPos pos, BlockState state) {
      if (!world.isClient) {
         Direction var4 = (Direction)state.get(FACING);
         boolean var5 = world.getBlockState(pos.north()).getBlock().isOpaque();
         boolean var6 = world.getBlockState(pos.south()).getBlock().isOpaque();
         if (var4 == Direction.NORTH && var5 && !var6) {
            var4 = Direction.SOUTH;
         } else if (var4 == Direction.SOUTH && var6 && !var5) {
            var4 = Direction.NORTH;
         } else {
            boolean var7 = world.getBlockState(pos.west()).getBlock().isOpaque();
            boolean var8 = world.getBlockState(pos.east()).getBlock().isOpaque();
            if (var4 == Direction.WEST && var7 && !var8) {
               var4 = Direction.EAST;
            } else if (var4 == Direction.EAST && var8 && !var7) {
               var4 = Direction.WEST;
            }
         }

         world.setBlockState(pos, state.set(FACING, var4).set(TRIGGERED, false), 2);
      }
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (world.isClient) {
         return true;
      } else {
         BlockEntity var9 = world.getBlockEntity(pos);
         if (var9 instanceof DispenserBlockEntity) {
            player.openInventoryMenu((DispenserBlockEntity)var9);
         }

         return true;
      }
   }

   protected void dispense(World world, BlockPos pos) {
      BlockSource var3 = new BlockSource(world, pos);
      DispenserBlockEntity var4 = (DispenserBlockEntity)var3.getBlockEntity();
      if (var4 != null) {
         int var5 = var4.pickNonEmptySlot();
         if (var5 < 0) {
            world.doEvent(1001, pos, 0);
         } else {
            ItemStack var6 = var4.getStack(var5);
            DispenseBehavior var7 = this.getDispenseBehavior(var6);
            if (var7 != DispenseBehavior.NONE) {
               ItemStack var8 = var7.dispense(var3, var6);
               var4.setStack(var5, var8.size == 0 ? null : var8);
            }
         }
      }
   }

   protected DispenseBehavior getDispenseBehavior(ItemStack stack) {
      return (DispenseBehavior)BEHAVIORS.get(stack == null ? null : stack.getItem());
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      boolean var5 = world.isReceivingPower(pos) || world.isReceivingPower(pos.up());
      boolean var6 = state.get(TRIGGERED);
      if (var5 && !var6) {
         world.scheduleTick(pos, this, this.getTickRate(world));
         world.setBlockState(pos, state.set(TRIGGERED, true), 4);
      } else if (!var5 && var6) {
         world.setBlockState(pos, state.set(TRIGGERED, false), 4);
      }
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (!world.isClient) {
         this.dispense(world, pos);
      }
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      return new DispenserBlockEntity();
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      return this.defaultState().set(FACING, PistonBaseBlock.getFacingForPlacement(world, pos, entity)).set(TRIGGERED, false);
   }

   @Override
   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
      world.setBlockState(pos, state.set(FACING, PistonBaseBlock.getFacingForPlacement(world, pos, entity)), 2);
      if (stack.hasCustomHoverName()) {
         BlockEntity var6 = world.getBlockEntity(pos);
         if (var6 instanceof DispenserBlockEntity) {
            ((DispenserBlockEntity)var6).setCustomName(stack.getHoverName());
         }
      }
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      BlockEntity var4 = world.getBlockEntity(pos);
      if (var4 instanceof DispenserBlockEntity) {
         InventoryUtils.dropContents(world, pos, (DispenserBlockEntity)var4);
         world.updateComparators(pos, this);
      }

      super.onRemoved(world, pos, state);
   }

   public static IPosition getDispensePos(IBlockSource block) {
      Direction var1 = getDirection(block.getBlockMetadata());
      double var2 = block.getX() + 0.7 * (double)var1.getOffsetX();
      double var4 = block.getY() + 0.7 * (double)var1.getOffsetY();
      double var6 = block.getZ() + 0.7 * (double)var1.getOffsetZ();
      return new Position(var2, var4, var6);
   }

   public static Direction getDirection(int id) {
      return Direction.byId(id & 7);
   }

   @Override
   public boolean hasAnalogOutput() {
      return true;
   }

   @Override
   public int getAnalogOutput(World world, BlockPos pos) {
      return InventoryMenu.getAnalogOutput(world.getBlockEntity(pos));
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int m_43rfjsapl(int i) {
      return Direction.SOUTH.getId();
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(FACING, getDirection(metadata)).set(TRIGGERED, (metadata & 8) > 0);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((Direction)state.get(FACING)).getId();
      if (state.get(TRIGGERED)) {
         var2 |= 8;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING, TRIGGERED);
   }
}
