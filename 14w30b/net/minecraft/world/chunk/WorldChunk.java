package net.minecraft.world.chunk;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeSource;
import net.minecraft.world.gen.WorldGeneratorType;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldChunk {
   private static final Logger LOGGER = LogManager.getLogger();
   private final WorldChunkSection[] sections = new WorldChunkSection[16];
   private final byte[] biomes = new byte[256];
   private final int[] precipitationHeight = new int[256];
   private final boolean[] needsLightUpdate = new boolean[256];
   private boolean loaded;
   private final World world;
   private final int[] heightMap;
   public final int chunkX;
   public final int chunkZ;
   private boolean recheckGap;
   private final Map blockEntities = Maps.newHashMap();
   private final List[] entitiesBySection;
   private boolean terrainPopulated;
   private boolean lightPopulated;
   private boolean hasTicked;
   private boolean dirty;
   private boolean containsEntities;
   private long lastSaveTime;
   private int lowestHeight;
   private long inhabitedTime;
   private int queuedLightChecks = 4096;

   public WorldChunk(World world, int chunkX, int chunkZ) {
      this.entitiesBySection = new List[16];
      this.world = world;
      this.chunkX = chunkX;
      this.chunkZ = chunkZ;
      this.heightMap = new int[256];

      for(int var4 = 0; var4 < this.entitiesBySection.length; ++var4) {
         this.entitiesBySection[var4] = Lists.newArrayList();
      }

      Arrays.fill(this.precipitationHeight, -999);
      Arrays.fill(this.biomes, (byte)-1);
   }

   public WorldChunk(World world, BlockStateStorage storage, int chunkX, int chunkZ) {
      this(world, chunkX, chunkZ);
      short var5 = 256;
      boolean var6 = !world.dimension.isDark();

      for(int var7 = 0; var7 < 16; ++var7) {
         for(int var8 = 0; var8 < 16; ++var8) {
            for(int var9 = 0; var9 < var5; ++var9) {
               int var10 = var7 * var5 * 16 | var8 * var5 | var9;
               BlockState var11 = storage.get(var10);
               if (var11.getBlock().getMaterial() != Material.AIR) {
                  int var12 = var9 >> 4;
                  if (this.sections[var12] == null) {
                     this.sections[var12] = new WorldChunkSection(var12 << 4, var6);
                  }

                  this.sections[var12].setBlockState(var7, var9 & 15, var8, var11);
               }
            }
         }
      }
   }

   public boolean isAt(int chunkX, int chunkZ) {
      return chunkX == this.chunkX && chunkZ == this.chunkZ;
   }

   public int getHeight(BlockPos pos) {
      return this.getHeight(pos.getX() & 15, pos.getZ() & 15);
   }

   public int getHeight(int sectionX, int sectionZ) {
      return this.heightMap[sectionZ << 4 | sectionX];
   }

   public int getHighestSectionOffset() {
      for(int var1 = this.sections.length - 1; var1 >= 0; --var1) {
         if (this.sections[var1] != null) {
            return this.sections[var1].getOffsetY();
         }
      }

      return 0;
   }

   public WorldChunkSection[] getSections() {
      return this.sections;
   }

   @Environment(EnvType.CLIENT)
   protected void populateHeightmap() {
      int var1 = this.getHighestSectionOffset();
      this.lowestHeight = Integer.MAX_VALUE;

      for(int var2 = 0; var2 < 16; ++var2) {
         for(int var3 = 0; var3 < 16; ++var3) {
            this.precipitationHeight[var2 + (var3 << 4)] = -999;

            for(int var4 = var1 + 16 - 1; var4 >= 0; --var4) {
               Block var5 = this.getBlockAt(var2, var4, var3);
               if (var5.getOpacity() != 0) {
                  this.heightMap[var3 << 4 | var2] = var4;
                  if (var4 < this.lowestHeight) {
                     this.lowestHeight = var4;
                  }
                  break;
               }
            }
         }
      }

      this.dirty = true;
   }

   public void populateSkylight() {
      int var1 = this.getHighestSectionOffset();
      this.lowestHeight = Integer.MAX_VALUE;

      for(int var2 = 0; var2 < 16; ++var2) {
         for(int var3 = 0; var3 < 16; ++var3) {
            this.precipitationHeight[var2 + (var3 << 4)] = -999;

            for(int var4 = var1 + 16 - 1; var4 >= 0; --var4) {
               if (this.getOpacityAt(var2, var4, var3) != 0) {
                  this.heightMap[var3 << 4 | var2] = var4;
                  if (var4 < this.lowestHeight) {
                     this.lowestHeight = var4;
                  }
                  break;
               }
            }

            if (!this.world.dimension.isDark()) {
               int var8 = 15;
               int var5 = var1 + 16 - 1;

               while(true) {
                  int var6 = this.getOpacityAt(var2, var5, var3);
                  if (var6 == 0 && var8 != 15) {
                     var6 = 1;
                  }

                  var8 -= var6;
                  if (var8 > 0) {
                     WorldChunkSection var7 = this.sections[var5 >> 4];
                     if (var7 != null) {
                        var7.setSkyLight(var2, var5 & 15, var3, var8);
                        this.world.onLightChanged(new BlockPos((this.chunkX << 4) + var2, var5, (this.chunkZ << 4) + var3));
                     }
                  }

                  if (--var5 <= 0 || var8 <= 0) {
                     break;
                  }
               }
            }
         }
      }

      this.dirty = true;
   }

   private void queueLightUpdate(int sectionX, int sectionZ) {
      this.needsLightUpdate[sectionX + sectionZ * 16] = true;
      this.recheckGap = true;
   }

   private void recheckGaps(boolean checkOne) {
      this.world.profiler.push("recheckGaps");
      if (this.world.isRegionLoaded(new BlockPos(this.chunkX * 16 + 8, 0, this.chunkZ * 16 + 8), 16)) {
         for(int var2 = 0; var2 < 16; ++var2) {
            for(int var3 = 0; var3 < 16; ++var3) {
               if (this.needsLightUpdate[var2 + var3 * 16]) {
                  this.needsLightUpdate[var2 + var3 * 16] = false;
                  int var4 = this.getHeight(var2, var3);
                  int var5 = this.chunkX * 16 + var2;
                  int var6 = this.chunkZ * 16 + var3;
                  int var7 = Integer.MAX_VALUE;

                  for(Direction var9 : Direction.Plane.HORIZONTAL) {
                     var7 = Math.min(var7, this.world.getLowestHeight(var5 + var9.getOffsetX(), var6 + var9.getOffsetZ()));
                  }

                  this.checkSkylight(var5, var6, var7);

                  for(Direction var11 : Direction.Plane.HORIZONTAL) {
                     this.checkSkylight(var5 + var11.getOffsetX(), var6 + var11.getOffsetZ(), var4);
                  }

                  if (checkOne) {
                     this.world.profiler.pop();
                     return;
                  }
               }
            }
         }

         this.recheckGap = false;
      }

      this.world.profiler.pop();
   }

   private void checkSkylight(int x, int z, int maxY) {
      int var4 = this.world.getHeight(new BlockPos(x, 0, z)).getY();
      if (var4 > maxY) {
         this.checkSkylight(x, z, maxY, var4 + 1);
      } else if (var4 < maxY) {
         this.checkSkylight(x, z, var4, maxY + 1);
      }
   }

   private void checkSkylight(int x, int z, int minY, int maxY) {
      if (maxY > minY && this.world.isRegionLoaded(new BlockPos(x, 0, z), 16)) {
         for(int var5 = minY; var5 < maxY; ++var5) {
            this.world.checkLight(LightType.SKY, new BlockPos(x, var5, z));
         }

         this.dirty = true;
      }
   }

   private void resetLightAt(int sectionX, int y, int sectionZ) {
      int var4 = this.heightMap[sectionZ << 4 | sectionX] & 0xFF;
      int var5 = var4;
      if (y > var4) {
         var5 = y;
      }

      while(var5 > 0 && this.getOpacityAt(sectionX, var5 - 1, sectionZ) == 0) {
         --var5;
      }

      if (var5 != var4) {
         this.world.checkLight(sectionX + this.chunkX * 16, sectionZ + this.chunkZ * 16, var5, var4);
         this.heightMap[sectionZ << 4 | sectionX] = var5;
         int var6 = this.chunkX * 16 + sectionX;
         int var7 = this.chunkZ * 16 + sectionZ;
         if (!this.world.dimension.isDark()) {
            if (var5 < var4) {
               for(int var13 = var5; var13 < var4; ++var13) {
                  WorldChunkSection var16 = this.sections[var13 >> 4];
                  if (var16 != null) {
                     var16.setSkyLight(sectionX, var13 & 15, sectionZ, 15);
                     this.world.onLightChanged(new BlockPos((this.chunkX << 4) + sectionX, var13, (this.chunkZ << 4) + sectionZ));
                  }
               }
            } else {
               for(int var8 = var4; var8 < var5; ++var8) {
                  WorldChunkSection var9 = this.sections[var8 >> 4];
                  if (var9 != null) {
                     var9.setSkyLight(sectionX, var8 & 15, sectionZ, 0);
                     this.world.onLightChanged(new BlockPos((this.chunkX << 4) + sectionX, var8, (this.chunkZ << 4) + sectionZ));
                  }
               }
            }

            int var14 = 15;

            while(var5 > 0 && var14 > 0) {
               int var17 = this.getOpacityAt(sectionX, --var5, sectionZ);
               if (var17 == 0) {
                  var17 = 1;
               }

               var14 -= var17;
               if (var14 < 0) {
                  var14 = 0;
               }

               WorldChunkSection var10 = this.sections[var5 >> 4];
               if (var10 != null) {
                  var10.setSkyLight(sectionX, var5 & 15, sectionZ, var14);
               }
            }
         }

         int var15 = this.heightMap[sectionZ << 4 | sectionX];
         int var18 = var4;
         int var19 = var15;
         if (var15 < var4) {
            var18 = var15;
            var19 = var4;
         }

         if (var15 < this.lowestHeight) {
            this.lowestHeight = var15;
         }

         if (!this.world.dimension.isDark()) {
            for(Direction var12 : Direction.Plane.HORIZONTAL) {
               this.checkSkylight(var6 + var12.getOffsetX(), var7 + var12.getOffsetZ(), var18, var19);
            }

            this.checkSkylight(var6, var7, var18, var19);
         }

         this.dirty = true;
      }
   }

   public int getOpacity(BlockPos pos) {
      return this.getBlock(pos).getOpacity();
   }

   private int getOpacityAt(int sectionX, int y, int sectionZ) {
      return this.getBlockAt(sectionX, y, sectionZ).getOpacity();
   }

   private Block getBlockAt(int sectionX, int y, int sectionZ) {
      Block var4 = Blocks.AIR;
      if (y >= 0 && y >> 4 < this.sections.length) {
         WorldChunkSection var5 = this.sections[y >> 4];
         if (var5 != null) {
            try {
               var4 = var5.getBlock(sectionX, y & 15, sectionZ);
            } catch (Throwable var8) {
               CrashReport var7 = CrashReport.of(var8, "Getting block");
               throw new CrashException(var7);
            }
         }
      }

      return var4;
   }

   public Block getBlock(int x, int y, int z) {
      try {
         return this.getBlockAt(x & 15, y, z & 15);
      } catch (CrashException var6) {
         CashReportCategory var5 = var6.getReport().addCategory("Block being got");
         var5.add("Location", new Callable() {
            public String call() {
               return CashReportCategory.formatPosition(new BlockPos(WorldChunk.this.chunkX * 16 + x, y, WorldChunk.this.chunkZ * 16 + z));
            }
         });
         throw var6;
      }
   }

   public Block getBlock(BlockPos pos) {
      try {
         return this.getBlockAt(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
      } catch (CrashException var4) {
         CashReportCategory var3 = var4.getReport().addCategory("Block being got");
         var3.add("Location", new Callable() {
            public String call() {
               return CashReportCategory.formatPosition(pos);
            }
         });
         throw var4;
      }
   }

   public BlockState getBlockState(BlockPos pos) {
      if (this.world.getGeneratorType() == WorldGeneratorType.DEBUG_ALL_BLOCK_STATES) {
         BlockState var7 = null;
         if (pos.getY() == 60) {
            var7 = Blocks.BARRIER.defaultState();
         }

         if (pos.getY() == 70) {
            var7 = DebugChunkGenerator.getBlockState(pos.getX(), pos.getZ());
         }

         return var7 == null ? Blocks.AIR.defaultState() : var7;
      } else {
         try {
            if (pos.getY() >= 0 && pos.getY() >> 4 < this.sections.length) {
               WorldChunkSection var2 = this.sections[pos.getY() >> 4];
               if (var2 != null) {
                  int var8 = pos.getX() & 15;
                  int var9 = pos.getY() & 15;
                  int var5 = pos.getZ() & 15;
                  return var2.getBlockState(var8, var9, var5);
               }
            }

            return Blocks.AIR.defaultState();
         } catch (Throwable var6) {
            CrashReport var3 = CrashReport.of(var6, "Getting block state");
            CashReportCategory var4 = var3.addCategory("Block being got");
            var4.add("Location", new Callable() {
               public String call() {
                  return CashReportCategory.formatPosition(pos);
               }
            });
            throw new CrashException(var3);
         }
      }
   }

   private int getBlockMetadataAt(int sectionX, int y, int sectionZ) {
      if (y >> 4 >= this.sections.length) {
         return 0;
      } else {
         WorldChunkSection var4 = this.sections[y >> 4];
         return var4 != null ? var4.getBlockMetadata(sectionX, y & 15, sectionZ) : 0;
      }
   }

   public int getBlockMetadata(BlockPos pos) {
      return this.getBlockMetadataAt(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
   }

   public BlockState setBlockState(BlockPos pos, BlockState state) {
      int var3 = pos.getX() & 15;
      int var4 = pos.getY();
      int var5 = pos.getZ() & 15;
      int var6 = var5 << 4 | var3;
      if (var4 >= this.precipitationHeight[var6] - 1) {
         this.precipitationHeight[var6] = -999;
      }

      int var7 = this.heightMap[var6];
      BlockState var8 = this.getBlockState(pos);
      if (var8 == state) {
         return null;
      } else {
         Block var9 = state.getBlock();
         Block var10 = var8.getBlock();
         WorldChunkSection var11 = this.sections[var4 >> 4];
         boolean var12 = false;
         if (var11 == null) {
            if (var9 == Blocks.AIR) {
               return null;
            }

            var11 = this.sections[var4 >> 4] = new WorldChunkSection(var4 >> 4 << 4, !this.world.dimension.isDark());
            var12 = var4 >= var7;
         }

         var11.setBlockState(var3, var4 & 15, var5, state);
         if (var10 != var9) {
            if (!this.world.isClient) {
               var10.onRemoved(this.world, pos, var8);
            } else if (var10 instanceof BlockEntityProvider) {
               this.world.removeBlockEntity(pos);
            }
         }

         if (var11.getBlock(var3, var4 & 15, var5) != var9) {
            return null;
         } else {
            if (var12) {
               this.populateSkylight();
            } else {
               int var13 = var9.getOpacity();
               int var14 = var10.getOpacity();
               if (var13 > 0) {
                  if (var4 >= var7) {
                     this.resetLightAt(var3, var4 + 1, var5);
                  }
               } else if (var4 == var7 - 1) {
                  this.resetLightAt(var3, var4, var5);
               }

               if (var13 != var14 && (var13 < var14 || this.getLight(LightType.SKY, pos) > 0 || this.getLight(LightType.BLOCK, pos) > 0)) {
                  this.queueLightUpdate(var3, var5);
               }
            }

            if (var10 instanceof BlockEntityProvider) {
               BlockEntity var15 = this.getBlockEntity(pos);
               if (var15 != null) {
                  var15.clearBlockCache();
               }
            }

            if (!this.world.isClient && var10 != var9) {
               var9.onAdded(this.world, pos, state);
            }

            if (var9 instanceof BlockEntityProvider) {
               BlockEntity var16 = this.getBlockEntity(pos);
               if (var16 == null) {
                  var16 = ((BlockEntityProvider)var9).createBlockEntity(this.world, var9.getMetadataFromState(state));
                  this.world.setBlockEntity(pos, var16);
               }

               if (var16 != null) {
                  var16.clearBlockCache();
               }
            }

            this.dirty = true;
            return var8;
         }
      }
   }

   public int getLight(LightType type, BlockPos pos) {
      int var3 = pos.getX() & 15;
      int var4 = pos.getY();
      int var5 = pos.getZ() & 15;
      WorldChunkSection var6 = this.sections[var4 >> 4];
      if (var6 == null) {
         return this.hasSkyAccess(pos) ? type.defaultValue : 0;
      } else if (type == LightType.SKY) {
         return this.world.dimension.isDark() ? 0 : var6.getSkyLight(var3, var4 & 15, var5);
      } else {
         return type == LightType.BLOCK ? var6.getBlockLight(var3, var4 & 15, var5) : type.defaultValue;
      }
   }

   public void setLight(LightType type, BlockPos pos, int light) {
      int var4 = pos.getX() & 15;
      int var5 = pos.getY();
      int var6 = pos.getZ() & 15;
      WorldChunkSection var7 = this.sections[var5 >> 4];
      if (var7 == null) {
         var7 = this.sections[var5 >> 4] = new WorldChunkSection(var5 >> 4 << 4, !this.world.dimension.isDark());
         this.populateSkylight();
      }

      this.dirty = true;
      if (type == LightType.SKY) {
         if (!this.world.dimension.isDark()) {
            var7.setSkyLight(var4, var5 & 15, var6, light);
         }
      } else if (type == LightType.BLOCK) {
         var7.setBlockLight(var4, var5 & 15, var6, light);
      }
   }

   public int getLight(BlockPos pos, int ambientDarkness) {
      int var3 = pos.getX() & 15;
      int var4 = pos.getY();
      int var5 = pos.getZ() & 15;
      WorldChunkSection var6 = this.sections[var4 >> 4];
      if (var6 == null) {
         return !this.world.dimension.isDark() && ambientDarkness < LightType.SKY.defaultValue ? LightType.SKY.defaultValue - ambientDarkness : 0;
      } else {
         int var7 = this.world.dimension.isDark() ? 0 : var6.getSkyLight(var3, var4 & 15, var5);
         var7 -= ambientDarkness;
         int var8 = var6.getBlockLight(var3, var4 & 15, var5);
         if (var8 > var7) {
            var7 = var8;
         }

         return var7;
      }
   }

   public void addEntity(Entity entity) {
      this.containsEntities = true;
      int var2 = MathHelper.floor(entity.x / 16.0);
      int var3 = MathHelper.floor(entity.z / 16.0);
      if (var2 != this.chunkX || var3 != this.chunkZ) {
         LOGGER.warn("Wrong location! (" + var2 + ", " + var3 + ") should be (" + this.chunkX + ", " + this.chunkZ + "), " + entity);
         Thread.dumpStack();
         entity.remove();
      }

      int var4 = MathHelper.floor(entity.y / 16.0);
      if (var4 < 0) {
         var4 = 0;
      }

      if (var4 >= this.entitiesBySection.length) {
         var4 = this.entitiesBySection.length - 1;
      }

      entity.isLoaded = true;
      entity.chunkX = this.chunkX;
      entity.chunkY = var4;
      entity.chunkZ = this.chunkZ;
      this.entitiesBySection[var4].add(entity);
   }

   public void removeEntity(Entity entity) {
      this.removeEntity(entity, entity.chunkY);
   }

   public void removeEntity(Entity entity, int chunkY) {
      if (chunkY < 0) {
         chunkY = 0;
      }

      if (chunkY >= this.entitiesBySection.length) {
         chunkY = this.entitiesBySection.length - 1;
      }

      this.entitiesBySection[chunkY].remove(entity);
   }

   public boolean hasSkyAccess(BlockPos pos) {
      int var2 = pos.getX() & 15;
      int var3 = pos.getY();
      int var4 = pos.getZ() & 15;
      return var3 >= this.heightMap[var4 << 4 | var2];
   }

   public BlockEntity getBlockEntity(BlockPos pos) {
      BlockEntity var2 = (BlockEntity)this.blockEntities.get(pos);
      if (var2 == null) {
         Block var3 = this.getBlock(pos);
         if (!var3.hasBlockEntity()) {
            return null;
         }

         var2 = ((BlockEntityProvider)var3).createBlockEntity(this.world, this.getBlockMetadata(pos));
         this.world.setBlockEntity(pos, var2);
      }

      if (var2 != null && var2.isRemoved()) {
         this.blockEntities.remove(pos);
         return null;
      } else {
         return var2;
      }
   }

   public void addBlockEntity(BlockEntity blockEntity) {
      this.setBlockEntity(blockEntity.getPos(), blockEntity);
      if (this.loaded) {
         this.world.addBlockEntity(blockEntity);
      }
   }

   public void setBlockEntity(BlockPos pos, BlockEntity blockEntity) {
      blockEntity.setWorld(this.world);
      blockEntity.setPos(pos);
      if (this.getBlock(pos) instanceof BlockEntityProvider) {
         if (this.blockEntities.containsKey(pos)) {
            ((BlockEntity)this.blockEntities.get(pos)).markRemoved();
         }

         blockEntity.cancelRemoval();
         this.blockEntities.put(pos, blockEntity);
      }
   }

   public void removeBlockEntity(BlockPos pos) {
      if (this.loaded) {
         BlockEntity var2 = (BlockEntity)this.blockEntities.remove(pos);
         if (var2 != null) {
            var2.markRemoved();
         }
      }
   }

   public void load() {
      this.loaded = true;
      this.world.addBlockEntities(this.blockEntities.values());

      for(int var1 = 0; var1 < this.entitiesBySection.length; ++var1) {
         for(Entity var3 : this.entitiesBySection[var1]) {
            var3.beforeLoadedIntoWorld();
         }

         this.world.addEntities(this.entitiesBySection[var1]);
      }
   }

   public void unload() {
      this.loaded = false;

      for(BlockEntity var2 : this.blockEntities.values()) {
         this.world.unloadBlockEntity(var2);
      }

      for(int var3 = 0; var3 < this.entitiesBySection.length; ++var3) {
         this.world.removeEntities(this.entitiesBySection[var3]);
      }
   }

   public void markDirty() {
      this.dirty = true;
   }

   public void getEntities(Entity exclude, Box box, List entities, Predicate filter) {
      int var5 = MathHelper.floor((box.minY - 2.0) / 16.0);
      int var6 = MathHelper.floor((box.maxY + 2.0) / 16.0);
      var5 = MathHelper.clamp(var5, 0, this.entitiesBySection.length - 1);
      var6 = MathHelper.clamp(var6, 0, this.entitiesBySection.length - 1);

      for(int var7 = var5; var7 <= var6; ++var7) {
         List var8 = this.entitiesBySection[var7];

         for(int var9 = 0; var9 < var8.size(); ++var9) {
            Entity var10 = (Entity)var8.get(var9);
            if (var10 != exclude && var10.getBoundingBox().intersects(box) && (filter == null || filter.apply(var10))) {
               entities.add(var10);
               Entity[] var11 = var10.getParts();
               if (var11 != null) {
                  for(int var12 = 0; var12 < var11.length; ++var12) {
                     var10 = var11[var12];
                     if (var10 != exclude && var10.getBoundingBox().intersects(box) && (filter == null || filter.apply(var10))) {
                        entities.add(var10);
                     }
                  }
               }
            }
         }
      }
   }

   public void getEntities(Class type, Box box, List entities, Predicate filter) {
      int var5 = MathHelper.floor((box.minY - 2.0) / 16.0);
      int var6 = MathHelper.floor((box.maxY + 2.0) / 16.0);
      var5 = MathHelper.clamp(var5, 0, this.entitiesBySection.length - 1);
      var6 = MathHelper.clamp(var6, 0, this.entitiesBySection.length - 1);

      for(int var7 = var5; var7 <= var6; ++var7) {
         List var8 = this.entitiesBySection[var7];

         for(int var9 = 0; var9 < var8.size(); ++var9) {
            Entity var10 = (Entity)var8.get(var9);
            if (type.isAssignableFrom(var10.getClass()) && var10.getBoundingBox().intersects(box) && (filter == null || filter.apply(var10))) {
               entities.add(var10);
            }
         }
      }
   }

   public boolean shouldSave(boolean saveEntities) {
      if (saveEntities) {
         if (this.containsEntities && this.world.getTime() != this.lastSaveTime || this.dirty) {
            return true;
         }
      } else if (this.containsEntities && this.world.getTime() >= this.lastSaveTime + 600L) {
         return true;
      }

      return this.dirty;
   }

   public Random getRandomForSlime(long seed) {
      return new Random(
         this.world.getSeed()
               + (long)(this.chunkX * this.chunkX * 4987142)
               + (long)(this.chunkX * 5947611)
               + (long)(this.chunkZ * this.chunkZ) * 4392871L
               + (long)(this.chunkZ * 389711)
            ^ seed
      );
   }

   public boolean isEmpty() {
      return false;
   }

   public void populate(ChunkSource source, ChunkSource generator, int chunkX, int chunkZ) {
      boolean var5 = source.isLoaded(chunkX, chunkZ - 1);
      boolean var6 = source.isLoaded(chunkX + 1, chunkZ);
      boolean var7 = source.isLoaded(chunkX, chunkZ + 1);
      boolean var8 = source.isLoaded(chunkX - 1, chunkZ);
      boolean var9 = source.isLoaded(chunkX - 1, chunkZ - 1);
      boolean var10 = source.isLoaded(chunkX + 1, chunkZ + 1);
      boolean var11 = source.isLoaded(chunkX - 1, chunkZ + 1);
      boolean var12 = source.isLoaded(chunkX + 1, chunkZ - 1);
      if (var6 && var7 && var10) {
         if (!this.terrainPopulated) {
            source.populate(generator, chunkX, chunkZ);
         } else {
            source.populateSpecial(generator, this, chunkX, chunkZ);
         }
      }

      if (var8 && var7 && var11) {
         WorldChunk var13 = source.getChunk(chunkX - 1, chunkZ);
         if (!var13.terrainPopulated) {
            source.populate(generator, chunkX - 1, chunkZ);
         } else {
            source.populateSpecial(generator, var13, chunkX - 1, chunkZ);
         }
      }

      if (var5 && var6 && var12) {
         WorldChunk var14 = source.getChunk(chunkX, chunkZ - 1);
         if (!var14.terrainPopulated) {
            source.populate(generator, chunkX, chunkZ - 1);
         } else {
            source.populateSpecial(generator, var14, chunkX, chunkZ - 1);
         }
      }

      if (var9 && var5 && var8) {
         WorldChunk var15 = source.getChunk(chunkX - 1, chunkZ - 1);
         if (!var15.terrainPopulated) {
            source.populate(generator, chunkX - 1, chunkZ - 1);
         } else {
            source.populateSpecial(generator, var15, chunkX - 1, chunkZ - 1);
         }
      }
   }

   public BlockPos getPrecipitationHeight(BlockPos pos) {
      int var2 = pos.getX() & 15;
      int var3 = pos.getZ() & 15;
      int var4 = var2 | var3 << 4;
      BlockPos var5 = new BlockPos(pos.getX(), this.precipitationHeight[var4], pos.getZ());
      if (var5.getY() == -999) {
         int var6 = this.getHighestSectionOffset() + 15;
         var5 = new BlockPos(pos.getX(), var6, pos.getZ());
         int var7 = -1;

         while(var5.getY() > 0 && var7 == -1) {
            Block var8 = this.getBlock(var5);
            Material var9 = var8.getMaterial();
            if (!var9.blocksMovement() && !var9.isLiquid()) {
               var5 = var5.down();
            } else {
               var7 = var5.getY() + 1;
            }
         }

         this.precipitationHeight[var4] = var7;
      }

      return new BlockPos(pos.getX(), this.precipitationHeight[var4], pos.getZ());
   }

   public void tick(boolean skipRecheckGaps) {
      if (this.recheckGap && !this.world.dimension.isDark() && !skipRecheckGaps) {
         this.recheckGaps(this.world.isClient);
      }

      this.hasTicked = true;
      if (!this.lightPopulated && this.terrainPopulated) {
         this.populateLight();
      }
   }

   public boolean isPopulated() {
      return this.hasTicked && this.terrainPopulated && this.lightPopulated;
   }

   public ChunkPos getPos() {
      return new ChunkPos(this.chunkX, this.chunkZ);
   }

   public boolean isEmpty(int minY, int maxY) {
      if (minY < 0) {
         minY = 0;
      }

      if (maxY >= 256) {
         maxY = 255;
      }

      for(int var3 = minY; var3 <= maxY; var3 += 16) {
         WorldChunkSection var4 = this.sections[var3 >> 4];
         if (var4 != null && !var4.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public void setSections(WorldChunkSection[] sections) {
      if (this.sections.length != sections.length) {
         LOGGER.warn("Could not set level chunk sections, array length is " + sections.length + " instead of " + this.sections.length);
      } else {
         for(int var2 = 0; var2 < this.sections.length; ++var2) {
            this.sections[var2] = sections[var2];
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public void set(byte[] rawChunkData, int sectionsWithData, boolean full) {
      int var4 = 0;
      boolean var5 = !this.world.dimension.isDark();

      for(int var6 = 0; var6 < this.sections.length; ++var6) {
         if ((sectionsWithData & 1 << var6) != 0) {
            if (this.sections[var6] == null) {
               this.sections[var6] = new WorldChunkSection(var6 << 4, var5);
            }

            char[] var7 = this.sections[var6].getBlockData();

            for(int var8 = 0; var8 < var7.length; ++var8) {
               var7[var8] = (char)((rawChunkData[var4 + 1] & 255) << 8 | rawChunkData[var4] & 255);
               var4 += 2;
            }
         } else if (full && this.sections[var6] != null) {
            this.sections[var6] = null;
         }
      }

      for(int var10 = 0; var10 < this.sections.length; ++var10) {
         if ((sectionsWithData & 1 << var10) != 0 && this.sections[var10] != null) {
            ChunkNibbleStorage var14 = this.sections[var10].getBlockLightStorage();
            System.arraycopy(rawChunkData, var4, var14.getData(), 0, var14.getData().length);
            var4 += var14.getData().length;
         }
      }

      if (var5) {
         for(int var11 = 0; var11 < this.sections.length; ++var11) {
            if ((sectionsWithData & 1 << var11) != 0 && this.sections[var11] != null) {
               ChunkNibbleStorage var15 = this.sections[var11].getSkyLightStorage();
               System.arraycopy(rawChunkData, var4, var15.getData(), 0, var15.getData().length);
               var4 += var15.getData().length;
            }
         }
      }

      if (full) {
         System.arraycopy(rawChunkData, var4, this.biomes, 0, this.biomes.length);
         var4 += this.biomes.length;
      }

      for(int var12 = 0; var12 < this.sections.length; ++var12) {
         if (this.sections[var12] != null && (sectionsWithData & 1 << var12) != 0) {
            this.sections[var12].validateBlockCounters();
         }
      }

      this.lightPopulated = true;
      this.terrainPopulated = true;
      this.populateHeightmap();

      for(BlockEntity var16 : this.blockEntities.values()) {
         var16.clearBlockCache();
      }
   }

   public Biome getBiome(BlockPos pos, BiomeSource source) {
      int var3 = pos.getX() & 15;
      int var4 = pos.getZ() & 15;
      int var5 = this.biomes[var4 << 4 | var3] & 255;
      if (var5 == 255) {
         Biome var6 = source.getBiomeOrDefault(pos, Biome.PLAINS);
         var5 = var6.id;
         this.biomes[var4 << 4 | var3] = (byte)(var5 & 0xFF);
      }

      Biome var7 = Biome.byId(var5);
      return var7 == null ? Biome.PLAINS : var7;
   }

   public byte[] getBiomes() {
      return this.biomes;
   }

   public void setBiomes(byte[] biomes) {
      if (this.biomes.length != biomes.length) {
         LOGGER.warn("Could not set level chunk biomes, array length is " + biomes.length + " instead of " + this.biomes.length);
      } else {
         for(int var2 = 0; var2 < this.biomes.length; ++var2) {
            this.biomes[var2] = biomes[var2];
         }
      }
   }

   public void clearLightChecks() {
      this.queuedLightChecks = 0;
   }

   public void checkLight() {
      BlockPos var1 = new BlockPos(this.chunkX << 4, 0, this.chunkZ << 4);

      for(int var2 = 0; var2 < 8; ++var2) {
         if (this.queuedLightChecks >= 4096) {
            return;
         }

         int var3 = this.queuedLightChecks % 16;
         int var4 = this.queuedLightChecks / 16 % 16;
         int var5 = this.queuedLightChecks / 256;
         ++this.queuedLightChecks;

         for(int var6 = 0; var6 < 16; ++var6) {
            BlockPos var7 = var1.add(var4, (var3 << 4) + var6, var5);
            boolean var8 = var6 == 0 || var6 == 15 || var4 == 0 || var4 == 15 || var5 == 0 || var5 == 15;
            if (this.sections[var3] == null && var8
               || this.sections[var3] != null && this.sections[var3].getBlock(var4, var6, var5).getMaterial() == Material.AIR) {
               for(Direction var12 : Direction.values()) {
                  BlockPos var13 = var7.offset(var12);
                  if (this.world.getBlockState(var13).getBlock().getLightLevel() > 0) {
                     this.world.checkLight(var13);
                  }
               }

               this.world.checkLight(var7);
            }
         }
      }
   }

   public void populateLight() {
      this.terrainPopulated = true;
      this.lightPopulated = true;
      BlockPos var1 = new BlockPos(this.chunkX << 4, 0, this.chunkZ << 4);
      if (!this.world.dimension.isDark()) {
         if (this.world.isRegionLoaded(var1.add(-1, 0, -1), var1.add(16, 63, 16))) {
            label44:
            for(int var2 = 0; var2 < 16; ++var2) {
               for(int var3 = 0; var3 < 16; ++var3) {
                  if (!this.checkLightAt(var2, var3)) {
                     this.lightPopulated = false;
                     break label44;
                  }
               }
            }

            if (this.lightPopulated) {
               for(Direction var6 : Direction.Plane.HORIZONTAL) {
                  int var4 = var6.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 16 : 1;
                  this.world.getChunk(var1.offset(var6, var4)).checkBorderLight(var6.getOpposite());
               }

               this.queueLightUpdates();
            }
         } else {
            this.lightPopulated = false;
         }
      }
   }

   private void queueLightUpdates() {
      for(int var1 = 0; var1 < this.needsLightUpdate.length; ++var1) {
         this.needsLightUpdate[var1] = true;
      }

      this.recheckGaps(false);
   }

   private void checkBorderLight(Direction side) {
      if (this.terrainPopulated) {
         if (side == Direction.EAST) {
            for(int var2 = 0; var2 < 16; ++var2) {
               this.checkLightAt(15, var2);
            }
         } else if (side == Direction.WEST) {
            for(int var3 = 0; var3 < 16; ++var3) {
               this.checkLightAt(0, var3);
            }
         } else if (side == Direction.SOUTH) {
            for(int var4 = 0; var4 < 16; ++var4) {
               this.checkLightAt(var4, 15);
            }
         } else if (side == Direction.NORTH) {
            for(int var5 = 0; var5 < 16; ++var5) {
               this.checkLightAt(var5, 0);
            }
         }
      }
   }

   private boolean checkLightAt(int sectionX, int sectionZ) {
      BlockPos var3 = new BlockPos(this.chunkX << 4, 0, this.chunkZ << 4);
      int var4 = this.getHighestSectionOffset();
      boolean var5 = false;
      boolean var6 = false;

      int var7;
      for(var7 = var4 + 16 - 1; var7 > 63 || var7 > 0 && !var6; --var7) {
         BlockPos var8 = var3.add(sectionX, var7, sectionZ);
         int var9 = this.getOpacity(var8);
         if (var9 == 255 && var7 < 63) {
            var6 = true;
         }

         if (!var5 && var9 > 0) {
            var5 = true;
         } else if (var5 && var9 == 0 && !this.world.checkLight(var8)) {
            return false;
         }
      }

      for(; var7 > 0; --var7) {
         BlockPos var10 = var3.add(sectionX, var7, sectionZ);
         if (this.getBlock(var10).getLightLevel() > 0) {
            this.world.checkLight(var10);
         }
      }

      return true;
   }

   public boolean isLoaded() {
      return this.loaded;
   }

   @Environment(EnvType.CLIENT)
   public void setLoaded(boolean loaded) {
      this.loaded = loaded;
   }

   public World getWorld() {
      return this.world;
   }

   public int[] getHeightMap() {
      return this.heightMap;
   }

   public void setHeightmap(int[] heightmap) {
      if (this.heightMap.length != heightmap.length) {
         LOGGER.warn("Could not set level chunk heightmap, array length is " + heightmap.length + " instead of " + this.heightMap.length);
      } else {
         for(int var2 = 0; var2 < this.heightMap.length; ++var2) {
            this.heightMap[var2] = heightmap[var2];
         }
      }
   }

   public Map getBlockEntities() {
      return this.blockEntities;
   }

   public List[] getEntitiesBySection() {
      return this.entitiesBySection;
   }

   public boolean isTerrainPopulated() {
      return this.terrainPopulated;
   }

   public void setTerrainPopulated(boolean populated) {
      this.terrainPopulated = populated;
   }

   public boolean isLightPopulated() {
      return this.lightPopulated;
   }

   public void setLightPopulated(boolean populated) {
      this.lightPopulated = populated;
   }

   public void setDirty(boolean dirty) {
      this.dirty = dirty;
   }

   public void setContainsEntities(boolean containsEntities) {
      this.containsEntities = containsEntities;
   }

   public void setLastSaveTime(long lastSaveTime) {
      this.lastSaveTime = lastSaveTime;
   }

   public int getLowestHeight() {
      return this.lowestHeight;
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void setInhabitedTime(long time) {
      this.inhabitedTime = time;
   }
}
