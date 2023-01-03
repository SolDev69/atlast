package net.minecraft.nbt;

import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NbtList extends NbtElement {
   private static final Logger LOGGER = LogManager.getLogger();
   private List elements = Lists.newArrayList();
   private byte type = 0;

   @Override
   void write(DataOutput output) {
      if (!this.elements.isEmpty()) {
         this.type = ((NbtElement)this.elements.get(0)).getType();
      } else {
         this.type = 0;
      }

      output.writeByte(this.type);
      output.writeInt(this.elements.size());

      for(int var2 = 0; var2 < this.elements.size(); ++var2) {
         ((NbtElement)this.elements.get(var2)).write(output);
      }
   }

   @Override
   void read(DataInput input, int depth, NbtReadLimiter limiter) {
      if (depth > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      } else {
         limiter.read(8L);
         this.type = input.readByte();
         int var4 = input.readInt();
         this.elements = Lists.newArrayList();

         for(int var5 = 0; var5 < var4; ++var5) {
            NbtElement var6 = NbtElement.create(this.type);
            var6.read(input, depth + 1, limiter);
            this.elements.add(var6);
         }
      }
   }

   @Override
   public byte getType() {
      return 9;
   }

   @Override
   public String toString() {
      String var1 = "[";
      int var2 = 0;

      for(NbtElement var4 : this.elements) {
         var1 = var1 + "" + var2 + ':' + var4 + ',';
         ++var2;
      }

      return var1 + "]";
   }

   public void add(NbtElement element) {
      if (this.type == 0) {
         this.type = element.getType();
      } else if (this.type != element.getType()) {
         LOGGER.warn("Adding mismatching tag types to tag list");
         return;
      }

      this.elements.add(element);
   }

   public void set(int index, NbtElement element) {
      if (index >= 0 && index < this.elements.size()) {
         if (this.type == 0) {
            this.type = element.getType();
         } else if (this.type != element.getType()) {
            LOGGER.warn("Adding mismatching tag types to tag list");
            return;
         }

         this.elements.set(index, element);
      } else {
         LOGGER.warn("index out of bounds to set tag in tag list");
      }
   }

   public NbtElement remove(int index) {
      return (NbtElement)this.elements.remove(index);
   }

   @Override
   public boolean isEmpty() {
      return this.elements.isEmpty();
   }

   public NbtCompound getCompound(int index) {
      if (index >= 0 && index < this.elements.size()) {
         NbtElement var2 = (NbtElement)this.elements.get(index);
         return var2.getType() == 10 ? (NbtCompound)var2 : new NbtCompound();
      } else {
         return new NbtCompound();
      }
   }

   public int[] getIntArray(int index) {
      if (index >= 0 && index < this.elements.size()) {
         NbtElement var2 = (NbtElement)this.elements.get(index);
         return var2.getType() == 11 ? ((NbtIntArray)var2).getIntArray() : new int[0];
      } else {
         return new int[0];
      }
   }

   public double getDouble(int index) {
      if (index >= 0 && index < this.elements.size()) {
         NbtElement var2 = (NbtElement)this.elements.get(index);
         return var2.getType() == 6 ? ((NbtDouble)var2).getDouble() : 0.0;
      } else {
         return 0.0;
      }
   }

   public float getFloat(int index) {
      if (index >= 0 && index < this.elements.size()) {
         NbtElement var2 = (NbtElement)this.elements.get(index);
         return var2.getType() == 5 ? ((NbtFloat)var2).getFloat() : 0.0F;
      } else {
         return 0.0F;
      }
   }

   public String getString(int index) {
      if (index >= 0 && index < this.elements.size()) {
         NbtElement var2 = (NbtElement)this.elements.get(index);
         return var2.getType() == 8 ? var2.asString() : var2.toString();
      } else {
         return "";
      }
   }

   public NbtElement get(int index) {
      return (NbtElement)(index >= 0 && index < this.elements.size() ? (NbtElement)this.elements.get(index) : new NbtEnd());
   }

   public int size() {
      return this.elements.size();
   }

   @Override
   public NbtElement copy() {
      NbtList var1 = new NbtList();
      var1.type = this.type;

      for(NbtElement var3 : this.elements) {
         NbtElement var4 = var3.copy();
         var1.elements.add(var4);
      }

      return var1;
   }

   @Override
   public boolean equals(Object object) {
      if (super.equals(object)) {
         NbtList var2 = (NbtList)object;
         if (this.type == var2.type) {
            return this.elements.equals(var2.elements);
         }
      }

      return false;
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.elements.hashCode();
   }

   public int getElementType() {
      return this.type;
   }
}
