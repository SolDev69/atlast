package net.minecraft.nbt;

import com.google.common.collect.Maps;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NbtCompound extends NbtElement {
   private static final Logger LOGGER = LogManager.getLogger();
   private Map elements = Maps.newHashMap();

   @Override
   void write(DataOutput output) {
      for(String var3 : this.elements.keySet()) {
         NbtElement var4 = (NbtElement)this.elements.get(var3);
         writeElement(var3, var4, output);
      }

      output.writeByte(0);
   }

   @Override
   void read(DataInput input, int depth, NbtReadLimiter limiter) {
      if (depth > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      } else {
         this.elements.clear();

         byte var4;
         while((var4 = readByte(input, limiter)) != false) {
            String var5 = readString(input, limiter);
            limiter.read((long)(16 * var5.length()));
            NbtElement var6 = createElement(var4, var5, input, depth + 1, limiter);
            this.elements.put(var5, var6);
         }
      }
   }

   public Set getKeys() {
      return this.elements.keySet();
   }

   @Override
   public byte getType() {
      return 10;
   }

   public void put(String key, NbtElement element) {
      this.elements.put(key, element);
   }

   public void putByte(String key, byte value) {
      this.elements.put(key, new NbtByte(value));
   }

   public void putShort(String key, short value) {
      this.elements.put(key, new NbtShort(value));
   }

   public void putInt(String key, int value) {
      this.elements.put(key, new NbtInt(value));
   }

   public void putLong(String key, long value) {
      this.elements.put(key, new NbtLong(value));
   }

   public void putFloat(String key, float value) {
      this.elements.put(key, new NbtFloat(value));
   }

   public void putDouble(String key, double value) {
      this.elements.put(key, new NbtDouble(value));
   }

   public void putString(String key, String value) {
      this.elements.put(key, new NbtString(value));
   }

   public void putByteArray(String key, byte[] value) {
      this.elements.put(key, new NbtByteArray(value));
   }

   public void putIntArray(String key, int[] value) {
      this.elements.put(key, new NbtIntArray(value));
   }

   public void putBoolean(String key, boolean value) {
      this.putByte(key, (byte)(value ? 1 : 0));
   }

   public NbtElement get(String key) {
      return (NbtElement)this.elements.get(key);
   }

   public byte getType(String key) {
      NbtElement var2 = (NbtElement)this.elements.get(key);
      return var2 != null ? var2.getType() : 0;
   }

   public boolean contains(String key) {
      return this.elements.containsKey(key);
   }

   public boolean isType(String key, int type) {
      byte var3 = this.getType(key);
      if (var3 == type) {
         return true;
      } else if (type != 99) {
         if (var3 > 0) {
         }

         return false;
      } else {
         return var3 == 1 || var3 == 2 || var3 == 3 || var3 == 4 || var3 == 5 || var3 == 6;
      }
   }

   public byte getByte(String key) {
      try {
         return !this.isType(key, 99) ? 0 : ((NbtElement.Number)this.elements.get(key)).getByte();
      } catch (ClassCastException var3) {
         return 0;
      }
   }

   public short getShort(String key) {
      try {
         return !this.isType(key, 99) ? 0 : ((NbtElement.Number)this.elements.get(key)).getShort();
      } catch (ClassCastException var3) {
         return 0;
      }
   }

   public int getInt(String key) {
      try {
         return !this.isType(key, 99) ? 0 : ((NbtElement.Number)this.elements.get(key)).getInt();
      } catch (ClassCastException var3) {
         return 0;
      }
   }

   public long getLong(String key) {
      try {
         return !this.isType(key, 99) ? 0L : ((NbtElement.Number)this.elements.get(key)).getLong();
      } catch (ClassCastException var3) {
         return 0L;
      }
   }

   public float getFloat(String key) {
      try {
         return !this.isType(key, 99) ? 0.0F : ((NbtElement.Number)this.elements.get(key)).getFloat();
      } catch (ClassCastException var3) {
         return 0.0F;
      }
   }

   public double getDouble(String key) {
      try {
         return !this.isType(key, 99) ? 0.0 : ((NbtElement.Number)this.elements.get(key)).getDouble();
      } catch (ClassCastException var3) {
         return 0.0;
      }
   }

   public String getString(String key) {
      try {
         return !this.isType(key, 8) ? "" : ((NbtElement)this.elements.get(key)).asString();
      } catch (ClassCastException var3) {
         return "";
      }
   }

   public byte[] getByteArray(String key) {
      try {
         return !this.isType(key, 7) ? new byte[0] : ((NbtByteArray)this.elements.get(key)).getByteArray();
      } catch (ClassCastException var3) {
         throw new CrashException(this.createCrashReport(key, 7, var3));
      }
   }

   public int[] getIntArray(String key) {
      try {
         return !this.isType(key, 11) ? new int[0] : ((NbtIntArray)this.elements.get(key)).getIntArray();
      } catch (ClassCastException var3) {
         throw new CrashException(this.createCrashReport(key, 11, var3));
      }
   }

   public NbtCompound getCompound(String key) {
      try {
         return !this.isType(key, 10) ? new NbtCompound() : (NbtCompound)this.elements.get(key);
      } catch (ClassCastException var3) {
         throw new CrashException(this.createCrashReport(key, 10, var3));
      }
   }

   public NbtList getList(String key, int type) {
      try {
         if (this.getType(key) != 9) {
            return new NbtList();
         } else {
            NbtList var3 = (NbtList)this.elements.get(key);
            return var3.size() > 0 && var3.getElementType() != type ? new NbtList() : var3;
         }
      } catch (ClassCastException var4) {
         throw new CrashException(this.createCrashReport(key, 9, var4));
      }
   }

   public boolean getBoolean(String key) {
      return this.getByte(key) != 0;
   }

   public void remove(String key) {
      this.elements.remove(key);
   }

   @Override
   public String toString() {
      String var1 = "{";

      for(String var3 : this.elements.keySet()) {
         var1 = var1 + var3 + ':' + this.elements.get(var3) + ',';
      }

      return var1 + "}";
   }

   @Override
   public boolean isEmpty() {
      return this.elements.isEmpty();
   }

   private CrashReport createCrashReport(String key, int expectedType, ClassCastException exception) {
      CrashReport var4 = CrashReport.of(exception, "Reading NBT data");
      CashReportCategory var5 = var4.addCategory("Corrupt NBT tag", 1);
      var5.add("Tag type found", new Callable() {
         public String call() {
            return NbtElement.TYPE_NAMES[((NbtElement)NbtCompound.this.elements.get(key)).getType()];
         }
      });
      var5.add("Tag type expected", new Callable() {
         public String call() {
            return NbtElement.TYPE_NAMES[expectedType];
         }
      });
      var5.add("Tag name", key);
      return var4;
   }

   @Override
   public NbtElement copy() {
      NbtCompound var1 = new NbtCompound();

      for(String var3 : this.elements.keySet()) {
         var1.put(var3, ((NbtElement)this.elements.get(var3)).copy());
      }

      return var1;
   }

   @Override
   public boolean equals(Object object) {
      if (super.equals(object)) {
         NbtCompound var2 = (NbtCompound)object;
         return this.elements.entrySet().equals(var2.elements.entrySet());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.elements.hashCode();
   }

   private static void writeElement(String key, NbtElement element, DataOutput output) {
      output.writeByte(element.getType());
      if (element.getType() != 0) {
         output.writeUTF(key);
         element.write(output);
      }
   }

   private static byte readByte(DataInput input, NbtReadLimiter limiter) {
      return input.readByte();
   }

   private static String readString(DataInput input, NbtReadLimiter limiter) {
      return input.readUTF();
   }

   static NbtElement createElement(byte type, String key, DataInput input, int depth, NbtReadLimiter limiter) {
      NbtElement var5 = NbtElement.create(type);

      try {
         var5.read(input, depth, limiter);
         return var5;
      } catch (IOException var9) {
         CrashReport var7 = CrashReport.of(var9, "Loading NBT data");
         CashReportCategory var8 = var7.addCategory("NBT Tag");
         var8.add("Tag name", key);
         var8.add("Tag type", type);
         throw new CrashException(var7);
      }
   }

   public void merge(NbtCompound nbt) {
      for(String var3 : nbt.elements.keySet()) {
         NbtElement var4 = (NbtElement)nbt.elements.get(var3);
         if (var4.getType() == 10) {
            if (this.isType(var3, 10)) {
               NbtCompound var5 = this.getCompound(var3);
               var5.merge((NbtCompound)var4);
            } else {
               this.put(var3, var4.copy());
            }
         } else {
            this.put(var3, var4.copy());
         }
      }
   }
}
