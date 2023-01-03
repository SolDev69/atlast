package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class TorchBlock extends Block {
   public static final DirectionProperty FACING = DirectionProperty.of("facing", new Predicate() {
      public boolean apply(Direction c_69garkogr) {
         return c_69garkogr != Direction.DOWN;
      }
   });

   protected TorchBlock() {
      super(Material.DECORATION);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.UP));
      this.setTicksRandomly(true);
      this.setItemGroup(ItemGroup.DECORATIONS);
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      return null;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   private boolean canSitOnTop(World world, BlockPos pos) {
      if (World.hasSolidTop(world, pos)) {
         return true;
      } else {
         Block var3 = world.getBlockState(pos).getBlock();
         return var3 == Blocks.FENCE
            || var3 == Blocks.NETHER_BRICK_FENCE
            || var3 == Blocks.GLASS
            || var3 == Blocks.COBBLESTONE_WALL
            || var3 == Blocks.STAINED_GLASS;
      }
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      for(Direction var4 : FACING.values()) {
         Direction var5 = var4.getOpposite();
         if (var4.getAxis().isHorizontal() && world.isOpaqueFullCube(pos.offset(var5), true)) {
            return true;
         }

         if (var4.getAxis().isVertical() && this.canSitOnTop(world, pos.offset(var5))) {
            return true;
         }
      }

      return false;
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      if ((!dir.getAxis().isHorizontal() || !world.isOpaqueFullCube(pos.offset(dir.getOpposite()), true))
         && (dir != Direction.UP || !this.canSitOnTop(world, pos.offset(dir.getOpposite())))) {
         if (dir == Direction.DOWN) {
            for(Direction var10 : Direction.Plane.HORIZONTAL) {
               if (world.isOpaqueFullCube(pos.offset(var10.getOpposite()), true)) {
                  return this.defaultState().set(FACING, var10);
               }
            }
         }

         return this.defaultState();
      } else {
         return this.defaultState().set(FACING, dir);
      }
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      this.canSurvive(world, pos, state);
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      this.canSurviveOrBreak(world, pos, state);
   }

   protected boolean canSurviveOrBreak(World world, BlockPos pos, BlockState state) {
      if (!this.canSurvive(world, pos, state)) {
         return true;
      } else {
         Direction var4 = (Direction)state.get(FACING);
         Direction.Axis var5 = var4.getAxis();
         Direction var6 = var4.getOpposite();
         boolean var7 = false;
         if (var5.isHorizontal() && !world.isOpaqueFullCube(pos.offset(var6), true)) {
            var7 = true;
         } else if (var5.isVertical() && !this.canSitOnTop(world, pos.offset(var6))) {
            var7 = true;
         }

         if (var7) {
            this.dropItems(world, pos, state, 0);
            world.removeBlock(pos);
            return true;
         } else {
            return false;
         }
      }
   }

   protected boolean canSurvive(World world, BlockPos pos, BlockState state) {
      if (this.canSurvive(world, pos)) {
         return true;
      } else {
         if (world.getBlockState(pos).getBlock() == this) {
            this.dropItems(world, pos, state, 0);
            world.removeBlock(pos);
         }

         return false;
      }
   }

   @Override
   public HitResult rayTrace(World world, BlockPos pos, Vec3d start, Vec3d end) {
      Direction var5 = (Direction)world.getBlockState(pos).get(FACING);
      float var6 = 0.15F;
      if (var5 == Direction.EAST) {
         this.setShape(0.0F, 0.2F, 0.5F - var6, var6 * 2.0F, 0.8F, 0.5F + var6);
      } else if (var5 == Direction.WEST) {
         this.setShape(1.0F - var6 * 2.0F, 0.2F, 0.5F - var6, 1.0F, 0.8F, 0.5F + var6);
      } else if (var5 == Direction.SOUTH) {
         this.setShape(0.5F - var6, 0.2F, 0.0F, 0.5F + var6, 0.8F, var6 * 2.0F);
      } else if (var5 == Direction.NORTH) {
         this.setShape(0.5F - var6, 0.2F, 1.0F - var6 * 2.0F, 0.5F + var6, 0.8F, 1.0F);
      } else {
         var6 = 0.1F;
         this.setShape(0.5F - var6, 0.0F, 0.5F - var6, 0.5F + var6, 0.6F, 0.5F + var6);
      }

      return super.rayTrace(world, pos, start, end);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random random) {
      Direction var5 = (Direction)state.get(FACING);
      double var6 = (double)((float)pos.getX() + 0.5F);
      double var8 = (double)((float)pos.getY() + 0.7F);
      double var10 = (double)((float)pos.getZ() + 0.5F);
      double var12 = 0.22F;
      double var14 = 0.27F;
      if (var5.getAxis().isHorizontal()) {
         Direction var16 = var5.getOpposite();
         world.addParticle(
            ParticleType.SMOKE_NORMAL, var6 + var14 * (double)var16.getOffsetX(), var8 + var12, var10 + var14 * (double)var16.getOffsetZ(), 0.0, 0.0, 0.0
         );
         world.addParticle(
            ParticleType.FLAME, var6 + var14 * (double)var16.getOffsetX(), var8 + var12, var10 + var14 * (double)var16.getOffsetZ(), 0.0, 0.0, 0.0
         );
      } else {
         world.addParticle(ParticleType.SMOKE_NORMAL, var6, var8, var10, 0.0, 0.0, 0.0);
         world.addParticle(ParticleType.FLAME, var6, var8, var10, 0.0, 0.0, 0.0);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      BlockState var2 = this.defaultState();
      switch(metadata) {
         case 1:
            var2 = var2.set(FACING, Direction.EAST);
            break;
         case 2:
            var2 = var2.set(FACING, Direction.WEST);
            break;
         case 3:
            var2 = var2.set(FACING, Direction.SOUTH);
            break;
         case 4:
            var2 = var2.set(FACING, Direction.NORTH);
            break;
         case 5:
         default:
            var2 = var2.set(FACING, Direction.UP);
      }

      return var2;
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      switch((Direction)state.get(FACING)) {
         case EAST:
            var2 |= 1;
            break;
         case WEST:
            var2 |= 2;
            break;
         case SOUTH:
            var2 |= 3;
            break;
         case NORTH:
            var2 |= 4;
            break;
         case DOWN:
         case UP:
         default:
            var2 |= 5;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING);
   }
}
