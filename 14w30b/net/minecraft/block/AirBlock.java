package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class AirBlock extends Block {
   protected AirBlock() {
      super(Material.AIR);
   }

   @Override
   public int getRenderType() {
      return -1;
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
   public boolean hasCollision(BlockState state, boolean allowFluids) {
      return false;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random random) {
      if (world.dimension.doesWaterVaporize() && random.nextInt(8) > pos.getY()) {
         world.addParticle(
            ParticleType.SUSPENDED_DEPTH,
            (double)((float)pos.getX() + random.nextFloat()),
            (double)((float)pos.getY() + random.nextFloat()),
            (double)((float)pos.getZ() + random.nextFloat()),
            0.0,
            0.0,
            0.0
         );
      }
   }

   @Override
   public void dropItems(World world, BlockPos pos, BlockState state, float luck, int fortuneLevel) {
   }
}
