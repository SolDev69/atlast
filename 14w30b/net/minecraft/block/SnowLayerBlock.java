package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.IntegerProperty;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SnowLayerBlock extends Block {
   public static final IntegerProperty LAYERS = IntegerProperty.of("layers", 1, 8);

   protected SnowLayerBlock() {
      super(Material.SNOW_LAYER);
      this.setDefaultState(this.stateDefinition.any().set(LAYERS, 1));
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
      this.setTicksRandomly(true);
      this.setItemGroup(ItemGroup.DECORATIONS);
      this.setBlockItemBounds();
   }

   @Override
   public boolean canWalkThrough(IWorld world, BlockPos pos) {
      return world.getBlockState(pos).get(LAYERS) >= 5;
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      int var4 = state.get(LAYERS) - 1;
      float var5 = 0.125F;
      return new Box(
         (double)pos.getX() + this.minX,
         (double)pos.getY() + this.minY,
         (double)pos.getZ() + this.minZ,
         (double)pos.getX() + this.maxX,
         (double)((float)pos.getY() + (float)var4 * var5),
         (double)pos.getZ() + this.maxZ
      );
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
   public void setBlockItemBounds() {
      this.setLayers(0);
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      BlockState var3 = world.getBlockState(pos);
      this.setLayers(var3.get(LAYERS));
   }

   protected void setLayers(int layer) {
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, (float)layer / 8.0F, 1.0F);
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      BlockState var3 = world.getBlockState(pos.down());
      Block var4 = var3.getBlock();
      if (var4 != Blocks.ICE && var4 != Blocks.PACKED_ICE) {
         if (var4.getMaterial() == Material.LEAVES) {
            return true;
         } else if (var4 == this && var3.get(LAYERS) == 7) {
            return true;
         } else {
            return var4.isOpaqueCube() && var4.material.blocksMovement();
         }
      } else {
         return false;
      }
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      this.canSurviveOrBreak(world, pos, state);
   }

   private boolean canSurviveOrBreak(World world, BlockPos pos, BlockState state) {
      if (!this.canSurvive(world, pos)) {
         this.dropItems(world, pos, state, 0);
         world.removeBlock(pos);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public void afterMinedByPlayer(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
      this.dropItems(world, pos, new ItemStack(Items.SNOWBALL, state.get(LAYERS) + 1, 0));
      world.removeBlock(pos);
      player.incrementStat(Stats.BLOCKS_MINED[Block.getRawId(this)]);
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Items.SNOWBALL;
   }

   @Override
   public int getBaseDropCount(Random random) {
      return 0;
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (world.getLight(LightType.BLOCK, pos) > 11) {
         this.dropItems(world, pos, world.getBlockState(pos), 0);
         world.removeBlock(pos);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldRenderFace(IWorld world, BlockPos pos, Direction face) {
      return face == Direction.UP ? true : super.shouldRenderFace(world, pos, face);
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(LAYERS, (metadata & 7) + 1);
   }

   @Override
   public boolean canBeReplaced(World world, BlockPos pos) {
      return world.getBlockState(pos).get(LAYERS) == 1;
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return state.get(LAYERS) - 1;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, LAYERS);
   }
}
