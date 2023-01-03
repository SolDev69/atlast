package net.minecraft.world.biome;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.SandBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateStorage;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.noise.PerlinNoiseGenerator;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class MesaBiome extends Biome {
   private byte[] clayLayers;
   private long seed;
   private PerlinNoiseGenerator clayPillarNoise;
   private PerlinNoiseGenerator claySurfaceNoise;
   private PerlinNoiseGenerator clayOffsetNoise;
   private boolean mutated;
   private boolean hasTrees;

   public MesaBiome(int id, boolean mutated, boolean hasTrees) {
      super(id);
      this.mutated = mutated;
      this.hasTrees = hasTrees;
      this.disableRain();
      this.setTemperatureAndDownfall(2.0F, 0.0F);
      this.passiveEntries.clear();
      this.surfaceBlock = Blocks.SAND.getStateFromMetadata(SandBlock.Variant.RED_SAND.getIndex());
      this.subsurfaceBlock = Blocks.STAINED_HARDENED_CLAY.defaultState();
      this.decorator.treeAttempts = -999;
      this.decorator.deadBushAttempts = 20;
      this.decorator.sugarcaneAttempts = 3;
      this.decorator.cactusAttempts = 5;
      this.decorator.flowerAttempts = 0;
      this.passiveEntries.clear();
      if (hasTrees) {
         this.decorator.treeAttempts = 5;
      }
   }

   @Override
   public AbstractTreeFeature getRandomTree(Random random) {
      return this.tree;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getFoliageColor(BlockPos pos) {
      return 10387789;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getGrassColor(BlockPos pos) {
      return 9470285;
   }

   @Override
   public void decorate(World world, Random random, BlockPos pos) {
      super.decorate(world, random, pos);
   }

   @Override
   public void populateChunk(World world, Random random, BlockStateStorage blocks, int x, int z, double noise) {
      if (this.clayLayers == null || this.seed != world.getSeed()) {
         this.placeClayLayers(world.getSeed());
      }

      if (this.clayPillarNoise == null || this.claySurfaceNoise == null || this.seed != world.getSeed()) {
         Random var8 = new Random(this.seed);
         this.clayPillarNoise = new PerlinNoiseGenerator(var8, 4);
         this.claySurfaceNoise = new PerlinNoiseGenerator(var8, 1);
      }

      this.seed = world.getSeed();
      double var22 = 0.0;
      if (this.mutated) {
         int var10 = (x & -16) + (z & 15);
         int var11 = (z & -16) + (x & 15);
         double var12 = Math.min(Math.abs(noise), this.clayPillarNoise.getNoise((double)var10 * 0.25, (double)var11 * 0.25));
         if (var12 > 0.0) {
            double var14 = 0.001953125;
            double var16 = Math.abs(this.claySurfaceNoise.getNoise((double)var10 * var14, (double)var11 * var14));
            var22 = var12 * var12 * 2.5;
            double var18 = Math.ceil(var16 * 50.0) + 14.0;
            if (var22 > var18) {
               var22 = var18;
            }

            var22 += 64.0;
         }
      }

      int var24 = x & 15;
      int var25 = z & 15;
      boolean var26 = true;
      BlockState var13 = Blocks.STAINED_HARDENED_CLAY.defaultState();
      BlockState var27 = this.subsurfaceBlock;
      int var15 = (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
      boolean var28 = Math.cos(noise / 3.0 * Math.PI) > 0.0;
      int var17 = -1;
      boolean var29 = false;

      for(int var19 = 255; var19 >= 0; --var19) {
         if (blocks.get(var25, var19, var24).getBlock().getMaterial() == Material.AIR && var19 < (int)var22) {
            blocks.set(var25, var19, var24, Blocks.STONE.defaultState());
         }

         if (var19 <= random.nextInt(5)) {
            blocks.set(var25, var19, var24, Blocks.BEDROCK.defaultState());
         } else {
            BlockState var20 = blocks.get(var25, var19, var24);
            if (var20.getBlock().getMaterial() == Material.AIR) {
               var17 = -1;
            } else if (var20.getBlock() == Blocks.STONE) {
               if (var17 == -1) {
                  var29 = false;
                  if (var15 <= 0) {
                     var13 = null;
                     var27 = Blocks.STONE.defaultState();
                  } else if (var19 >= 59 && var19 <= 64) {
                     var13 = Blocks.STAINED_HARDENED_CLAY.defaultState();
                     var27 = this.subsurfaceBlock;
                  }

                  if (var19 < 63 && (var13 == null || var13.getBlock().getMaterial() == Material.AIR)) {
                     var13 = Blocks.WATER.defaultState();
                  }

                  var17 = var15 + Math.max(0, var19 - 63);
                  if (var19 >= 62) {
                     if (this.hasTrees && var19 > 86 + var15 * 2) {
                        if (var28) {
                           int var30 = DirtBlock.Variant.DOARSE_DIRT.getIndex() & 0xFF;
                           blocks.set(var25, var19, var24, Blocks.DIRT.getStateFromMetadata(var30));
                        } else {
                           blocks.set(var25, var19, var24, Blocks.GRASS.defaultState());
                        }
                     } else if (var19 > 66 + var15) {
                        byte var21 = 16;
                        if (var19 < 64 || var19 > 127) {
                           var21 = (byte)(~DyeColor.ORANGE.getMetadata() & 15);
                        } else if (!var28) {
                           var21 = this.getClayLayer(x, var19, z);
                        }

                        if (var21 < 16) {
                           blocks.set(var25, var19, var24, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var21));
                        } else {
                           blocks.set(var25, var19, var24, Blocks.HARDENED_CLAY.defaultState());
                        }
                     } else {
                        blocks.set(var25, var19, var24, this.surfaceBlock);
                        var29 = true;
                     }
                  } else {
                     blocks.set(var25, var19, var24, var27);
                     if (var27.getBlock() == Blocks.STAINED_HARDENED_CLAY) {
                        blocks.set(var25, var19, var24, var27.getBlock().getStateFromMetadata(~DyeColor.ORANGE.getMetadata() & 15));
                     }
                  }
               } else if (var17 > 0) {
                  --var17;
                  if (var29) {
                     blocks.set(var25, var19, var24, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(~DyeColor.ORANGE.getMetadata() & 15));
                  } else {
                     byte var31 = this.getClayLayer(x, var19, z);
                     if (var31 < 16) {
                        blocks.set(var25, var19, var24, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var31));
                     } else {
                        blocks.set(var25, var19, var24, Blocks.HARDENED_CLAY.defaultState());
                     }
                  }
               }
            }
         }
      }
   }

   private void placeClayLayers(long seed) {
      this.clayLayers = new byte[64];
      Arrays.fill(this.clayLayers, (byte)16);
      Random var3 = new Random(seed);
      this.clayOffsetNoise = new PerlinNoiseGenerator(var3, 1);

      for(int var12 = 0; var12 < 64; ++var12) {
         var12 += var3.nextInt(5) + 1;
         if (var12 < 64) {
            this.clayLayers[var12] = (byte)(~DyeColor.ORANGE.getMetadata() & 15);
         }
      }

      int var13 = var3.nextInt(4) + 2;

      for(int var5 = 0; var5 < var13; ++var5) {
         int var6 = var3.nextInt(3) + 1;
         int var7 = var3.nextInt(64);

         for(int var8 = 0; var7 + var8 < 64 && var8 < var6; ++var8) {
            this.clayLayers[var7 + var8] = (byte)(~DyeColor.YELLOW.getMetadata() & 15);
         }
      }

      int var14 = var3.nextInt(4) + 2;

      for(int var15 = 0; var15 < var14; ++var15) {
         int var17 = var3.nextInt(3) + 2;
         int var20 = var3.nextInt(64);

         for(int var9 = 0; var20 + var9 < 64 && var9 < var17; ++var9) {
            this.clayLayers[var20 + var9] = (byte)(~DyeColor.BROWN.getMetadata() & 15);
         }
      }

      int var16 = var3.nextInt(4) + 2;

      for(int var18 = 0; var18 < var16; ++var18) {
         int var21 = var3.nextInt(3) + 1;
         int var23 = var3.nextInt(64);

         for(int var10 = 0; var23 + var10 < 64 && var10 < var21; ++var10) {
            this.clayLayers[var23 + var10] = (byte)(~DyeColor.RED.getMetadata() & 15);
         }
      }

      int var19 = var3.nextInt(3) + 3;
      int var22 = 0;

      for(int var24 = 0; var24 < var19; ++var24) {
         byte var25 = 1;
         var22 += var3.nextInt(16) + 4;

         for(int var11 = 0; var22 + var11 < 64 && var11 < var25; ++var11) {
            this.clayLayers[var22 + var11] = (byte)(~DyeColor.WHITE.getMetadata() & 15);
            if (var22 + var11 > 1 && var3.nextBoolean()) {
               this.clayLayers[var22 + var11 - 1] = (byte)(~DyeColor.SILVER.getMetadata() & 15);
            }

            if (var22 + var11 < 63 && var3.nextBoolean()) {
               this.clayLayers[var22 + var11 + 1] = (byte)(~DyeColor.SILVER.getMetadata() & 15);
            }
         }
      }
   }

   private byte getClayLayer(int x, int y, int z) {
      int var4 = (int)Math.round(this.clayOffsetNoise.getNoise((double)x * 1.0 / 512.0, (double)x * 1.0 / 512.0) * 2.0);
      return this.clayLayers[(y + var4 + 64) % 64];
   }

   @Override
   protected Biome mutate(int id) {
      boolean var2 = this.id == Biome.MESA.id;
      MesaBiome var3 = new MesaBiome(id, var2, this.hasTrees);
      if (!var2) {
         var3.setHeight(HILLS_HEIGHT);
         var3.setName(this.name + " M");
      } else {
         var3.setName(this.name + " (Bryce)");
      }

      var3.setColor(this.biomeColor, true);
      return var3;
   }
}
