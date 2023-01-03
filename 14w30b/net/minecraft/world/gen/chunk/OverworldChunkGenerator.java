package net.minecraft.world.gen.chunk;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.living.mob.MobSpawnGroup;
import net.minecraft.entity.living.mob.MobSpawnerHelper;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.BlockStateStorage;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.Generator;
import net.minecraft.world.gen.WorldGeneratorType;
import net.minecraft.world.gen.carver.OverworldCaveCarver;
import net.minecraft.world.gen.carver.RavineCarver;
import net.minecraft.world.gen.feature.DungeonFeature;
import net.minecraft.world.gen.feature.LakeFeature;
import net.minecraft.world.gen.noise.OctaveNoiseGenerator;
import net.minecraft.world.gen.noise.PerlinNoiseGenerator;
import net.minecraft.world.gen.structure.MineshaftStructure;
import net.minecraft.world.gen.structure.OceanMonumentStructure;
import net.minecraft.world.gen.structure.StrongholdStructure;
import net.minecraft.world.gen.structure.TempleStructure;
import net.minecraft.world.gen.structure.VillageStructure;

public class OverworldChunkGenerator implements ChunkSource {
   private Random random;
   private OctaveNoiseGenerator minLimitPerlinNoise;
   private OctaveNoiseGenerator maxLimitPerlinNoise;
   private OctaveNoiseGenerator mainPerlinNoise;
   private PerlinNoiseGenerator surfaceNoise;
   public OctaveNoiseGenerator scaleNoise;
   public OctaveNoiseGenerator depthNoise;
   public OctaveNoiseGenerator forestNoise;
   private World world;
   private final boolean structures;
   private WorldGeneratorType type;
   private final double[] heightMap;
   private final float[] biomeWeights;
   private GeneratorOptions options;
   private Block oceanLiquid = Blocks.WATER;
   private double[] depthBuffer = new double[256];
   private Generator cave = new OverworldCaveCarver();
   private StrongholdStructure stronghold = new StrongholdStructure();
   private VillageStructure village = new VillageStructure();
   private MineshaftStructure mineshaft = new MineshaftStructure();
   private TempleStructure witchHut = new TempleStructure();
   private Generator ravine = new RavineCarver();
   private OceanMonumentStructure oceanMonument = new OceanMonumentStructure();
   private Biome[] biomes;
   double[] mainNoiseBuffer;
   double[] minLimitNoiseBuffer;
   double[] maxLimitNoiseBuffer;
   double[] depthNoiseBuffer;

   public OverworldChunkGenerator(World world, long seed, boolean structures, String generatorOptions) {
      this.world = world;
      this.structures = structures;
      this.type = world.getData().getGeneratorType();
      this.random = new Random(seed);
      this.minLimitPerlinNoise = new OctaveNoiseGenerator(this.random, 16);
      this.maxLimitPerlinNoise = new OctaveNoiseGenerator(this.random, 16);
      this.mainPerlinNoise = new OctaveNoiseGenerator(this.random, 8);
      this.surfaceNoise = new PerlinNoiseGenerator(this.random, 4);
      this.scaleNoise = new OctaveNoiseGenerator(this.random, 10);
      this.depthNoise = new OctaveNoiseGenerator(this.random, 16);
      this.forestNoise = new OctaveNoiseGenerator(this.random, 8);
      this.heightMap = new double[825];
      this.biomeWeights = new float[25];

      for(int var6 = -2; var6 <= 2; ++var6) {
         for(int var7 = -2; var7 <= 2; ++var7) {
            float var8 = 10.0F / MathHelper.sqrt((float)(var6 * var6 + var7 * var7) + 0.2F);
            this.biomeWeights[var6 + 2 + (var7 + 2) * 5] = var8;
         }
      }

      if (generatorOptions != null) {
         this.options = GeneratorOptions.Factory.fromJson(generatorOptions).create();
         this.oceanLiquid = this.options.useLavaOceans ? Blocks.LAVA : Blocks.WATER;
      }
   }

