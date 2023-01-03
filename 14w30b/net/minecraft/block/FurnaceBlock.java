package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.inventory.InventoryUtils;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class FurnaceBlock extends BlockWithBlockEntity {
   public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.Plane.HORIZONTAL);
   private final boolean lit;
   private static boolean ignoreBlockRemoval;

   protected FurnaceBlock(boolean lit) {
      super(Material.STONE);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH));
      this.lit = lit;
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Item.byBlock(Blocks.FURNACE);
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      this.updateFacing(world, pos, state);
   }

   private void updateFacing(World world, BlockPos pos, BlockState state) {
      if (!world.isClient) {
         Block var4 = world.getBlockState(pos.north()).getBlock();
         Block var5 = world.getBlockState(pos.south()).getBlock();
         Block var6 = world.getBlockState(pos.west()).getBlock();
         Block var7 = world.getBlockState(pos.east()).getBlock();
         Direction var8 = (Direction)state.get(FACING);
         if (var8 == Direction.NORTH && var4.isOpaque() && !var5.isOpaque()) {
            var8 = Direction.SOUTH;
         } else if (var8 == Direction.SOUTH && var5.isOpaque() && !var4.isOpaque()) {
            var8 = Direction.NORTH;
         } else if (var8 == Direction.WEST && var6.isOpaque() && !var7.isOpaque()) {
            var8 = Direction.EAST;
         } else if (var8 == Direction.EAST && var7.isOpaque() && !var6.isOpaque()) {
            var8 = Direction.WEST;
         }

         world.setBlockState(pos, state.set(FACING, var8), 2);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random random) {
      if (this.lit) {
         Direction var5 = (Direction)state.get(FACING);
         float var6 = (float)pos.getX() + 0.5F;
         float var7 = (float)pos.getY() + 0.0F + random.nextFloat() * 6.0F / 16.0F;
         float var8 = (float)pos.getZ() + 0.5F;
         float var9 = 0.52F;
         float var10 = random.nextFloat() * 0.6F - 0.3F;
         switch(var5) {
            case WEST:
               world.addParticle(ParticleType.SMOKE_NORMAL, (double)(var6 - var9), (double)var7, (double)(var8 + var10), 0.0, 0.0, 0.0);
               world.addParticle(ParticleType.FLAME, (double)(var6 - var9), (double)var7, (double)(var8 + var10), 0.0, 0.0, 0.0);
               break;
            case EAST:
               world.addParticle(ParticleType.SMOKE_NORMAL, (double)(var6 + var9), (double)var7, (double)(var8 + var10), 0.0, 0.0, 0.0);
               world.addParticle(ParticleType.FLAME, (double)(var6 + var9), (double)var7, (double)(var8 + var10), 0.0, 0.0, 0.0);
               break;
            case NORTH:
               world.addParticle(ParticleType.SMOKE_NORMAL, (double)(var6 + var10), (double)var7, (double)(var8 - var9), 0.0, 0.0, 0.0);
               world.addParticle(ParticleType.FLAME, (double)(var6 + var10), (double)var7, (double)(var8 - var9), 0.0, 0.0, 0.0);
               break;
            case SOUTH:
               world.addParticle(ParticleType.SMOKE_NORMAL, (double)(var6 + var10), (double)var7, (double)(var8 + var9), 0.0, 0.0, 0.0);
               world.addParticle(ParticleType.FLAME, (double)(var6 + var10), (double)var7, (double)(var8 + var9), 0.0, 0.0, 0.0);
         }
      }
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (world.isClient) {
         return true;
      } else {
         BlockEntity var9 = world.getBlockEntity(pos);
         if (var9 instanceof FurnaceBlockEntity) {
            player.openInventoryMenu((FurnaceBlockEntity)var9);
         }

         return true;
      }
   }

   public static void updateLitState(boolean lit, World world, BlockPos x) {
      BlockState var3 = world.getBlockState(x);
      BlockEntity var4 = world.getBlockEntity(x);
      ignoreBlockRemoval = true;
      if (lit) {
         world.setBlockState(x, Blocks.LIT_FURNACE.defaultState().set(FACING, var3.get(FACING)), 3);
         world.setBlockState(x, Blocks.LIT_FURNACE.defaultState().set(FACING, var3.get(FACING)), 3);
      } else {
         world.setBlockState(x, Blocks.FURNACE.defaultState().set(FACING, var3.get(FACING)), 3);
         world.setBlockState(x, Blocks.FURNACE.defaultState().set(FACING, var3.get(FACING)), 3);
      }

      ignoreBlockRemoval = false;
      if (var4 != null) {
         var4.cancelRemoval();
         world.setBlockEntity(x, var4);
      }
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      return new FurnaceBlockEntity();
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      return this.defaultState().set(FACING, entity.getDirection().getOpposite());
   }

   @Override
   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
      world.setBlockState(pos, state.set(FACING, entity.getDirection().getOpposite()), 2);
      if (stack.hasCustomHoverName()) {
         BlockEntity var6 = world.getBlockEntity(pos);
         if (var6 instanceof FurnaceBlockEntity) {
            ((FurnaceBlockEntity)var6).setCustomName(stack.getHoverName());
         }
      }
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      if (!ignoreBlockRemoval) {
         BlockEntity var4 = world.getBlockEntity(pos);
         if (var4 instanceof FurnaceBlockEntity) {
            InventoryUtils.dropContents(world, pos, (FurnaceBlockEntity)var4);
            world.updateComparators(pos, this);
         }
      }

      super.onRemoved(world, pos, state);
   }

   @Override
   public boolean hasAnalogOutput() {
      return true;
   }

   @Override
   public int getAnalogOutput(World world, BlockPos pos) {
      return InventoryMenu.getAnalogOutput(world.getBlockEntity(pos));
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Item.byBlock(Blocks.FURNACE);
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
