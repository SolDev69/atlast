package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.attribute.IEntityAttributeInstance;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.entity.living.mob.passive.animal.tamable.Tameable;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;

public abstract class TrackTargetGoal extends Goal {
   protected PathAwareEntity entity;
   protected boolean checkVisibility;
   private boolean checkCanNavigate;
   private int canNavigateFlag;
   private int checkCanNavigateCooldown;
   private int timeWithoutVisibility;

   public TrackTargetGoal(PathAwareEntity c_60guwxsid, boolean bl) {
      this(c_60guwxsid, bl, false);
   }

   public TrackTargetGoal(PathAwareEntity entity, boolean checkVisibility, boolean checkCanNavigate) {
      this.entity = entity;
      this.checkVisibility = checkVisibility;
      this.checkCanNavigate = checkCanNavigate;
   }

   @Override
   public boolean shouldContinue() {
      LivingEntity var1 = this.entity.getTargetEntity();
      if (var1 == null) {
         return false;
      } else if (!var1.isAlive()) {
         return false;
      } else {
         double var2 = this.getFollowRange();
         if (this.entity.getSquaredDistanceTo(var1) > var2 * var2) {
            return false;
         } else {
            if (this.checkVisibility) {
               if (this.entity.getMobVisibilityCache().canSee(var1)) {
                  this.timeWithoutVisibility = 0;
               } else if (++this.timeWithoutVisibility > 60) {
                  return false;
               }
            }

            return !(var1 instanceof PlayerEntity) || !((PlayerEntity)var1).abilities.invulnerable;
         }
      }
   }

   protected double getFollowRange() {
      IEntityAttributeInstance var1 = this.entity.initializeAttribute(EntityAttributes.FOLLOW_RANGE);
      return var1 == null ? 16.0 : var1.get();
   }

   @Override
   public void start() {
      this.canNavigateFlag = 0;
      this.checkCanNavigateCooldown = 0;
      this.timeWithoutVisibility = 0;
   }

   @Override
   public void stop() {
      this.entity.setAttackTarget(null);
   }

   public static boolean m_87zsdnsmm(MobEntity c_81psrrogw, LivingEntity c_97zulxhng, boolean bl, boolean bl2) {
      if (c_97zulxhng == null) {
         return false;
      } else if (c_97zulxhng == c_81psrrogw) {
         return false;
      } else if (!c_97zulxhng.isAlive()) {
         return false;
      } else if (!c_81psrrogw.canAttackEntity(c_97zulxhng.getClass())) {
         return false;
      } else {
         if (c_81psrrogw instanceof Tameable && StringUtils.isNotEmpty(((Tameable)c_81psrrogw).getOwnerName())) {
            if (c_97zulxhng instanceof Tameable && ((Tameable)c_81psrrogw).getOwnerName().equals(((Tameable)c_97zulxhng).getOwnerName())) {
               return false;
            }

            if (c_97zulxhng == ((Tameable)c_81psrrogw).getOwner()) {
               return false;
            }
         } else if (c_97zulxhng instanceof PlayerEntity && !bl && ((PlayerEntity)c_97zulxhng).abilities.invulnerable) {
            return false;
         }

         return !bl2 || c_81psrrogw.getMobVisibilityCache().canSee(c_97zulxhng);
      }
   }

   protected boolean canTarget(LivingEntity targetEntity, boolean isPlayer) {
      if (!m_87zsdnsmm(this.entity, targetEntity, isPlayer, this.checkVisibility)) {
         return false;
      } else if (!this.entity.isPosInVillage(new BlockPos(targetEntity))) {
         return false;
      } else {
         if (this.checkCanNavigate) {
            if (--this.checkCanNavigateCooldown <= 0) {
               this.canNavigateFlag = 0;
            }

            if (this.canNavigateFlag == 0) {
               this.canNavigateFlag = this.canNavigateToTarget(targetEntity) ? 1 : 2;
            }

            if (this.canNavigateFlag == 2) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean canNavigateToTarget(LivingEntity targetEntity) {
      this.checkCanNavigateCooldown = 10 + this.entity.getRandom().nextInt(5);
      Path var2 = this.entity.getNavigation().getNavigation(targetEntity);
      if (var2 == null) {
         return false;
      } else {
         PathNode var3 = var2.getTarget();
         if (var3 == null) {
            return false;
         } else {
            int var4 = var3.posX - MathHelper.floor(targetEntity.x);
            int var5 = var3.posZ - MathHelper.floor(targetEntity.z);
            return (double)(var4 * var4 + var5 * var5) <= 2.25;
         }
      }
   }
}
