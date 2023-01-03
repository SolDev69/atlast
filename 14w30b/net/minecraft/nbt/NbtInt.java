package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public class NbtInt extends NbtElement.Number {
   private int value;

   NbtInt() {
   }

   public NbtInt(int value) {
      this.value = value;
   }

   @Override
   void write(DataOutput output) {
      output.writeInt(this.value);
   }

   @Override
   void read(DataInput input, int depth, NbtReadLimiter limiter) {
      limiter.read(32L);
      this.value = input.readInt();
   }

   @Override
   public byte getType() {
      return 3;
   }

   @Override
   public String toString() {
      return "" + this.value;
   }

   @Override
   public NbtElement copy() {
      return new NbtInt(this.value);
   }

   @Override
   public boolean equals(Object object) {
      if (super.equals(object)) {
         NbtInt var2 = (NbtInt)object;
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
      return (short)(this.value & 65535);
   }

   @Override
   public byte getByte() {
      return (byte)(this.value & 0xFF);
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
