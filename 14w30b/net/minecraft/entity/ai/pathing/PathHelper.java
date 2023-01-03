package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class PathHelper {
   private PathMinHeap pathHeap = new PathMinHeap();
   private PathNode[] pathNodes = new PathNode[32];
   private AbstractPathNodeHelper f_85pckaafr;

   public PathHelper(AbstractPathNodeHelper world) {
      this.f_85pckaafr = world;
   }

   public Path getPathToEntity(IWorld entity, Entity target, Entity pathRange, float f) {
      return this.m_10jyyskiz(entity, target, pathRange.x, pathRange.getBoundingBox().minY, pathRange.z, f);
   }

   public Path getPathToPos(IWorld entity, Entity x, BlockPos y, float f) {
      return this.m_10jyyskiz(entity, x, (double)((float)y.getX() + 0.5F), (double)((float)y.getY() + 0.5F), (double)((float)y.getZ() + 0.5F), f);
   }

   private Path m_10jyyskiz(IWorld c_05ktcjdzx, Entity c_47ldwddrb, double d, double e, double f, float g) {
      this.pathHeap.clear();
      this.f_85pckaafr.m_41nzanimf(c_05ktcjdzx, c_47ldwddrb);
      PathNode var10 = this.f_85pckaafr.m_17vajbguf(c_47ldwddrb);
      PathNode var11 = this.f_85pckaafr.m_97krmhugx(c_47ldwddrb, d, e, f);
      Path var12 = this.buildPath(c_47ldwddrb, var10, var11, g);
      this.f_85pckaafr.m_46ezxzbdo();
      return var12;
   }

   private Path buildPath(Entity entity, PathNode entityPathNode, PathNode targetPathNode, float increasedEntityHitBox) {
      entityPathNode.distanceFromStart = 0.0F;
      entityPathNode.distanceToTarget = entityPathNode.squaredDistanceTo(targetPathNode);
      entityPathNode.totalPathDistance = entityPathNode.distanceToTarget;
      this.pathHeap.clear();
      this.pathHeap.insert(entityPathNode);
      PathNode var5 = entityPathNode;

      while(!this.pathHeap.isEmpty()) {
         PathNode var6 = this.pathHeap.removePathNode();
         if (var6.equals(targetPathNode)) {
            return this.createPathFromNodeLinkedList(entityPathNode, targetPathNode);
         }

         if (var6.squaredDistanceTo(targetPathNode) < var5.squaredDistanceTo(targetPathNode)) {
            var5 = var6;
         }

         var6.visited = true;
         int var7 = this.f_85pckaafr.m_27ozwikog(this.pathNodes, entity, var6, targetPathNode, increasedEntityHitBox);

         for(int var8 = 0; var8 < var7; ++var8) {
            PathNode var9 = this.pathNodes[var8];
            float var10 = var6.distanceFromStart + var6.squaredDistanceTo(var9);
            if (var10 < increasedEntityHitBox * 2.0F && (!var9.inHeap() || var10 < var9.distanceFromStart)) {
               var9.previousNode = var6;
               var9.distanceFromStart = var10;
               var9.distanceToTarget = var9.squaredDistanceTo(targetPathNode);
               if (var9.inHeap()) {
                  this.pathHeap.insertWithTotalDistance(var9, var9.distanceFromStart + var9.distanceToTarget);
               } else {
                  var9.totalPathDistance = var9.distanceFromStart + var9.distanceToTarget;
                  this.pathHeap.insert(var9);
               }
            }
         }
      }

      return var5 == entityPathNode ? null : this.createPathFromNodeLinkedList(entityPathNode, var5);
   }

   private Path createPathFromNodeLinkedList(PathNode entityPathNode, PathNode targetPathNode) {
      int var3 = 1;

      for(PathNode var4 = targetPathNode; var4.previousNode != null; var4 = var4.previousNode) {
         ++var3;
      }

      PathNode[] var5 = new PathNode[var3];
      PathNode var7 = targetPathNode;
      --var3;

      for(var5[var3] = targetPathNode; var7.previousNode != null; var5[var3] = var7) {
         var7 = var7.previousNode;
         --var3;
      }

      return new Path(var5);
   }
}
