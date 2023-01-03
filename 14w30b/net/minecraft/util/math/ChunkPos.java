package net.minecraft.util.math;

public class ChunkPos {
   public final int x;
   public final int z;

   public ChunkPos(int x, int z) {
      this.x = x;
      this.z = z;
   }

   public static long toLong(int x, int y) {
      return (long)x & 4294967295L | ((long)y & 4294967295L) << 32;
   }

   @Override
   public int hashCode() {
      int var1 = 1664525 * this.x + 1013904223;
      int var2 = 1664525 * (this.z ^ -559038737) + 1013904223;
      return var1 ^ var2;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof ChunkPos)) {
         return false;
      } else {
         ChunkPos var2 = (ChunkPos)obj;
         return this.x == var2.x && this.z == var2.z;
      }
   }

   public int getCenterBlockPosX() {
      return (this.x << 4) + 8;
   }

   public int getCenterBlockPosZ() {
      return (this.z << 4) + 8;
   }

   public int getMinBlockPosX() {
      return this.x << 4;
   }

   public int getMinBlockPosZ() {
      return this.z << 4;
   }

   public int getMaxBlockPosX() {
      return (this.x << 4) + 15;
   }

   public int getMaxBlockPosZ() {
      return (this.z << 4) + 15;
   }

   public BlockPos getBlockPos(int sectionX, int y, int sectionZ) {
      return new BlockPos((this.x << 4) + sectionX, y, (this.z << 4) + sectionZ);
   }

   public BlockPos getCenterBlockPos(int y) {
      return new BlockPos(this.getCenterBlockPosX(), y, this.getCenterBlockPosZ());
   }

   @Override
   public String toString() {
      return "[" + this.x + ", " + this.z + "]";
   }
}
