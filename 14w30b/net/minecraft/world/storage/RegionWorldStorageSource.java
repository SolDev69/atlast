package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.WorldData;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionWorldStorageSource implements WorldStorageSource {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final File dir;

   public RegionWorldStorageSource(File dir) {
      if (!dir.exists()) {
         dir.mkdirs();
      }

      this.dir = dir;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public String getName() {
      return "Old Format";
   }

   @Environment(EnvType.CLIENT)
   @Override
   public List getAll() {
      ArrayList var1 = Lists.newArrayList();

      for(int var2 = 0; var2 < 5; ++var2) {
         String var3 = "World" + (var2 + 1);
         WorldData var4 = this.getData(var3);
         if (var4 != null) {
            var1.add(
               new WorldSaveInfo(
                  var3, "", var4.getLastPlayed(), var4.getSizeOnDisk(), var4.getDefaultGamemode(), false, var4.isHardcore(), var4.allowCommands()
               )
            );
         }
      }

      return var1;
   }

   @Override
   public void clearRegionIo() {
   }

   @Override
   public WorldData getData(String worldName) {
      File var2 = new File(this.dir, worldName);
      if (!var2.exists()) {
         return null;
      } else {
         File var3 = new File(var2, "level.dat");
         if (var3.exists()) {
            try {
               NbtCompound var9 = NbtIo.read(new FileInputStream(var3));
               NbtCompound var10 = var9.getCompound("Data");
               return new WorldData(var10);
            } catch (Exception var7) {
               LOGGER.error("Exception reading " + var3, var7);
            }
         }

         var3 = new File(var2, "level.dat_old");
         if (var3.exists()) {
            try {
               NbtCompound var4 = NbtIo.read(new FileInputStream(var3));
               NbtCompound var5 = var4.getCompound("Data");
               return new WorldData(var5);
            } catch (Exception var6) {
               LOGGER.error("Exception reading " + var3, var6);
            }
         }

         return null;
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void rename(String worldName, String newName) {
      File var3 = new File(this.dir, worldName);
      if (var3.exists()) {
         File var4 = new File(var3, "level.dat");
         if (var4.exists()) {
            try {
               NbtCompound var5 = NbtIo.read(new FileInputStream(var4));
               NbtCompound var6 = var5.getCompound("Data");
               var6.putString("LevelName", newName);
               NbtIo.write(var5, new FileOutputStream(var4));
            } catch (Exception var7) {
               var7.printStackTrace();
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean canCreate(String worldName) {
      File var2 = new File(this.dir, worldName);
      if (var2.exists()) {
         return false;
      } else {
         try {
            var2.mkdir();
            var2.delete();
            return true;
         } catch (Throwable var4) {
            LOGGER.warn("Couldn't make new level", var4);
            return false;
         }
      }
   }

   @Override
   public boolean delete(String worldName) {
      File var2 = new File(this.dir, worldName);
      if (!var2.exists()) {
         return true;
      } else {
         LOGGER.info("Deleting level " + worldName);

         for(int var3 = 1; var3 <= 5; ++var3) {
            LOGGER.info("Attempt " + var3 + "...");
            if (deleteFilesAndDirs(var2.listFiles())) {
               break;
            }

            LOGGER.warn("Unsuccessful in deleting contents.");
            if (var3 < 5) {
               try {
                  Thread.sleep(500L);
               } catch (InterruptedException var5) {
               }
            }
         }

         return var2.delete();
      }
   }

   protected static boolean deleteFilesAndDirs(File[] files) {
      for(int var1 = 0; var1 < files.length; ++var1) {
         File var2 = files[var1];
         LOGGER.debug("Deleting " + var2);
         if (var2.isDirectory() && !deleteFilesAndDirs(var2.listFiles())) {
            LOGGER.warn("Couldn't delete directory " + var2);
            return false;
         }

         if (!var2.delete()) {
            LOGGER.warn("Couldn't delete file " + var2);
            return false;
         }
      }

      return true;
   }

   @Override
   public WorldStorage get(String worldName, boolean createPlayerDataDir) {
      return new RegionWorldStorage(this.dir, worldName, createPlayerDataDir);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean isConvertible(String worldName) {
      return false;
   }

   @Override
   public boolean needsConversion(String worldName) {
      return false;
   }

   @Override
   public boolean convert(String worldName, ProgressListener listener) {
      return false;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean exists(String worldName) {
      File var2 = new File(this.dir, worldName);
      return var2.isDirectory();
   }
}
