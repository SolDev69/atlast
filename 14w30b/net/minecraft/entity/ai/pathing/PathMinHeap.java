package net.minecraft.entity.ai.pathing;

public class PathMinHeap {
   private PathNode[] pathNodes = new PathNode[1024];
   private int count;

   public PathNode insert(PathNode pathNode) {
      if (pathNode.heapIndex >= 0) {
         throw new IllegalStateException("OW KNOWS!");
      } else {
         if (this.count == this.pathNodes.length) {
            PathNode[] var2 = new PathNode[this.count << 1];
            System.arraycopy(this.pathNodes, 0, var2, 0, this.count);
            this.pathNodes = var2;
         }

         this.pathNodes[this.count] = pathNode;
         pathNode.heapIndex = this.count;
         this.shiftUp(this.count++);
         return pathNode;
      }
   }

   public void clear() {
      this.count = 0;
   }

   public PathNode removePathNode() {
      PathNode var1 = this.pathNodes[0];
      this.pathNodes[0] = this.pathNodes[--this.count];
      this.pathNodes[this.count] = null;
      if (this.count > 0) {
         this.shiftDown(0);
      }

      var1.heapIndex = -1;
      return var1;
   }

   public void insertWithTotalDistance(PathNode pathNode, float totalDistance) {
      float var3 = pathNode.totalPathDistance;
      pathNode.totalPathDistance = totalDistance;
      if (totalDistance < var3) {
         this.shiftUp(pathNode.heapIndex);
      } else {
         this.shiftDown(pathNode.heapIndex);
      }
   }

   private void shiftUp(int index) {
      PathNode var2 = this.pathNodes[index];

      int var4;
      for(float var3 = var2.totalPathDistance; index > 0; index = var4) {
         var4 = index - 1 >> 1;
         PathNode var5 = this.pathNodes[var4];
         if (!(var3 < var5.totalPathDistance)) {
            break;
         }

         this.pathNodes[index] = var5;
         var5.heapIndex = index;
      }

      this.pathNodes[index] = var2;
      var2.heapIndex = index;
   }

   private void shiftDown(int index) {
      PathNode var2 = this.pathNodes[index];
      float var3 = var2.totalPathDistance;

      while(true) {
         int var4 = 1 + (index << 1);
         int var5 = var4 + 1;
         if (var4 >= this.count) {
            break;
         }

         PathNode var6 = this.pathNodes[var4];
         float var7 = var6.totalPathDistance;
         PathNode var8;
         float var9;
         if (var5 >= this.count) {
            var8 = null;
            var9 = Float.POSITIVE_INFINITY;
         } else {
            var8 = this.pathNodes[var5];
            var9 = var8.totalPathDistance;
         }

         if (var7 < var9) {
            if (!(var7 < var3)) {
               break;
            }

            this.pathNodes[index] = var6;
            var6.heapIndex = index;
            index = var4;
         } else {
            if (!(var9 < var3)) {
               break;
            }

            this.pathNodes[index] = var8;
            var8.heapIndex = index;
            index = var5;
         }
      }

      this.pathNodes[index] = var2;
      var2.heapIndex = index;
   }

   public boolean isEmpty() {
      return this.count == 0;
   }
}
