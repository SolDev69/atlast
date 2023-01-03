package net.minecraft.entity.living.mob;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class FlyingEntity extends MobEntity {
   public FlyingEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   @Override
   public void applyFallDamage(float distance, float g) {
   }

   @Override
   protected void onFall(double dy, boolean landed, Block block, BlockPos pos) {
   }

   @Override
   public void moveEntityWithVelocity(float sidewaysVelocity, float forwardVelocity) {
      if (this.isInWater()) {
         this.updateVelocity(sidewaysVelocity, forwardVelocity, 0.02F);
         this.move(this.velocityX, this.velocityY, this.velocityZ);
         this.velocityX *= 0.8F;
         this.velocityY *= 0.8F;
         this.velocityZ *= 0.8F;
      } else if (this.isInLava()) {
         this.updateVelocity(sidewaysVelocity, forwardVelocity, 0.02F);
         this.move(this.velocityX, this.velocityY, this.velocityZ);
         this.velocityX *= 0.5;
         this.velocityY *= 0.5;
         this.velocityZ *= 0.5;
      } else {
         float var3 = 0.91F;
         if (this.onGround) {
            var3 = this.world
                  .getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.z)))
                  .getBlock()
                  .slipperiness
               * 0.91F;
         }

         float var4 = 0.16277136F / (var3 * var3 * var3);
         this.updateVelocity(sidewaysVelocity, forwardVelocity, this.onGround ? 0.1F * var4 : 0.02F);
         var3 = 0.91F;
         if (this.onGround) {
            var3 = this.world
                  .getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.z)))
                  .getBlock()
                  .slipperiness
               * 0.91F;
         }

         this.move(this.velocityX, this.velocityY, this.velocityZ);
         this.velocityX *= (double)var3;
         this.velocityY *= (double)var3;
         this.velocityZ *= (double)var3;
      }

      this.prevHandSwingAmount = this.handSwingAmount;
      double var9 = this.x - this.prevX;
      double var5 = this.z - this.prevZ;
      float var7 = MathHelper.sqrt(var9 * var9 + var5 * var5) * 4.0F;
      if (var7 > 1.0F) {
         var7 = 1.0F;
      }

      this.handSwingAmount += (var7 - this.handSwingAmount) * 0.4F;
      this.handSwing += this.handSwingAmount;
   }

   @Override
   public boolean isClimbing() {
      return false;
   }
}
