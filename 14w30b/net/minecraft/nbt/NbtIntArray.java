package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.Arrays;

public class NbtIntArray extends NbtElement {
   private int[] value;

   NbtIntArray() {
   }

   public NbtIntArray(int[] value) {
      this.value = value;
   }

   @Override
   void write(DataOutput output) {
      output.writeInt(this.value.length);

      for(int var2 = 0; var2 < this.value.length; ++var2) {
         output.writeInt(this.value[var2]);
      }
   }

   @Override
   void read(DataInput input, int depth, NbtReadLimiter limiter) {
      int var4 = input.readInt();
      limiter.read((long)(32 * var4));
      this.value = new int[var4];

      for(int var5 = 0; var5 < var4; ++var5) {
         this.value[var5] = input.readInt();
      }
   }

   @Override
   public byte getType() {
      return 11;
   }

   @Override
   public String toString() {
      String var1 = "[";

      for(int var5 : this.value) {
         var1 = var1 + var5 + ",";
      }

      return var1 + "]";
   }

   @Override
   public NbtElement copy() {
      int[] var1 = new int[this.value.length];
      System.arraycopy(this.value, 0, var1, 0, this.value.length);
      return new NbtIntArray(var1);
   }

   @Override
   public boolean equals(Object object) {
      return super.equals(object) ? Arrays.equals(this.value, ((NbtIntArray)object).value) : false;
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ Arrays.hashCode(this.value);
   }

   public int[] getIntArray() {
      return this.value;
   }
}
