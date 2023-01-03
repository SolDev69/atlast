package net.minecraft.entity.living.mob.hostile;

import com.google.common.base.Predicate;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.mob.Monster;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public abstract class HostileEntity extends PathAwareEntity implements Monster {
   protected final Goal fleeExplodingCreeperGoal = new FleeEntityGoal(this, new Predicate() {
      public boolean apply(Entity c_47ldwddrb) {
         return c_47ldwddrb instanceof CreeperEntity && ((CreeperEntity)c_47ldwddrb).getFuseSpeed() > 0;
      }
   }, 4.0F, 1.0, 2.0);

   public HostileEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.experiencePoints = 5;
   }

   @Override
   public void tickAI() {
      this.updateHandSwing();
      float var1 = this.getBrightness(1.0F);
      if (var1 > 0.5F) {
         this.despawnTicks += 2;
      }

      super.tickAI();
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.world.isClient && this.world.getDifficulty() == Difficulty.PEACEFUL) {
         this.remove();
      }
   }

   @Override
   protected String getSwimSound() {
      return "game.hostile.swim";
   }

   @Override
   protected String getSplashSound() {
      return "game.hostile.swim.splash";
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else if (super.damage(source, amount)) {
         Entity var3 = source.getAttacker();
         return this.rider != var3 && this.vehicle != var3 ? true : true;
      } else {
         return false;
      }
   }

   @Override
   protected String getHurtSound() {
      return "game.hostile.hurt";
   }

   @Override
   protected String getDeathSound() {
      return "game.hostile.die";
   }

   @Override
   protected String getFallSound(int distance) {
      return distance > 4 ? "game.hostile.hurt.fall.big" : "game.hostile.hurt.fall.small";
   }

   @Override
   public boolean attack(Entity entity) {
      float var2 = (float)this.initializeAttribute(EntityAttributes.ATTACK_DAMAGE).get();
      int var3 = 0;
      if (entity instanceof LivingEntity) {
         var2 += EnchantmentHelper.m_01divsmlb(this, (LivingEntity)entity);
         var3 += EnchantmentHelper.getKnockbackLevel(this, (LivingEntity)entity);
      }

      boolean var4 = entity.damage(DamageSource.mob(this), var2);
      if (var4) {
         if (var3 > 0) {
            entity.addVelocity(
               (double)(-MathHelper.sin(this.yaw * (float) Math.PI / 180.0F) * (float)var3 * 0.5F),
               0.1,
               (double)(MathHelper.cos(this.yaw * (float) Math.PI / 180.0F) * (float)var3 * 0.5F)
            );
            this.velocityX *= 0.6;
            this.velocityZ *= 0.6;
         }

         int var5 = EnchantmentHelper.getFireAspectLevel(this);
         if (var5 > 0) {
            entity.setOnFireFor(var5 * 4);
         }

         if (entity instanceof LivingEntity) {
            EnchantmentHelper.applyProtectionWildcard((LivingEntity)entity, this);
         }

         EnchantmentHelper.applyDamageWildcard(this, entity);
      }

      return var4;
   }

   @Override
   public float getPathfindingFavor(BlockPos x) {
      return 0.5F - this.world.getBrightness(x);
   }

   protected boolean canSpawnAtLightLevel() {
      BlockPos var1 = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
      if (this.world.getLight(LightType.SKY, var1) > this.random.nextInt(32)) {
         return false;
      } else {
         int var2 = this.world.getRawBrightness(var1);
         if (this.world.isThundering()) {
            int var3 = this.world.getAmbientDarkness();
            this.world.setAmbientDarkness(10);
            var2 = this.world.getRawBrightness(var1);
            this.world.setAmbientDarkness(var3);
         }

         return var2 <= this.random.nextInt(8);
      }
   }

   @Override
   public boolean canSpawn() {
      return this.world.getDifficulty() != Difficulty.PEACEFUL && this.canSpawnAtLightLevel() && super.canSpawn();
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributes().registerAttribute(EntityAttributes.ATTACK_DAMAGE);
   }

   @Override
   protected boolean isGrownUp() {
      return true;
   }
}
