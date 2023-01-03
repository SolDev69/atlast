package net.minecraft.entity.living.mob;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;

public final class MobSpawnerHelper {
   private static final int f_59vszgzdf = (int)Math.pow(17.0, 2.0);
   private final Set spawnedMobs = Sets.newHashSet();

   public int spawnEntities(ServerWorld world, boolean allowAnimals, boolean allowMonsters, boolean shouldSpawnAnimals) {
      if (!allowAnimals && !allowMonsters) {
         return 0;
      } else {
         this.spawnedMobs.clear();
         int var5 = 0;

         for(PlayerEntity var7 : world.players) {
            if (!var7.isSpectator()) {
               int var8 = MathHelper.floor(var7.x / 16.0);
               int var9 = MathHelper.floor(var7.z / 16.0);
               byte var10 = 8;

               for(int var11 = -var10; var11 <= var10; ++var11) {
                  for(int var12 = -var10; var12 <= var10; ++var12) {
                     boolean var13 = var11 == -var10 || var11 == var10 || var12 == -var10 || var12 == var10;
                     ChunkPos var14 = new ChunkPos(var11 + var8, var12 + var9);
                     if (!this.spawnedMobs.contains(var14)) {
                        ++var5;
                        if (!var13 && world.getWorldBorder().contains(var14)) {
                           this.spawnedMobs.add(var14);
                        }
                     }
                  }
               }
            }
         }

         int var36 = 0;
         BlockPos var37 = world.getSpawnPoint();

         for(MobSpawnGroup var41 : MobSpawnGroup.values()) {
            if ((!var41.isPeaceful() || allowMonsters) && (var41.isPeaceful() || allowAnimals) && (!var41.isRare() || shouldSpawnAnimals)) {
               int var42 = world.getEntityCount(var41.getType());
               int var43 = var41.getCapacity() * var5 / f_59vszgzdf;
               if (var42 <= var43) {
                  label126:
                  for(ChunkPos var15 : this.spawnedMobs) {
                     BlockPos var16 = getRandomPosInChunk(world, var15.x, var15.z);
                     int var17 = var16.getX();
                     int var18 = var16.getY();
                     int var19 = var16.getZ();
                     Block var20 = world.getBlockState(var16).getBlock();
                     if (!var20.isConductor()) {
                        int var21 = 0;

                        for(int var22 = 0; var22 < 3; ++var22) {
                           int var23 = var17;
                           int var24 = var18;
                           int var25 = var19;
                           byte var26 = 6;
                           Biome.SpawnEntry var27 = null;
                           EntityData var28 = null;

                           for(int var29 = 0; var29 < 4; ++var29) {
                              var23 += world.random.nextInt(var26) - world.random.nextInt(var26);
                              var24 += world.random.nextInt(1) - world.random.nextInt(1);
                              var25 += world.random.nextInt(var26) - world.random.nextInt(var26);
                              BlockPos var30 = new BlockPos(var23, var24, var25);
                              float var31 = (float)var23 + 0.5F;
                              float var32 = (float)var25 + 0.5F;
                              if (!world.isPlayerWithinRange((double)var31, (double)var24, (double)var32, 24.0)
                                 && !(var37.squaredDistanceTo((double)var31, (double)var24, (double)var32) < 576.0)) {
                                 if (var27 == null) {
                                    var27 = world.pickSpawnEntry(var41, var30);
                                    if (var27 == null) {
                                       break;
                                    }
                                 }

                                 MobEntity var33;
                                 try {
                                    var33 = (MobEntity)var27.type.getConstructor(World.class).newInstance(world);
                                 } catch (Exception var35) {
                                    var35.printStackTrace();
                                    return var36;
                                 }

                                 if (canSpawn(var33.m_84jincljh(), world, var30)) {
                                    var33.refreshPositionAndAngles((double)var31, (double)var24, (double)var32, world.random.nextFloat() * 360.0F, 0.0F);
                                    if (var33.canSpawn() && var33.m_52qkzdxky()) {
                                       var28 = var33.initialize(world.getLocalDifficulty(new BlockPos(var33)), var28);
                                       if (var33.m_52qkzdxky()) {
                                          ++var21;
                                          world.addEntity(var33);
                                       }

                                       if (var21 >= var33.getLimitPerChunk()) {
                                          continue label126;
                                       }
                                    }

                                    var36 += var21;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         return var36;
      }
   }

   protected static BlockPos getRandomPosInChunk(World world, int x, int z) {
      WorldChunk var3 = world.getChunkAt(x, z);
      int var4 = x * 16 + world.random.nextInt(16);
      int var5 = z * 16 + world.random.nextInt(16);
      int var6 = MathHelper.roundUp(var3.getHeight(new BlockPos(var4, 0, var5)), 16);
      int var7 = world.random.nextInt(var6 > 0 ? var6 : var3.getHighestSectionOffset() + 16 - 1);
      return new BlockPos(var4, var7, var5);
   }

   public static boolean canSpawn(MobEntity.Environment spawnGroup, World world, BlockPos pos) {
      if (!world.getWorldBorder().contains(pos)) {
         return false;
      } else {
         Block var3 = world.getBlockState(pos).getBlock();
         if (spawnGroup == MobEntity.Environment.IN_WATER) {
            return var3.getMaterial().isLiquid()
               && world.getBlockState(pos.down()).getBlock().getMaterial().isLiquid()
               && !world.getBlockState(pos.up()).getBlock().isConductor();
         } else {
            BlockPos var4 = pos.down();
            if (!World.hasSolidTop(world, var4)) {
               return false;
            } else {
               return world.getBlockState(var4).getBlock() != Blocks.BEDROCK
                  && !var3.isConductor()
                  && !var3.getMaterial().isLiquid()
                  && !world.getBlockState(pos.up()).getBlock().isConductor();
            }
         }
      }
   }

   public static void populateEntities(World world, Biome biome, int chunkStartX, int chunkStartY, int chunkSizeX, int chunkSizeY, Random random) {
      List var7 = biome.getSpawnEntries(MobSpawnGroup.CREATURE);
      if (!var7.isEmpty()) {
         while(random.nextFloat() < biome.getSpawnChance()) {
            Biome.SpawnEntry var8 = (Biome.SpawnEntry)WeightedPicker.pick(world.random, var7);
            int var9 = var8.minGroupSize + random.nextInt(1 + var8.maxGroupSize - var8.minGroupSize);
            EntityData var10 = null;
            int var11 = chunkStartX + random.nextInt(chunkSizeX);
            int var12 = chunkStartY + random.nextInt(chunkSizeY);
            int var13 = var11;
            int var14 = var12;

            for(int var15 = 0; var15 < var9; ++var15) {
               boolean var16 = false;

               for(int var17 = 0; !var16 && var17 < 4; ++var17) {
                  BlockPos var18 = world.getSurfaceHeight(new BlockPos(var11, 0, var12));
                  if (canSpawn(MobEntity.Environment.ON_GROUND, world, var18)) {
                     MobEntity var19;
                     try {
                        var19 = (MobEntity)var8.type.getConstructor(World.class).newInstance(world);
                     } catch (Exception var21) {
                        var21.printStackTrace();
                        continue;
                     }

                     var19.refreshPositionAndAngles(
                        (double)((float)var11 + 0.5F), (double)var18.getY(), (double)((float)var12 + 0.5F), random.nextFloat() * 360.0F, 0.0F
                     );
                     world.addEntity(var19);
                     var10 = var19.initialize(world.getLocalDifficulty(new BlockPos(var19)), var10);
                     var16 = true;
                  }

                  var11 += random.nextInt(5) - random.nextInt(5);

                  for(var12 += random.nextInt(5) - random.nextInt(5);
                     var11 < chunkStartX || var11 >= chunkStartX + chunkSizeX || var12 < chunkStartY || var12 >= chunkStartY + chunkSizeX;
                     var12 = var14 + random.nextInt(5) - random.nextInt(5)
                  ) {
                     var11 = var13 + random.nextInt(5) - random.nextInt(5);
                  }
               }
            }
         }
      }
   }
}
