package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public class NbtString extends NbtElement {
   private String value;

   public NbtString() {
      this.value = "";
   }

   public NbtString(String value) {
      this.value = value;
      if (value == null) {
         throw new IllegalArgumentException("Empty string not allowed");
      }
   }

   @Override
   void write(DataOutput output) {
      output.writeUTF(this.value);
   }

   @Override
   void read(DataInput input, int depth, NbtReadLimiter limiter) {
      this.value = input.readUTF();
      limiter.read((long)(16 * this.value.length()));
   }

   @Override
   public byte getType() {
      return 8;
   }

   @Override
   public String toString() {
      return "\"" + this.value.replace("\"", "\\\"") + "\"";
   }

   @Override
   public NbtElement copy() {
      return new NbtString(this.value);
   }

   @Override
   public boolean isEmpty() {
      return this.value.isEmpty();
   }

   @Override
   public boolean equals(Object object) {
      if (!super.equals(object)) {
         return false;
      } else {
         NbtString var2 = (NbtString)object;
         return this.value == null && var2.value == null || this.value != null && this.value.equals(var2.value);
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.value.hashCode();
   }

   @Override
   public String asString() {
      return this.value;
   }
}
