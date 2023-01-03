package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class Path {
   private final PathNode[] nodes;
   private int currentIndex;
   private int length;

   public Path(PathNode[] nodes) {
      this.nodes = nodes;
      this.length = nodes.length;
   }

   public void m_96yudziba() {
      ++this.currentIndex;
   }

   public boolean reachedTarget() {
      return this.currentIndex >= this.length;
   }

   public PathNode getTarget() {
      return this.length > 0 ? this.nodes[this.length - 1] : null;
   }

   public PathNode getPathNode(int index) {
      return this.nodes[index];
   }

   public int getPathLength() {
      return this.length;
   }

   public void setPathLength(int length) {
      this.length = length;
   }

   public int getIndexInPath() {
      return this.currentIndex;
   }

   public void setIndexInPath(int index) {
      this.currentIndex = index;
   }

   public Vec3d getNextPos(Entity entity, int index) {
      double var3 = (double)this.nodes[index].posX + (double)((int)(entity.width + 1.0F)) * 0.5;
      double var5 = (double)this.nodes[index].posY;
      double var7 = (double)this.nodes[index].posZ + (double)((int)(entity.width + 1.0F)) * 0.5;
      return new Vec3d(var3, var5, var7);
   }

   public Vec3d getNextPos(Entity entity) {
      return this.getNextPos(entity, this.currentIndex);
   }

   public boolean equals(Path path) {
      if (path == null) {
         return false;
      } else if (path.nodes.length != this.nodes.length) {
         return false;
      } else {
         for(int var2 = 0; var2 < this.nodes.length; ++var2) {
            if (this.nodes[var2].posX != path.nodes[var2].posX
               || this.nodes[var2].posY != path.nodes[var2].posY
               || this.nodes[var2].posZ != path.nodes[var2].posZ) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean isTarget(Vec3d pos) {
      PathNode var2 = this.getTarget();
      if (var2 == null) {
         return false;
      } else {
         return var2.posX == (int)pos.x && var2.posZ == (int)pos.z;
      }
   }
}
