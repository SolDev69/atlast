package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.IntegerProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class CakeBlock extends Block {
   public static final IntegerProperty BITES = IntegerProperty.of("bites", 0, 6);

   protected CakeBlock() {
      super(Material.CAKE);
      this.setDefaultState(this.stateDefinition.any().set(BITES, 0));
      this.setTicksRandomly(true);
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      float var3 = 0.0625F;
      float var4 = (float)(1 + world.getBlockState(pos).get(BITES) * 2) / 16.0F;
      float var5 = 0.5F;
      this.setShape(var4, 0.0F, var3, 1.0F - var3, var5, 1.0F - var3);
   }

   @Override
   public void setBlockItemBounds() {
      float var1 = 0.0625F;
      float var2 = 0.5F;
      this.setShape(var1, 0.0F, var1, 1.0F - var1, var2, 1.0F - var1);
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      float var4 = 0.0625F;
      float var5 = (float)(1 + state.get(BITES) * 2) / 16.0F;
      float var6 = 0.5F;
      return new Box(
         (double)((float)pos.getX() + var5),
         (double)pos.getY(),
         (double)((float)pos.getZ() + var4),
         (double)((float)(pos.getX() + 1) - var4),
         (double)((float)pos.getY() + var6),
         (double)((float)(pos.getZ() + 1) - var4)
      );
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Box getOutlineShape(World world, BlockPos pos) {
      return this.getCollisionShape(world, pos, world.getBlockState(pos));
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
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      this.tryEatCake(world, pos, state, player);
      return true;
   }

   @Override
   public void startMining(World world, BlockPos pos, PlayerEntity player) {
      this.tryEatCake(world, pos, world.getBlockState(pos), player);
   }

   private void tryEatCake(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      if (player.canEat(false)) {
         player.getHungerManager().add(2, 0.1F);
         int var5 = state.get(BITES);
         if (var5 < 6) {
            world.setBlockState(pos, state.set(BITES, var5 + 1), 3);
         } else {
            world.removeBlock(pos);
         }
      }
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      return super.canSurvive(world, pos) ? this.isSupported(world, pos) : false;
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (!this.isSupported(world, pos)) {
         world.removeBlock(pos);
      }
   }

   private boolean isSupported(World world, BlockPos pos) {
      return world.getBlockState(pos.down()).getBlock().getMaterial().isSolid();
   }

   @Override
   public int getBaseDropCount(Random random) {
      return 0;
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return null;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Items.CAKE;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(BITES, metadata);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return state.get(BITES);
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, BITES);
   }

   @Override
   public int getAnalogOutput(World world, BlockPos pos) {
      return (7 - world.getBlockState(pos).get(BITES)) * 2;
   }

   @Override
   public boolean hasAnalogOutput() {
      return true;
   }
}
