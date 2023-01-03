package net.minecraft.world.gen.chunk;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockPredicate;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.mob.MobSpawnGroup;
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
import net.minecraft.world.gen.carver.NetherCaveCarver;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FirePatchFeature;
import net.minecraft.world.gen.feature.GlowstoneClusterFeature;
import net.minecraft.world.gen.feature.LiquidPocketFeature;
import net.minecraft.world.gen.feature.PlantFeature;
import net.minecraft.world.gen.feature.UpsideDownGlowstoneClusterFeature;
import net.minecraft.world.gen.feature.VeinFeature;
import net.minecraft.world.gen.noise.OctaveNoiseGenerator;
import net.minecraft.world.gen.structure.FortressStructure;

public class NetherChunkGenerator implements ChunkSource {
   private final World world;
   private final boolean structures;
   private final Random random;
   private double[] soulSandNoiseBuffer = new double[256];
   private double[] gravelNoiseBuffer = new double[256];
   private double[] lavaLakeNoiseBuffer = new double[256];
   private double[] heightMap;
   private final OctaveNoiseGenerator minLimitPerlinNoise;
   private final OctaveNoiseGenerator maxLimitPerlinNoise;
   private final OctaveNoiseGenerator mainPerlinNoise;
   private final OctaveNoiseGenerator soulSandGravelNoise;
   private final OctaveNoiseGenerator lavaLakeNoise;
   public final OctaveNoiseGenerator scaleNoise;
   public final OctaveNoiseGenerator depthNoise;
   private final FirePatchFeature firePatch = new FirePatchFeature();
   private final UpsideDownGlowstoneClusterFeature upsideDownGlowstoneCluster = new UpsideDownGlowstoneClusterFeature();
   private final GlowstoneClusterFeature glowstoneCluster = new GlowstoneClusterFeature();
   private final Feature quartzOreVein = new VeinFeature(Blocks.QUARTZ_ORE.defaultState(), 14, BlockPredicate.of(Blocks.NETHERRACK));
   private final LiquidPocketFeature exposedLavaPocket = new LiquidPocketFeature(Blocks.FLOWING_LAVA, true);
   private final LiquidPocketFeature lavaPocket = new LiquidPocketFeature(Blocks.FLOWING_LAVA, false);
   private final PlantFeature brownMushroom = new PlantFeature(Blocks.BROWN_MUSHROOM);
   private final PlantFeature redMushroom = new PlantFeature(Blocks.RED_MUSHROOM);
   private final FortressStructure fortress = new FortressStructure();
   private final Generator cave = new NetherCaveCarver();
   double[] mainNoiseBuffer;
   double[] minLimitNoiseBuffer;
   double[] maxLimitNoiseBuffer;
   double[] scaleNoiseBuffer;
   double[] depthNoiseBuffer;

   public NetherChunkGenerator(World world, boolean structures, long seed) {
      this.world = world;
      this.structures = structures;
      this.random = new Random(seed);
      this.minLimitPerlinNoise = new OctaveNoiseGenerator(this.random, 16);
      this.maxLimitPerlinNoise = new OctaveNoiseGenerator(this.random, 16);
      this.mainPerlinNoise = new OctaveNoiseGenerator(this.random, 8);
      this.soulSandGravelNoise = new OctaveNoiseGenerator(this.random, 4);
      this.lavaLakeNoise = new OctaveNoiseGenerator(this.random, 4);
      this.scaleNoise = new OctaveNoiseGenerator(this.random, 10);
      this.depthNoise = new OctaveNoiseGenerator(this.random, 16);
   }

