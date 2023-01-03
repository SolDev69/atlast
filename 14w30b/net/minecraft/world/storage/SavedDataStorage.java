package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtShort;
import net.minecraft.world.saved.SavedData;

public class SavedDataStorage {
   private WorldStorage storage;
   protected Map loadedDataById = Maps.newHashMap();
   private List loadedData = Lists.newArrayList();
   private Map idCounts = Maps.newHashMap();

   public SavedDataStorage(WorldStorage storage) {
      this.storage = storage;
      this.loadIdCounts();
   }

   public SavedData loadData(Class type, String id) {
      SavedData var3 = (SavedData)this.loadedDataById.get(id);
      if (var3 != null) {
         return var3;
      } else {
         if (this.storage != null) {
            try {
               File var4 = this.storage.getDataFile(id);
               if (var4 != null && var4.exists()) {
                  try {
                     var3 = (SavedData)type.getConstructor(String.class).newInstance(id);
                  } catch (Exception var7) {
                     throw new RuntimeException("Failed to instantiate " + type.toString(), var7);
                  }

                  FileInputStream var5 = new FileInputStream(var4);
                  NbtCompound var6 = NbtIo.read(var5);
                  var5.close();
                  var3.readNbt(var6.getCompound("data"));
               }
            } catch (Exception var8) {
               var8.printStackTrace();
            }
         }

         if (var3 != null) {
            this.loadedDataById.put(id, var3);
            this.loadedData.add(var3);
         }

         return var3;
      }
   }

   public void setData(String id, SavedData data) {
      if (this.loadedDataById.containsKey(id)) {
         this.loadedData.remove(this.loadedDataById.remove(id));
      }

      this.loadedDataById.put(id, data);
      this.loadedData.add(data);
   }

   public void save() {
      for(int var1 = 0; var1 < this.loadedData.size(); ++var1) {
         SavedData var2 = (SavedData)this.loadedData.get(var1);
         if (var2.isDirty()) {
            this.saveData(var2);
            var2.setDirty(false);
         }
      }
   }

   private void saveData(SavedData data) {
      if (this.storage != null) {
         try {
            File var2 = this.storage.getDataFile(data.id);
            if (var2 != null) {
               NbtCompound var3 = new NbtCompound();
               data.writeNbt(var3);
               NbtCompound var4 = new NbtCompound();
               var4.put("data", var3);
               FileOutputStream var5 = new FileOutputStream(var2);
               NbtIo.write(var4, var5);
               var5.close();
            }
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }
   }

   private void loadIdCounts() {
      try {
         this.idCounts.clear();
         if (this.storage == null) {
            return;
         }

         File var1 = this.storage.getDataFile("idcounts");
         if (var1 != null && var1.exists()) {
            DataInputStream var2 = new DataInputStream(new FileInputStream(var1));
            NbtCompound var3 = NbtIo.read(var2);
            var2.close();

            for(String var5 : var3.getKeys()) {
               NbtElement var6 = var3.get(var5);
               if (var6 instanceof NbtShort) {
                  NbtShort var7 = (NbtShort)var6;
                  short var9 = var7.getShort();
                  this.idCounts.put(var5, var9);
               }
            }
         }
      } catch (Exception var10) {
         var10.printStackTrace();
      }
   }

   public int getNextCount(String id) {
      Short var2 = (Short)this.idCounts.get(id);
      if (var2 == null) {
         var2 = (short)0;
      } else {
         var2 = (short)(var2 + 1);
      }

      this.idCounts.put(id, var2);
      if (this.storage == null) {
         return var2;
      } else {
         try {
            File var3 = this.storage.getDataFile("idcounts");
            if (var3 != null) {
               NbtCompound var4 = new NbtCompound();

               for(String var6 : this.idCounts.keySet()) {
                  short var7 = this.idCounts.get(var6);
                  var4.putShort(var6, var7);
               }

               DataOutputStream var10 = new DataOutputStream(new FileOutputStream(var3));
               NbtIo.write(var4, (DataOutput)var10);
               var10.close();
            }
         } catch (Exception var8) {
            var8.printStackTrace();
         }

         return var2;
      }
   }
}
