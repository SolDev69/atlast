package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.Arrays;

public class NbtByteArray extends NbtElement {
   private byte[] value;

   NbtByteArray() {
   }

   public NbtByteArray(byte[] value) {
      this.value = value;
   }

   @Override
   void write(DataOutput output) {
      output.writeInt(this.value.length);
      output.write(this.value);
   }

   @Override
   void read(DataInput input, int depth, NbtReadLimiter limiter) {
      int var4 = input.readInt();
      limiter.read((long)(8 * var4));
      this.value = new byte[var4];
      input.readFully(this.value);
   }

   @Override
   public byte getType() {
      return 7;
   }

   @Override
   public String toString() {
      return "[" + this.value.length + " bytes]";
   }

   @Override
   public NbtElement copy() {
      byte[] var1 = new byte[this.value.length];
      System.arraycopy(this.value, 0, var1, 0, this.value.length);
      return new NbtByteArray(var1);
   }

   @Override
   public boolean equals(Object object) {
      return super.equals(object) ? Arrays.equals(this.value, ((NbtByteArray)object).value) : false;
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ Arrays.hashCode(this.value);
   }

   public byte[] getByteArray() {
      return this.value;
   }
}
