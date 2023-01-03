package net.minecraft.world.chunk;

public class ChunkNibbleStorage {
   private final byte[] data;
   private final int zBitOffset;
   private final int yBitOffset;

   public ChunkNibbleStorage(int size, int zBitOffset) {
      this.data = new byte[size >> 1];
      this.zBitOffset = zBitOffset;
      this.yBitOffset = zBitOffset + 4;
   }

   public ChunkNibbleStorage(byte[] data, int zBitOffset) {
      this.data = data;
      this.zBitOffset = zBitOffset;
      this.yBitOffset = zBitOffset + 4;
   }

   public int get(int x, int y, int z) {
      int var4 = y << this.yBitOffset | z << this.zBitOffset | x;
      int var5 = var4 >> 1;
      int var6 = var4 & 1;
      return var6 == 0 ? this.data[var5] & 15 : this.data[var5] >> 4 & 15;
   }

   public void set(int x, int y, int z, int value) {
      int var5 = y << this.yBitOffset | z << this.zBitOffset | x;
      int var6 = var5 >> 1;
      int var7 = var5 & 1;
      if (var7 == 0) {
         this.data[var6] = (byte)(this.data[var6] & 240 | value & 15);
      } else {
         this.data[var6] = (byte)(this.data[var6] & 15 | (value & 15) << 4);
      }
   }

   public byte[] getData() {
      return this.data;
   }
}
