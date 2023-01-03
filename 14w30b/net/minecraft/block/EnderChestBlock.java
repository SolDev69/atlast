package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EnderChestBlock extends BlockWithBlockEntity {
   public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.Plane.HORIZONTAL);

   protected EnderChestBlock() {
      super(Material.STONE);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH));
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
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Item.byBlock(Blocks.OBSIDIAN);
   }

   @Override
   public int getBaseDropCount(Random random) {
      return 8;
   }

   @Override
   protected boolean hasSilkTouchDrops() {
      return true;
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      return this.defaultState().set(FACING, entity.getDirection().getOpposite());
   }

   @Override
   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
      world.setBlockState(pos, state.set(FACING, entity.getDirection().getOpposite()), 2);
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      EnderChestInventory var9 = player.getEnderChestInventory();
      BlockEntity var10 = world.getBlockEntity(pos);
      if (var9 == null || !(var10 instanceof EnderChestBlockEntity)) {
         return true;
      } else if (world.getBlockState(pos.up()).getBlock().isConductor()) {
         return true;
      } else if (world.isClient) {
         return true;
      } else {
         var9.setCurrentBlockEntity((EnderChestBlockEntity)var10);
         player.openInventoryMenu(var9);
         return true;
      }
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      return new EnderChestBlockEntity();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random random) {
      for(int var5 = 0; var5 < 3; ++var5) {
         int var6 = random.nextInt(2) * 2 - 1;
         int var7 = random.nextInt(2) * 2 - 1;
         double var8 = (double)pos.getX() + 0.5 + 0.25 * (double)var6;
         double var10 = (double)((float)pos.getY() + random.nextFloat());
         double var12 = (double)pos.getZ() + 0.5 + 0.25 * (double)var7;
         double var14 = (double)(random.nextFloat() * (float)var6);
         double var16 = ((double)random.nextFloat() - 0.5) * 0.125;
         double var18 = (double)(random.nextFloat() * (float)var7);
         world.addParticle(ParticleType.PORTAL, var8, var10, var12, var14, var16, var18);
      }
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