   public void generate(int chunkX, int chunkZ, BlockStateStorage blocks) {
      this.biomes = this.world.getBiomeSource().getBiomesForGeneration(this.biomes, chunkX * 4 - 2, chunkZ * 4 - 2, 10, 10);
      this.populateHeightMap(chunkX * 4, 0, chunkZ * 4);

      for(int var4 = 0; var4 < 4; ++var4) {
         int var5 = var4 * 5;
         int var6 = (var4 + 1) * 5;

         for(int var7 = 0; var7 < 4; ++var7) {
            int var8 = (var5 + var7) * 33;
            int var9 = (var5 + var7 + 1) * 33;
            int var10 = (var6 + var7) * 33;
            int var11 = (var6 + var7 + 1) * 33;

            for(int var12 = 0; var12 < 32; ++var12) {
               double var13 = 0.125;
               double var15 = this.heightMap[var8 + var12];
               double var17 = this.heightMap[var9 + var12];
               double var19 = this.heightMap[var10 + var12];
               double var21 = this.heightMap[var11 + var12];
               double var23 = (this.heightMap[var8 + var12 + 1] - var15) * var13;
               double var25 = (this.heightMap[var9 + var12 + 1] - var17) * var13;
               double var27 = (this.heightMap[var10 + var12 + 1] - var19) * var13;
               double var29 = (this.heightMap[var11 + var12 + 1] - var21) * var13;

               for(int var31 = 0; var31 < 8; ++var31) {
                  double var32 = 0.25;
                  double var34 = var15;
                  double var36 = var17;
                  double var38 = (var19 - var15) * var32;
                  double var40 = (var21 - var17) * var32;

                  for(int var42 = 0; var42 < 4; ++var42) {
                     double var43 = 0.25;
                     double var47 = (var36 - var34) * var43;
                     double var45 = var34 - var47;

                     for(int var49 = 0; var49 < 4; ++var49) {
                        if ((var45 += var47) > 0.0) {
                           blocks.set(var4 * 4 + var42, var12 * 8 + var31, var7 * 4 + var49, Blocks.STONE.defaultState());
                        } else if (var12 * 8 + var31 < this.options.seaLevel) {
                           blocks.set(var4 * 4 + var42, var12 * 8 + var31, var7 * 4 + var49, this.oceanLiquid.defaultState());
                        }
                     }

                     var34 += var38;
                     var36 += var40;
                  }

                  var15 += var23;
                  var17 += var25;
                  var19 += var27;
                  var21 += var29;
               }
            }
         }
      }
   }

   public void generateBiomes(int chunkX, int chunkZ, BlockStateStorage blocks, Biome[] biomes) {
      double var5 = 0.03125;
      this.depthBuffer = this.surfaceNoise.getNoise(this.depthBuffer, (double)(chunkX * 16), (double)(chunkZ * 16), 16, 16, var5 * 2.0, var5 * 2.0, 1.0);

      for(int var7 = 0; var7 < 16; ++var7) {
         for(int var8 = 0; var8 < 16; ++var8) {
            Biome var9 = biomes[var8 + var7 * 16];
            var9.populateChunk(this.world, this.random, blocks, chunkX * 16 + var7, chunkZ * 16 + var8, this.depthBuffer[var8 + var7 * 16]);
         }
      }
   }

   @Override
   public WorldChunk getChunk(int chunkX, int chunkZ) {
      this.random.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);
      BlockStateStorage var3 = new BlockStateStorage();
      this.generate(chunkX, chunkZ, var3);
      this.biomes = this.world.getBiomeSource().getBiomes(this.biomes, chunkX * 16, chunkZ * 16, 16, 16);
      this.generateBiomes(chunkX, chunkZ, var3, this.biomes);
      if (this.options.useCaves) {
         this.cave.place(this, this.world, chunkX, chunkZ, var3);
      }

