package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MobEntitySwimNavigation extends EntityNavigation {
   public MobEntitySwimNavigation(MobEntity c_81psrrogw, World c_54ruxjwzt) {
      super(c_81psrrogw, c_54ruxjwzt);
   }

   @Override
   protected PathHelper createPathHelper() {
      return new PathHelper(new SwimPathNodeHelper());
   }

   @Override
   protected boolean canPathFind() {
      return this.mobEntityInLiquid();
   }

   @Override
   protected Vec3d getEntityPosAboveWater() {
      return new Vec3d(this.mob.x, this.mob.y + (double)this.mob.height * 0.5, this.mob.z);
   }

   @Override
   protected void updatePath() {
      Vec3d var1 = this.getEntityPosAboveWater();
      float var2 = this.mob.width * this.mob.width;
      byte var3 = 6;
      if (var1.squaredDistanceTo(this.currentPath.getNextPos(this.mob, this.currentPath.getIndexInPath())) < (double)var2) {
         this.currentPath.m_96yudziba();
      }

      for(int var4 = Math.min(this.currentPath.getIndexInPath() + var3, this.currentPath.getPathLength() - 1); var4 > this.currentPath.getIndexInPath(); --var4) {
         Vec3d var5 = this.currentPath.getNextPos(this.mob, var4);
         if (!(var5.squaredDistanceTo(var1) > 36.0) && this.isCurrentPathNode(var1, var5, 0, 0, 0)) {
            this.currentPath.setIndexInPath(var4);
            break;
         }
      }

      this.m_77qkxpxlr(var1);
   }

   @Override
   protected void avoidSunLight() {
      super.avoidSunLight();
   }

   @Override
   protected boolean isCurrentPathNode(Vec3d posAboveWater, Vec3d pathNode, int mobEntityWidth, int mobEntityHeight, int mobEntityDepth) {
      HitResult var6 = this.world.rayTrace(posAboveWater, new Vec3d(pathNode.x, pathNode.y + (double)this.mob.height * 0.5, pathNode.z), false, true, false);
      return var6 == null || var6.type == HitResult.Type.MISS;
   }
}
