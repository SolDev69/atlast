package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.passive.animal.tamable.OcelotEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryUtils;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.menu.LockableMenuProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ChestBlock extends BlockWithBlockEntity {
   public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.Plane.HORIZONTAL);
   private final Random random = new Random();
   public final int type;

   protected ChestBlock(int type) {
      super(Material.WOOD);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH));
      this.type = type;
      this.setItemGroup(ItemGroup.DECORATIONS);
      this.setShape(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
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
   public int getRenderType() {
      return 2;
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      if (world.getBlockState(pos.north()).getBlock() == this) {
         this.setShape(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
      } else if (world.getBlockState(pos.south()).getBlock() == this) {
         this.setShape(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
      } else if (world.getBlockState(pos.west()).getBlock() == this) {
         this.setShape(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
      } else if (world.getBlockState(pos.east()).getBlock() == this) {
         this.setShape(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
      } else {
         this.setShape(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
      }
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      this.updateState(world, pos, state);

      for(Direction var5 : Direction.Plane.HORIZONTAL) {
         BlockPos var6 = pos.offset(var5);
         BlockState var7 = world.getBlockState(var6);
         if (var7.getBlock() == this) {
            this.updateState(world, var6, var7);
         }
      }
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      return this.defaultState().set(FACING, entity.getDirection());
   }

   @Override
   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
      Direction var6 = Direction.byIdHorizontal(MathHelper.floor((double)(entity.yaw * 4.0F / 360.0F) + 0.5) & 3).getOpposite();
      state = state.set(FACING, var6);
      BlockPos var7 = pos.north();
      BlockPos var8 = pos.south();
      BlockPos var9 = pos.west();
      BlockPos var10 = pos.east();
      boolean var11 = this == world.getBlockState(var7).getBlock();
      boolean var12 = this == world.getBlockState(var8).getBlock();
      boolean var13 = this == world.getBlockState(var9).getBlock();
      boolean var14 = this == world.getBlockState(var10).getBlock();
      if (!var11 && !var12 && !var13 && !var14) {
         world.setBlockState(pos, state, 3);
      } else if (var6.getAxis() != Direction.Axis.X || !var11 && !var12) {
         if (var6.getAxis() == Direction.Axis.Z && (var13 || var14)) {
            if (var13) {
               world.setBlockState(var9, state, 3);
            } else {
               world.setBlockState(var10, state, 3);
            }

            world.setBlockState(pos, state, 3);
         }
      } else {
         if (var11) {
            world.setBlockState(var7, state, 3);
         } else {
            world.setBlockState(var8, state, 3);
         }

         world.setBlockState(pos, state, 3);
      }

      if (stack.hasCustomHoverName()) {
         BlockEntity var15 = world.getBlockEntity(pos);
         if (var15 instanceof ChestBlockEntity) {
            ((ChestBlockEntity)var15).setCustomName(stack.getHoverName());
         }
      }
   }

   public BlockState updateState(World world, BlockPos pos, BlockState state) {
      if (world.isClient) {
         return state;
      } else {
         BlockState var4 = world.getBlockState(pos.north());
         BlockState var5 = world.getBlockState(pos.south());
         BlockState var6 = world.getBlockState(pos.west());
         BlockState var7 = world.getBlockState(pos.east());
         Direction var8 = (Direction)state.get(FACING);
         Block var9 = var4.getBlock();
         Block var10 = var5.getBlock();
         Block var11 = var6.getBlock();
         Block var12 = var7.getBlock();
         if (var9 != this && var10 != this) {
            boolean var22 = var9.isOpaque();
            boolean var23 = var10.isOpaque();
            if (var11 == this || var12 == this) {
               BlockPos var24 = var11 == this ? pos.west() : pos.east();
               BlockState var25 = world.getBlockState(var24.north());
               BlockState var26 = world.getBlockState(var24.south());
               var8 = Direction.SOUTH;
               Direction var27;
               if (var11 == this) {
                  var27 = (Direction)var6.get(FACING);
               } else {
                  var27 = (Direction)var7.get(FACING);
               }

               if (var27 == Direction.NORTH) {
                  var8 = Direction.NORTH;
               }

               Block var19 = var25.getBlock();
               Block var20 = var26.getBlock();
               if ((var22 || var19.isOpaque()) && !var23 && !var20.isOpaque()) {
                  var8 = Direction.SOUTH;
               }

               if ((var23 || var20.isOpaque()) && !var22 && !var19.isOpaque()) {
                  var8 = Direction.NORTH;
               }
            }
         } else {
            BlockPos var13 = var9 == this ? pos.north() : pos.south();
            BlockState var14 = world.getBlockState(var13.west());
            BlockState var15 = world.getBlockState(var13.east());
            var8 = Direction.EAST;
            Direction var16;
            if (var9 == this) {
               var16 = (Direction)var4.get(FACING);
            } else {
               var16 = (Direction)var5.get(FACING);
            }

            if (var16 == Direction.WEST) {
               var8 = Direction.WEST;
            }

            Block var17 = var14.getBlock();
            Block var18 = var15.getBlock();
            if ((var11.isOpaque() || var17.isOpaque()) && !var12.isOpaque() && !var18.isOpaque()) {
               var8 = Direction.EAST;
            }

            if ((var12.isOpaque() || var18.isOpaque()) && !var11.isOpaque() && !var17.isOpaque()) {
               var8 = Direction.WEST;
            }
         }

         state = state.set(FACING, var8);
         world.setBlockState(pos, state, 3);
         return state;
      }
   }

   public BlockState updateFacing(World world, BlockPos pos, BlockState state) {
      Direction var4 = null;

      for(Direction var6 : Direction.Plane.HORIZONTAL) {
         BlockState var7 = world.getBlockState(pos.offset(var6));
         if (var7.getBlock() == this) {
            return state;
         }

         if (var7.getBlock().isOpaque()) {
            if (var4 != null) {
               var4 = null;
               break;
            }

            var4 = var6;
         }
      }

      if (var4 != null) {
         return state.set(FACING, var4.getOpposite());
      } else {
         Direction var8 = (Direction)state.get(FACING);
         if (world.getBlockState(pos.offset(var8)).getBlock().isOpaque()) {
            var8 = var8.getOpposite();
         }

         if (world.getBlockState(pos.offset(var8)).getBlock().isOpaque()) {
            var8 = var8.clockwiseY();
         }

         if (world.getBlockState(pos.offset(var8)).getBlock().isOpaque()) {
            var8 = var8.getOpposite();
         }

         return state.set(FACING, var8);
      }
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      int var3 = 0;
      BlockPos var4 = pos.west();
      BlockPos var5 = pos.east();
      BlockPos var6 = pos.north();
      BlockPos var7 = pos.south();
      if (world.getBlockState(var4).getBlock() == this) {
         if (this.isDoubleChest(world, var4)) {
            return false;
         }

         ++var3;
      }

      if (world.getBlockState(var5).getBlock() == this) {
         if (this.isDoubleChest(world, var5)) {
            return false;
         }

         ++var3;
      }

      if (world.getBlockState(var6).getBlock() == this) {
         if (this.isDoubleChest(world, var6)) {
            return false;
         }

         ++var3;
      }

      if (world.getBlockState(var7).getBlock() == this) {
         if (this.isDoubleChest(world, var7)) {
            return false;
         }

         ++var3;
      }

      return var3 <= 1;
   }

   private boolean isDoubleChest(World world, BlockPos pos) {
      if (world.getBlockState(pos).getBlock() != this) {
         return false;
      } else {
         for(Direction var4 : Direction.Plane.HORIZONTAL) {
            if (world.getBlockState(pos.offset(var4)).getBlock() == this) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      super.update(world, pos, state, neighborBlock);
      BlockEntity var5 = world.getBlockEntity(pos);
      if (var5 instanceof ChestBlockEntity) {
         var5.clearBlockCache();
      }
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      BlockEntity var4 = world.getBlockEntity(pos);
      if (var4 instanceof Inventory) {
         InventoryUtils.dropContents(world, pos, (Inventory)var4);
         world.updateComparators(pos, this);
      }

      super.onRemoved(world, pos, state);
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (world.isClient) {
         return true;
      } else {
         LockableMenuProvider var9 = this.getInventory(world, pos);
         if (var9 != null) {
            player.openInventoryMenu(var9);
         }

         return true;
      }
   }

   public LockableMenuProvider getInventory(World world, BlockPos pos) {
      BlockEntity var3 = world.getBlockEntity(pos);
      if (!(var3 instanceof ChestBlockEntity)) {
         return null;
      } else {
         Object var4 = (ChestBlockEntity)var3;
         if (this.isLocked(world, pos)) {
            return null;
         } else {
            for(Direction var6 : Direction.Plane.HORIZONTAL) {
               BlockPos var7 = pos.offset(var6);
               Block var8 = world.getBlockState(var7).getBlock();
               if (var8 == this) {
                  if (this.isLocked(world, var7)) {
                     return null;
                  }

                  BlockEntity var9 = world.getBlockEntity(var7);
                  if (var9 instanceof ChestBlockEntity) {
                     if (var6 != Direction.WEST && var6 != Direction.NORTH) {
                        var4 = new DoubleInventory("container.chestDouble", (LockableMenuProvider)var4, (ChestBlockEntity)var9);
                     } else {
                        var4 = new DoubleInventory("container.chestDouble", (ChestBlockEntity)var9, (LockableMenuProvider)var4);
                     }
                  }
               }
            }

            return (LockableMenuProvider)var4;
         }
      }
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      return new ChestBlockEntity();
   }

   @Override
   public boolean isPowerSource() {
      return this.type == 1;
   }

   @Override
   public int getEmittedWeakPower(IWorld world, BlockPos pos, BlockState state, Direction dir) {
      if (!this.isPowerSource()) {
         return 0;
      } else {
         int var5 = 0;
         BlockEntity var6 = world.getBlockEntity(pos);
         if (var6 instanceof ChestBlockEntity) {
            var5 = ((ChestBlockEntity)var6).viewerCount;
         }

         return MathHelper.clamp(var5, 0, 15);
      }
   }

   @Override
   public int getEmittedStrongPower(IWorld world, BlockPos pos, BlockState state, Direction dir) {
      return dir == Direction.UP ? this.getEmittedWeakPower(world, pos, state, dir) : 0;
   }

   private boolean isLocked(World world, BlockPos pos) {
      return this.isLockedByBlock(world, pos) || this.isLockedByOcelot(world, pos);
   }

   private boolean isLockedByBlock(World world, BlockPos pos) {
      return world.getBlockState(pos.up()).getBlock().isConductor();
   }

   private boolean isLockedByOcelot(World world, BlockPos pos) {
      for(Entity var4 : world.getEntities(
         OcelotEntity.class,
         new Box((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 2), (double)(pos.getZ() + 1))
      )) {
         OcelotEntity var5 = (OcelotEntity)var4;
         if (var5.isSitting()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean hasAnalogOutput() {
      return true;
   }

   @Override
   public int getAnalogOutput(World world, BlockPos pos) {
      return InventoryMenu.getAnalogOutput(this.getInventory(world, pos));
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      Direction var2 = Direction.byId(metadata);
      if (var2.getAxis() == Direction.Axis.Y) {
         var2 = Direction.NORTH;
      }

      return this.defaultState().set(FACING, var2);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return ((Direction)state.get(FACING)).getId();
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING);
   }
}
