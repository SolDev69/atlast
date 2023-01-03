package net.minecraft.entity.ai.goal;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFilter;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

public class FleeEntityGoal extends Goal {
   public final Predicate entityFilter = new Predicate() {
      public boolean apply(Entity c_47ldwddrb) {
         return c_47ldwddrb.isAlive() && FleeEntityGoal.this.mob.getMobVisibilityCache().canSee(c_47ldwddrb);
      }
   };
   protected PathAwareEntity mob;
   private double slowSpeed;
   private double fastSpeed;
   protected Entity targetEntity;
   private float distance;
   private Path fleePath;
   private EntityNavigation fleeingEntityNavigation;
   private Predicate f_86tlsarmg;

   public FleeEntityGoal(PathAwareEntity mob, Predicate classToFleeFrom, float distance, double slowSpeed, double e) {
      this.mob = mob;
      this.f_86tlsarmg = classToFleeFrom;
      this.distance = distance;
      this.slowSpeed = slowSpeed;
      this.fastSpeed = e;
      this.fleeingEntityNavigation = mob.getNavigation();
      this.setControls(1);
   }

   @Override
   public boolean canStart() {
      List var1 = this.mob
         .world
         .getEntities(
            this.mob,
            this.mob.getBoundingBox().expand((double)this.distance, 3.0, (double)this.distance),
            Predicates.and(new Predicate[]{EntityFilter.NOT_SPECTATOR, this.entityFilter, this.f_86tlsarmg})
         );
      if (var1.isEmpty()) {
         return false;
      } else {
         this.targetEntity = (Entity)var1.get(0);
         Vec3d var2 = TargetFinder.getTargetAwayFromEntity(this.mob, 16, 7, new Vec3d(this.targetEntity.x, this.targetEntity.y, this.targetEntity.z));
         if (var2 == null) {
            return false;
         } else if (this.targetEntity.getSquaredDistanceTo(var2.x, var2.y, var2.z) < this.targetEntity.getSquaredDistanceTo(this.mob)) {
            return false;
         } else {
            this.fleePath = this.fleeingEntityNavigation.findPathTo(var2.x, var2.y, var2.z);
            if (this.fleePath == null) {
               return false;
            } else {
               return this.fleePath.isTarget(var2);
            }
         }
      }
   }

   @Override
   public boolean shouldContinue() {
      return !this.fleeingEntityNavigation.isIdle();
   }

   @Override
   public void start() {
      this.fleeingEntityNavigation.startMovingAlong(this.fleePath, this.slowSpeed);
   }

   @Override
   public void stop() {
      this.targetEntity = null;
   }

   @Override
   public void tick() {
      if (this.mob.getSquaredDistanceTo(this.targetEntity) < 49.0) {
         this.mob.getNavigation().setSpeed(this.fastSpeed);
      } else {
         this.mob.getNavigation().setSpeed(this.slowSpeed);
      }
   }
}
