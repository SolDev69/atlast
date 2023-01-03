package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.attribute.IEntityAttributeInstance;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class EntityNavigation {
   protected MobEntity mob;
   protected World world;
   protected Path currentPath;
   protected double speed;
   private IEntityAttributeInstance followRange;
   private int tickCounter;
   private int lastUpdate;
   private Vec3d entityPosAboveWater = new Vec3d(0.0, 0.0, 0.0);
   private float f_50wtorclr = 1.0F;
   private PathHelper f_86qldgoad;

   public EntityNavigation(MobEntity mob, World world) {
      this.mob = mob;
      this.world = world;
      this.followRange = mob.initializeAttribute(EntityAttributes.FOLLOW_RANGE);
      this.f_86qldgoad = this.createPathHelper();
   }

   protected abstract PathHelper createPathHelper();

   public void setSpeed(double speed) {
      this.speed = speed;
   }

   public float getFollowRange() {
      return (float)this.followRange.get();
   }

   public final Path findPathTo(double posX, double posY, double posZ) {
      return this.m_79ixqqkkt(new BlockPos(MathHelper.floor(posX), (int)posY, MathHelper.floor(posZ)));
   }

   public Path m_79ixqqkkt(BlockPos c_76varpwca) {
      return !this.canPathFind() ? null : this.world.pathFindEntity(this.mob, c_76varpwca, this.getFollowRange(), this.f_86qldgoad);
   }

   public boolean startMovingTo(double posX, double posY, double posZ, double speed) {
      Path var9 = this.findPathTo((double)MathHelper.floor(posX), (double)((int)posY), (double)MathHelper.floor(posZ));
      return this.startMovingAlong(var9, speed);
   }

   public void m_74ipbvgvj(float f) {
      this.f_50wtorclr = f;
   }

   public Path getNavigation(Entity target) {
      return !this.canPathFind() ? null : this.world.pathFindEntity(this.mob, target, this.getFollowRange(), this.f_86qldgoad);
   }

   public boolean startMovingTo(Entity entity, double speed) {
      Path var4 = this.getNavigation(entity);
      return var4 != null ? this.startMovingAlong(var4, speed) : false;
   }

   public boolean startMovingAlong(Path path, double speed) {
      if (path == null) {
         this.currentPath = null;
         return false;
      } else {
         if (!path.equals(this.currentPath)) {
            this.currentPath = path;
         }

         this.avoidSunLight();
         if (this.currentPath.getPathLength() == 0) {
            return false;
         } else {
            this.speed = speed;
            Vec3d var4 = this.getEntityPosAboveWater();
            this.lastUpdate = this.tickCounter;
            this.entityPosAboveWater = var4;
            return true;
         }
      }
   }

   public Path getCurrentPath() {
      return this.currentPath;
   }

   public void tick() {
      ++this.tickCounter;
      if (!this.isIdle()) {
         if (this.canPathFind()) {
            this.updatePath();
         }

         if (!this.isIdle()) {
            Vec3d var1 = this.currentPath.getNextPos(this.mob);
            if (var1 != null) {
               this.mob.getMovementControl().update(var1.x, var1.y, var1.z, this.speed);
            }
         }
      }
   }

   protected void updatePath() {
      Vec3d var1 = this.getEntityPosAboveWater();
      int var2 = this.currentPath.getPathLength();

      for(int var3 = this.currentPath.getIndexInPath(); var3 < this.currentPath.getPathLength(); ++var3) {
         if (this.currentPath.getPathNode(var3).posY != (int)var1.y) {
            var2 = var3;
            break;
         }
      }

      float var8 = this.mob.width * this.mob.width * this.f_50wtorclr;

      for(int var4 = this.currentPath.getIndexInPath(); var4 < var2; ++var4) {
         if (var1.squaredDistanceTo(this.currentPath.getNextPos(this.mob, var4)) < (double)var8) {
            this.currentPath.setIndexInPath(var4 + 1);
         }
      }

      int var9 = MathHelper.ceil(this.mob.width);
      int var5 = (int)this.mob.height + 1;
      int var6 = var9;

      for(int var7 = var2 - 1; var7 >= this.currentPath.getIndexInPath(); --var7) {
         if (this.isCurrentPathNode(var1, this.currentPath.getNextPos(this.mob, var7), var9, var5, var6)) {
            this.currentPath.setIndexInPath(var7);
            break;
         }
      }

      this.m_77qkxpxlr(var1);
   }

   protected void m_77qkxpxlr(Vec3d c_60pmlswez) {
      if (this.tickCounter - this.lastUpdate > 100) {
         if (c_60pmlswez.squaredDistanceTo(this.entityPosAboveWater) < 2.25) {
            this.stopCurrentNavigation();
         }

         this.lastUpdate = this.tickCounter;
         this.entityPosAboveWater = c_60pmlswez;
      }
   }

   public boolean isIdle() {
      return this.currentPath == null || this.currentPath.reachedTarget();
   }

   public void stopCurrentNavigation() {
      this.currentPath = null;
   }

   protected abstract Vec3d getEntityPosAboveWater();

   protected abstract boolean canPathFind();

   protected boolean mobEntityInLiquid() {
      return this.mob.isInWater() || this.mob.isInLava();
   }

   protected void avoidSunLight() {
   }

   protected abstract boolean isCurrentPathNode(Vec3d posAboveWater, Vec3d pathNode, int mobEntityWidth, int mobEntityHeight, int mobEntityDepth);
}
