package net.minecraft.entity.ai.goal;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.RangedAttackMob;
import net.minecraft.util.math.MathHelper;

public class ProjectileAttackGoal extends Goal {
   private final MobEntity entity;
   private final RangedAttackMob rangedAttackMob;
   private LivingEntity target;
   private int updateCountdownTicks = -1;
   private double mobSpeed;
   private int seenTargetTicks;
   private int minCooldown;
   private int maxCooldown;
   private float attackRange;
   private float squaredAttackRange;

   public ProjectileAttackGoal(RangedAttackMob mob, double mobSpeed, int cooldown, float f) {
      this(mob, mobSpeed, cooldown, cooldown, f);
   }

   public ProjectileAttackGoal(RangedAttackMob mob, double mobSpeed, int minCooldown, int maxCooldown, float f) {
      if (!(mob instanceof LivingEntity)) {
         throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
      } else {
         this.rangedAttackMob = mob;
         this.entity = (MobEntity)mob;
         this.mobSpeed = mobSpeed;
         this.minCooldown = minCooldown;
         this.maxCooldown = maxCooldown;
         this.attackRange = f;
         this.squaredAttackRange = f * f;
         this.setControls(3);
      }
   }

   @Override
   public boolean canStart() {
      LivingEntity var1 = this.entity.getTargetEntity();
      if (var1 == null) {
         return false;
      } else {
         this.target = var1;
         return true;
      }
   }

   @Override
   public boolean shouldContinue() {
      return this.canStart() || !this.entity.getNavigation().isIdle();
   }

   @Override
   public void stop() {
      this.target = null;
      this.seenTargetTicks = 0;
      this.updateCountdownTicks = -1;
   }

   @Override
   public void tick() {
      double var1 = this.entity.getSquaredDistanceTo(this.target.x, this.target.getBoundingBox().minY, this.target.z);
      boolean var3 = this.entity.getMobVisibilityCache().canSee(this.target);
      if (var3) {
         ++this.seenTargetTicks;
      } else {
         this.seenTargetTicks = 0;
      }

      if (!(var1 > (double)this.squaredAttackRange) && this.seenTargetTicks >= 20) {
         this.entity.getNavigation().stopCurrentNavigation();
      } else {
         this.entity.getNavigation().startMovingTo(this.target, this.mobSpeed);
      }

      this.entity.getLookControl().setLookatValues(this.target, 30.0F, 30.0F);
      if (--this.updateCountdownTicks == 0) {
         if (var1 > (double)this.squaredAttackRange || !var3) {
            return;
         }

         float var4 = MathHelper.sqrt(var1) / this.attackRange;
         float var5 = MathHelper.clamp(var4, 0.1F, 1.0F);
         this.rangedAttackMob.doRangedAttack(this.target, var5);
         this.updateCountdownTicks = MathHelper.floor(var4 * (float)(this.maxCooldown - this.minCooldown) + (float)this.minCooldown);
      } else if (this.updateCountdownTicks < 0) {
         float var6 = MathHelper.sqrt(var1) / this.attackRange;
         this.updateCountdownTicks = MathHelper.floor(var6 * (float)(this.maxCooldown - this.minCooldown) + (float)this.minCooldown);
      }
   }
}
