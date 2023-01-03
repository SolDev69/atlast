package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public class NbtShort extends NbtElement.Number {
   private short value;

   public NbtShort() {
   }

   public NbtShort(short value) {
      this.value = value;
   }

   @Override
   void write(DataOutput output) {
      output.writeShort(this.value);
   }

   @Override
   void read(DataInput input, int depth, NbtReadLimiter limiter) {
      limiter.read(16L);
      this.value = input.readShort();
   }

   @Override
   public byte getType() {
      return 2;
   }

   @Override
   public String toString() {
      return "" + this.value + "s";
   }

   @Override
   public NbtElement copy() {
      return new NbtShort(this.value);
   }

   @Override
   public boolean equals(Object object) {
      if (super.equals(object)) {
         NbtShort var2 = (NbtShort)object;
         return this.value == var2.value;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.value;
   }

   @Override
   public long getLong() {
      return (long)this.value;
   }

   @Override
   public int getInt() {
      return this.value;
   }

   @Override
   public short getShort() {
      return this.value;
   }

   @Override
   public byte getByte() {
      return (byte)(this.value & 255);
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
