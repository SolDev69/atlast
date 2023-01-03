package net.minecraft.entity.ai.goal;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFilter;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.entity.living.player.PlayerEntity;

public class ActiveTargetGoal extends TrackTargetGoal {
   protected final Class targetTypeClass;
   private final int chanceToStartGoal;
   protected final ActiveTargetGoal.EntityDistanceComparator entityDistanceComparator;
   protected final Predicate canTargetEntityFilter;
   protected LivingEntity targetEntity;

   public ActiveTargetGoal(PathAwareEntity entity, Class targetTypeClass, boolean chanceToStartGoal) {
      this(entity, targetTypeClass, chanceToStartGoal, false);
   }

   public ActiveTargetGoal(PathAwareEntity entity, Class targetTypeClass, boolean chanceToStartGoal, boolean checkVisibility) {
      this(entity, targetTypeClass, 10, chanceToStartGoal, checkVisibility, null);
   }

   public ActiveTargetGoal(
      PathAwareEntity entity, Class targetTypeClass, int chanceToStartGoal, boolean checkVisibility, boolean checkCanNavigate, Predicate predicate
   ) {
      super(entity, checkVisibility, checkCanNavigate);
      this.targetTypeClass = targetTypeClass;
      this.chanceToStartGoal = chanceToStartGoal;
      this.entityDistanceComparator = new ActiveTargetGoal.EntityDistanceComparator(entity);
      this.setControls(1);
      this.canTargetEntityFilter = new Predicate() {
         public boolean apply(LivingEntity c_97zulxhng) {
            if (predicate != null && !predicate.apply(c_97zulxhng)) {
               return false;
            } else {
               if (c_97zulxhng instanceof PlayerEntity) {
                  double var2 = ActiveTargetGoal.this.getFollowRange();
                  if (c_97zulxhng.isSneaking()) {
                     var2 *= 0.8F;
                  }

                  if (c_97zulxhng.isInvisible()) {
                     float var4 = ((PlayerEntity)c_97zulxhng).getArmorEquippedRatio();
                     if (var4 < 0.1F) {
                        var4 = 0.1F;
                     }

                     var2 *= (double)(0.7F * var4);
                  }

                  if ((double)c_97zulxhng.getDistanceTo(ActiveTargetGoal.this.entity) > var2) {
                     return false;
                  }
               }

               return ActiveTargetGoal.this.canTarget(c_97zulxhng, false);
            }
         }
      };
   }

   @Override
   public boolean canStart() {
      if (this.chanceToStartGoal > 0 && this.entity.getRandom().nextInt(this.chanceToStartGoal) != 0) {
         return false;
      } else {
         double var1 = this.getFollowRange();
         List var3 = this.entity
            .world
            .getEntities(
               this.targetTypeClass,
               this.entity.getBoundingBox().expand(var1, 4.0, var1),
               Predicates.and(this.canTargetEntityFilter, EntityFilter.NOT_SPECTATOR)
            );
         Collections.sort(var3, this.entityDistanceComparator);
         if (var3.isEmpty()) {
            return false;
         } else {
            this.targetEntity = (LivingEntity)var3.get(0);
            return true;
         }
      }
   }

   @Override
   public void start() {
      this.entity.setAttackTarget(this.targetEntity);
      super.start();
   }

   public static class EntityDistanceComparator implements Comparator {
      private final Entity entity;

      public EntityDistanceComparator(Entity entity) {
         this.entity = entity;
      }

      public int compare(Entity c_47ldwddrb, Entity c_47ldwddrb2) {
         double var3 = this.entity.getSquaredDistanceTo(c_47ldwddrb);
         double var5 = this.entity.getSquaredDistanceTo(c_47ldwddrb2);
         if (var3 < var5) {
            return -1;
         } else {
            return var3 > var5 ? 1 : 0;
         }
      }
   }
}
