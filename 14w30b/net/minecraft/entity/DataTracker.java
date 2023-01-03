package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.lang3.ObjectUtils;

public class DataTracker {
   private final Entity entity;
   private boolean empty = true;
   private static final Map DATA_TYPES = Maps.newHashMap();
   private final Map entries = Maps.newHashMap();
   private boolean dirty;
   private ReadWriteLock lock = new ReentrantReadWriteLock();

   public DataTracker(Entity entity) {
      this.entity = entity;
   }

   public void put(int id, Object value) {
      Integer var3 = (Integer)DATA_TYPES.get(value.getClass());
      if (var3 == null) {
         throw new IllegalArgumentException("Unknown data type: " + value.getClass());
      } else if (id > 31) {
         throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + 31 + ")");
      } else if (this.entries.containsKey(id)) {
         throw new IllegalArgumentException("Duplicate id value for " + id + "!");
      } else {
         DataTracker.Entry var4 = new DataTracker.Entry(var3, id, value);
         this.lock.writeLock().lock();
         this.entries.put(id, var4);
         this.lock.writeLock().unlock();
         this.empty = false;
      }
   }

   public void add(int type, int id) {
      DataTracker.Entry var3 = new DataTracker.Entry(id, type, null);
      this.lock.writeLock().lock();
      this.entries.put(type, var3);
      this.lock.writeLock().unlock();
      this.empty = false;
   }

   public byte getByte(int id) {
      return this.get(id).getValue();
   }

   public short getShort(int id) {
      return this.get(id).getValue();
   }

   public int getInt(int id) {
      return this.get(id).getValue();
   }

   public float getFloat(int id) {
      return this.get(id).getValue();
   }

   public String getString(int id) {
      return (String)this.get(id).getValue();
   }

   public ItemStack getStack(int id) {
      return (ItemStack)this.get(id).getValue();
   }

   private DataTracker.Entry get(int id) {
      this.lock.readLock().lock();

      DataTracker.Entry var2;
      try {
         var2 = (DataTracker.Entry)this.entries.get(id);
      } catch (Throwable var6) {
         CrashReport var4 = CrashReport.of(var6, "Getting synched entity data");
         CashReportCategory var5 = var4.addCategory("Synched entity data");
         var5.add("Data ID", id);
         throw new CrashException(var4);
      }

      this.lock.readLock().unlock();
      return var2;
   }

   public void update(int id, Object value) {
      DataTracker.Entry var3 = this.get(id);
      if (ObjectUtils.notEqual(value, var3.getValue())) {
         var3.setValue(value);
         this.entity.onDataValueChanged(id);
         var3.setModified(true);
         this.dirty = true;
      }
   }

   public void markDirty(int id) {
      this.get(id).modified = true;
      this.dirty = true;
   }

   public boolean isDirty() {
      return this.dirty;
   }

   public static void write(List entries, PacketByteBuf buffer) {
      if (entries != null) {
         for(DataTracker.Entry var3 : entries) {
            write(buffer, var3);
         }
      }

      buffer.writeByte(127);
   }

   public List collectModifiedEntries() {
      ArrayList var1 = null;
      if (this.dirty) {
         this.lock.readLock().lock();

         for(DataTracker.Entry var3 : this.entries.values()) {
            if (var3.isModified()) {
               var3.setModified(false);
               if (var1 == null) {
                  var1 = Lists.newArrayList();
               }

               var1.add(var3);
            }
         }

         this.lock.readLock().unlock();
      }

      this.dirty = false;
      return var1;
   }

   public void write(PacketByteBuf buffer) {
      this.lock.readLock().lock();

      for(DataTracker.Entry var3 : this.entries.values()) {
         write(buffer, var3);
      }

      this.lock.readLock().unlock();
      buffer.writeByte(127);
   }

   public List collectEntries() {
      ArrayList var1 = null;
      this.lock.readLock().lock();

      for(DataTracker.Entry var3 : this.entries.values()) {
         if (var1 == null) {
            var1 = Lists.newArrayList();
         }

         var1.add(var3);
      }

      this.lock.readLock().unlock();
      return var1;
   }

   private static void write(PacketByteBuf buffer, DataTracker.Entry entry) {
      int var2 = (entry.getType() << 5 | entry.getId() & 31) & 0xFF;
      buffer.writeByte(var2);
      switch(entry.getType()) {
         case 0:
            buffer.writeByte(entry.getValue());
            break;
         case 1:
            buffer.writeShort(entry.getValue());
            break;
         case 2:
            buffer.writeInt(entry.getValue());
            break;
         case 3:
            buffer.writeFloat(entry.getValue());
            break;
         case 4:
            buffer.writeString((String)entry.getValue());
            break;
         case 5:
            ItemStack var4 = (ItemStack)entry.getValue();
            buffer.writeItemStack(var4);
            break;
         case 6:
            BlockPos var3 = (BlockPos)entry.getValue();
            buffer.writeInt(var3.getX());
            buffer.writeInt(var3.getY());
            buffer.writeInt(var3.getZ());
      }
   }

   public static List read(PacketByteBuf buffer) {
      ArrayList var1 = null;

      for(byte var2 = buffer.readByte(); var2 != 127; var2 = buffer.readByte()) {
         if (var1 == null) {
            var1 = Lists.newArrayList();
         }

         int var3 = (var2 & 224) >> 5;
         int var4 = var2 & 31;
         DataTracker.Entry var5 = null;
         switch(var3) {
            case 0:
               var5 = new DataTracker.Entry(var3, var4, buffer.readByte());
               break;
            case 1:
               var5 = new DataTracker.Entry(var3, var4, buffer.readShort());
               break;
            case 2:
               var5 = new DataTracker.Entry(var3, var4, buffer.readInt());
               break;
            case 3:
               var5 = new DataTracker.Entry(var3, var4, buffer.readFloat());
               break;
            case 4:
               var5 = new DataTracker.Entry(var3, var4, buffer.readString(32767));
               break;
            case 5:
               var5 = new DataTracker.Entry(var3, var4, buffer.readItemStack());
               break;
            case 6:
               int var6 = buffer.readInt();
               int var7 = buffer.readInt();
               int var8 = buffer.readInt();
               var5 = new DataTracker.Entry(var3, var4, new BlockPos(var6, var7, var8));
         }

         var1.add(var5);
      }

      return var1;
   }

   @Environment(EnvType.CLIENT)
   public void update(List entries) {
      this.lock.writeLock().lock();

      for(DataTracker.Entry var3 : entries) {
         DataTracker.Entry var4 = (DataTracker.Entry)this.entries.get(var3.getId());
         if (var4 != null) {
            var4.setValue(var3.getValue());
            this.entity.onDataValueChanged(var3.getId());
         }
      }

      this.lock.writeLock().unlock();
      this.dirty = true;
   }

   public boolean isEmpty() {
      return this.empty;
   }

   public void markClean() {
      this.dirty = false;
   }

   static {
      DATA_TYPES.put(Byte.class, 0);
      DATA_TYPES.put(Short.class, 1);
      DATA_TYPES.put(Integer.class, 2);
      DATA_TYPES.put(Float.class, 3);
      DATA_TYPES.put(String.class, 4);
      DATA_TYPES.put(ItemStack.class, 5);
      DATA_TYPES.put(BlockPos.class, 6);
   }

   public static class Entry {
      private final int type;
      private final int id;
      private Object value;
      private boolean modified;

      public Entry(int type, int id, Object value) {
         this.id = id;
         this.value = value;
         this.type = type;
         this.modified = true;
      }

      public int getId() {
         return this.id;
      }

      public void setValue(Object value) {
         this.value = value;
      }

      public Object getValue() {
         return this.value;
      }

      public int getType() {
         return this.type;
      }

      public boolean isModified() {
         return this.modified;
      }

      public void setModified(boolean modified) {
         this.modified = modified;
      }
   }
}
