package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeSource;
import net.minecraft.world.chunk.ChunkNibbleStorage;

public class AlphaChunkStorage {
   public static AlphaChunkStorage.Chunk load(NbtCompound nbt) {
      int var1 = nbt.getInt("xPos");
      int var2 = nbt.getInt("zPos");
      AlphaChunkStorage.Chunk var3 = new AlphaChunkStorage.Chunk(var1, var2);
      var3.blocks = nbt.getByteArray("Blocks");
      var3.data = new AlphaChunkDataArray(nbt.getByteArray("Data"), 7);
      var3.skyLight = new AlphaChunkDataArray(nbt.getByteArray("SkyLight"), 7);
      var3.blockLight = new AlphaChunkDataArray(nbt.getByteArray("BlockLight"), 7);
      var3.heightMap = nbt.getByteArray("HeightMap");
      var3.terrainPopulated = nbt.getBoolean("TerrainPopulated");
      var3.entities = nbt.getList("Entities", 10);
      var3.tileEntities = nbt.getList("TileEntities", 10);
      var3.tileTicks = nbt.getList("TileTicks", 10);

      try {
         var3.lastUpdate = nbt.getLong("LastUpdate");
      } catch (ClassCastException var5) {
         var3.lastUpdate = (long)nbt.getInt("LastUpdate");
      }

      return var3;
   }

   public static void convertToAnvilFormat(AlphaChunkStorage.Chunk chunk, NbtCompound nbt, BiomeSource biomeSource) {
      nbt.putInt("xPos", chunk.chunkX);
      nbt.putInt("zPos", chunk.chunkZ);
      nbt.putLong("LastUpdate", chunk.lastUpdate);
      int[] var3 = new int[chunk.heightMap.length];

      for(int var4 = 0; var4 < chunk.heightMap.length; ++var4) {
         var3[var4] = chunk.heightMap[var4];
      }

      nbt.putIntArray("HeightMap", var3);
      nbt.putBoolean("TerrainPopulated", chunk.terrainPopulated);
      NbtList var16 = new NbtList();

      for(int var5 = 0; var5 < 8; ++var5) {
         boolean var6 = true;

         for(int var7 = 0; var7 < 16 && var6; ++var7) {
            for(int var8 = 0; var8 < 16 && var6; ++var8) {
               for(int var9 = 0; var9 < 16; ++var9) {
                  int var10 = var7 << 11 | var9 << 7 | var8 + (var5 << 4);
                  byte var11 = chunk.blocks[var10];
                  if (var11 != 0) {
                     var6 = false;
                     break;
                  }
               }
            }
         }

         if (!var6) {
            byte[] var19 = new byte[4096];
            ChunkNibbleStorage var21 = new ChunkNibbleStorage(var19.length, 4);
            ChunkNibbleStorage var22 = new ChunkNibbleStorage(var19.length, 4);
            ChunkNibbleStorage var23 = new ChunkNibbleStorage(var19.length, 4);

            for(int var24 = 0; var24 < 16; ++var24) {
               for(int var12 = 0; var12 < 16; ++var12) {
                  for(int var13 = 0; var13 < 16; ++var13) {
                     int var14 = var24 << 11 | var13 << 7 | var12 + (var5 << 4);
                     byte var15 = chunk.blocks[var14];
                     var19[var12 << 8 | var13 << 4 | var24] = (byte)(var15 & 255);
                     var21.set(var24, var12, var13, chunk.data.get(var24, var12 + (var5 << 4), var13));
                     var22.set(var24, var12, var13, chunk.skyLight.get(var24, var12 + (var5 << 4), var13));
                     var23.set(var24, var12, var13, chunk.blockLight.get(var24, var12 + (var5 << 4), var13));
                  }
               }
            }

            NbtCompound var25 = new NbtCompound();
            var25.putByte("Y", (byte)(var5 & 0xFF));
            var25.putByteArray("Blocks", var19);
            var25.putByteArray("Data", var21.getData());
            var25.putByteArray("SkyLight", var22.getData());
            var25.putByteArray("BlockLight", var23.getData());
            var16.add(var25);
         }
      }

      nbt.put("Sections", var16);
      byte[] var17 = new byte[256];

      for(int var18 = 0; var18 < 16; ++var18) {
         for(int var20 = 0; var20 < 16; ++var20) {
            var17[var20 << 4 | var18] = (byte)(
               biomeSource.getBiomeOrDefault(new BlockPos(chunk.chunkX << 4 | var18, 0, chunk.chunkZ << 4 | var20), Biome.DEFAULT).id & 0xFF
            );
         }
      }

      nbt.putByteArray("Biomes", var17);
      nbt.put("Entities", chunk.entities);
      nbt.put("TileEntities", chunk.tileEntities);
      if (chunk.tileTicks != null) {
         nbt.put("TileTicks", chunk.tileTicks);
      }
   }

   public static class Chunk {
      public long lastUpdate;
      public boolean terrainPopulated;
      public byte[] heightMap;
      public AlphaChunkDataArray blockLight;
      public AlphaChunkDataArray skyLight;
      public AlphaChunkDataArray data;
      public byte[] blocks;
      public NbtList entities;
      public NbtList tileEntities;
      public NbtList tileTicks;
      public final int chunkX;
      public final int chunkZ;

      public Chunk(int chunkX, int chunkZ) {
         this.chunkX = chunkX;
         this.chunkZ = chunkZ;
      }
   }
}
