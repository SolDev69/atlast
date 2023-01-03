package net.minecraft.entity.ai.goal;

import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.attribute.IEntityAttributeInstance;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MobEntityActiveTargetGoal extends Goal {
   private static final Logger LOGGER = LogManager.getLogger();
   private MobEntity mob;
   private final Predicate targetFilter;
   private final ActiveTargetGoal.EntityDistanceComparator entityDistanceComparator;
   private LivingEntity target;
   private Class targetType;

   public MobEntityActiveTargetGoal(MobEntity mob, Class targetType) {
      this.mob = mob;
      this.targetType = targetType;
      if (mob instanceof PathAwareEntity) {
         LOGGER.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
      }

      this.targetFilter = new Predicate() {
         public boolean apply(LivingEntity c_97zulxhng) {
            double var2 = MobEntityActiveTargetGoal.this.getFollowRange();
            if (c_97zulxhng.isSneaking()) {
               var2 *= 0.8F;
            }

            if (c_97zulxhng.isInvisible()) {
               return false;
            } else {
               return (double)c_97zulxhng.getDistanceTo(MobEntityActiveTargetGoal.this.mob) > var2
                  ? false
                  : TrackTargetGoal.m_87zsdnsmm(MobEntityActiveTargetGoal.this.mob, c_97zulxhng, false, true);
            }
         }
      };
      this.entityDistanceComparator = new ActiveTargetGoal.EntityDistanceComparator(mob);
   }

   @Override
   public boolean canStart() {
      double var1 = this.getFollowRange();
      List var3 = this.mob.world.getEntities(this.targetType, this.mob.getBoundingBox().expand(var1, 4.0, var1), this.targetFilter);
      Collections.sort(var3, this.entityDistanceComparator);
      if (var3.isEmpty()) {
         return false;
      } else {
         this.target = (LivingEntity)var3.get(0);
         return true;
      }
   }

   @Override
   public boolean shouldContinue() {
      LivingEntity var1 = this.mob.getTargetEntity();
      if (var1 == null) {
         return false;
      } else if (!var1.isAlive()) {
         return false;
      } else {
         double var2 = this.getFollowRange();
         if (this.mob.getSquaredDistanceTo(var1) > var2 * var2) {
            return false;
         } else {
            return !(var1 instanceof ServerPlayerEntity) || !((ServerPlayerEntity)var1).interactionManager.isCreative();
         }
      }
   }

   @Override
   public void start() {
      this.mob.setAttackTarget(this.target);
      super.start();
   }

   @Override
   public void stop() {
      this.mob.setAttackTarget(null);
      super.start();
   }

   protected double getFollowRange() {
      IEntityAttributeInstance var1 = this.mob.initializeAttribute(EntityAttributes.FOLLOW_RANGE);
      return var1 == null ? 16.0 : var1.get();
   }
}
