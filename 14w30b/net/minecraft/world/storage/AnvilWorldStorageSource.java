package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldData;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeSource;
import net.minecraft.world.biome.SingleBiomeSource;
import net.minecraft.world.chunk.storage.AlphaChunkStorage;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.chunk.storage.RegionIo;
import net.minecraft.world.gen.WorldGeneratorType;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilWorldStorageSource extends RegionWorldStorageSource {
   private static final Logger LOGGER = LogManager.getLogger();

   public AnvilWorldStorageSource(File file) {
      super(file);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public String getName() {
      return "Anvil";
   }

   @Environment(EnvType.CLIENT)
   @Override
   public List getAll() {
      if (this.dir != null && this.dir.exists() && this.dir.isDirectory()) {
         ArrayList var1 = Lists.newArrayList();
         File[] var2 = this.dir.listFiles();

         for(File var6 : var2) {
            if (var6.isDirectory()) {
               String var7 = var6.getName();
               WorldData var8 = this.getData(var7);
               if (var8 != null && (var8.getVersion() == 19132 || var8.getVersion() == 19133)) {
                  boolean var9 = var8.getVersion() != this.getVersion();
                  String var10 = var8.getName();
                  if (var10 == null || MathHelper.isEmpty(var10)) {
                     var10 = var7;
                  }

                  long var11 = 0L;
                  var1.add(
                     new WorldSaveInfo(var7, var10, var8.getLastPlayed(), var11, var8.getDefaultGamemode(), var9, var8.isHardcore(), var8.allowCommands())
                  );
               }
            }
         }

         return var1;
      } else {
         throw new WorldStorageException("Unable to read or access folder where game worlds are saved!");
      }
   }

   protected int getVersion() {
      return 19133;
   }

   @Override
   public void clearRegionIo() {
      RegionIo.clear();
   }

   @Override
   public WorldStorage get(String worldName, boolean createPlayerDataDir) {
      return new AnvilWorldStorage(this.dir, worldName, createPlayerDataDir);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean isConvertible(String worldName) {
      WorldData var2 = this.getData(worldName);
      return var2 != null && var2.getVersion() == 19132;
   }

   @Override
   public boolean needsConversion(String worldName) {
      WorldData var2 = this.getData(worldName);
      return var2 != null && var2.getVersion() != this.getVersion();
   }

   @Override
   public boolean convert(String worldName, ProgressListener listener) {
      listener.progressStagePercentage(0);
      ArrayList var3 = Lists.newArrayList();
      ArrayList var4 = Lists.newArrayList();
      ArrayList var5 = Lists.newArrayList();
      File var6 = new File(this.dir, worldName);
      File var7 = new File(var6, "DIM-1");
      File var8 = new File(var6, "DIM1");
      LOGGER.info("Scanning folders...");
      this.collectRegions(var6, var3);
      if (var7.exists()) {
         this.collectRegions(var7, var4);
      }

      if (var8.exists()) {
         this.collectRegions(var8, var5);
      }

      int var9 = var3.size() + var4.size() + var5.size();
      LOGGER.info("Total conversion count is " + var9);
      WorldData var10 = this.getData(worldName);
      Object var11 = null;
      if (var10.getGeneratorType() == WorldGeneratorType.FLAT) {
         var11 = new SingleBiomeSource(Biome.PLAINS, 0.5F);
      } else {
         var11 = new BiomeSource(var10.getSeed(), var10.getGeneratorType(), var10.getGeneratorOptions());
      }

      this.convertRegionsToAnvil(new File(var6, "region"), var3, (BiomeSource)var11, 0, var9, listener);
      this.convertRegionsToAnvil(new File(var7, "region"), var4, new SingleBiomeSource(Biome.HELL, 0.0F), var3.size(), var9, listener);
      this.convertRegionsToAnvil(new File(var8, "region"), var5, new SingleBiomeSource(Biome.THE_END, 0.0F), var3.size() + var4.size(), var9, listener);
      var10.setVersion(19133);
      if (var10.getGeneratorType() == WorldGeneratorType.DEFAULT_1_1) {
         var10.setGeneratorType(WorldGeneratorType.DEFAULT);
      }

      this.createLevelDatMcrBackup(worldName);
      WorldStorage var12 = this.get(worldName, false);
      var12.saveData(var10);
      return true;
   }

   private void createLevelDatMcrBackup(String worldName) {
      File var2 = new File(this.dir, worldName);
      if (!var2.exists()) {
         LOGGER.warn("Unable to create level.dat_mcr backup");
      } else {
         File var3 = new File(var2, "level.dat");
         if (!var3.exists()) {
            LOGGER.warn("Unable to create level.dat_mcr backup");
         } else {
            File var4 = new File(var2, "level.dat_mcr");
            if (!var3.renameTo(var4)) {
               LOGGER.warn("Unable to create level.dat_mcr backup");
            }
         }
      }
   }

   private void convertRegionsToAnvil(File dst, Iterable src, BiomeSource biomeSource, int startFileIndex, int endFileIndex, ProgressListener listener) {
      for(File var8 : src) {
         this.convertRegionToAnvil(dst, var8, biomeSource, startFileIndex, endFileIndex, listener);
         ++startFileIndex;
         int var9 = (int)Math.round(100.0 * (double)startFileIndex / (double)endFileIndex);
         listener.progressStagePercentage(var9);
      }
   }

   private void convertRegionToAnvil(File dst, File src, BiomeSource biomeSource, int startFileIndex, int endFileIndex, ProgressListener listener) {
      try {
         String var7 = src.getName();
         RegionFile var8 = new RegionFile(src);
         RegionFile var9 = new RegionFile(new File(dst, var7.substring(0, var7.length() - ".mcr".length()) + ".mca"));

         for(int var10 = 0; var10 < 32; ++var10) {
            for(int var11 = 0; var11 < 32; ++var11) {
               if (var8.hasChunkData(var10, var11) && !var9.hasChunkData(var10, var11)) {
                  DataInputStream var12 = var8.getChunkInputStream(var10, var11);
                  if (var12 == null) {
                     LOGGER.warn("Failed to fetch input stream");
                  } else {
                     NbtCompound var13 = NbtIo.read(var12);
                     var12.close();
                     NbtCompound var14 = var13.getCompound("Level");
                     AlphaChunkStorage.Chunk var15 = AlphaChunkStorage.load(var14);
                     NbtCompound var16 = new NbtCompound();
                     NbtCompound var17 = new NbtCompound();
                     var16.put("Level", var17);
                     AlphaChunkStorage.convertToAnvilFormat(var15, var17, biomeSource);
                     DataOutputStream var18 = var9.getChunkOutputStream(var10, var11);
                     NbtIo.write(var16, (DataOutput)var18);
                     var18.close();
                  }
               }
            }

            int var20 = (int)Math.round(100.0 * (double)(startFileIndex * 1024) / (double)(endFileIndex * 1024));
            int var21 = (int)Math.round(100.0 * (double)((var10 + 1) * 32 + startFileIndex * 1024) / (double)(endFileIndex * 1024));
            if (var21 > var20) {
               listener.progressStagePercentage(var21);
            }
         }

         var8.close();
         var9.close();
      } catch (IOException var19) {
         var19.printStackTrace();
      }
   }

   private void collectRegions(File dir, Collection files) {
      File var3 = new File(dir, "region");
      File[] var4 = var3.listFiles(new FilenameFilter() {
         @Override
         public boolean accept(File file, String filename) {
            return filename.endsWith(".mcr");
         }
      });
      if (var4 != null) {
         Collections.addAll(files, var4);
      }
   }
}
