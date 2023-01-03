package net.minecraft.world.gen.chunk;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.mob.MobSpawnGroup;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.BlockStateStorage;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.noise.OctaveNoiseGenerator;

public class TheEndChunkGenerator implements ChunkSource {
   private Random random;
   private OctaveNoiseGenerator minLimitPerlinNoise;
   private OctaveNoiseGenerator maxLimitPerlinNoise;
   private OctaveNoiseGenerator mainPerlinNoise;
   public OctaveNoiseGenerator scaleNoiseGenerator;
   public OctaveNoiseGenerator depthNoiseGenerator;
   private World world;
   private double[] heightMap;
   private Biome[] biomes;
   double[] mainNoiseBuffer;
   double[] minLimitNoiseBuffer;
   double[] maxLimitNoiseBuffer;
   double[] scaleNoiseBuffer;
   double[] depthNoiseBuffer;

   public TheEndChunkGenerator(World world, long seed) {
      this.world = world;
      this.random = new Random(seed);
      this.minLimitPerlinNoise = new OctaveNoiseGenerator(this.random, 16);
      this.maxLimitPerlinNoise = new OctaveNoiseGenerator(this.random, 16);
      this.mainPerlinNoise = new OctaveNoiseGenerator(this.random, 8);
      this.scaleNoiseGenerator = new OctaveNoiseGenerator(this.random, 10);
      this.depthNoiseGenerator = new OctaveNoiseGenerator(this.random, 16);
   }

   public void generate(int chunkX, int chunkZ, BlockStateStorage blocks) {
      byte var4 = 2;
      int var5 = var4 + 1;
      byte var6 = 33;
      int var7 = var4 + 1;
      this.heightMap = this.populateHeightMap(this.heightMap, chunkX * var4, 0, chunkZ * var4, var5, var6, var7);

      for(int var8 = 0; var8 < var4; ++var8) {
         for(int var9 = 0; var9 < var4; ++var9) {
            for(int var10 = 0; var10 < 32; ++var10) {
               double var11 = 0.25;
               double var13 = this.heightMap[((var8 + 0) * var7 + var9 + 0) * var6 + var10 + 0];
               double var15 = this.heightMap[((var8 + 0) * var7 + var9 + 1) * var6 + var10 + 0];
               double var17 = this.heightMap[((var8 + 1) * var7 + var9 + 0) * var6 + var10 + 0];
               double var19 = this.heightMap[((var8 + 1) * var7 + var9 + 1) * var6 + var10 + 0];
               double var21 = (this.heightMap[((var8 + 0) * var7 + var9 + 0) * var6 + var10 + 1] - var13) * var11;
               double var23 = (this.heightMap[((var8 + 0) * var7 + var9 + 1) * var6 + var10 + 1] - var15) * var11;
               double var25 = (this.heightMap[((var8 + 1) * var7 + var9 + 0) * var6 + var10 + 1] - var17) * var11;
               double var27 = (this.heightMap[((var8 + 1) * var7 + var9 + 1) * var6 + var10 + 1] - var19) * var11;

               for(int var29 = 0; var29 < 4; ++var29) {
                  double var30 = 0.125;
                  double var32 = var13;
                  double var34 = var15;
                  double var36 = (var17 - var13) * var30;
                  double var38 = (var19 - var15) * var30;

                  for(int var40 = 0; var40 < 8; ++var40) {
                     double var41 = 0.125;
                     double var43 = var32;
                     double var45 = (var34 - var32) * var41;

                     for(int var47 = 0; var47 < 8; ++var47) {
                        BlockState var48 = null;
                        if (var43 > 0.0) {
                           var48 = Blocks.END_STONE.defaultState();
                        }

                        int var49 = var40 + var8 * 8;
                        int var50 = var29 + var10 * 4;
                        int var51 = var47 + var9 * 8;
                        blocks.set(var49, var50, var51, var48);
                        var43 += var45;
                     }

                     var32 += var36;
                     var34 += var38;
                  }

                  var13 += var21;
                  var15 += var23;
                  var17 += var25;
                  var19 += var27;
               }
            }
         }
      }
   }

   public void generateSurfaces(BlockStateStorage blocks) {
      for(int var2 = 0; var2 < 16; ++var2) {
         for(int var3 = 0; var3 < 16; ++var3) {
            byte var4 = 1;
            int var5 = -1;
            BlockState var6 = Blocks.END_STONE.defaultState();
            BlockState var7 = Blocks.END_STONE.defaultState();

            for(int var8 = 127; var8 >= 0; --var8) {
               BlockState var9 = blocks.get(var2, var8, var3);
               if (var9.getBlock().getMaterial() == Material.AIR) {
                  var5 = -1;
               } else if (var9.getBlock() == Blocks.STONE) {
                  if (var5 == -1) {
                     if (var4 <= 0) {
                        var6 = Blocks.AIR.defaultState();
                        var7 = Blocks.END_STONE.defaultState();
                     }

                     var5 = var4;
                     if (var8 >= 0) {
                        blocks.set(var2, var8, var3, var6);
                     } else {
                        blocks.set(var2, var8, var3, var7);
                     }
                  } else if (var5 > 0) {
                     --var5;
                     blocks.set(var2, var8, var3, var7);
                  }
               }
            }
         }
      }
   }

