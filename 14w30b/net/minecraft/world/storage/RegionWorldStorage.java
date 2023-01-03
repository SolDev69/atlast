package net.minecraft.world.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldData;
import net.minecraft.world.chunk.storage.ChunkStorage;
import net.minecraft.world.dimension.Dimension;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionWorldStorage implements WorldStorage, PlayerDataStorage {
   private static final Logger LOGGER = LogManager.getLogger();
   private final File dir;
   private final File playerDataDir;
   private final File dataDir;
   private final long startTime = MinecraftServer.getTimeMillis();
   private final String name;

   public RegionWorldStorage(File savesDir, String name, boolean createPlayerDataDir) {
      this.dir = new File(savesDir, name);
      this.dir.mkdirs();
      this.playerDataDir = new File(this.dir, "playerdata");
      this.dataDir = new File(this.dir, "data");
      this.dataDir.mkdirs();
      this.name = name;
      if (createPlayerDataDir) {
         this.playerDataDir.mkdirs();
      }

      this.writeSessionLock();
   }

   private void writeSessionLock() {
      try {
         File var1 = new File(this.dir, "session.lock");
         DataOutputStream var2 = new DataOutputStream(new FileOutputStream(var1));

         try {
            var2.writeLong(this.startTime);
         } finally {
            var2.close();
         }
      } catch (IOException var7) {
         var7.printStackTrace();
         throw new RuntimeException("Failed to check session lock, aborting");
      }
   }

   @Override
   public File getDir() {
      return this.dir;
   }

   @Override
   public void checkSessionLock() {
      try {
         File var1 = new File(this.dir, "session.lock");
         DataInputStream var2 = new DataInputStream(new FileInputStream(var1));

         try {
            if (var2.readLong() != this.startTime) {
               throw new net.minecraft.world.storage.exception.WorldStorageException("The save is being accessed from another location, aborting");
            }
         } finally {
            var2.close();
         }
      } catch (IOException var7) {
         throw new net.minecraft.world.storage.exception.WorldStorageException("Failed to check session lock, aborting");
      }
   }

   @Override
   public ChunkStorage getChunkStorage(Dimension dimension) {
      throw new RuntimeException("Old Chunk Storage is no longer supported.");
   }

   @Override
   public WorldData loadData() {
      File var1 = new File(this.dir, "level.dat");
      if (var1.exists()) {
         try {
            NbtCompound var7 = NbtIo.read(new FileInputStream(var1));
            NbtCompound var8 = var7.getCompound("Data");
            return new WorldData(var8);
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      }

      var1 = new File(this.dir, "level.dat_old");
      if (var1.exists()) {
         try {
            NbtCompound var2 = NbtIo.read(new FileInputStream(var1));
            NbtCompound var3 = var2.getCompound("Data");
            return new WorldData(var3);
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }

      return null;
   }

   @Override
   public void saveData(WorldData data, NbtCompound playerData) {
      NbtCompound var3 = data.toNbt(playerData);
      NbtCompound var4 = new NbtCompound();
      var4.put("Data", var3);

      try {
         File var5 = new File(this.dir, "level.dat_new");
         File var6 = new File(this.dir, "level.dat_old");
         File var7 = new File(this.dir, "level.dat");
         NbtIo.write(var4, new FileOutputStream(var5));
         if (var6.exists()) {
            var6.delete();
         }

         var7.renameTo(var6);
         if (var7.exists()) {
            var7.delete();
         }

         var5.renameTo(var7);
         if (var5.exists()) {
            var5.delete();
         }
      } catch (Exception var8) {
         var8.printStackTrace();
      }
   }

   @Override
   public void saveData(WorldData data) {
      NbtCompound var2 = data.toNbt();
      NbtCompound var3 = new NbtCompound();
      var3.put("Data", var2);

      try {
         File var4 = new File(this.dir, "level.dat_new");
         File var5 = new File(this.dir, "level.dat_old");
         File var6 = new File(this.dir, "level.dat");
         NbtIo.write(var3, new FileOutputStream(var4));
         if (var5.exists()) {
            var5.delete();
         }

         var6.renameTo(var5);
         if (var6.exists()) {
            var6.delete();
         }

         var4.renameTo(var6);
         if (var4.exists()) {
            var4.delete();
         }
      } catch (Exception var7) {
         var7.printStackTrace();
      }
   }

   @Override
   public void savePlayerData(PlayerEntity player) {
      try {
         NbtCompound var2 = new NbtCompound();
         player.writeEntityNbt(var2);
         File var3 = new File(this.playerDataDir, player.getUuid().toString() + ".dat.tmp");
         File var4 = new File(this.playerDataDir, player.getUuid().toString() + ".dat");
         NbtIo.write(var2, new FileOutputStream(var3));
         if (var4.exists()) {
            var4.delete();
         }

         var3.renameTo(var4);
      } catch (Exception var5) {
         LOGGER.warn("Failed to save player data for " + player.getName());
      }
   }

   @Override
   public NbtCompound loadPlayerData(PlayerEntity player) {
      NbtCompound var2 = null;

      try {
         File var3 = new File(this.playerDataDir, player.getUuid().toString() + ".dat");
         if (var3.exists() && var3.isFile()) {
            var2 = NbtIo.read(new FileInputStream(var3));
         }
      } catch (Exception var4) {
         LOGGER.warn("Failed to load player data for " + player.getName());
      }

      if (var2 != null) {
         player.readEntityNbt(var2);
      }

      return var2;
   }

   @Override
   public PlayerDataStorage getPlayerDataStorage() {
      return this;
   }

   @Override
   public String[] getSavedPlayerIds() {
      String[] var1 = this.playerDataDir.list();
      if (var1 == null) {
         var1 = new String[0];
      }

      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var1[var2].endsWith(".dat")) {
            var1[var2] = var1[var2].substring(0, var1[var2].length() - 4);
         }
      }

      return var1;
   }

   @Override
   public void waitIfSaving() {
   }

   @Override
   public File getDataFile(String name) {
      return new File(this.dataDir, name + ".dat");
   }

   @Override
   public String getName() {
      return this.name;
   }
}
