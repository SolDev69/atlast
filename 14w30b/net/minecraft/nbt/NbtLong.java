package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public class NbtLong extends NbtElement.Number {
   private long value;

   NbtLong() {
   }

   public NbtLong(long value) {
      this.value = value;
   }

   @Override
   void write(DataOutput output) {
      output.writeLong(this.value);
   }

   @Override
   void read(DataInput input, int depth, NbtReadLimiter limiter) {
      limiter.read(64L);
      this.value = input.readLong();
   }

   @Override
   public byte getType() {
      return 4;
   }

   @Override
   public String toString() {
      return "" + this.value + "L";
   }

   @Override
   public NbtElement copy() {
      return new NbtLong(this.value);
   }

   @Override
   public boolean equals(Object object) {
      if (super.equals(object)) {
         NbtLong var2 = (NbtLong)object;
         return this.value == var2.value;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ (int)(this.value ^ this.value >>> 32);
   }

   @Override
   public long getLong() {
      return this.value;
   }

   @Override
   public int getInt() {
      return (int)(this.value & -1L);
   }

   @Override
   public short getShort() {
      return (short)((int)(this.value & 65535L));
   }

   @Override
   public byte getByte() {
      return (byte)((int)(this.value & 255L));
   }

   @Override
   public double getDouble() {
      return (double)this.value;
   }

   @Override
   public float getFloat() {
      return (float)this.value;
   }
}
