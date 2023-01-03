package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public class NbtEnd extends NbtElement {
   NbtEnd() {
   }

   @Override
   void read(DataInput input, int depth, NbtReadLimiter limiter) {
   }

   @Override
   void write(DataOutput output) {
   }

   @Override
   public byte getType() {
      return 0;
   }

   @Override
   public String toString() {
      return "END";
   }

   @Override
   public NbtElement copy() {
      return new NbtEnd();
   }
}