   @Override
   public WorldChunk getChunk(int chunkX, int chunkZ) {
      this.random.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);
      BlockStateStorage var3 = new BlockStateStorage();
      this.biomes = this.world.getBiomeSource().getBiomes(this.biomes, chunkX * 16, chunkZ * 16, 16, 16);
      this.generate(chunkX, chunkZ, var3);
      this.generateSurfaces(var3);
      WorldChunk var4 = new WorldChunk(this.world, var3, chunkX, chunkZ);
      byte[] var5 = var4.getBiomes();

      for(int var6 = 0; var6 < var5.length; ++var6) {
         var5[var6] = (byte)this.biomes[var6].id;
      }

      var4.populateSkylight();
      return var4;
   }

   private double[] populateHeightMap(double[] heightMap, int x, int y, int z, int sizeX, int sizeY, int sizeZ) {
      if (heightMap == null) {
         heightMap = new double[sizeX * sizeY * sizeZ];
      }

      double var8 = 684.412;
      double var10 = 684.412;
      this.scaleNoiseBuffer = this.scaleNoiseGenerator.getNoise(this.scaleNoiseBuffer, x, z, sizeX, sizeZ, 1.121, 1.121, 0.5);
      this.depthNoiseBuffer = this.depthNoiseGenerator.getNoise(this.depthNoiseBuffer, x, z, sizeX, sizeZ, 200.0, 200.0, 0.5);
      var8 *= 2.0;
      this.mainNoiseBuffer = this.mainPerlinNoise.getNoise(this.mainNoiseBuffer, x, y, z, sizeX, sizeY, sizeZ, var8 / 80.0, var10 / 160.0, var8 / 80.0);
      this.minLimitNoiseBuffer = this.minLimitPerlinNoise.getNoise(this.minLimitNoiseBuffer, x, y, z, sizeX, sizeY, sizeZ, var8, var10, var8);
      this.maxLimitNoiseBuffer = this.maxLimitPerlinNoise.getNoise(this.maxLimitNoiseBuffer, x, y, z, sizeX, sizeY, sizeZ, var8, var10, var8);
      int var12 = 0;

      for(int var13 = 0; var13 < sizeX; ++var13) {
         for(int var14 = 0; var14 < sizeZ; ++var14) {
            float var15 = (float)(var13 + x) / 1.0F;
            float var16 = (float)(var14 + z) / 1.0F;
            float var17 = 100.0F - MathHelper.sqrt(var15 * var15 + var16 * var16) * 8.0F;
            if (var17 > 80.0F) {
               var17 = 80.0F;
            }

            if (var17 < -100.0F) {
               var17 = -100.0F;
            }

            for(int var18 = 0; var18 < sizeY; ++var18) {
               double var19 = 0.0;
               double var21 = this.minLimitNoiseBuffer[var12] / 512.0;
               double var23 = this.maxLimitNoiseBuffer[var12] / 512.0;
               double var25 = (this.mainNoiseBuffer[var12] / 10.0 + 1.0) / 2.0;
               if (var25 < 0.0) {
                  var19 = var21;
               } else if (var25 > 1.0) {
                  var19 = var23;
               } else {
                  var19 = var21 + (var23 - var21) * var25;
               }

               var19 -= 8.0;
               var19 += (double)var17;
               byte var27 = 2;
               if (var18 > sizeY / 2 - var27) {
                  double var28 = (double)((float)(var18 - (sizeY / 2 - var27)) / 64.0F);
                  var28 = MathHelper.clamp(var28, 0.0, 1.0);
                  var19 = var19 * (1.0 - var28) + -3000.0 * var28;
               }

               var27 = 8;
               if (var18 < var27) {
                  double var36 = (double)((float)(var27 - var18) / ((float)var27 - 1.0F));
                  var19 = var19 * (1.0 - var36) + -30.0 * var36;
               }

               heightMap[var12] = var19;
               ++var12;
            }
         }
      }

      return heightMap;
   }

   @Override
   public boolean isLoaded(int chunkX, int chunkZ) {
      return true;
   }

   @Override
   public void populate(ChunkSource source, int chunkX, int chunkZ) {
      FallingBlock.fallImmediately = true;
      BlockPos var4 = new BlockPos(chunkX * 16, 0, chunkZ * 16);
      this.world.getBiome(var4.add(16, 0, 16)).decorate(this.world, this.world.random, var4);
      FallingBlock.fallImmediately = false;
   }

   @Override
   public boolean populateSpecial(ChunkSource source, WorldChunk chunk, int chunkX, int chunkZ) {
      return false;
   }

   @Override
   public boolean save(boolean saveEntities, ProgressListener listener) {
      return true;
   }

   @Override
   public void save() {
   }

   @Override
   public boolean tick() {
      return false;
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   public String getName() {
      return "RandomLevelSource";
   }

   @Override
   public List getSpawnEntries(MobSpawnGroup spawnGroup, BlockPos pos) {
      return this.world.getBiome(pos).getSpawnEntries(spawnGroup);
   }

   @Override
   public BlockPos findNearestStructure(World world, String name, BlockPos pos) {
      return null;
   }

   @Override
   public int getLoadedCount() {
      return 0;
   }

   @Override
   public void placeStructures(WorldChunk chunk, int chunkX, int chunkZ) {
   }

   @Override
   public WorldChunk getChunk(BlockPos pos) {
      return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
   }
}
