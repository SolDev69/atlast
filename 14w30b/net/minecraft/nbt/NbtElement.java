package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public abstract class NbtElement {
   public static final String[] TYPE_NAMES = new String[]{
      "END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]"
   };

   abstract void write(DataOutput output);

   abstract void read(DataInput input, int depth, NbtReadLimiter limiter);

   @Override
   public abstract String toString();

   public abstract byte getType();

   protected NbtElement() {
   }

   protected static NbtElement create(byte type) {
      switch(type) {
         case 0:
            return new NbtEnd();
         case 1:
            return new NbtByte();
         case 2:
            return new NbtShort();
         case 3:
            return new NbtInt();
         case 4:
            return new NbtLong();
         case 5:
            return new NbtFloat();
         case 6:
            return new NbtDouble();
         case 7:
            return new NbtByteArray();
         case 8:
            return new NbtString();
         case 9:
            return new NbtList();
         case 10:
            return new NbtCompound();
         case 11:
            return new NbtIntArray();
         default:
            return null;
      }
   }

   public abstract NbtElement copy();

   public boolean isEmpty() {
      return false;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof NbtElement)) {
         return false;
      } else {
         NbtElement var2 = (NbtElement)obj;
         return this.getType() == var2.getType();
      }
   }

   @Override
   public int hashCode() {
      return this.getType();
   }

   protected String asString() {
      return this.toString();
   }

   public abstract static class Number extends NbtElement {
      protected Number() {
      }

      public abstract long getLong();

      public abstract int getInt();

      public abstract short getShort();

      public abstract byte getByte();

      public abstract double getDouble();

      public abstract float getFloat();
   }
}
