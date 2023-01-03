package net.minecraft.world.chunk.storage;

public class AlphaChunkDataArray {
   public final byte[] data;
   private final int zBitOffset;
   private final int xBitOffset;

   public AlphaChunkDataArray(byte[] data, int zBitOffset) {
      this.data = data;
      this.zBitOffset = zBitOffset;
      this.xBitOffset = zBitOffset + 4;
   }

   public int get(int x, int y, int z) {
      int var4 = x << this.xBitOffset | z << this.zBitOffset | y;
      int var5 = var4 >> 1;
      int var6 = var4 & 1;
      return var6 == 0 ? this.data[var5] & 15 : this.data[var5] >> 4 & 15;
   }
}
