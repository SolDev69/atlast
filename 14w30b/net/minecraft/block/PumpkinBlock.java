package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockPointer;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockStatePredicate;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.IronGolemEntity;
import net.minecraft.entity.living.mob.SnowGolemEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PumpkinBlock extends HorizontalFacingBlock {
   private BlockPattern snowGolemBodyPattern;
   private BlockPattern snowGolemPattern;
   private BlockPattern ironGolemBodyPattern;
   private BlockPattern ironGolemPattern;

   protected PumpkinBlock() {
      super(Material.PUMPKIN);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH));
      this.setTicksRandomly(true);
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      super.onAdded(world, pos, state);
      this.trySpawnGolem(world, pos);
   }

   public boolean canSpawnGolem(World world, BlockPos pos) {
      return this.getSnowGolemBodyPattern().find(world, pos) != null || this.getIronGolemBodyPattern().find(world, pos) != null;
   }

   private void trySpawnGolem(World world, BlockPos pos) {
      BlockPattern.Match var3;
      if ((var3 = this.getSnowGolemPattern().find(world, pos)) != null) {
         for(int var4 = 0; var4 < this.getSnowGolemPattern().getWidth(); ++var4) {
            BlockPointer var5 = var3.getBlock(0, var4, 0);
            world.setBlockState(var5.getPos(), Blocks.AIR.defaultState(), 2);
         }

         SnowGolemEntity var10 = new SnowGolemEntity(world);
         BlockPos var13 = var3.getBlock(0, 2, 0).getPos();
         var10.refreshPositionAndAngles((double)var13.getX() + 0.5, (double)var13.getY() + 0.05, (double)var13.getZ() + 0.5, 0.0F, 0.0F);
         world.addEntity(var10);

         for(int var6 = 0; var6 < 120; ++var6) {
            world.addParticle(
               ParticleType.SNOW_SHOVEL,
               (double)var13.getX() + world.random.nextDouble(),
               (double)var13.getY() + world.random.nextDouble() * 2.5,
               (double)var13.getZ() + world.random.nextDouble(),
               0.0,
               0.0,
               0.0
            );
         }

         for(int var16 = 0; var16 < this.getSnowGolemPattern().getWidth(); ++var16) {
            BlockPointer var7 = var3.getBlock(0, var16, 0);
            world.onBlockChanged(var7.getPos(), Blocks.AIR);
         }
      } else if ((var3 = this.getIronGolemPattern().find(world, pos)) != null) {
         for(int var11 = 0; var11 < this.getIronGolemPattern().getHeight(); ++var11) {
            for(int var14 = 0; var14 < this.getIronGolemPattern().getWidth(); ++var14) {
               world.setBlockState(var3.getBlock(var11, var14, 0).getPos(), Blocks.AIR.defaultState(), 2);
            }
         }

         BlockPos var12 = var3.getBlock(1, 2, 0).getPos();
         IronGolemEntity var15 = new IronGolemEntity(world);
         var15.setPlayerCreated(true);
         var15.refreshPositionAndAngles((double)var12.getX() + 0.5, (double)var12.getY() + 0.05, (double)var12.getZ() + 0.5, 0.0F, 0.0F);
         world.addEntity(var15);

         for(int var17 = 0; var17 < 120; ++var17) {
            world.addParticle(
               ParticleType.SNOWBALL,
               (double)var12.getX() + world.random.nextDouble(),
               (double)var12.getY() + world.random.nextDouble() * 3.9,
               (double)var12.getZ() + world.random.nextDouble(),
               0.0,
               0.0,
               0.0
            );
         }

         for(int var18 = 0; var18 < this.getIronGolemPattern().getHeight(); ++var18) {
            for(int var19 = 0; var19 < this.getIronGolemPattern().getWidth(); ++var19) {
               BlockPointer var8 = var3.getBlock(var18, var19, 0);
               world.onBlockChanged(var8.getPos(), Blocks.AIR);
            }
         }
      }
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      return world.getBlockState(pos).getBlock().material.isReplaceable() && World.hasSolidTop(world, pos.down());
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      return this.defaultState().set(FACING, entity.getDirection().getOpposite());
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(FACING, Direction.byIdHorizontal(metadata));
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return ((Direction)state.get(FACING)).getIdHorizontal();
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING);
   }

   protected BlockPattern getSnowGolemBodyPattern() {
      if (this.snowGolemBodyPattern == null) {
         this.snowGolemBodyPattern = BlockPatternBuilder.start()
            .aisle(" ", "#", "#")
            .with('#', BlockPointer.hasState(BlockStatePredicate.of(Blocks.SNOW)))
            .build();
      }

      return this.snowGolemBodyPattern;
   }

   protected BlockPattern getSnowGolemPattern() {
      if (this.snowGolemPattern == null) {
         this.snowGolemPattern = BlockPatternBuilder.start()
            .aisle("^", "#", "#")
            .with('^', BlockPointer.hasState(BlockStatePredicate.of(Blocks.PUMPKIN)))
            .with('#', BlockPointer.hasState(BlockStatePredicate.of(Blocks.SNOW)))
            .build();
      }

      return this.snowGolemPattern;
   }

   protected BlockPattern getIronGolemBodyPattern() {
      if (this.ironGolemBodyPattern == null) {
         this.ironGolemBodyPattern = BlockPatternBuilder.start()
            .aisle("~ ~", "###", "~#~")
            .with('#', BlockPointer.hasState(BlockStatePredicate.of(Blocks.IRON_BLOCK)))
            .with('~', BlockPointer.hasState(BlockStatePredicate.of(Blocks.AIR)))
            .build();
      }

      return this.ironGolemBodyPattern;
   }

   protected BlockPattern getIronGolemPattern() {
      if (this.ironGolemPattern == null) {
         this.ironGolemPattern = BlockPatternBuilder.start()
            .aisle("~^~", "###", "~#~")
            .with('^', BlockPointer.hasState(BlockStatePredicate.of(Blocks.PUMPKIN)))
            .with('#', BlockPointer.hasState(BlockStatePredicate.of(Blocks.IRON_BLOCK)))
            .with('~', BlockPointer.hasState(BlockStatePredicate.of(Blocks.AIR)))
            .build();
      }

      return this.ironGolemPattern;
   }
}
