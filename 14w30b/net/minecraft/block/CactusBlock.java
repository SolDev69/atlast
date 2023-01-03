package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.IntegerProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class CactusBlock extends Block {
   public static final IntegerProperty AGE = IntegerProperty.of("age", 0, 15);

   protected CactusBlock() {
      super(Material.CACTUS);
      this.setDefaultState(this.stateDefinition.any().set(AGE, 0));
      this.setTicksRandomly(true);
      this.setItemGroup(ItemGroup.DECORATIONS);
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      BlockPos var5 = pos.up();
      if (world.isAir(var5)) {
         int var6 = 1;

         while(world.getBlockState(pos.down(var6)).getBlock() == this) {
            ++var6;
         }

         if (var6 < 3) {
            int var7 = state.get(AGE);
            if (var7 == 15) {
               world.setBlockState(var5, this.defaultState());
               BlockState var8 = state.set(AGE, 0);
               world.setBlockState(pos, var8, 4);
               this.update(world, var5, var8, this);
            } else {
               world.setBlockState(pos, state.set(AGE, var7 + 1), 4);
            }
         }
      }
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      float var4 = 0.0625F;
      return new Box(
         (double)((float)pos.getX() + var4),
         (double)pos.getY(),
         (double)((float)pos.getZ() + var4),
         (double)((float)(pos.getX() + 1) - var4),
         (double)((float)(pos.getY() + 1) - var4),
         (double)((float)(pos.getZ() + 1) - var4)
      );
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Box getOutlineShape(World world, BlockPos pos) {
      float var3 = 0.0625F;
      return new Box(
         (double)((float)pos.getX() + var3),
         (double)pos.getY(),
         (double)((float)pos.getZ() + var3),
         (double)((float)(pos.getX() + 1) - var3),
         (double)(pos.getY() + 1),
         (double)((float)(pos.getZ() + 1) - var3)
      );
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
   public boolean canSurvive(World world, BlockPos pos) {
      return super.canSurvive(world, pos) ? this.isSupported(world, pos) : false;
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (!this.isSupported(world, pos)) {
         world.breakBlock(pos, true);
      }
   }

   public boolean isSupported(World world, BlockPos pos) {
      for(Direction var4 : Direction.Plane.HORIZONTAL) {
         if (world.getBlockState(pos.offset(var4)).getBlock().getMaterial().isSolid()) {
            return false;
         }
      }

      Block var5 = world.getBlockState(pos.down()).getBlock();
      return var5 == Blocks.CACTUS || var5 == Blocks.SAND;
   }

   @Override
   public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
      entity.damage(DamageSource.CACTUS, 1.0F);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(AGE, metadata);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return state.get(AGE);
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, AGE);
   }
}
