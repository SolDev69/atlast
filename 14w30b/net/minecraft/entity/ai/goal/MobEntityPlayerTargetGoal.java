package net.minecraft.entity.ai.goal;

import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.attribute.IEntityAttributeInstance;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MobEntityPlayerTargetGoal extends Goal {
   private static final Logger LOGGER = LogManager.getLogger();
   private MobEntity mob;
   private final Predicate targetFilter;
   private final ActiveTargetGoal.EntityDistanceComparator entityDistanceComparator;
   private LivingEntity target;
   private Class f_77aqnmpxq;

   public MobEntityPlayerTargetGoal(MobEntity mob) {
      this.mob = mob;
      this.f_77aqnmpxq = this.f_77aqnmpxq;
      if (mob instanceof PathAwareEntity) {
         LOGGER.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
      }

      this.targetFilter = new Predicate() {
         public boolean apply(Entity c_47ldwddrb) {
            if (!(c_47ldwddrb instanceof PlayerEntity)) {
               return false;
            } else {
               double var2 = MobEntityPlayerTargetGoal.this.getFollowRange();
               if (c_47ldwddrb.isSneaking()) {
                  var2 *= 0.8F;
               }

               if (c_47ldwddrb.isInvisible()) {
                  float var4 = ((PlayerEntity)c_47ldwddrb).getArmorEquippedRatio();
                  if (var4 < 0.1F) {
                     var4 = 0.1F;
                  }

                  var2 *= (double)(0.7F * var4);
               }

               return (double)c_47ldwddrb.getDistanceTo(MobEntityPlayerTargetGoal.this.mob) > var2
                  ? false
                  : TrackTargetGoal.m_87zsdnsmm(MobEntityPlayerTargetGoal.this.mob, (LivingEntity)c_47ldwddrb, false, true);
            }
         }
      };
      this.entityDistanceComparator = new ActiveTargetGoal.EntityDistanceComparator(mob);
   }

   @Override
   public boolean canStart() {
      double var1 = this.getFollowRange();
      List var3 = this.mob.world.getEntities(PlayerEntity.class, this.mob.getBoundingBox().expand(var1, 4.0, var1), this.targetFilter);
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
