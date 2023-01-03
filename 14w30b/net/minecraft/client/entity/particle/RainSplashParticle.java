package net.minecraft.client.entity.particle;

import net.minecraft.block.Block;
import net.minecraft.block.LiquidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class RainSplashParticle extends Particle {
   protected RainSplashParticle(World c_54ruxjwzt, double d, double e, double f) {
      super(c_54ruxjwzt, d, e, f, 0.0, 0.0, 0.0);
      this.velocityX *= 0.3F;
      this.velocityY = Math.random() * 0.2F + 0.1F;
      this.velocityZ *= 0.3F;
      this.red = 1.0F;
      this.green = 1.0F;
      this.blue = 1.0F;
      this.setMiscTexture(19 + this.random.nextInt(4));
      this.setDimensions(0.01F, 0.01F);
      this.gravity = 0.06F;
      this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
   }

   @Override
   public void tick() {
      this.prevX = this.x;
      this.prevY = this.y;
      this.prevZ = this.z;
      this.velocityY -= (double)this.gravity;
      this.move(this.velocityX, this.velocityY, this.velocityZ);
      this.velocityX *= 0.98F;
      this.velocityY *= 0.98F;
      this.velocityZ *= 0.98F;
      if (this.maxAge-- <= 0) {
         this.remove();
      }

      if (this.onGround) {
         if (Math.random() < 0.5) {
            this.remove();
         }

         this.velocityX *= 0.7F;
         this.velocityZ *= 0.7F;
      }

      BlockPos var1 = new BlockPos(this);
      BlockState var2 = this.world.getBlockState(var1);
      Block var3 = var2.getBlock();
      var3.updateShape(this.world, var1);
      Material var4 = var2.getBlock().getMaterial();
      if (var4.isLiquid() || var4.isSolid()) {
         double var5 = 0.0;
         if (var2.getBlock() instanceof LiquidBlock) {
            var5 = (double)(1.0F - LiquidBlock.getHeightLoss(var2.get(LiquidBlock.LEVEL)));
         } else {
            var5 = var3.getMaxY();
         }

         double var7 = (double)MathHelper.floor(this.y) + var5;
         if (this.y < var7) {
            this.remove();
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         return new RainSplashParticle(world, x, y, z);
      }
   }
}
