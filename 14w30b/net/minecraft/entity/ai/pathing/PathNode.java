package net.minecraft.entity.ai.pathing;

import net.minecraft.util.math.MathHelper;

public class PathNode {
   public final int posX;
   public final int posY;
   public final int posZ;
   private final int hash;
   int heapIndex = -1;
   float distanceFromStart;
   float distanceToTarget;
   float totalPathDistance;
   PathNode previousNode;
   public boolean visited;

   public PathNode(int posX, int posY, int posZ) {
      this.posX = posX;
      this.posY = posY;
      this.posZ = posZ;
      this.hash = hash(posX, posY, posZ);
   }

   public static int hash(int x, int y, int z) {
      return y & 0xFF | (x & 32767) << 8 | (z & 32767) << 24 | (x < 0 ? Integer.MIN_VALUE : 0) | (z < 0 ? 32768 : 0);
   }

   public float getDistanceTo(PathNode pathNode) {
      float var2 = (float)(pathNode.posX - this.posX);
      float var3 = (float)(pathNode.posY - this.posY);
      float var4 = (float)(pathNode.posZ - this.posZ);
      return MathHelper.sqrt(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public float squaredDistanceTo(PathNode pathNode) {
      float var2 = (float)(pathNode.posX - this.posX);
      float var3 = (float)(pathNode.posY - this.posY);
      float var4 = (float)(pathNode.posZ - this.posZ);
      return var2 * var2 + var3 * var3 + var4 * var4;
   }

   @Override
   public boolean equals(Object object) {
      if (!(object instanceof PathNode)) {
         return false;
      } else {
         PathNode var2 = (PathNode)object;
         return this.hash == var2.hash && this.posX == var2.posX && this.posY == var2.posY && this.posZ == var2.posZ;
      }
   }

   @Override
   public int hashCode() {
      return this.hash;
   }

   public boolean inHeap() {
      return this.heapIndex >= 0;
   }

   @Override
   public String toString() {
      return this.posX + ", " + this.posY + ", " + this.posZ;
   }
}
