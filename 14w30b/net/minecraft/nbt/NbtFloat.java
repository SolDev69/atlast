package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import net.minecraft.util.math.MathHelper;

public class NbtFloat extends NbtElement.Number {
   private float value;

   NbtFloat() {
   }

   public NbtFloat(float value) {
      this.value = value;
   }

   @Override
   void write(DataOutput output) {
      output.writeFloat(this.value);
   }

   @Override
   void read(DataInput input, int depth, NbtReadLimiter limiter) {
      limiter.read(32L);
      this.value = input.readFloat();
   }

   @Override
   public byte getType() {
      return 5;
   }

   @Override
   public String toString() {
      return "" + this.value + "f";
   }

   @Override
   public NbtElement copy() {
      return new NbtFloat(this.value);
   }

   @Override
   public boolean equals(Object object) {
      if (super.equals(object)) {
         NbtFloat var2 = (NbtFloat)object;
         return this.value == var2.value;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ Float.floatToIntBits(this.value);
   }

   @Override
   public long getLong() {
      return (long)this.value;
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
      return (double)this.value;
   }

   @Override
   public float getFloat() {
      return this.value;
   }
}
