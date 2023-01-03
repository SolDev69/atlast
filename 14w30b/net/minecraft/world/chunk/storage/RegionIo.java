package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class RegionIo {
   private static final Map REGION_FILES = Maps.newHashMap();

   public static synchronized RegionFile getRegionFile(File worldDir, int chunkX, int chunkZ) {
      File var3 = new File(worldDir, "region");
      File var4 = new File(var3, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + ".mca");
      RegionFile var5 = (RegionFile)REGION_FILES.get(var4);
      if (var5 != null) {
         return var5;
      } else {
         if (!var3.exists()) {
            var3.mkdirs();
         }

         if (REGION_FILES.size() >= 256) {
            clear();
         }

         RegionFile var6 = new RegionFile(var4);
         REGION_FILES.put(var4, var6);
         return var6;
      }
   }

   public static synchronized void clear() {
      for(RegionFile var1 : REGION_FILES.values()) {
         try {
            if (var1 != null) {
               var1.close();
            }
         } catch (IOException var3) {
            var3.printStackTrace();
         }
      }

      REGION_FILES.clear();
   }

   public static DataInputStream getChunkInputStream(File worldDir, int chunkX, int chunkZ) {
      RegionFile var3 = getRegionFile(worldDir, chunkX, chunkZ);
      return var3.getChunkInputStream(chunkX & 31, chunkZ & 31);
   }

   public static DataOutputStream getChunkOutputStream(File worldDir, int chunkX, int chunkZ) {
      RegionFile var3 = getRegionFile(worldDir, chunkX, chunkZ);
      return var3.getChunkOutputStream(chunkX & 31, chunkZ & 31);
   }
}
