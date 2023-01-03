package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MeleeAttackGoal extends Goal {
   World world;
   protected PathAwareEntity entity;
   int attackCooldown;
   double speed;
   boolean pauseWhenMobIdle;
   Path path;
   Class targetType;
   private int updateCountdownTicks;
   private double targetX;
   private double targetY;
   private double targetZ;

   public MeleeAttackGoal(PathAwareEntity entity, Class targetType, double speed, boolean bl) {
      this(entity, speed, bl);
      this.targetType = targetType;
   }

   public MeleeAttackGoal(PathAwareEntity entity, double speed, boolean bl) {
      this.entity = entity;
      this.world = entity.world;
      this.speed = speed;
      this.pauseWhenMobIdle = bl;
      this.setControls(3);
   }

   @Override
   public boolean canStart() {
      LivingEntity var1 = this.entity.getTargetEntity();
      if (var1 == null) {
         return false;
      } else if (!var1.isAlive()) {
         return false;
      } else if (this.targetType != null && !this.targetType.isAssignableFrom(var1.getClass())) {
         return false;
      } else {
         this.path = this.entity.getNavigation().getNavigation(var1);
         return this.path != null;
      }
   }

   @Override
   public boolean shouldContinue() {
      LivingEntity var1 = this.entity.getTargetEntity();
      if (var1 == null) {
         return false;
      } else if (!var1.isAlive()) {
         return false;
      } else if (!this.pauseWhenMobIdle) {
         return !this.entity.getNavigation().isIdle();
      } else {
         return this.entity.isPosInVillage(new BlockPos(var1));
      }
   }

   @Override
   public void start() {
      this.entity.getNavigation().startMovingAlong(this.path, this.speed);
      this.updateCountdownTicks = 0;
   }

   @Override
   public void stop() {
      this.entity.getNavigation().stopCurrentNavigation();
   }

   @Override
   public void tick() {
      LivingEntity var1 = this.entity.getTargetEntity();
      this.entity.getLookControl().setLookatValues(var1, 30.0F, 30.0F);
      double var2 = this.entity.getSquaredDistanceTo(var1.x, var1.getBoundingBox().minY, var1.z);
      double var4 = this.m_29wetoiav(var1);
      --this.updateCountdownTicks;
      if ((this.pauseWhenMobIdle || this.entity.getMobVisibilityCache().canSee(var1))
         && this.updateCountdownTicks <= 0
         && (
            this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0
               || var1.getSquaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0
               || this.entity.getRandom().nextFloat() < 0.05F
         )) {
         this.targetX = var1.x;
         this.targetY = var1.getBoundingBox().minY;
         this.targetZ = var1.z;
         this.updateCountdownTicks = 4 + this.entity.getRandom().nextInt(7);
         if (var2 > 1024.0) {
            this.updateCountdownTicks += 10;
         } else if (var2 > 256.0) {
            this.updateCountdownTicks += 5;
         }

         if (!this.entity.getNavigation().startMovingTo(var1, this.speed)) {
            this.updateCountdownTicks += 15;
         }
      }

      this.attackCooldown = Math.max(this.attackCooldown - 1, 0);
      if (var2 <= var4 && this.attackCooldown <= 0) {
         this.attackCooldown = 20;
         if (this.entity.getStackInHand() != null) {
            this.entity.swingHand();
         }

         this.entity.attack(var1);
      }
   }

   protected double m_29wetoiav(LivingEntity c_97zulxhng) {
      return (double)(this.entity.width * 2.0F * this.entity.width * 2.0F + c_97zulxhng.width);
   }
}
