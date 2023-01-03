package net.minecraft.entity.projectile;

import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class WitherSkullEntity extends ProjectileEntity {
   public WitherSkullEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.3125F, 0.3125F);
   }

   public WitherSkullEntity(World c_54ruxjwzt, LivingEntity c_97zulxhng, double d, double e, double f) {
      super(c_54ruxjwzt, c_97zulxhng, d, e, f);
      this.setDimensions(0.3125F, 0.3125F);
   }

   @Override
   protected float getDrag() {
      return this.isCharged() ? 0.73F : super.getDrag();
   }

   @Environment(EnvType.CLIENT)
   public WitherSkullEntity(World c_54ruxjwzt, double d, double e, double f, double g, double h, double i) {
      super(c_54ruxjwzt, d, e, f, g, h, i);
      this.setDimensions(0.3125F, 0.3125F);
   }

   @Override
   public boolean isOnFire() {
      return false;
   }

   @Override
   public float getBlastResistance(Explosion explosion, World world, BlockPos x, BlockState y) {
      float var5 = super.getBlastResistance(explosion, world, x, y);
      if (this.isCharged()
         && y.getBlock() != Blocks.BEDROCK
         && y.getBlock() != Blocks.END_PORTAL
         && y.getBlock() != Blocks.END_PORTAL_FRAME
         && y.getBlock() != Blocks.COMMAND_BLOCK) {
         var5 = Math.min(0.8F, var5);
      }

      return var5;
   }

   @Override
   protected void onHit(HitResult hitResult) {
      if (!this.world.isClient) {
         if (hitResult.entity != null) {
            if (this.shooter != null) {
               if (hitResult.entity.damage(DamageSource.mob(this.shooter), 8.0F) && !hitResult.entity.isAlive()) {
                  this.shooter.heal(5.0F);
               }
            } else {
               hitResult.entity.damage(DamageSource.MAGIC, 5.0F);
            }

            if (hitResult.entity instanceof LivingEntity) {
               byte var2 = 0;
               if (this.world.getDifficulty() == Difficulty.NORMAL) {
                  var2 = 10;
               } else if (this.world.getDifficulty() == Difficulty.HARD) {
                  var2 = 40;
               }

               if (var2 > 0) {
                  ((LivingEntity)hitResult.entity).addStatusEffect(new StatusEffectInstance(StatusEffect.WITHER.id, 20 * var2, 1));
               }
            }
         }

         this.world.explode(this, this.x, this.y, this.z, 1.0F, false, this.world.getGameRules().getBoolean("mobGriefing"));
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

   @Override
   protected void initDataTracker() {
      this.dataTracker.put(10, (byte)0);
   }

   public boolean isCharged() {
      return this.dataTracker.getByte(10) == 1;
   }

   public void setCharged(boolean charged) {
      this.dataTracker.update(10, Byte.valueOf((byte)(charged ? 1 : 0)));
   }
}