   public void generate(int chunkX, int chunkZ, BlockStateStorage blocks) {
      byte var4 = 4;
      byte var5 = 32;
      int var6 = var4 + 1;
      byte var7 = 17;
      int var8 = var4 + 1;
      this.heightMap = this.populateHeightMap(this.heightMap, chunkX * var4, 0, chunkZ * var4, var6, var7, var8);

      for(int var9 = 0; var9 < var4; ++var9) {
         for(int var10 = 0; var10 < var4; ++var10) {
            for(int var11 = 0; var11 < 16; ++var11) {
               double var12 = 0.125;
               double var14 = this.heightMap[((var9 + 0) * var8 + var10 + 0) * var7 + var11 + 0];
               double var16 = this.heightMap[((var9 + 0) * var8 + var10 + 1) * var7 + var11 + 0];
               double var18 = this.heightMap[((var9 + 1) * var8 + var10 + 0) * var7 + var11 + 0];
               double var20 = this.heightMap[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 0];
               double var22 = (this.heightMap[((var9 + 0) * var8 + var10 + 0) * var7 + var11 + 1] - var14) * var12;
               double var24 = (this.heightMap[((var9 + 0) * var8 + var10 + 1) * var7 + var11 + 1] - var16) * var12;
               double var26 = (this.heightMap[((var9 + 1) * var8 + var10 + 0) * var7 + var11 + 1] - var18) * var12;
               double var28 = (this.heightMap[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 1] - var20) * var12;

               for(int var30 = 0; var30 < 8; ++var30) {
                  double var31 = 0.25;
                  double var33 = var14;
                  double var35 = var16;
                  double var37 = (var18 - var14) * var31;
                  double var39 = (var20 - var16) * var31;

                  for(int var41 = 0; var41 < 4; ++var41) {
                     double var42 = 0.25;
                     double var44 = var33;
                     double var46 = (var35 - var33) * var42;

                     for(int var48 = 0; var48 < 4; ++var48) {
                        BlockState var49 = null;
                        if (var11 * 8 + var30 < var5) {
                           var49 = Blocks.LAVA.defaultState();
                        }

                        if (var44 > 0.0) {
                           var49 = Blocks.NETHERRACK.defaultState();
                        }

                        int var50 = var41 + var9 * 4;
                        int var51 = var30 + var11 * 8;
                        int var52 = var48 + var10 * 4;
                        blocks.set(var50, var51, var52, var49);
                        var44 += var46;
                     }

                     var33 += var37;
                     var35 += var39;
                  }

                  var14 += var22;
                  var16 += var24;
                  var18 += var26;
                  var20 += var28;
               }
            }
         }
      }
   }

   public void generateSurfaces(int chunkX, int chunkZ, BlockStateStorage blocks) {
      byte var4 = 64;
      double var5 = 0.03125;
      this.soulSandNoiseBuffer = this.soulSandGravelNoise.getNoise(this.soulSandNoiseBuffer, chunkX * 16, chunkZ * 16, 0, 16, 16, 1, var5, var5, 1.0);
      this.gravelNoiseBuffer = this.soulSandGravelNoise.getNoise(this.gravelNoiseBuffer, chunkX * 16, 109, chunkZ * 16, 16, 1, 16, var5, 1.0, var5);
      this.lavaLakeNoiseBuffer = this.lavaLakeNoise
         .getNoise(this.lavaLakeNoiseBuffer, chunkX * 16, chunkZ * 16, 0, 16, 16, 1, var5 * 2.0, var5 * 2.0, var5 * 2.0);

      for(int var7 = 0; var7 < 16; ++var7) {
         for(int var8 = 0; var8 < 16; ++var8) {
            boolean var9 = this.soulSandNoiseBuffer[var7 + var8 * 16] + this.random.nextDouble() * 0.2 > 0.0;
            boolean var10 = this.gravelNoiseBuffer[var7 + var8 * 16] + this.random.nextDouble() * 0.2 > 0.0;
            int var11 = (int)(this.lavaLakeNoiseBuffer[var7 + var8 * 16] / 3.0 + 3.0 + this.random.nextDouble() * 0.25);
            int var12 = -1;
            BlockState var13 = Blocks.NETHERRACK.defaultState();
            BlockState var14 = Blocks.NETHERRACK.defaultState();

            for(int var15 = 127; var15 >= 0; --var15) {
               if (var15 < 127 - this.random.nextInt(5) && var15 > this.random.nextInt(5)) {
                  BlockState var16 = blocks.get(var8, var15, var7);
                  if (var16.getBlock() == null || var16.getBlock().getMaterial() == Material.AIR) {
                     var12 = -1;
                  } else if (var16.getBlock() == Blocks.NETHERRACK) {
                     if (var12 == -1) {
                        if (var11 <= 0) {
                           var13 = null;
                           var14 = Blocks.NETHERRACK.defaultState();
                        } else if (var15 >= var4 - 4 && var15 <= var4 + 1) {
                           var13 = Blocks.NETHERRACK.defaultState();
                           var14 = Blocks.NETHERRACK.defaultState();
                           if (var10) {
                              var13 = Blocks.GRAVEL.defaultState();
                              var14 = Blocks.NETHERRACK.defaultState();
                           }

                           if (var9) {
                              var13 = Blocks.SOUL_SAND.defaultState();
                              var14 = Blocks.SOUL_SAND.defaultState();
                           }
                        }

                        if (var15 < var4 && (var13 == null || var13.getBlock().getMaterial() == Material.AIR)) {
                           var13 = Blocks.LAVA.defaultState();
                        }

                        var12 = var11;
                        if (var15 >= var4 - 1) {
                           blocks.set(var8, var15, var7, var13);
                        } else {
                           blocks.set(var8, var15, var7, var14);
                        }
                     } else if (var12 > 0) {
                        --var12;
                        blocks.set(var8, var15, var7, var14);
                     }
                  }
               } else {
                  blocks.set(var8, var15, var7, Blocks.BEDROCK.defaultState());
               }
            }
         }
      }
   }

