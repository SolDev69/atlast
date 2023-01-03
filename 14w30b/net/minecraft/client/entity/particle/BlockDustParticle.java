package net.minecraft.client.entity.particle;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockDustParticle extends BlockBreakingParticle {
   protected BlockDustParticle(World c_54ruxjwzt, double d, double e, double f, double g, double h, double i, BlockState c_17agfiprw) {
      super(c_54ruxjwzt, d, e, f, g, h, i, c_17agfiprw);
      this.velocityX = g;
      this.velocityY = h;
      this.velocityZ = i;
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         BlockState var16 = Block.deserialize(parameters[0]);
         return var16.getBlock().getRenderType() == -1 ? null : new BlockDustParticle(world, x, y, z, velocityX, velocityY, velocityZ, var16).updateColor();
      }
   }
}
