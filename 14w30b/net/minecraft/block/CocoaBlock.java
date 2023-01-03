package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.IntegerProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class CocoaBlock extends HorizontalFacingBlock implements Fertilizable {
   public static final IntegerProperty AGE = IntegerProperty.of("age", 0, 2);

   public CocoaBlock() {
      super(Material.PLANT);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH).set(AGE, 0));
      this.setTicksRandomly(true);
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (!this.isSupported(world, pos, state)) {
         this.breakCocao(world, pos, state);
      } else if (world.random.nextInt(5) == 0) {
         int var5 = state.get(AGE);
         if (var5 < 2) {
            world.setBlockState(pos, state.set(AGE, var5 + 1), 2);
         }
      }
   }

   public boolean isSupported(World world, BlockPos pos, BlockState state) {
      pos = pos.offset((Direction)state.get(FACING));
      BlockState var4 = world.getBlockState(pos);
      return var4.getBlock() == Blocks.LOG && var4.get(PlanksBlock.VARIANT) == PlanksBlock.Variant.JUNGLE;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      this.updateShape(world, pos);
      return super.getCollisionShape(world, pos, state);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Box getOutlineShape(World world, BlockPos pos) {
      this.updateShape(world, pos);
      return super.getOutlineShape(world, pos);
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      BlockState var3 = world.getBlockState(pos);
      Direction var4 = (Direction)var3.get(FACING);
      int var5 = var3.get(AGE);
      int var6 = 4 + var5 * 2;
      int var7 = 5 + var5 * 2;
      float var8 = (float)var6 / 2.0F;
      switch(var4) {
         case SOUTH:
            this.setShape((8.0F - var8) / 16.0F, (12.0F - (float)var7) / 16.0F, (15.0F - (float)var6) / 16.0F, (8.0F + var8) / 16.0F, 0.75F, 0.9375F);
            break;
         case NORTH:
            this.setShape((8.0F - var8) / 16.0F, (12.0F - (float)var7) / 16.0F, 0.0625F, (8.0F + var8) / 16.0F, 0.75F, (1.0F + (float)var6) / 16.0F);
            break;
         case WEST:
            this.setShape(0.0625F, (12.0F - (float)var7) / 16.0F, (8.0F - var8) / 16.0F, (1.0F + (float)var6) / 16.0F, 0.75F, (8.0F + var8) / 16.0F);
            break;
         case EAST:
            this.setShape((15.0F - (float)var6) / 16.0F, (12.0F - (float)var7) / 16.0F, (8.0F - var8) / 16.0F, 0.9375F, 0.75F, (8.0F + var8) / 16.0F);
      }
   }

   @Override
   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
      Direction var6 = Direction.byRotation((double)entity.yaw);
      world.setBlockState(pos, state.set(FACING, var6), 2);
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      if (!dir.getAxis().isHorizontal()) {
         dir = Direction.NORTH;
      }

      return this.defaultState().set(FACING, dir.getOpposite()).set(AGE, 0);
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (!this.isSupported(world, pos, state)) {
         this.breakCocao(world, pos, state);
      }
   }

   private void breakCocao(World world, BlockPos pos, BlockState state) {
      world.setBlockState(pos, Blocks.AIR.defaultState(), 3);
      this.dropItems(world, pos, state, 0);
   }

   @Override
   public void dropItems(World world, BlockPos pos, BlockState state, float luck, int fortuneLevel) {
      int var6 = state.get(AGE);
      byte var7 = 1;
      if (var6 >= 2) {
         var7 = 3;
      }

      for(int var8 = 0; var8 < var7; ++var8) {
         this.dropItems(world, pos, new ItemStack(Items.DYE, 1, DyeColor.BROWN.getMetadata()));
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Items.DYE;
   }

   @Override
   public int getPickItemMetadata(World world, BlockPos pos) {
      return DyeColor.BROWN.getMetadata();
   }

   @Override
   public boolean canGrow(World world, BlockPos pos, BlockState state, boolean bl) {
      return state.get(AGE) < 2;
   }

   @Override
   public boolean canBeFertilized(World world, Random rand, BlockPos pos, BlockState state) {
      return true;
   }

   @Override
   public void grow(World world, Random rand, BlockPos pos, BlockState state) {
      world.setBlockState(pos, state.set(AGE, state.get(AGE) + 1), 2);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(FACING, Direction.byIdHorizontal(metadata)).set(AGE, (metadata & 15) >> 2);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((Direction)state.get(FACING)).getIdHorizontal();
      return var2 | state.get(AGE) << 2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING, AGE);
   }
}
