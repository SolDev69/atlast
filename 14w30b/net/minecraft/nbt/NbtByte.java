package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public class NbtByte extends NbtElement.Number {
   private byte value;

   NbtByte() {
   }

   public NbtByte(byte value) {
      this.value = value;
   }

   @Override
   void write(DataOutput output) {
      output.writeByte(this.value);
   }

   @Override
   void read(DataInput input, int depth, NbtReadLimiter limiter) {
      limiter.read(8L);
      this.value = input.readByte();
   }

   @Override
   public byte getType() {
      return 1;
   }

   @Override
   public String toString() {
      return "" + this.value + "b";
   }

   @Override
   public NbtElement copy() {
      return new NbtByte(this.value);
   }

   @Override
   public boolean equals(Object object) {
      if (super.equals(object)) {
         NbtByte var2 = (NbtByte)object;
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
      return (short)this.value;
   }

   @Override
   public byte getByte() {
      return this.value;
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
