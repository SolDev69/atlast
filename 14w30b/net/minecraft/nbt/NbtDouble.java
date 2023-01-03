package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import net.minecraft.util.math.MathHelper;

public class NbtDouble extends NbtElement.Number {
   private double value;

   NbtDouble() {
   }

   public NbtDouble(double value) {
      this.value = value;
   }

   @Override
   void write(DataOutput output) {
      output.writeDouble(this.value);
   }

   @Override
   void read(DataInput input, int depth, NbtReadLimiter limiter) {
      limiter.read(64L);
      this.value = input.readDouble();
   }

   @Override
   public byte getType() {
      return 6;
   }

   @Override
   public String toString() {
      return "" + this.value + "d";
   }

   @Override
   public NbtElement copy() {
      return new NbtDouble(this.value);
   }

   @Override
   public boolean equals(Object object) {
      if (super.equals(object)) {
         NbtDouble var2 = (NbtDouble)object;
         return this.value == var2.value;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      long var1 = Double.doubleToLongBits(this.value);
      return super.hashCode() ^ (int)(var1 ^ var1 >>> 32);
   }

   @Override
   public long getLong() {
      return (long)Math.floor(this.value);
   }

   @Override
   public int getInt() {
      return MathHelper.floor(this.value);
   }

   @Override
   public short getShort() {
      return (short)(MathHelper.floor(this.value) & 65535);
   }

   @Override
   public byte getByte() {
      return (byte)(MathHelper.floor(this.value) & 0xFF);
   }

   @Override
   public double getDouble() {
      return this.value;
   }

   @Override
   public float getFloat() {
      return (float)this.value;
   }
}
