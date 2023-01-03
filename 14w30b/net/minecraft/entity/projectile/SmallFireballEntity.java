package net.minecraft.entity.projectile;

import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SmallFireballEntity extends ProjectileEntity {
   public SmallFireballEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.3125F, 0.3125F);
   }

   public SmallFireballEntity(World c_54ruxjwzt, LivingEntity c_97zulxhng, double d, double e, double f) {
      super(c_54ruxjwzt, c_97zulxhng, d, e, f);
      this.setDimensions(0.3125F, 0.3125F);
   }

   public SmallFireballEntity(World c_54ruxjwzt, double d, double e, double f, double g, double h, double i) {
      super(c_54ruxjwzt, d, e, f, g, h, i);
      this.setDimensions(0.3125F, 0.3125F);
   }

   @Override
   protected void onHit(HitResult hitResult) {
      if (!this.world.isClient) {
         if (hitResult.entity != null) {
            if (!hitResult.entity.isImmuneToFire() && hitResult.entity.damage(DamageSource.fire(this, this.shooter), 5.0F)) {
               hitResult.entity.setOnFireFor(5);
            }
         } else {
            BlockPos var2 = hitResult.getBlockPos().offset(hitResult.face);
            if (this.world.isAir(var2)) {
               this.world.setBlockState(var2, Blocks.FIRE.defaultState());
            }
         }

         this.remove();
      }
   }

   @Override
   public boolean hasCollision() {
      return false;
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      return false;
   }
}
