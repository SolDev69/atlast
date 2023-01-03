package net.minecraft.block;

import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityProvider;
import net.minecraft.block.entity.MovingBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.piston.PistonMoveStructureResolver;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PistonBaseBlock extends Block {
   public static final DirectionProperty FACING = DirectionProperty.of("facing");
   public static final BooleanProperty EXTENDED = BooleanProperty.of("extended");
   private final boolean sticky;

   public PistonBaseBlock(boolean sticky) {
      super(Material.PISTON);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH).set(EXTENDED, false));
      this.sticky = sticky;
      this.setSound(STONE_SOUND);
      this.setStrength(0.5F);
      this.setItemGroup(ItemGroup.REDSTONE);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
      world.setBlockState(pos, state.set(FACING, getFacingForPlacement(world, pos, entity)), 2);
      if (!world.isClient) {
         this.updateExtended(world, pos, state);
      }
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (!world.isClient) {
         this.updateExtended(world, pos, state);
      }
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      if (!world.isClient && world.getBlockEntity(pos) == null) {
         this.updateExtended(world, pos, state);
      }
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      return this.defaultState().set(FACING, getFacingForPlacement(world, pos, entity)).set(EXTENDED, false);
   }

   private void updateExtended(World world, BlockPos pos, BlockState state) {
      Direction var4 = (Direction)state.get(FACING);
      boolean var5 = this.shouldBeExtended(world, pos, var4);
      if (var5 && !state.get(EXTENDED)) {
         if (new PistonMoveStructureResolver(world, pos, var4, true).resolve()) {
            world.addBlockEvent(pos, this, 0, var4.getId());
         }
      } else if (!var5 && state.get(EXTENDED)) {
         world.setBlockState(pos, state.set(EXTENDED, false), 2);
         world.addBlockEvent(pos, this, 1, var4.getId());
      }
   }

   private boolean shouldBeExtended(World world, BlockPos pos, Direction facing) {
      for(Direction var7 : Direction.values()) {
         if (var7 != facing && world.isEmittingPower(pos.offset(var7), var7)) {
            return true;
         }
      }

      if (world.isEmittingPower(pos, Direction.NORTH)) {
         return true;
      } else {
         BlockPos var9 = pos.up();

         for(Direction var8 : Direction.values()) {
            if (var8 != Direction.DOWN && world.isEmittingPower(var9.offset(var8), var8)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public boolean doEvent(World world, BlockPos pos, BlockState state, int type, int data) {
      Direction var6 = (Direction)state.get(FACING);
      if (!world.isClient) {
         boolean var7 = this.shouldBeExtended(world, pos, var6);
         if (var7 && type == 1) {
            world.setBlockState(pos, state.set(EXTENDED, true), 2);
            return false;
         }

         if (!var7 && type == 0) {
            return false;
         }
      }

      if (type == 0) {
         if (!this.move(world, pos, var6, true)) {
            return false;
         }

         world.setBlockState(pos, state.set(EXTENDED, true), 2);
         world.playSound(
            (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "tile.piston.out", 0.5F, world.random.nextFloat() * 0.25F + 0.6F
         );
      } else if (type == 1) {
         BlockEntity var13 = world.getBlockEntity(pos.offset(var6));
         if (var13 instanceof MovingBlockEntity) {
            ((MovingBlockEntity)var13).finish();
         }

         world.setBlockState(
            pos,
            Blocks.MOVING_BLOCK
               .defaultState()
               .set(MovingBlock.FACING, var6)
               .set(MovingBlock.TYPE, this.sticky ? PistonHeadBlock.Type.STICKY : PistonHeadBlock.Type.DEFAULT),
            3
         );
         world.setBlockEntity(pos, MovingBlock.createMovingBlockEntity(this.getStateFromMetadata(data), var6, false, true));
         if (this.sticky) {
            BlockPos var8 = pos.add(var6.getOffsetX() * 2, var6.getOffsetY() * 2, var6.getOffsetZ() * 2);
            Block var9 = world.getBlockState(var8).getBlock();
            boolean var10 = false;
            if (var9 == Blocks.MOVING_BLOCK) {
               BlockEntity var11 = world.getBlockEntity(var8);
               if (var11 instanceof MovingBlockEntity) {
                  MovingBlockEntity var12 = (MovingBlockEntity)var11;
                  if (var12.getFacing() == var6 && var12.isExtending()) {
                     var12.finish();
                     var10 = true;
                  }
               }
            }

            if (!var10
               && var9.getMaterial() != Material.AIR
               && canMoveBlock(var9, world, var8, false)
               && (var9.getPistonMoveBehavior() == 0 || var9 == Blocks.PISTON || var9 == Blocks.STICKY_PISTON)) {
               this.move(world, pos, var6, false);
            }
         } else {
            world.removeBlock(pos.offset(var6));
         }

         world.playSound(
            (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "tile.piston.in", 0.5F, world.random.nextFloat() * 0.15F + 0.6F
         );
      }

      return true;
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      BlockState var3 = world.getBlockState(pos);
      if (var3.getBlock() == this && var3.get(EXTENDED)) {
         float var4 = 0.25F;
         Direction var5 = (Direction)var3.get(FACING);
         if (var5 != null) {
            switch(var5) {
               case DOWN:
                  this.setShape(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
                  break;
               case UP:
                  this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
                  break;
               case NORTH:
                  this.setShape(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
                  break;
               case SOUTH:
                  this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
                  break;
               case WEST:
                  this.setShape(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                  break;
               case EAST:
                  this.setShape(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
            }
         }
      } else {
         this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   @Override
   public void setBlockItemBounds() {
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void getCollisionBoxes(World world, BlockPos pos, BlockState state, Box entityBox, List boxes, Entity entity) {
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      this.updateShape(world, pos);
      return super.getCollisionShape(world, pos, state);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   public static Direction getFacing(int metadata) {
      int var1 = metadata & 7;
      return var1 > 5 ? null : Direction.byId(var1);
   }

   public static Direction getFacingForPlacement(World world, BlockPos pos, LivingEntity entity) {
      if (MathHelper.abs((float)entity.x - (float)pos.getX()) < 2.0F && MathHelper.abs((float)entity.z - (float)pos.getZ()) < 2.0F) {
         double var3 = entity.y + (double)entity.getEyeHeight();
         if (var3 - (double)pos.getY() > 2.0) {
            return Direction.UP;
         }

         if ((double)pos.getY() - var3 > 0.0) {
            return Direction.DOWN;
         }
      }

      return entity.getDirection().getOpposite();
   }

   public static boolean canMoveBlock(Block block, World world, BlockPos pos, boolean allowBreaking) {
      if (block == Blocks.OBSIDIAN) {
         return false;
      } else if (!world.getWorldBorder().contains(pos)) {
         return false;
      } else {
         if (block != Blocks.PISTON && block != Blocks.STICKY_PISTON) {
            if (block.getMiningSpeed(world, pos) == -1.0F) {
               return false;
            }

            if (block.getPistonMoveBehavior() == 2) {
               return false;
            }

            if (block.getPistonMoveBehavior() == 1) {
               if (!allowBreaking) {
                  return false;
               }

               return true;
            }
         } else if (world.getBlockState(pos).get(EXTENDED)) {
            return false;
         }

         return !(block instanceof BlockEntityProvider);
      }
   }

   private boolean move(World world, BlockPos pos, Direction facing, boolean extend) {
      if (!extend) {
         world.removeBlock(pos.offset(facing));
      }

      PistonMoveStructureResolver var5 = new PistonMoveStructureResolver(world, pos, facing, extend);
      List var6 = var5.getToMove();
      List var7 = var5.getToBreak();
      if (!var5.resolve()) {
         return false;
      } else {
         int var8 = var6.size() + var7.size();
         Block[] var9 = new Block[var8];
         Direction var10 = extend ? facing : facing.getOpposite();

         for(int var11 = var7.size() - 1; var11 >= 0; --var11) {
            BlockPos var12 = (BlockPos)var7.get(var11);
            Block var13 = world.getBlockState(var12).getBlock();
            var13.dropItems(world, var12, world.getBlockState(var12), 0);
            world.removeBlock(var12);
            --var8;
            var9[var8] = var13;
         }

         for(int var16 = var6.size() - 1; var16 >= 0; --var16) {
            BlockPos var18 = (BlockPos)var6.get(var16);
            BlockState var23 = world.getBlockState(var18);
            Block var14 = var23.getBlock();
            int var15 = var14.getMetadataFromState(var23);
            world.removeBlock(var18);
            var18 = var18.offset(var10);
            world.setBlockState(var18, Blocks.MOVING_BLOCK.defaultState().set(FACING, facing), 4);
            world.setBlockEntity(var18, MovingBlock.createMovingBlockEntity(var23, facing, extend, false));
            --var8;
            var9[var8] = var14;
         }

         BlockPos var17 = pos.offset(facing);
         if (extend) {
            int var20 = facing.getId() | (this.sticky ? 8 : 0);
            BlockState var24 = Blocks.MOVING_BLOCK
               .defaultState()
               .set(MovingBlock.FACING, facing)
               .set(MovingBlock.TYPE, this.sticky ? PistonHeadBlock.Type.STICKY : PistonHeadBlock.Type.DEFAULT);
            world.setBlockState(var17, var24, 4);
            world.setBlockEntity(var17, MovingBlock.createMovingBlockEntity(Blocks.PISTON_HEAD.getStateFromMetadata(var20), facing, true, false));
         }

         for(int var21 = var7.size() - 1; var21 >= 0; --var21) {
            world.updateNeighbors((BlockPos)var7.get(var21), var9[var8++]);
         }

         for(int var22 = var6.size() - 1; var22 >= 0; --var22) {
            world.updateNeighbors((BlockPos)var6.get(var22), var9[var8++]);
         }

         if (extend) {
            world.updateNeighbors(var17, Blocks.PISTON_HEAD);
            world.updateNeighbors(pos, this);
         }

         return true;
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int m_43rfjsapl(int i) {
      return Direction.UP.getId();
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(FACING, getFacing(metadata)).set(EXTENDED, (metadata & 8) > 0);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((Direction)state.get(FACING)).getId();
      if (state.get(EXTENDED)) {
         var2 |= 8;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING, EXTENDED);
   }
}
