package net.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class C_78ehokafe extends MobEntityNavigation {
   private BlockPos f_18labzmry;

   public C_78ehokafe(MobEntity c_81psrrogw, World c_54ruxjwzt) {
      super(c_81psrrogw, c_54ruxjwzt);
   }

   @Override
   public Path m_79ixqqkkt(BlockPos c_76varpwca) {
      this.f_18labzmry = c_76varpwca;
      return super.m_79ixqqkkt(c_76varpwca);
   }

   @Override
   public Path getNavigation(Entity target) {
      this.f_18labzmry = new BlockPos(target);
      return super.getNavigation(target);
   }

   @Override
   public boolean startMovingTo(Entity entity, double speed) {
      Path var4 = this.getNavigation(entity);
      if (var4 != null) {
         return this.startMovingAlong(var4, speed);
      } else {
         this.f_18labzmry = new BlockPos(entity);
         this.speed = speed;
         return true;
      }
   }

   @Override
   public void tick() {
      if (!this.isIdle()) {
         super.tick();
      } else {
         if (this.f_18labzmry != null) {
            double var1 = (double)(this.mob.width * this.mob.width);
            if (!(this.mob.getSquaredDistanceToCenter(this.f_18labzmry) < var1)
               && (
                  !(this.mob.y > (double)this.f_18labzmry.getY())
                     || !(
                        this.mob.getSquaredDistanceToCenter(new BlockPos(this.f_18labzmry.getX(), MathHelper.floor(this.mob.y), this.f_18labzmry.getZ()))
                           < var1
                     )
               )) {
               this.mob
                  .getMovementControl()
                  .update((double)this.f_18labzmry.getX(), (double)this.f_18labzmry.getY(), (double)this.f_18labzmry.getZ(), this.speed);
            } else {
               this.f_18labzmry = null;
            }
         }
      }
   }
}
