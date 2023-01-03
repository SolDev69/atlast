package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Long2ObjectHashMap;

public class BiomeCache {
   private final BiomeSource source;
   private long timeOfLastCleanUp;
   private Long2ObjectHashMap entriesByChunkPos = new Long2ObjectHashMap();
   private List entries = Lists.newArrayList();

   public BiomeCache(BiomeSource source) {
      this.source = source;
   }

   public BiomeCache.Entry getEntry(int x, int z) {
      x >>= 4;
      z >>= 4;
      long var3 = (long)x & 4294967295L | ((long)z & 4294967295L) << 32;
      BiomeCache.Entry var5 = (BiomeCache.Entry)this.entriesByChunkPos.get(var3);
      if (var5 == null) {
         var5 = new BiomeCache.Entry(x, z);
         this.entriesByChunkPos.put(var3, var5);
         this.entries.add(var5);
      }

      var5.timeOfCreation = MinecraftServer.getTimeMillis();
      return var5;
   }

   public Biome getBiomeOfDefault(int x, int z, Biome defaultValue) {
      Biome var4 = this.getEntry(x, z).getBiome(x, z);
      return var4 == null ? defaultValue : var4;
   }

   public void cleanUp() {
      long var1 = MinecraftServer.getTimeMillis();
      long var3 = var1 - this.timeOfLastCleanUp;
      if (var3 > 7500L || var3 < 0L) {
         this.timeOfLastCleanUp = var1;

         for(int var5 = 0; var5 < this.entries.size(); ++var5) {
            BiomeCache.Entry var6 = (BiomeCache.Entry)this.entries.get(var5);
            long var7 = var1 - var6.timeOfCreation;
            if (var7 > 30000L || var7 < 0L) {
               this.entries.remove(var5--);
               long var9 = (long)var6.chunkX & 4294967295L | ((long)var6.chunkZ & 4294967295L) << 32;
               this.entriesByChunkPos.remove(var9);
            }
         }
      }
   }

   public Biome[] getBiomesInChunk(int x, int z) {
      return this.getEntry(x, z).biomes;
   }

   public class Entry {
      public float[] downfall = new float[256];
      public Biome[] biomes = new Biome[256];
      public int chunkX;
      public int chunkZ;
      public long timeOfCreation;

      public Entry(int chunkX, int chunkZ) {
         this.chunkX = chunkX;
         this.chunkZ = chunkZ;
         BiomeCache.this.source.getDownfall(this.downfall, chunkX << 4, chunkZ << 4, 16, 16);
         BiomeCache.this.source.getBiomes(this.biomes, chunkX << 4, chunkZ << 4, 16, 16, false);
      }

      public Biome getBiome(int x, int z) {
         return this.biomes[x & 15 | (z & 15) << 4];
      }
   }
}