   @Override
   public WorldChunk getChunk(int chunkX, int chunkZ) {
      this.random.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);
      BlockStateStorage var3 = new BlockStateStorage();
      this.generate(chunkX, chunkZ, var3);
      this.generateSurfaces(chunkX, chunkZ, var3);
      this.cave.place(this, this.world, chunkX, chunkZ, var3);
      if (this.structures) {
         this.fortress.place(this, this.world, chunkX, chunkZ, var3);
      }

      WorldChunk var4 = new WorldChunk(this.world, var3, chunkX, chunkZ);
      Biome[] var5 = this.world.getBiomeSource().getBiomes(null, chunkX * 16, chunkZ * 16, 16, 16);
      byte[] var6 = var4.getBiomes();

      for(int var7 = 0; var7 < var6.length; ++var7) {
         var6[var7] = (byte)var5[var7].id;
      }

      var4.clearLightChecks();
      return var4;
   }

   private double[] populateHeightMap(double[] heightMap, int x, int y, int z, int sizeX, int sizeY, int sizeZ) {
      if (heightMap == null) {
         heightMap = new double[sizeX * sizeY * sizeZ];
      }

      double var8 = 684.412;
      double var10 = 2053.236;
      this.scaleNoiseBuffer = this.scaleNoise.getNoise(this.scaleNoiseBuffer, x, y, z, sizeX, 1, sizeZ, 1.0, 0.0, 1.0);
      this.depthNoiseBuffer = this.depthNoise.getNoise(this.depthNoiseBuffer, x, y, z, sizeX, 1, sizeZ, 100.0, 0.0, 100.0);
      this.mainNoiseBuffer = this.mainPerlinNoise.getNoise(this.mainNoiseBuffer, x, y, z, sizeX, sizeY, sizeZ, var8 / 80.0, var10 / 60.0, var8 / 80.0);
      this.minLimitNoiseBuffer = this.minLimitPerlinNoise.getNoise(this.minLimitNoiseBuffer, x, y, z, sizeX, sizeY, sizeZ, var8, var10, var8);
      this.maxLimitNoiseBuffer = this.maxLimitPerlinNoise.getNoise(this.maxLimitNoiseBuffer, x, y, z, sizeX, sizeY, sizeZ, var8, var10, var8);
      int var12 = 0;
      double[] var13 = new double[sizeY];

      for(int var14 = 0; var14 < sizeY; ++var14) {
         var13[var14] = Math.cos((double)var14 * Math.PI * 6.0 / (double)sizeY) * 2.0;
         double var15 = (double)var14;
         if (var14 > sizeY / 2) {
            var15 = (double)(sizeY - 1 - var14);
         }

         if (var15 < 4.0) {
            var15 = 4.0 - var15;
            var13[var14] -= var15 * var15 * var15 * 10.0;
         }
      }

      for(int var31 = 0; var31 < sizeX; ++var31) {
         for(int var33 = 0; var33 < sizeZ; ++var33) {
            double var16 = 0.0;

            for(int var18 = 0; var18 < sizeY; ++var18) {
               double var19 = 0.0;
               double var21 = var13[var18];
               double var23 = this.minLimitNoiseBuffer[var12] / 512.0;
               double var25 = this.maxLimitNoiseBuffer[var12] / 512.0;
               double var27 = (this.mainNoiseBuffer[var12] / 10.0 + 1.0) / 2.0;
               if (var27 < 0.0) {
                  var19 = var23;
               } else if (var27 > 1.0) {
                  var19 = var25;
               } else {
                  var19 = var23 + (var25 - var23) * var27;
               }

               var19 -= var21;
               if (var18 > sizeY - 4) {
                  double var29 = (double)((float)(var18 - (sizeY - 4)) / 3.0F);
                  var19 = var19 * (1.0 - var29) + -10.0 * var29;
               }

               if ((double)var18 < var16) {
                  double var36 = (var16 - (double)var18) / 4.0;
                  var36 = MathHelper.clamp(var36, 0.0, 1.0);
                  var19 = var19 * (1.0 - var36) + -10.0 * var36;
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
      ChunkPos var5 = new ChunkPos(chunkX, chunkZ);
      this.fortress.place(this.world, this.random, var5);

      for(int var6 = 0; var6 < 8; ++var6) {
         this.lavaPocket.place(this.world, this.random, var4.add(this.random.nextInt(16) + 8, this.random.nextInt(120) + 4, this.random.nextInt(16) + 8));
      }

      for(int var7 = 0; var7 < this.random.nextInt(this.random.nextInt(10) + 1) + 1; ++var7) {
         this.firePatch.place(this.world, this.random, var4.add(this.random.nextInt(16) + 8, this.random.nextInt(120) + 4, this.random.nextInt(16) + 8));
      }

      for(int var8 = 0; var8 < this.random.nextInt(this.random.nextInt(10) + 1); ++var8) {
         this.upsideDownGlowstoneCluster
            .place(this.world, this.random, var4.add(this.random.nextInt(16) + 8, this.random.nextInt(120) + 4, this.random.nextInt(16) + 8));
      }

      for(int var9 = 0; var9 < 10; ++var9) {
         this.glowstoneCluster.place(this.world, this.random, var4.add(this.random.nextInt(16) + 8, this.random.nextInt(128), this.random.nextInt(16) + 8));
      }

      if (this.random.nextBoolean()) {
         this.brownMushroom.place(this.world, this.random, var4.add(this.random.nextInt(16) + 8, this.random.nextInt(128), this.random.nextInt(16) + 8));
      }

      if (this.random.nextBoolean()) {
         this.redMushroom.place(this.world, this.random, var4.add(this.random.nextInt(16) + 8, this.random.nextInt(128), this.random.nextInt(16) + 8));
      }

      for(int var10 = 0; var10 < 16; ++var10) {
         this.quartzOreVein.place(this.world, this.random, var4.add(this.random.nextInt(16), this.random.nextInt(108) + 10, this.random.nextInt(16)));
      }

      for(int var11 = 0; var11 < 16; ++var11) {
         this.exposedLavaPocket.place(this.world, this.random, var4.add(this.random.nextInt(16), this.random.nextInt(108) + 10, this.random.nextInt(16)));
      }

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
      return "HellRandomLevelSource";
   }

   @Override
   public List getSpawnEntries(MobSpawnGroup spawnGroup, BlockPos pos) {
      if (spawnGroup == MobSpawnGroup.MONSTER) {
         if (this.fortress.isInsideStructure(pos)) {
            return this.fortress.getMonsterSpawns();
         }

         if (this.fortress.isInsideStructureBox(this.world, pos) && this.world.getBlockState(pos.down()).getBlock() == Blocks.NETHER_BRICKS) {
            return this.fortress.getMonsterSpawns();
         }
      }

      Biome var3 = this.world.getBiome(pos);
      return var3.getSpawnEntries(spawnGroup);
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
      this.fortress.place(this, this.world, chunkX, chunkZ, null);
   }

   @Override
   public WorldChunk getChunk(BlockPos pos) {
      return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
   }
}
