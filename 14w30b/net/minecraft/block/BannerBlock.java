package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.block.state.property.IntegerProperty;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BannerBlock extends BlockWithBlockEntity {
   public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.Plane.HORIZONTAL);
   public static final IntegerProperty ROTATION = IntegerProperty.of("rotation", 0, 15);

   protected BannerBlock() {
      super(Material.WOOD);
      float var1 = 0.25F;
      float var2 = 1.0F;
      this.setShape(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, var2, 0.5F + var1);
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      return null;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Box getOutlineShape(World world, BlockPos pos) {
      this.updateShape(world, pos);
      return super.getOutlineShape(world, pos);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean canWalkThrough(IWorld world, BlockPos pos) {
      return true;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      return new BannerBlockEntity();
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Items.BANNER;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Items.BANNER;
   }

   @Override
   public void dropItems(World world, BlockPos pos, BlockState state, float luck, int fortuneLevel) {
      BlockEntity var6 = world.getBlockEntity(pos);
      if (var6 instanceof BannerBlockEntity) {
         ItemStack var7 = new ItemStack(Items.BANNER, 1, ((BannerBlockEntity)var6).getBase());
         NbtCompound var8 = new NbtCompound();
         var6.writeNbt(var8);
         var8.remove("x");
         var8.remove("y");
         var8.remove("z");
         var8.remove("id");
         var7.addToNbt("BlockEntityTag", var8);
         this.dropItems(world, pos, var7);
      } else {
         super.dropItems(world, pos, state, luck, fortuneLevel);
      }
   }

   @Override
   public void afterMinedByPlayer(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
      if (blockEntity instanceof BannerBlockEntity) {
         ItemStack var6 = new ItemStack(Items.BANNER, 1, ((BannerBlockEntity)blockEntity).getBase());
         NbtCompound var7 = new NbtCompound();
         blockEntity.writeNbt(var7);
         var7.remove("x");
         var7.remove("y");
         var7.remove("z");
         var7.remove("id");
         var6.addToNbt("BlockEntityTag", var7);
         this.dropItems(world, pos, var6);
      } else {
         super.afterMinedByPlayer(world, player, pos, state, null);
      }
   }

   public static class Standing extends BannerBlock {
      public Standing() {
         this.setDefaultState(this.stateDefinition.any().set(ROTATION, 0));
      }

      @Override
      public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
         if (!world.getBlockState(pos.down()).getBlock().getMaterial().isSolid()) {
            this.dropItems(world, pos, state, 0);
            world.removeBlock(pos);
         }

         super.update(world, pos, state, neighborBlock);
      }

      @Override
      public BlockState getStateFromMetadata(int metadata) {
         return this.defaultState().set(ROTATION, metadata);
      }

      @Override
      public int getMetadataFromState(BlockState state) {
         return state.get(ROTATION);
      }

      @Override
      protected StateDefinition createStateDefinition() {
         return new StateDefinition(this, ROTATION);
      }
   }

   public static class Wall extends BannerBlock {
      public Wall() {
         this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH));
      }

      @Override
      public void updateShape(IWorld world, BlockPos pos) {
         Direction var3 = (Direction)world.getBlockState(pos).get(FACING);
         float var4 = 0.0F;
         float var5 = 0.78125F;
         float var6 = 0.0F;
         float var7 = 1.0F;
         float var8 = 0.125F;
         this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
         switch(var3) {
            case NORTH:
            default:
               this.setShape(var6, var4, 1.0F - var8, var7, var5, 1.0F);
               break;
            case SOUTH:
               this.setShape(var6, var4, 0.0F, var7, var5, var8);
               break;
            case WEST:
               this.setShape(1.0F - var8, var4, var6, 1.0F, var5, var7);
               break;
            case EAST:
               this.setShape(0.0F, var4, var6, var8, var5, var7);
         }
      }

      @Override
      public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
         Direction var5 = (Direction)state.get(FACING);
         if (!world.getBlockState(pos.offset(var5.getOpposite())).getBlock().getMaterial().isSolid()) {
            this.dropItems(world, pos, state, 0);
            world.removeBlock(pos);
         }

         super.update(world, pos, state, neighborBlock);
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
}