      if (this.options.useRavines) {
         this.ravine.place(this, this.world, chunkX, chunkZ, var3);
      }

      if (this.options.useMineshafts && this.structures) {
         this.mineshaft.place(this, this.world, chunkX, chunkZ, var3);
      }

      if (this.options.useVillages && this.structures) {
         this.village.place(this, this.world, chunkX, chunkZ, var3);
      }

      if (this.options.useStrongholds && this.structures) {
         this.stronghold.place(this, this.world, chunkX, chunkZ, var3);
      }

      if (this.options.useTemples && this.structures) {
         this.witchHut.place(this, this.world, chunkX, chunkZ, var3);
      }

      if (this.options.useMonuments && this.structures) {
         this.oceanMonument.place(this, this.world, chunkX, chunkZ, var3);
      }

      WorldChunk var4 = new WorldChunk(this.world, var3, chunkX, chunkZ);
      byte[] var5 = var4.getBiomes();

      for(int var6 = 0; var6 < var5.length; ++var6) {
         var5[var6] = (byte)this.biomes[var6].id;
      }

      var4.populateSkylight();
      return var4;
   }

   private void populateHeightMap(int x, int y, int z) {
      this.depthNoiseBuffer = this.depthNoise
         .getNoise(
            this.depthNoiseBuffer,
            x,
            z,
            5,
            5,
            (double)this.options.depthNoiseScaleX,
            (double)this.options.depthNoiseScaleZ,
            (double)this.options.depthNoiseScaleExponent
         );
      float var4 = this.options.coordinateScale;
      float var5 = this.options.heightScale;
      this.mainNoiseBuffer = this.mainPerlinNoise
         .getNoise(
            this.mainNoiseBuffer,
            x,
            y,
            z,
            5,
            33,
            5,
            (double)(var4 / this.options.mainNoiseScaleX),
            (double)(var5 / this.options.mainNoiseScaleY),
            (double)(var4 / this.options.mainNoiseScaleZ)
         );
      this.minLimitNoiseBuffer = this.minLimitPerlinNoise.getNoise(this.minLimitNoiseBuffer, x, y, z, 5, 33, 5, (double)var4, (double)var5, (double)var4);
      this.maxLimitNoiseBuffer = this.maxLimitPerlinNoise.getNoise(this.maxLimitNoiseBuffer, x, y, z, 5, 33, 5, (double)var4, (double)var5, (double)var4);
      int var37 = false;
      int var36 = false;
      int var6 = 0;
      int var7 = 0;

      for(int var8 = 0; var8 < 5; ++var8) {
         for(int var9 = 0; var9 < 5; ++var9) {
            float var10 = 0.0F;
            float var11 = 0.0F;
            float var12 = 0.0F;
            byte var13 = 2;
            Biome var14 = this.biomes[var8 + 2 + (var9 + 2) * 10];

            for(int var15 = -var13; var15 <= var13; ++var15) {
               for(int var16 = -var13; var16 <= var13; ++var16) {
                  Biome var17 = this.biomes[var8 + var15 + 2 + (var9 + var16 + 2) * 10];
                  float var18 = this.options.biomeDepthOffset + var17.baseHeight * this.options.biomeDepthWeight;
                  float var19 = this.options.biomeScaleOffset + var17.heightVariation * this.options.biomeScaleWeight;
                  if (this.type == WorldGeneratorType.AMPLIFIED && var18 > 0.0F) {
                     var18 = 1.0F + var18 * 2.0F;
                     var19 = 1.0F + var19 * 4.0F;
                  }

                  float var20 = this.biomeWeights[var15 + 2 + (var16 + 2) * 5] / (var18 + 2.0F);
                  if (var17.baseHeight > var14.baseHeight) {
                     var20 /= 2.0F;
                  }

                  var10 += var19 * var20;
                  var11 += var18 * var20;
                  var12 += var20;
               }
            }

            var10 /= var12;
            var11 /= var12;
            var10 = var10 * 0.9F + 0.1F;
            var11 = (var11 * 4.0F - 1.0F) / 8.0F;
            double var42 = this.depthNoiseBuffer[var7] / 8000.0;
            if (var42 < 0.0) {
               var42 = -var42 * 0.3;
            }

            var42 = var42 * 3.0 - 2.0;
            if (var42 < 0.0) {
               var42 /= 2.0;
               if (var42 < -1.0) {
                  var42 = -1.0;
               }

               var42 /= 1.4;
               var42 /= 2.0;
            } else {
               if (var42 > 1.0) {
                  var42 = 1.0;
               }

               var42 /= 8.0;
            }

            ++var7;
            double var47 = (double)var11;
            double var50 = (double)var10;
            var47 += var42 * 0.2;
            var47 = var47 * (double)this.options.baseSize / 8.0;
            double var21 = (double)this.options.baseSize + var47 * 4.0;

            for(int var23 = 0; var23 < 33; ++var23) {
               double var24 = ((double)var23 - var21) * (double)this.options.stretchY * 128.0 / 256.0 / var50;
               if (var24 < 0.0) {
                  var24 *= 4.0;
               }

               double var26 = this.minLimitNoiseBuffer[var6] / (double)this.options.lowerLimitScale;
               double var28 = this.maxLimitNoiseBuffer[var6] / (double)this.options.upperLimitScale;
               double var30 = (this.mainNoiseBuffer[var6] / 10.0 + 1.0) / 2.0;
               double var32 = MathHelper.clampedLerp(var26, var28, var30) - var24;
               if (var23 > 29) {
                  double var34 = (double)((float)(var23 - 29) / 3.0F);
                  var32 = var32 * (1.0 - var34) + -10.0 * var34;
               }

               this.heightMap[var6] = var32;
               ++var6;
            }
         }
      }
   }

   @Override
   public boolean isLoaded(int chunkX, int chunkZ) {
      return true;
   }

   @Override
   public void populate(ChunkSource source, int chunkX, int chunkZ) {
      FallingBlock.fallImmediately = true;
      int var4 = chunkX * 16;
      int var5 = chunkZ * 16;
      BlockPos var6 = new BlockPos(var4, 0, var5);
      Biome var7 = this.world.getBiome(var6.add(16, 0, 16));
      this.random.setSeed(this.world.getSeed());
      long var8 = this.random.nextLong() / 2L * 2L + 1L;
      long var10 = this.random.nextLong() / 2L * 2L + 1L;
      this.random.setSeed((long)chunkX * var8 + (long)chunkZ * var10 ^ this.world.getSeed());
      boolean var12 = false;
      ChunkPos var13 = new ChunkPos(chunkX, chunkZ);
      if (this.options.useMineshafts && this.structures) {
         this.mineshaft.place(this.world, this.random, var13);
      }

      if (this.options.useVillages && this.structures) {
         var12 = this.village.place(this.world, this.random, var13);
      }

      if (this.options.useStrongholds && this.structures) {
         this.stronghold.place(this.world, this.random, var13);
      }

      if (this.options.useTemples && this.structures) {
         this.witchHut.place(this.world, this.random, var13);
      }

      if (this.options.useMonuments && this.structures) {
         this.oceanMonument.place(this.world, this.random, var13);
      }

      if (var7 != Biome.DESERT && var7 != Biome.DESERT_HILLS && this.options.useWaterLakes && !var12 && this.random.nextInt(this.options.waterLakeChance) == 0) {
         int var14 = this.random.nextInt(16) + 8;
         int var15 = this.random.nextInt(256);
         int var16 = this.random.nextInt(16) + 8;
         new LakeFeature(Blocks.WATER).place(this.world, this.random, var6.add(var14, var15, var16));
      }

      if (!var12 && this.random.nextInt(this.options.lavaLakeChance / 10) == 0 && this.options.useLavaLakes) {
         int var19 = this.random.nextInt(16) + 8;
         int var22 = this.random.nextInt(this.random.nextInt(248) + 8);
         int var25 = this.random.nextInt(16) + 8;
         if (var22 < 63 || this.random.nextInt(this.options.lavaLakeChance / 8) == 0) {
            new LakeFeature(Blocks.LAVA).place(this.world, this.random, var6.add(var19, var22, var25));
         }
      }

      if (this.options.useDungeons) {
         for(int var20 = 0; var20 < this.options.dungeonChance; ++var20) {
            int var23 = this.random.nextInt(16) + 8;
            int var26 = this.random.nextInt(256);
            int var17 = this.random.nextInt(16) + 8;
            new DungeonFeature().place(this.world, this.random, var6.add(var23, var26, var17));
         }
      }

      var7.decorate(this.world, this.random, new BlockPos(var4, 0, var5));
      MobSpawnerHelper.populateEntities(this.world, var7, var4 + 8, var5 + 8, 16, 16, this.random);
      var6 = var6.add(8, 0, 8);

      for(int var21 = 0; var21 < 16; ++var21) {
         for(int var24 = 0; var24 < 16; ++var24) {
            BlockPos var27 = this.world.getPrecipitationHeight(var6.add(var21, 0, var24));
            BlockPos var28 = var27.down();
            if (this.world.canFreeze(var28)) {
               this.world.setBlockState(var28, Blocks.ICE.defaultState(), 2);
            }

            if (this.world.canSnowFall(var27, true)) {
               this.world.setBlockState(var27, Blocks.SNOW_LAYER.defaultState(), 2);
            }
         }
      }

      FallingBlock.fallImmediately = false;
   }

   @Override
   public boolean populateSpecial(ChunkSource source, WorldChunk chunk, int chunkX, int chunkZ) {
      boolean var5 = false;
      if (this.options.useMonuments && this.structures && chunk.getInhabitedTime() < 3600L) {
         var5 |= this.oceanMonument.place(this.world, this.random, new ChunkPos(chunkX, chunkZ));
      }

      return var5;
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
      Biome var3 = this.world.getBiome(pos);
      if (this.structures) {
         if (spawnGroup == MobSpawnGroup.MONSTER && this.witchHut.isValid(pos)) {
            return this.witchHut.getSpawnEntries();
         }

         if (spawnGroup == MobSpawnGroup.MONSTER && this.options.useMonuments && this.oceanMonument.isInsideStructureBox(this.world, pos)) {
            return this.oceanMonument.getSpawnEntries();
         }
      }

      return var3.getSpawnEntries(spawnGroup);
   }

   @Override
   public BlockPos findNearestStructure(World world, String name, BlockPos pos) {
      return "Stronghold".equals(name) && this.stronghold != null ? this.stronghold.findNearestStructure(world, pos) : null;
   }

   @Override
   public int getLoadedCount() {
      return 0;
   }

   @Override
   public void placeStructures(WorldChunk chunk, int chunkX, int chunkZ) {
      if (this.options.useMineshafts && this.structures) {
         this.mineshaft.place(this, this.world, chunkX, chunkZ, null);
      }

      if (this.options.useVillages && this.structures) {
         this.village.place(this, this.world, chunkX, chunkZ, null);
      }

      if (this.options.useStrongholds && this.structures) {
         this.stronghold.place(this, this.world, chunkX, chunkZ, null);
      }

      if (this.options.useTemples && this.structures) {
         this.witchHut.place(this, this.world, chunkX, chunkZ, null);
      }

      if (this.options.useMonuments && this.structures) {
         this.oceanMonument.place(this, this.world, chunkX, chunkZ, null);
      }
   }

   @Override
   public WorldChunk getChunk(BlockPos pos) {
      return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
   }
}
