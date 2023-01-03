package net.minecraft.world;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.LiquidBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SnowLayerBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFilter;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathHelper;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.HitResult;
import net.minecraft.util.Int2ObjectHashMap;
import net.minecraft.util.Tickable;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeSource;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.WorldGeneratorType;
import net.minecraft.world.gen.structure.StructureBox;
import net.minecraft.world.saved.SavedData;
import net.minecraft.world.storage.SavedDataStorage;
import net.minecraft.world.storage.WorldStorage;
import net.minecraft.world.village.SavedVillageData;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class World implements IWorld {
   protected boolean doTicksImmediately;
   public final List entities = Lists.newArrayList();
   protected final List entitiesToRemove = Lists.newArrayList();
   public final List blockEntities = Lists.newArrayList();
   public final List tickingBlockEntities = Lists.newArrayList();
   private final List pendingBlockEntities = Lists.newArrayList();
   private final List removedBlockEntities = Lists.newArrayList();
   public final List players = Lists.newArrayList();
   public final List globalEntities = Lists.newArrayList();
   protected final Int2ObjectHashMap entitiesByNetworkId = new Int2ObjectHashMap();
   private long cloudColor = 16777215L;
   private int ambientDarkness;
   protected int randomTickLCG = new Random().nextInt();
   protected final int randomTickLCGIncrement = 1013904223;
   protected float prevRain;
   protected float rain;
   protected float prevThunder;
   protected float thunder;
   private int lightningCooldown;
   public final Random random = new Random();
   public final Dimension dimension;
   protected List eventListeners = Lists.newArrayList();
   protected ChunkSource chunkSource;
   protected final WorldStorage storage;
   protected WorldData data;
   protected boolean isSearchingSpawnPoint;
   protected SavedDataStorage savedDataStorage;
   protected SavedVillageData villageData;
   public final Profiler profiler;
   private final Calendar calendar = Calendar.getInstance();
   protected Scoreboard scoreboard = new Scoreboard();
   public final boolean isClient;
   protected Set tickingChunks = Sets.newHashSet();
   private int ambientSoundCooldown = this.random.nextInt(12000);
   protected boolean allowAnimals = true;
   protected boolean allowMonsters = true;
   private boolean isTickingBlockEntities;
   private final WorldBorder worldBorder;
   int[] scheduledLightUpdates = new int[32768];

   protected World(WorldStorage storage, WorldData data, Dimension dimension, Profiler profiler, boolean isClient) {
      this.storage = storage;
      this.profiler = profiler;
      this.data = data;
      this.dimension = dimension;
      this.isClient = isClient;
      this.worldBorder = dimension.getWorldBorder();
   }

   public World init() {
      return this;
   }

   @Override
   public Biome getBiome(BlockPos pos) {
      if (this.isLoaded(pos)) {
         WorldChunk var2 = this.getChunk(pos);

         try {
            return var2.getBiome(pos, this.dimension.getBiomeSource());
         } catch (Throwable var6) {
            CrashReport var4 = CrashReport.of(var6, "Getting biome");
            CashReportCategory var5 = var4.addCategory("Coordinates of biome request");
            var5.add("Location", new Callable() {
               public String call() {
                  return CashReportCategory.formatPosition(pos);
               }
            });
            throw new CrashException(var4);
         }
      } else {
         return this.dimension.getBiomeSource().getBiomeOrDefault(pos, Biome.PLAINS);
      }
   }

   public BiomeSource getBiomeSource() {
      return this.dimension.getBiomeSource();
   }

   protected abstract ChunkSource createChunkCache();

   public void init(WorldSettings settings) {
      this.data.setInitialized(true);
   }

   @Environment(EnvType.CLIENT)
   public void resetSpawnPoint() {
      this.setSpawnPoint(new BlockPos(8, 64, 8));
   }

   public Block getSurfaceBlock(BlockPos pos) {
      BlockPos var2 = new BlockPos(pos.getX(), 63, pos.getZ());

      while(!this.isAir(var2.up())) {
         var2 = var2.up();
      }

      return this.getBlockState(var2).getBlock();
   }

   private boolean contains(BlockPos pos) {
      return pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000 && pos.getY() >= 0 && pos.getY() < 256;
   }

   @Override
   public boolean isAir(BlockPos pos) {
      return this.getBlockState(pos).getBlock().getMaterial() == Material.AIR;
   }

   public boolean isLoaded(BlockPos pos) {
      return this.isLoaded(pos, true);
   }

   public boolean isLoaded(BlockPos pos, boolean allowEmpty) {
      return !this.contains(pos) ? false : this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, allowEmpty);
   }

   public boolean isRegionLoaded(BlockPos pos, int range) {
      return this.isRegionLoaded(pos, range, true);
   }

   public boolean isRegionLoaded(BlockPos pos, int range, boolean allowEmpty) {
      return this.isRegionLoaded(
         pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range, allowEmpty
      );
   }

   public boolean isRegionLoaded(BlockPos minPos, BlockPos maxPos) {
      return this.isRegionLoaded(minPos, maxPos, true);
   }

   public boolean isRegionLoaded(BlockPos minPos, BlockPos maxPos, boolean allowEmpty) {
      return this.isRegionLoaded(minPos.getX(), minPos.getY(), minPos.getZ(), maxPos.getX(), maxPos.getY(), maxPos.getZ(), allowEmpty);
   }

   public boolean isRegionLoaded(StructureBox box) {
      return this.isRegionLoaded(box, true);
   }

   public boolean isRegionLoaded(StructureBox box, boolean allowEmpty) {
      return this.isRegionLoaded(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, allowEmpty);
   }

   private boolean isRegionLoaded(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean allowEmpty) {
      if (maxY >= 0 && minY < 256) {
         minX >>= 4;
         minZ >>= 4;
         maxX >>= 4;
         maxZ >>= 4;

         for(int var8 = minX; var8 <= maxX; ++var8) {
            for(int var9 = minZ; var9 <= maxZ; ++var9) {
               if (!this.isChunkLoaded(var8, var9, allowEmpty)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean isChunkLoaded(int chunkX, int chunkZ, boolean allowEmpty) {
      return this.chunkSource.isLoaded(chunkX, chunkZ) && (allowEmpty || !this.chunkSource.getChunk(chunkX, chunkZ).isEmpty());
   }

   public WorldChunk getChunk(BlockPos pos) {
      return this.getChunkAt(pos.getX() >> 4, pos.getZ() >> 4);
   }

   public WorldChunk getChunkAt(int chunkX, int chunkZ) {
      return this.chunkSource.getChunk(chunkX, chunkZ);
   }

   public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
      if (!this.contains(pos)) {
         return false;
      } else if (!this.isClient && this.data.getGeneratorType() == WorldGeneratorType.DEBUG_ALL_BLOCK_STATES) {
         return false;
      } else {
         WorldChunk var4 = this.getChunk(pos);
         Block var5 = state.getBlock();
         BlockState var6 = var4.setBlockState(pos, state);
         if (var6 == null) {
            return false;
         } else {
            Block var7 = var6.getBlock();
            if (var5.getOpacity() != var7.getOpacity() || var5.getLightLevel() != var7.getLightLevel()) {
               this.profiler.push("checkLight");
               this.checkLight(pos);
               this.profiler.pop();
            }

            if ((flags & 2) != 0 && (!this.isClient || (flags & 4) == 0) && var4.isPopulated()) {
               this.onBlockChanged(pos);
            }

            if (!this.isClient && (flags & 1) != 0) {
               this.onBlockChanged(pos, var6.getBlock());
               if (var5.hasAnalogOutput()) {
                  this.updateComparators(pos, var5);
               }
            }

            return true;
         }
      }
   }

   public boolean removeBlock(BlockPos pos) {
      return this.setBlockState(pos, Blocks.AIR.defaultState(), 3);
   }

   public boolean breakBlock(BlockPos pos, boolean dropItems) {
      BlockState var3 = this.getBlockState(pos);
      Block var4 = var3.getBlock();
      if (var4.getMaterial() == Material.AIR) {
         return false;
      } else {
         this.doEvent(2001, pos, Block.serialize(var3));
         if (dropItems) {
            var4.dropItems(this, pos, var3, 0);
         }

         return this.setBlockState(pos, Blocks.AIR.defaultState(), 3);
      }
   }

   public boolean setBlockState(BlockPos pos, BlockState state) {
      return this.setBlockState(pos, state, 3);
   }

   public void onBlockChanged(BlockPos pos) {
      for(int var2 = 0; var2 < this.eventListeners.size(); ++var2) {
         ((WorldEventListener)this.eventListeners.get(var2)).onBlockChanged(pos);
      }
   }

   public void onBlockChanged(BlockPos pos, Block block) {
      if (this.data.getGeneratorType() != WorldGeneratorType.DEBUG_ALL_BLOCK_STATES) {
         this.updateNeighbors(pos, block);
      }
   }

   public void checkLight(int x, int z, int minY, int maxY) {
      if (minY > maxY) {
         int var5 = maxY;
         maxY = minY;
         minY = var5;
      }

      if (!this.dimension.isDark()) {
         for(int var6 = minY; var6 <= maxY; ++var6) {
            this.checkLight(LightType.SKY, new BlockPos(x, var6, z));
         }
      }

      this.onRegionChanged(x, minY, z, x, maxY, z);
   }

   public void onRegionChanged(BlockPos minPos, BlockPos maxPos) {
      this.onRegionChanged(minPos.getX(), minPos.getY(), minPos.getZ(), maxPos.getX(), maxPos.getY(), maxPos.getZ());
   }

   public void onRegionChanged(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      for(int var7 = 0; var7 < this.eventListeners.size(); ++var7) {
         ((WorldEventListener)this.eventListeners.get(var7)).onRegionChanged(minX, minY, minZ, maxX, maxY, maxZ);
      }
   }

   public void updateNeighbors(BlockPos pos, Block block) {
      this.updateBlock(pos.west(), block);
      this.updateBlock(pos.east(), block);
      this.updateBlock(pos.down(), block);
      this.updateBlock(pos.up(), block);
      this.updateBlock(pos.north(), block);
      this.updateBlock(pos.south(), block);
   }

   public void updateNeighborsExcept(BlockPos pos, Block block, Direction dir) {
      if (dir != Direction.WEST) {
         this.updateBlock(pos.west(), block);
      }

      if (dir != Direction.EAST) {
         this.updateBlock(pos.east(), block);
      }

      if (dir != Direction.DOWN) {
         this.updateBlock(pos.down(), block);
      }

      if (dir != Direction.UP) {
         this.updateBlock(pos.up(), block);
      }

      if (dir != Direction.NORTH) {
         this.updateBlock(pos.north(), block);
      }

      if (dir != Direction.SOUTH) {
         this.updateBlock(pos.south(), block);
      }
   }

   public void updateBlock(BlockPos pos, Block neighborBlock) {
      if (!this.isClient) {
         BlockState var3 = this.getBlockState(pos);

         try {
            var3.getBlock().update(this, pos, var3, neighborBlock);
         } catch (Throwable var7) {
            CrashReport var5 = CrashReport.of(var7, "Exception while updating neighbours");
            CashReportCategory var6 = var5.addCategory("Block being updated");
            var6.add(
               "Source block type",
               new Callable() {
                  public String call() {
                     try {
                        return String.format(
                           "ID #%d (%s // %s)", Block.getRawId(neighborBlock), neighborBlock.getTranslationKey(), neighborBlock.getClass().getCanonicalName()
                        );
                     } catch (Throwable var2) {
                        return "ID #" + Block.getRawId(neighborBlock);
                     }
                  }
               }
            );
            CashReportCategory.addBlockDetails(var6, pos, var3);
            throw new CrashException(var5);
         }
      }
   }

   public boolean willTickThisTick(BlockPos pos, Block block) {
      return false;
   }

   public boolean hasSkyAccess(BlockPos pos) {
      return this.getChunk(pos).hasSkyAccess(pos);
   }

   public boolean hasSkyAccessIgnoreLiquids(BlockPos pos) {
      if (pos.getY() >= 63) {
         return this.hasSkyAccess(pos);
      } else {
         BlockPos var2 = new BlockPos(pos.getX(), 63, pos.getZ());
         if (!this.hasSkyAccess(var2)) {
            return false;
         } else {
            for(BlockPos var4 = var2.down(); var4.getY() > pos.getY(); var4 = var4.down()) {
               Block var3 = this.getBlockState(var4).getBlock();
               if (var3.getOpacity() > 0 && !var3.getMaterial().isLiquid()) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int getLight(BlockPos pos) {
      if (pos.getY() < 0) {
         return 0;
      } else {
         if (pos.getY() >= 256) {
            pos = new BlockPos(pos.getX(), 255, pos.getZ());
         }

         return this.getChunk(pos).getLight(pos, 0);
      }
   }

   public int getRawBrightness(BlockPos pos) {
      return this.getRawBrightness(pos, true);
   }

   public int getRawBrightness(BlockPos pos, boolean useNeighborLight) {
      if (pos.getX() < -30000000 || pos.getZ() < -30000000 || pos.getX() >= 30000000 || pos.getZ() >= 30000000) {
         return 15;
      } else if (useNeighborLight && this.getBlockState(pos).getBlock().usesNeighborLight()) {
         int var8 = this.getRawBrightness(pos.up(), false);
         int var4 = this.getRawBrightness(pos.east(), false);
         int var5 = this.getRawBrightness(pos.west(), false);
         int var6 = this.getRawBrightness(pos.south(), false);
         int var7 = this.getRawBrightness(pos.north(), false);
         if (var4 > var8) {
            var8 = var4;
         }

         if (var5 > var8) {
            var8 = var5;
         }

         if (var6 > var8) {
            var8 = var6;
         }

         if (var7 > var8) {
            var8 = var7;
         }

         return var8;
      } else if (pos.getY() < 0) {
         return 0;
      } else {
         if (pos.getY() >= 256) {
            pos = new BlockPos(pos.getX(), 255, pos.getZ());
         }

         WorldChunk var3 = this.getChunk(pos);
         return var3.getLight(pos, this.ambientDarkness);
      }
   }

   public BlockPos getHeight(BlockPos pos) {
      int var2;
      if (pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000) {
         if (this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, true)) {
            var2 = this.getChunkAt(pos.getX() >> 4, pos.getZ() >> 4).getHeight(pos.getX() & 15, pos.getZ() & 15);
         } else {
            var2 = 0;
         }
      } else {
         var2 = 64;
      }

      return new BlockPos(pos.getX(), var2, pos.getZ());
   }

   public int getLowestHeight(int x, int z) {
      if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
         if (!this.isChunkLoaded(x >> 4, z >> 4, true)) {
            return 0;
         } else {
            WorldChunk var3 = this.getChunkAt(x >> 4, z >> 4);
            return var3.getLowestHeight();
         }
      } else {
         return 64;
      }
   }

   @Environment(EnvType.CLIENT)
   public int getBrightness(LightType type, BlockPos pos) {
      if (this.dimension.isDark() && type == LightType.SKY) {
         return 0;
      } else {
         if (pos.getY() < 0) {
            pos = new BlockPos(pos.getX(), 0, pos.getZ());
         }

         if (!this.contains(pos)) {
            return type.defaultValue;
         } else if (!this.isLoaded(pos)) {
            return type.defaultValue;
         } else if (this.getBlockState(pos).getBlock().usesNeighborLight()) {
            int var8 = this.getLight(type, pos.up());
            int var4 = this.getLight(type, pos.east());
            int var5 = this.getLight(type, pos.west());
            int var6 = this.getLight(type, pos.south());
            int var7 = this.getLight(type, pos.north());
            if (var4 > var8) {
               var8 = var4;
            }

            if (var5 > var8) {
               var8 = var5;
            }

            if (var6 > var8) {
               var8 = var6;
            }

            if (var7 > var8) {
               var8 = var7;
            }

            return var8;
         } else {
            WorldChunk var3 = this.getChunk(pos);
            return var3.getLight(type, pos);
         }
      }
   }

   public int getLight(LightType type, BlockPos pos) {
      if (pos.getY() < 0) {
         pos = new BlockPos(pos.getX(), 0, pos.getZ());
      }

      if (!this.contains(pos)) {
         return type.defaultValue;
      } else if (!this.isLoaded(pos)) {
         return type.defaultValue;
      } else {
         WorldChunk var3 = this.getChunk(pos);
         return var3.getLight(type, pos);
      }
   }

   public void setLight(LightType type, BlockPos pos, int light) {
      if (this.contains(pos)) {
         if (this.isLoaded(pos)) {
            WorldChunk var4 = this.getChunk(pos);
            var4.setLight(type, pos, light);

            for(int var5 = 0; var5 < this.eventListeners.size(); ++var5) {
               ((WorldEventListener)this.eventListeners.get(var5)).onLightChanged(pos);
            }
         }
      }
   }

   public void onLightChanged(BlockPos pos) {
      for(int var2 = 0; var2 < this.eventListeners.size(); ++var2) {
         ((WorldEventListener)this.eventListeners.get(var2)).onLightChanged(pos);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getLightColor(BlockPos pos, int blockLight) {
      int var3 = this.getBrightness(LightType.SKY, pos);
      int var4 = this.getBrightness(LightType.BLOCK, pos);
      if (var4 < blockLight) {
         var4 = blockLight;
      }

      return var3 << 20 | var4 << 4;
   }

   public float getBrightness(BlockPos pos) {
      return this.dimension.getBrightnessTable()[this.getRawBrightness(pos)];
   }

   @Override
   public BlockState getBlockState(BlockPos pos) {
      if (!this.contains(pos)) {
         return Blocks.AIR.defaultState();
      } else {
         WorldChunk var2 = this.getChunk(pos);
         return var2.getBlockState(pos);
      }
   }

   public boolean isSunny() {
      return this.ambientDarkness < 4;
   }

   public HitResult rayTrace(Vec3d from, Vec3d to) {
      return this.rayTrace(from, to, false, false, false);
   }

   public HitResult rayTrace(Vec3d from, Vec3d to, boolean allowLiquids) {
      return this.rayTrace(from, to, allowLiquids, false, false);
   }

   public HitResult rayTrace(Vec3d from, Vec3d to, boolean allowLiquids, boolean ignoreBlocksWithoutCollision, boolean bl3) {
      if (Double.isNaN(from.x) || Double.isNaN(from.y) || Double.isNaN(from.z)) {
         return null;
      } else if (!Double.isNaN(to.x) && !Double.isNaN(to.y) && !Double.isNaN(to.z)) {
         int var6 = MathHelper.floor(to.x);
         int var7 = MathHelper.floor(to.y);
         int var8 = MathHelper.floor(to.z);
         int var9 = MathHelper.floor(from.x);
         int var10 = MathHelper.floor(from.y);
         int var11 = MathHelper.floor(from.z);
         BlockPos var12 = new BlockPos(var9, var10, var11);
         new BlockPos(var6, var7, var8);
         BlockState var14 = this.getBlockState(var12);
         Block var15 = var14.getBlock();
         if ((!ignoreBlocksWithoutCollision || var15.getCollisionShape(this, var12, var14) != null) && var15.hasCollision(var14, allowLiquids)) {
            HitResult var16 = var15.rayTrace(this, var12, from, to);
            if (var16 != null) {
               return var16;
            }
         }

         HitResult var42 = null;
         int var43 = 200;

         while(var43-- >= 0) {
            if (Double.isNaN(from.x) || Double.isNaN(from.y) || Double.isNaN(from.z)) {
               return null;
            }

            if (var9 == var6 && var10 == var7 && var11 == var8) {
               return bl3 ? var42 : null;
            }

            boolean var44 = true;
            boolean var17 = true;
            boolean var18 = true;
            double var19 = 999.0;
            double var21 = 999.0;
            double var23 = 999.0;
            if (var6 > var9) {
               var19 = (double)var9 + 1.0;
            } else if (var6 < var9) {
               var19 = (double)var9 + 0.0;
            } else {
               var44 = false;
            }

            if (var7 > var10) {
               var21 = (double)var10 + 1.0;
            } else if (var7 < var10) {
               var21 = (double)var10 + 0.0;
            } else {
               var17 = false;
            }

            if (var8 > var11) {
               var23 = (double)var11 + 1.0;
            } else if (var8 < var11) {
               var23 = (double)var11 + 0.0;
            } else {
               var18 = false;
            }

            double var25 = 999.0;
            double var27 = 999.0;
            double var29 = 999.0;
            double var31 = to.x - from.x;
            double var33 = to.y - from.y;
            double var35 = to.z - from.z;
            if (var44) {
               var25 = (var19 - from.x) / var31;
            }

            if (var17) {
               var27 = (var21 - from.y) / var33;
            }

            if (var18) {
               var29 = (var23 - from.z) / var35;
            }

            if (var25 == -0.0) {
               var25 = -1.0E-4;
            }

            if (var27 == -0.0) {
               var27 = -1.0E-4;
            }

            if (var29 == -0.0) {
               var29 = -1.0E-4;
            }

            Direction var37;
            if (var25 < var27 && var25 < var29) {
               var37 = var6 > var9 ? Direction.WEST : Direction.EAST;
               from = new Vec3d(var19, from.y + var33 * var25, from.z + var35 * var25);
            } else if (var27 < var29) {
               var37 = var7 > var10 ? Direction.DOWN : Direction.UP;
               from = new Vec3d(from.x + var31 * var27, var21, from.z + var35 * var27);
            } else {
               var37 = var8 > var11 ? Direction.NORTH : Direction.SOUTH;
               from = new Vec3d(from.x + var31 * var29, from.y + var33 * var29, var23);
            }

            var9 = MathHelper.floor(from.x) - (var37 == Direction.EAST ? 1 : 0);
            var10 = MathHelper.floor(from.y) - (var37 == Direction.UP ? 1 : 0);
            var11 = MathHelper.floor(from.z) - (var37 == Direction.SOUTH ? 1 : 0);
            var12 = new BlockPos(var9, var10, var11);
            BlockState var38 = this.getBlockState(var12);
            Block var39 = var38.getBlock();
            if (!ignoreBlocksWithoutCollision || var39.getCollisionShape(this, var12, var38) != null) {
               if (var39.hasCollision(var38, allowLiquids)) {
                  HitResult var40 = var39.rayTrace(this, var12, from, to);
                  if (var40 != null) {
                     return var40;
                  }
               } else {
                  var42 = new HitResult(HitResult.Type.MISS, from, var37, var12);
               }
            }
         }

         return bl3 ? var42 : null;
      } else {
         return null;
      }
   }

   public void playSound(Entity source, String sound, float volume, float pitch) {
      for(int var5 = 0; var5 < this.eventListeners.size(); ++var5) {
         ((WorldEventListener)this.eventListeners.get(var5)).playSound(sound, source.x, source.y, source.z, volume, pitch);
      }
   }

   public void playSound(PlayerEntity source, String sound, float volume, float pitch) {
      for(int var5 = 0; var5 < this.eventListeners.size(); ++var5) {
         ((WorldEventListener)this.eventListeners.get(var5)).playSound(source, sound, source.x, source.y, source.z, volume, pitch);
      }
   }

   public void playSound(double x, double y, double z, String sound, float volume, float pitch) {
      for(int var10 = 0; var10 < this.eventListeners.size(); ++var10) {
         ((WorldEventListener)this.eventListeners.get(var10)).playSound(sound, x, y, z, volume, pitch);
      }
   }

   public void playSound(double x, double y, double z, String sound, float volume, float pitch, boolean ignoreDistance) {
   }

   public void onRecordRemoved(BlockPos pos, String name) {
      for(int var3 = 0; var3 < this.eventListeners.size(); ++var3) {
         ((WorldEventListener)this.eventListeners.get(var3)).onRecordRemoved(name, pos);
      }
   }

   public void addParticle(ParticleType type, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
      this.addParticle(type.getId(), type.ignoreDistance(), x, y, z, velocityX, velocityY, velocityZ, parameters);
   }

   @Environment(EnvType.CLIENT)
   public void addParticle(
      ParticleType type, boolean ignoreDistance, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters
   ) {
      this.addParticle(type.getId(), type.ignoreDistance() | ignoreDistance, x, y, z, velocityX, velocityY, velocityZ, parameters);
   }

   private void addParticle(
      int type, boolean ignoreDistance, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters
   ) {
      for(int var16 = 0; var16 < this.eventListeners.size(); ++var16) {
         ((WorldEventListener)this.eventListeners.get(var16)).addParticle(type, ignoreDistance, x, y, z, velocityX, velocityY, velocityZ, parameters);
      }
   }

   public boolean addGlobalEntity(Entity entity) {
      this.globalEntities.add(entity);
      return true;
   }

   public boolean addEntity(Entity entity) {
      int var2 = MathHelper.floor(entity.x / 16.0);
      int var3 = MathHelper.floor(entity.z / 16.0);
      boolean var4 = entity.teleporting;
      if (entity instanceof PlayerEntity) {
         var4 = true;
      }

      if (!var4 && !this.isChunkLoaded(var2, var3, true)) {
         return false;
      } else {
         if (entity instanceof PlayerEntity) {
            PlayerEntity var5 = (PlayerEntity)entity;
            this.players.add(var5);
            this.updateSleepingPlayers();
         }

         this.getChunkAt(var2, var3).addEntity(entity);
         this.entities.add(entity);
         this.onEntityAdded(entity);
         return true;
      }
   }

   protected void onEntityAdded(Entity entity) {
      for(int var2 = 0; var2 < this.eventListeners.size(); ++var2) {
         ((WorldEventListener)this.eventListeners.get(var2)).onEntityAdded(entity);
      }
   }

   protected void onEntityRemoved(Entity entity) {
      for(int var2 = 0; var2 < this.eventListeners.size(); ++var2) {
         ((WorldEventListener)this.eventListeners.get(var2)).onEntityRemoved(entity);
      }
   }

   public void removeEntity(Entity entity) {
      if (entity.rider != null) {
         entity.rider.startRiding(null);
      }

      if (entity.vehicle != null) {
         entity.startRiding(null);
      }

      entity.remove();
      if (entity instanceof PlayerEntity) {
         this.players.remove(entity);
         this.updateSleepingPlayers();
         this.onEntityRemoved(entity);
      }
   }

   public void removeEntityNow(Entity entity) {
      entity.remove();
      if (entity instanceof PlayerEntity) {
         this.players.remove(entity);
         this.updateSleepingPlayers();
      }

      int var2 = entity.chunkX;
      int var3 = entity.chunkZ;
      if (entity.isLoaded && this.isChunkLoaded(var2, var3, true)) {
         this.getChunkAt(var2, var3).removeEntity(entity);
      }

      this.entities.remove(entity);
      this.onEntityRemoved(entity);
   }

   public void addEventListener(WorldEventListener listener) {
      this.eventListeners.add(listener);
   }

   @Environment(EnvType.CLIENT)
   public void removeEventListener(WorldEventListener listener) {
      this.eventListeners.remove(listener);
   }

   public List getCollisions(Entity entity, Box box) {
      ArrayList var3 = Lists.newArrayList();
      int var4 = MathHelper.floor(box.minX);
      int var5 = MathHelper.floor(box.maxX + 1.0);
      int var6 = MathHelper.floor(box.minY);
      int var7 = MathHelper.floor(box.maxY + 1.0);
      int var8 = MathHelper.floor(box.minZ);
      int var9 = MathHelper.floor(box.maxZ + 1.0);

      for(int var10 = var4; var10 < var5; ++var10) {
         for(int var11 = var8; var11 < var9; ++var11) {
            if (this.isLoaded(new BlockPos(var10, 64, var11))) {
               for(int var12 = var6 - 1; var12 < var7; ++var12) {
                  BlockPos var13 = new BlockPos(var10, var12, var11);
                  boolean var14 = entity.m_13uofunxk();
                  boolean var15 = this.isWithinBorder(this.getWorldBorder(), entity);
                  if (var14 && var15) {
                     entity.m_72yjttsjc(false);
                  } else if (!var14 && !var15) {
                     entity.m_72yjttsjc(true);
                  }

                  BlockState var16;
                  if (!this.getWorldBorder().contains(var13) && var15) {
                     var16 = Blocks.STONE.defaultState();
                  } else {
                     var16 = this.getBlockState(var13);
                  }

                  var16.getBlock().getCollisionBoxes(this, var13, var16, box, var3, entity);
               }
            }
         }
      }

      double var17 = 0.25;
      List var18 = this.getEntities(entity, box.expand(var17, var17, var17));

      for(int var19 = 0; var19 < var18.size(); ++var19) {
         if (entity.rider != var18 && entity.vehicle != var18) {
            Box var20 = ((Entity)var18.get(var19)).getBox();
            if (var20 != null && var20.intersects(box)) {
               var3.add(var20);
            }

            var20 = entity.getHardCollisionBox((Entity)var18.get(var19));
            if (var20 != null && var20.intersects(box)) {
               var3.add(var20);
            }
         }
      }

      return var3;
   }

   public boolean isWithinBorder(WorldBorder border, Entity entity) {
      double var3 = border.getMinX();
      double var5 = border.getMinZ();
      double var7 = border.getMaxX();
      double var9 = border.getMaxZ();
      if (entity.m_13uofunxk()) {
         ++var3;
         ++var5;
         --var7;
         --var9;
      } else {
         --var3;
         --var5;
         ++var7;
         ++var9;
      }

      return entity.x > var3 && entity.x < var7 && entity.z > var5 && entity.z < var9;
   }

   public List getCollisionBoxes(Box box) {
      ArrayList var2 = Lists.newArrayList();
      int var3 = MathHelper.floor(box.minX);
      int var4 = MathHelper.floor(box.maxX + 1.0);
      int var5 = MathHelper.floor(box.minY);
      int var6 = MathHelper.floor(box.maxY + 1.0);
      int var7 = MathHelper.floor(box.minZ);
      int var8 = MathHelper.floor(box.maxZ + 1.0);

      for(int var9 = var3; var9 < var4; ++var9) {
         for(int var10 = var7; var10 < var8; ++var10) {
            if (this.isLoaded(new BlockPos(var9, 64, var10))) {
               for(int var11 = var5 - 1; var11 < var6; ++var11) {
                  BlockPos var13 = new BlockPos(var9, var11, var10);
                  BlockState var12;
                  if (var9 >= -30000000 && var9 < 30000000 && var10 >= -30000000 && var10 < 30000000) {
                     var12 = this.getBlockState(var13);
                  } else {
                     var12 = Blocks.BEDROCK.defaultState();
                  }

                  var12.getBlock().getCollisionBoxes(this, var13, var12, box, var2, null);
               }
            }
         }
      }

      return var2;
   }

   public int calculateAmbientDarkness(float tickDelta) {
      float var2 = this.getTimeOfDay(tickDelta);
      float var3 = 1.0F - (MathHelper.cos(var2 * (float) Math.PI * 2.0F) * 2.0F + 0.5F);
      var3 = MathHelper.clamp(var3, 0.0F, 1.0F);
      var3 = 1.0F - var3;
      var3 = (float)((double)var3 * (1.0 - (double)(this.getRain(tickDelta) * 5.0F) / 16.0));
      var3 = (float)((double)var3 * (1.0 - (double)(this.getThunder(tickDelta) * 5.0F) / 16.0));
      var3 = 1.0F - var3;
      return (int)(var3 * 11.0F);
   }

   @Environment(EnvType.CLIENT)
   public float calculateAmbientLight(float tickDelta) {
      float var2 = this.getTimeOfDay(tickDelta);
      float var3 = 1.0F - (MathHelper.cos(var2 * (float) Math.PI * 2.0F) * 2.0F + 0.2F);
      var3 = MathHelper.clamp(var3, 0.0F, 1.0F);
      var3 = 1.0F - var3;
      var3 = (float)((double)var3 * (1.0 - (double)(this.getRain(tickDelta) * 5.0F) / 16.0));
      var3 = (float)((double)var3 * (1.0 - (double)(this.getThunder(tickDelta) * 5.0F) / 16.0));
      return var3 * 0.8F + 0.2F;
   }

   @Environment(EnvType.CLIENT)
   public Vec3d getSkyColor(Entity entity, float tickDelta) {
      float var3 = this.getTimeOfDay(tickDelta);
      float var4 = MathHelper.cos(var3 * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
      var4 = MathHelper.clamp(var4, 0.0F, 1.0F);
      int var5 = MathHelper.floor(entity.x);
      int var6 = MathHelper.floor(entity.y);
      int var7 = MathHelper.floor(entity.z);
      BlockPos var8 = new BlockPos(var5, var6, var7);
      Biome var9 = this.getBiome(var8);
      float var10 = var9.getTemperature(var8);
      int var11 = var9.getSkyColor(var10);
      float var12 = (float)(var11 >> 16 & 0xFF) / 255.0F;
      float var13 = (float)(var11 >> 8 & 0xFF) / 255.0F;
      float var14 = (float)(var11 & 0xFF) / 255.0F;
      var12 *= var4;
      var13 *= var4;
      var14 *= var4;
      float var15 = this.getRain(tickDelta);
      if (var15 > 0.0F) {
         float var16 = (var12 * 0.3F + var13 * 0.59F + var14 * 0.11F) * 0.6F;
         float var17 = 1.0F - var15 * 0.75F;
         var12 = var12 * var17 + var16 * (1.0F - var17);
         var13 = var13 * var17 + var16 * (1.0F - var17);
         var14 = var14 * var17 + var16 * (1.0F - var17);
      }

      float var23 = this.getThunder(tickDelta);
      if (var23 > 0.0F) {
         float var24 = (var12 * 0.3F + var13 * 0.59F + var14 * 0.11F) * 0.2F;
         float var18 = 1.0F - var23 * 0.75F;
         var12 = var12 * var18 + var24 * (1.0F - var18);
         var13 = var13 * var18 + var24 * (1.0F - var18);
         var14 = var14 * var18 + var24 * (1.0F - var18);
      }

      if (this.lightningCooldown > 0) {
         float var25 = (float)this.lightningCooldown - tickDelta;
         if (var25 > 1.0F) {
            var25 = 1.0F;
         }

         var25 *= 0.45F;
         var12 = var12 * (1.0F - var25) + 0.8F * var25;
         var13 = var13 * (1.0F - var25) + 0.8F * var25;
         var14 = var14 * (1.0F - var25) + 1.0F * var25;
      }

      return new Vec3d((double)var12, (double)var13, (double)var14);
   }

   public float getTimeOfDay(float tickDelta) {
      return this.dimension.getTimeOfDay(this.data.getTimeOfDay(), tickDelta);
   }

   @Environment(EnvType.CLIENT)
   public int getMoonPhase() {
      return this.dimension.getMoonPhase(this.data.getTimeOfDay());
   }

   public float getMoonSize() {
      return Dimension.MOON_PHASE_TO_SIZE[this.dimension.getMoonPhase(this.data.getTimeOfDay())];
   }

   public float getSunAngle(float tickDelta) {
      float var2 = this.getTimeOfDay(tickDelta);
      return var2 * (float) Math.PI * 2.0F;
   }

   @Environment(EnvType.CLIENT)
   public Vec3d getCloudColor(float tickDelta) {
      float var2 = this.getTimeOfDay(tickDelta);
      float var3 = MathHelper.cos(var2 * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
      var3 = MathHelper.clamp(var3, 0.0F, 1.0F);
      float var4 = (float)(this.cloudColor >> 16 & 255L) / 255.0F;
      float var5 = (float)(this.cloudColor >> 8 & 255L) / 255.0F;
      float var6 = (float)(this.cloudColor & 255L) / 255.0F;
      float var7 = this.getRain(tickDelta);
      if (var7 > 0.0F) {
         float var8 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.6F;
         float var9 = 1.0F - var7 * 0.95F;
         var4 = var4 * var9 + var8 * (1.0F - var9);
         var5 = var5 * var9 + var8 * (1.0F - var9);
         var6 = var6 * var9 + var8 * (1.0F - var9);
      }

      var4 *= var3 * 0.9F + 0.1F;
      var5 *= var3 * 0.9F + 0.1F;
      var6 *= var3 * 0.85F + 0.15F;
      float var15 = this.getThunder(tickDelta);
      if (var15 > 0.0F) {
         float var16 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.2F;
         float var10 = 1.0F - var15 * 0.95F;
         var4 = var4 * var10 + var16 * (1.0F - var10);
         var5 = var5 * var10 + var16 * (1.0F - var10);
         var6 = var6 * var10 + var16 * (1.0F - var10);
      }

      return new Vec3d((double)var4, (double)var5, (double)var6);
   }

   @Environment(EnvType.CLIENT)
   public Vec3d getFogColor(float tickDelta) {
      float var2 = this.getTimeOfDay(tickDelta);
      return this.dimension.getFogColor(var2, tickDelta);
   }

   public BlockPos getPrecipitationHeight(BlockPos pos) {
      return this.getChunk(pos).getPrecipitationHeight(pos);
   }

   public BlockPos getSurfaceHeight(BlockPos pos) {
      WorldChunk var2 = this.getChunk(pos);

      BlockPos var3;
      BlockPos var4;
      for(var3 = new BlockPos(pos.getX(), var2.getHighestSectionOffset() + 16, pos.getZ()); var3.getY() >= 0; var3 = var4) {
         var4 = var3.down();
         Material var5 = var2.getBlock(var4).getMaterial();
         if (var5.blocksMovement() && var5 != Material.LEAVES) {
            break;
         }
      }

      return var3;
   }

   @Environment(EnvType.CLIENT)
   public float getStarBrightness(float tickDelta) {
      float var2 = this.getTimeOfDay(tickDelta);
      float var3 = 1.0F - (MathHelper.cos(var2 * (float) Math.PI * 2.0F) * 2.0F + 0.25F);
      var3 = MathHelper.clamp(var3, 0.0F, 1.0F);
      return var3 * var3 * 0.5F;
   }

   public void scheduleTick(BlockPos pos, Block block, int delay) {
   }

   public void scheduleTick(BlockPos pos, Block block, int delay, int priority) {
   }

   public void loadScheduledTick(BlockPos pos, Block block, int delay, int priority) {
   }

   public void tickEntities() {
      this.profiler.push("entities");
      this.profiler.push("global");

      for(int var1 = 0; var1 < this.globalEntities.size(); ++var1) {
         Entity var2 = (Entity)this.globalEntities.get(var1);

         try {
            ++var2.time;
            var2.tick();
         } catch (Throwable var9) {
            CrashReport var4 = CrashReport.of(var9, "Ticking entity");
            CashReportCategory var5 = var4.addCategory("Entity being ticked");
            if (var2 == null) {
               var5.add("Entity", "~~NULL~~");
            } else {
               var2.populateCrashReport(var5);
            }

            throw new CrashException(var4);
         }

         if (var2.removed) {
            this.globalEntities.remove(var1--);
         }
      }

      this.profiler.swap("remove");
      this.entities.removeAll(this.entitiesToRemove);

      for(int var10 = 0; var10 < this.entitiesToRemove.size(); ++var10) {
         Entity var14 = (Entity)this.entitiesToRemove.get(var10);
         int var3 = var14.chunkX;
         int var21 = var14.chunkZ;
         if (var14.isLoaded && this.isChunkLoaded(var3, var21, true)) {
            this.getChunkAt(var3, var21).removeEntity(var14);
         }
      }

      for(int var11 = 0; var11 < this.entitiesToRemove.size(); ++var11) {
         this.onEntityRemoved((Entity)this.entitiesToRemove.get(var11));
      }

      this.entitiesToRemove.clear();
      this.profiler.swap("regular");

      for(int var12 = 0; var12 < this.entities.size(); ++var12) {
         Entity var15 = (Entity)this.entities.get(var12);
         if (var15.vehicle != null) {
            if (!var15.vehicle.removed && var15.vehicle.rider == var15) {
               continue;
            }

            var15.vehicle.rider = null;
            var15.vehicle = null;
         }

         this.profiler.push("tick");
         if (!var15.removed) {
            try {
               this.tickEntity(var15);
            } catch (Throwable var8) {
               CrashReport var22 = CrashReport.of(var8, "Ticking entity");
               CashReportCategory var24 = var22.addCategory("Entity being ticked");
               var15.populateCrashReport(var24);
               throw new CrashException(var22);
            }
         }

         this.profiler.pop();
         this.profiler.push("remove");
         if (var15.removed) {
            int var18 = var15.chunkX;
            int var23 = var15.chunkZ;
            if (var15.isLoaded && this.isChunkLoaded(var18, var23, true)) {
               this.getChunkAt(var18, var23).removeEntity(var15);
            }

            this.entities.remove(var12--);
            this.onEntityRemoved(var15);
         }

         this.profiler.pop();
      }

      this.profiler.swap("blockEntities");
      this.isTickingBlockEntities = true;
      Iterator var13 = this.tickingBlockEntities.iterator();

      while(var13.hasNext()) {
         BlockEntity var16 = (BlockEntity)var13.next();
         if (!var16.isRemoved() && var16.hasWorld()) {
            BlockPos var19 = var16.getPos();
            if (this.isLoaded(var19) && this.worldBorder.contains(var19)) {
               try {
                  ((Tickable)var16).tick();
               } catch (Throwable var7) {
                  CrashReport var25 = CrashReport.of(var7, "Ticking block entity");
                  CashReportCategory var6 = var25.addCategory("Block entity being ticked");
                  var16.populateCrashReport(var6);
                  throw new CrashException(var25);
               }
            }
         }

         if (var16.isRemoved()) {
            var13.remove();
            this.blockEntities.remove(var16);
            if (this.isLoaded(var16.getPos())) {
               this.getChunk(var16.getPos()).removeBlockEntity(var16.getPos());
            }
         }
      }

      this.isTickingBlockEntities = false;
      if (!this.removedBlockEntities.isEmpty()) {
         this.tickingBlockEntities.removeAll(this.removedBlockEntities);
         this.blockEntities.removeAll(this.removedBlockEntities);
         this.removedBlockEntities.clear();
      }

      this.profiler.swap("pendingBlockEntities");
      if (!this.pendingBlockEntities.isEmpty()) {
         for(int var17 = 0; var17 < this.pendingBlockEntities.size(); ++var17) {
            BlockEntity var20 = (BlockEntity)this.pendingBlockEntities.get(var17);
            if (!var20.isRemoved()) {
               if (!this.blockEntities.contains(var20)) {
                  this.addBlockEntity(var20);
               }

               if (this.isLoaded(var20.getPos())) {
                  this.getChunk(var20.getPos()).setBlockEntity(var20.getPos(), var20);
               }

               this.onBlockChanged(var20.getPos());
            }
         }

         this.pendingBlockEntities.clear();
      }

      this.profiler.pop();
      this.profiler.pop();
   }

   public boolean addBlockEntity(BlockEntity blockEntity) {
      boolean var2 = this.blockEntities.add(blockEntity);
      if (var2 && blockEntity instanceof Tickable) {
         this.tickingBlockEntities.add(blockEntity);
      }

      return var2;
   }

   public void addBlockEntities(Collection blockEntities) {
      if (this.isTickingBlockEntities) {
         this.pendingBlockEntities.addAll(blockEntities);
      } else {
         for(BlockEntity var3 : blockEntities) {
            this.blockEntities.add(var3);
            if (var3 instanceof Tickable) {
               this.tickingBlockEntities.add(var3);
            }
         }
      }
   }

   public void tickEntity(Entity entity) {
      this.tickEntity(entity, true);
   }

   public void tickEntity(Entity entity, boolean requireLoaded) {
      int var3 = MathHelper.floor(entity.x);
      int var4 = MathHelper.floor(entity.z);
      byte var5 = 32;
      if (!requireLoaded || this.isRegionLoaded(var3 - var5, 0, var4 - var5, var3 + var5, 0, var4 + var5, true)) {
         entity.prevTickX = entity.x;
         entity.prevTickY = entity.y;
         entity.prevTickZ = entity.z;
         entity.prevYaw = entity.yaw;
         entity.prevPitch = entity.pitch;
         if (requireLoaded && entity.isLoaded) {
            ++entity.time;
            if (entity.vehicle != null) {
               entity.tickRiding();
            } else {
               entity.tick();
            }
         }

         this.profiler.push("chunkCheck");
         if (Double.isNaN(entity.x) || Double.isInfinite(entity.x)) {
            entity.x = entity.prevTickX;
         }

         if (Double.isNaN(entity.y) || Double.isInfinite(entity.y)) {
            entity.y = entity.prevTickY;
         }

         if (Double.isNaN(entity.z) || Double.isInfinite(entity.z)) {
            entity.z = entity.prevTickZ;
         }

         if (Double.isNaN((double)entity.pitch) || Double.isInfinite((double)entity.pitch)) {
            entity.pitch = entity.prevPitch;
         }

         if (Double.isNaN((double)entity.yaw) || Double.isInfinite((double)entity.yaw)) {
            entity.yaw = entity.prevYaw;
         }

         int var6 = MathHelper.floor(entity.x / 16.0);
         int var7 = MathHelper.floor(entity.y / 16.0);
         int var8 = MathHelper.floor(entity.z / 16.0);
         if (!entity.isLoaded || entity.chunkX != var6 || entity.chunkY != var7 || entity.chunkZ != var8) {
            if (entity.isLoaded && this.isChunkLoaded(entity.chunkX, entity.chunkZ, true)) {
               this.getChunkAt(entity.chunkX, entity.chunkZ).removeEntity(entity, entity.chunkY);
            }

            if (this.isChunkLoaded(var6, var8, true)) {
               entity.isLoaded = true;
               this.getChunkAt(var6, var8).addEntity(entity);
            } else {
               entity.isLoaded = false;
            }
         }

         this.profiler.pop();
         if (requireLoaded && entity.isLoaded && entity.rider != null) {
            if (!entity.rider.removed && entity.rider.vehicle == entity) {
               this.tickEntity(entity.rider);
            } else {
               entity.rider.vehicle = null;
               entity.rider = null;
            }
         }
      }
   }

   public boolean canBuildIn(Box box) {
      return this.canBuildIn(box, null);
   }

   public boolean canBuildIn(Box box, Entity ignore) {
      List var3 = this.getEntities(null, box);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         Entity var5 = (Entity)var3.get(var4);
         if (!var5.removed && var5.blocksBuilding && var5 != ignore && (ignore == null || ignore.vehicle != var5 && ignore.rider != var5)) {
            return false;
         }
      }

      return true;
   }

   public boolean containsNonAir(Box box) {
      int var2 = MathHelper.floor(box.minX);
      int var3 = MathHelper.floor(box.maxX);
      int var4 = MathHelper.floor(box.minY);
      int var5 = MathHelper.floor(box.maxY);
      int var6 = MathHelper.floor(box.minZ);
      int var7 = MathHelper.floor(box.maxZ);

      for(int var8 = var2; var8 <= var3; ++var8) {
         for(int var9 = var4; var9 <= var5; ++var9) {
            for(int var10 = var6; var10 <= var7; ++var10) {
               Block var11 = this.getBlockState(new BlockPos(var8, var9, var10)).getBlock();
               if (var11.getMaterial() != Material.AIR) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean containsLiquid(Box box) {
      int var2 = MathHelper.floor(box.minX);
      int var3 = MathHelper.floor(box.maxX);
      int var4 = MathHelper.floor(box.minY);
      int var5 = MathHelper.floor(box.maxY);
      int var6 = MathHelper.floor(box.minZ);
      int var7 = MathHelper.floor(box.maxZ);

      for(int var8 = var2; var8 <= var3; ++var8) {
         for(int var9 = var4; var9 <= var5; ++var9) {
            for(int var10 = var6; var10 <= var7; ++var10) {
               Block var11 = this.getBlockState(new BlockPos(var8, var9, var10)).getBlock();
               if (var11.getMaterial().isLiquid()) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean containsFireSource(Box box) {
      int var2 = MathHelper.floor(box.minX);
      int var3 = MathHelper.floor(box.maxX + 1.0);
      int var4 = MathHelper.floor(box.minY);
      int var5 = MathHelper.floor(box.maxY + 1.0);
      int var6 = MathHelper.floor(box.minZ);
      int var7 = MathHelper.floor(box.maxZ + 1.0);
      if (this.isRegionLoaded(var2, var4, var6, var3, var5, var7, true)) {
         for(int var8 = var2; var8 < var3; ++var8) {
            for(int var9 = var4; var9 < var5; ++var9) {
               for(int var10 = var6; var10 < var7; ++var10) {
                  Block var11 = this.getBlockState(new BlockPos(var8, var9, var10)).getBlock();
                  if (var11 == Blocks.FIRE || var11 == Blocks.FLOWING_LAVA || var11 == Blocks.LAVA) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public boolean applyMaterialDrag(Box box, Material material, Entity entity) {
      int var4 = MathHelper.floor(box.minX);
      int var5 = MathHelper.floor(box.maxX + 1.0);
      int var6 = MathHelper.floor(box.minY);
      int var7 = MathHelper.floor(box.maxY + 1.0);
      int var8 = MathHelper.floor(box.minZ);
      int var9 = MathHelper.floor(box.maxZ + 1.0);
      if (!this.isRegionLoaded(var4, var6, var8, var5, var7, var9, true)) {
         return false;
      } else {
         boolean var10 = false;
         Vec3d var11 = new Vec3d(0.0, 0.0, 0.0);

         for(int var12 = var4; var12 < var5; ++var12) {
            for(int var13 = var6; var13 < var7; ++var13) {
               for(int var14 = var8; var14 < var9; ++var14) {
                  BlockPos var15 = new BlockPos(var12, var13, var14);
                  BlockState var16 = this.getBlockState(var15);
                  Block var17 = var16.getBlock();
                  if (var17.getMaterial() == material) {
                     double var18 = (double)((float)(var13 + 1) - LiquidBlock.getHeightLoss(var16.get(LiquidBlock.LEVEL)));
                     if ((double)var7 >= var18) {
                        var10 = true;
                        var11 = var17.applyMaterialDrag(this, var15, entity, var11);
                     }
                  }
               }
            }
         }

         if (var11.length() > 0.0 && entity.hasLiquidCollision()) {
            var11 = var11.normalize();
            double var21 = 0.014;
            entity.velocityX += var11.x * var21;
            entity.velocityY += var11.y * var21;
            entity.velocityZ += var11.z * var21;
         }

         return var10;
      }
   }

   public boolean containsMaterial(Box box, Material material) {
      int var3 = MathHelper.floor(box.minX);
      int var4 = MathHelper.floor(box.maxX + 1.0);
      int var5 = MathHelper.floor(box.minY);
      int var6 = MathHelper.floor(box.maxY + 1.0);
      int var7 = MathHelper.floor(box.minZ);
      int var8 = MathHelper.floor(box.maxZ + 1.0);

      for(int var9 = var3; var9 < var4; ++var9) {
         for(int var10 = var5; var10 < var6; ++var10) {
            for(int var11 = var7; var11 < var8; ++var11) {
               if (this.getBlockState(new BlockPos(var9, var10, var11)).getBlock().getMaterial() == material) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean containsLiquid(Box area, Material material) {
      int var3 = MathHelper.floor(area.minX);
      int var4 = MathHelper.floor(area.maxX + 1.0);
      int var5 = MathHelper.floor(area.minY);
      int var6 = MathHelper.floor(area.maxY + 1.0);
      int var7 = MathHelper.floor(area.minZ);
      int var8 = MathHelper.floor(area.maxZ + 1.0);

      for(int var9 = var3; var9 < var4; ++var9) {
         for(int var10 = var5; var10 < var6; ++var10) {
            for(int var11 = var7; var11 < var8; ++var11) {
               BlockPos var12 = new BlockPos(var9, var10, var11);
               BlockState var13 = this.getBlockState(var12);
               Block var14 = var13.getBlock();
               if (var14.getMaterial() == material) {
                  int var15 = var13.get(LiquidBlock.LEVEL);
                  double var16 = (double)(var10 + 1);
                  if (var15 < 8) {
                     var16 = (double)(var10 + 1) - (double)var15 / 8.0;
                  }

                  if (var16 >= area.minY) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public Explosion explode(Entity source, double x, double y, double z, float power, boolean destructive) {
      return this.explode(source, x, y, z, power, false, destructive);
   }

   public Explosion explode(Entity source, double x, double y, double z, float power, boolean createFire, boolean destructive) {
      Explosion var11 = new Explosion(this, source, x, y, z, power, createFire, destructive);
      var11.damageEntities();
      var11.damageBlocks(true);
      return var11;
   }

   public float getBlockDensity(Vec3d vec, Box box) {
      double var3 = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
      double var5 = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
      double var7 = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);
      if (!(var3 < 0.0) && !(var5 < 0.0) && !(var7 < 0.0)) {
         int var9 = 0;
         int var10 = 0;

         for(float var11 = 0.0F; var11 <= 1.0F; var11 = (float)((double)var11 + var3)) {
            for(float var12 = 0.0F; var12 <= 1.0F; var12 = (float)((double)var12 + var5)) {
               for(float var13 = 0.0F; var13 <= 1.0F; var13 = (float)((double)var13 + var7)) {
                  double var14 = box.minX + (box.maxX - box.minX) * (double)var11;
                  double var16 = box.minY + (box.maxY - box.minY) * (double)var12;
                  double var18 = box.minZ + (box.maxZ - box.minZ) * (double)var13;
                  if (this.rayTrace(new Vec3d(var14, var16, var18), vec) == null) {
                     ++var9;
                  }

                  ++var10;
               }
            }
         }

         return (float)var9 / (float)var10;
      } else {
         return 0.0F;
      }
   }

   public boolean extinguishFire(PlayerEntity player, BlockPos pos, Direction face) {
      pos = pos.offset(face);
      if (this.getBlockState(pos).getBlock() == Blocks.FIRE) {
         this.doEvent(player, 1004, pos, 0);
         this.removeBlock(pos);
         return true;
      } else {
         return false;
      }
   }

   @Environment(EnvType.CLIENT)
   public String getEntitiesDebugInfo() {
      return "All: " + this.entities.size();
   }

   @Environment(EnvType.CLIENT)
   public String getChunkSourceDebugInfo() {
      return this.chunkSource.getName();
   }

   @Override
   public BlockEntity getBlockEntity(BlockPos pos) {
      if (!this.contains(pos)) {
         return null;
      } else {
         BlockEntity var2 = null;
         if (this.isTickingBlockEntities) {
            for(int var3 = 0; var3 < this.pendingBlockEntities.size(); ++var3) {
               BlockEntity var4 = (BlockEntity)this.pendingBlockEntities.get(var3);
               if (!var4.isRemoved() && var4.getPos().equals(pos)) {
                  var2 = var4;
                  break;
               }
            }
         }

         if (var2 == null) {
            var2 = this.getChunk(pos).getBlockEntity(pos);
         }

         if (var2 == null) {
            for(int var5 = 0; var5 < this.pendingBlockEntities.size(); ++var5) {
               BlockEntity var6 = (BlockEntity)this.pendingBlockEntities.get(var5);
               if (!var6.isRemoved() && var6.getPos().equals(pos)) {
                  var2 = var6;
                  break;
               }
            }
         }

         return var2;
      }
   }

   public void setBlockEntity(BlockPos pos, BlockEntity blockEntity) {
      if (blockEntity != null && !blockEntity.isRemoved()) {
         if (this.isTickingBlockEntities) {
            blockEntity.setPos(pos);
            Iterator var3 = this.pendingBlockEntities.iterator();

            while(var3.hasNext()) {
               BlockEntity var4 = (BlockEntity)var3.next();
               if (var4.getPos().equals(pos)) {
                  var4.markRemoved();
                  var3.remove();
               }
            }

            this.pendingBlockEntities.add(blockEntity);
         } else {
            this.addBlockEntity(blockEntity);
            this.getChunk(pos).setBlockEntity(pos, blockEntity);
         }
      }
   }

   public void removeBlockEntity(BlockPos pos) {
      BlockEntity var2 = this.getBlockEntity(pos);
      if (var2 != null && this.isTickingBlockEntities) {
         var2.markRemoved();
         this.pendingBlockEntities.remove(var2);
      } else {
         if (var2 != null) {
            this.pendingBlockEntities.remove(var2);
            this.blockEntities.remove(var2);
            this.tickingBlockEntities.remove(var2);
         }

         this.getChunk(pos).removeBlockEntity(pos);
      }
   }

   public void unloadBlockEntity(BlockEntity blockEntity) {
      this.removedBlockEntities.add(blockEntity);
   }

   public boolean isFullCube(BlockPos pos) {
      BlockState var2 = this.getBlockState(pos);
      Box var3 = var2.getBlock().getCollisionShape(this, pos, var2);
      return var3 != null && var3.getAverageSideLength() >= 1.0;
   }

   public static boolean hasSolidTop(IWorld world, BlockPos pos) {
      BlockState var2 = world.getBlockState(pos);
      Block var3 = var2.getBlock();
      if (var3.getMaterial().isSolidBlocking() && var3.isFullCube()) {
         return true;
      } else if (var3 instanceof StairsBlock) {
         return var2.get(StairsBlock.HALF) == StairsBlock.Half.TOP;
      } else if (var3 instanceof SlabBlock) {
         return var2.get(SlabBlock.HALF) == SlabBlock.Half.TOP;
      } else if (var3 instanceof HopperBlock) {
         return true;
      } else if (var3 instanceof SnowLayerBlock) {
         return var2.get(SnowLayerBlock.LAYERS) == 7;
      } else {
         return false;
      }
   }

   public boolean isOpaqueFullCube(BlockPos pos, boolean defaultValue) {
      if (!this.contains(pos)) {
         return defaultValue;
      } else {
         WorldChunk var3 = this.chunkSource.getChunk(pos);
         if (var3.isEmpty()) {
            return defaultValue;
         } else {
            Block var4 = this.getBlockState(pos).getBlock();
            return var4.getMaterial().isSolidBlocking() && var4.isFullCube();
         }
      }
   }

   public void initAmbientDarkness() {
      int var1 = this.calculateAmbientDarkness(1.0F);
      if (var1 != this.ambientDarkness) {
         this.ambientDarkness = var1;
      }
   }

   public void setAllowedMobSpawns(boolean allowAnimals, boolean allowMonsters) {
      this.allowAnimals = allowAnimals;
      this.allowMonsters = allowMonsters;
   }

   public void tick() {
      this.tickWeather();
   }

   protected void initWeather() {
      if (this.data.isRaining()) {
         this.rain = 1.0F;
         if (this.data.isThundering()) {
            this.thunder = 1.0F;
         }
      }
   }

   protected void tickWeather() {
      if (!this.dimension.isDark()) {
         if (!this.isClient) {
            int var1 = this.data.getClearWeatherTime();
            if (var1 > 0) {
               this.data.setClearWeatherTime(--var1);
               this.data.setThunderTime(this.data.isThundering() ? 1 : 2);
               this.data.setRainTime(this.data.isRaining() ? 1 : 2);
            }

            int var2 = this.data.getThunderTime();
            if (var2 <= 0) {
               if (this.data.isThundering()) {
                  this.data.setThunderTime(this.random.nextInt(12000) + 3600);
               } else {
                  this.data.setThunderTime(this.random.nextInt(168000) + 12000);
               }
            } else {
               this.data.setThunderTime(--var2);
               if (var2 <= 0) {
                  this.data.setThundering(!this.data.isThundering());
               }
            }

            this.prevThunder = this.thunder;
            if (this.data.isThundering()) {
               this.thunder = (float)((double)this.thunder + 0.01);
            } else {
               this.thunder = (float)((double)this.thunder - 0.01);
            }

            this.thunder = MathHelper.clamp(this.thunder, 0.0F, 1.0F);
            int var3 = this.data.getRainTime();
            if (var3 <= 0) {
               if (this.data.isRaining()) {
                  this.data.setRainTime(this.random.nextInt(12000) + 12000);
               } else {
                  this.data.setRainTime(this.random.nextInt(168000) + 12000);
               }
            } else {
               this.data.setRainTime(--var3);
               if (var3 <= 0) {
                  this.data.setRaining(!this.data.isRaining());
               }
            }

            this.prevRain = this.rain;
            if (this.data.isRaining()) {
               this.rain = (float)((double)this.rain + 0.01);
            } else {
               this.rain = (float)((double)this.rain - 0.01);
            }

            this.rain = MathHelper.clamp(this.rain, 0.0F, 1.0F);
         }
      }
   }

   protected void purgeTickingChunks() {
      this.tickingChunks.clear();
      this.profiler.push("buildList");

      for(int var1 = 0; var1 < this.players.size(); ++var1) {
         PlayerEntity var2 = (PlayerEntity)this.players.get(var1);
         int var3 = MathHelper.floor(var2.x / 16.0);
         int var4 = MathHelper.floor(var2.z / 16.0);
         int var5 = this.getChunkViewDistance();

         for(int var6 = -var5; var6 <= var5; ++var6) {
            for(int var7 = -var5; var7 <= var5; ++var7) {
               this.tickingChunks.add(new ChunkPos(var6 + var3, var7 + var4));
            }
         }
      }

      this.profiler.pop();
      if (this.ambientSoundCooldown > 0) {
         --this.ambientSoundCooldown;
      }

      this.profiler.push("playerCheckLight");
      if (!this.players.isEmpty()) {
         int var8 = this.random.nextInt(this.players.size());
         PlayerEntity var9 = (PlayerEntity)this.players.get(var8);
         int var10 = MathHelper.floor(var9.x) + this.random.nextInt(11) - 5;
         int var11 = MathHelper.floor(var9.y) + this.random.nextInt(11) - 5;
         int var12 = MathHelper.floor(var9.z) + this.random.nextInt(11) - 5;
         this.checkLight(new BlockPos(var10, var11, var12));
      }

      this.profiler.pop();
   }

   protected abstract int getChunkViewDistance();

   protected void tickAmbienceAndLight(int x, int z, WorldChunk chunk) {
      this.profiler.swap("moodSound");
      if (this.ambientSoundCooldown == 0 && !this.isClient) {
         this.randomTickLCG = this.randomTickLCG * 3 + 1013904223;
         int var4 = this.randomTickLCG >> 2;
         int var5 = var4 & 15;
         int var6 = var4 >> 8 & 15;
         int var7 = var4 >> 16 & 0xFF;
         BlockPos var8 = new BlockPos(var5, var7, var6);
         Block var9 = chunk.getBlock(var8);
         var5 += x;
         var6 += z;
         if (var9.getMaterial() == Material.AIR && this.getLight(var8) <= this.random.nextInt(8) && this.getLight(LightType.SKY, var8) <= 0) {
            PlayerEntity var10 = this.getClosestPlayer((double)var5 + 0.5, (double)var7 + 0.5, (double)var6 + 0.5, 8.0);
            if (var10 != null && var10.getSquaredDistanceTo((double)var5 + 0.5, (double)var7 + 0.5, (double)var6 + 0.5) > 4.0) {
               this.playSound((double)var5 + 0.5, (double)var7 + 0.5, (double)var6 + 0.5, "ambient.cave.cave", 0.7F, 0.8F + this.random.nextFloat() * 0.2F);
               this.ambientSoundCooldown = this.random.nextInt(12000) + 6000;
            }
         }
      }

      this.profiler.swap("checkLight");
      chunk.checkLight();
   }

   protected void tickChunks() {
      this.purgeTickingChunks();
   }

   public void tickBlockNow(Block block, BlockPos pos, Random random) {
      this.doTicksImmediately = true;
      block.tick(this, pos, this.getBlockState(pos), random);
      this.doTicksImmediately = false;
   }

   public boolean canFreeze(BlockPos pos) {
      return this.canFreeze(pos, false);
   }

   public boolean canFreezeNaturally(BlockPos pos) {
      return this.canFreeze(pos, true);
   }

   public boolean canFreeze(BlockPos pos, boolean needsAdjacentNonWaterBlock) {
      Biome var3 = this.getBiome(pos);
      float var4 = var3.getTemperature(pos);
      if (var4 > 0.15F) {
         return false;
      } else {
         if (pos.getY() >= 0 && pos.getY() < 256 && this.getLight(LightType.BLOCK, pos) < 10) {
            BlockState var5 = this.getBlockState(pos);
            Block var6 = var5.getBlock();
            if ((var6 == Blocks.WATER || var6 == Blocks.FLOWING_WATER) && var5.get(LiquidBlock.LEVEL) == 0) {
               if (!needsAdjacentNonWaterBlock) {
                  return true;
               }

               boolean var7 = this.isWater(pos.west()) && this.isWater(pos.east()) && this.isWater(pos.north()) && this.isWater(pos.south());
               if (!var7) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private boolean isWater(BlockPos pos) {
      return this.getBlockState(pos).getBlock().getMaterial() == Material.WATER;
   }

   public boolean canSnowFall(BlockPos pos, boolean checkLight) {
      Biome var3 = this.getBiome(pos);
      float var4 = var3.getTemperature(pos);
      if (var4 > 0.15F) {
         return false;
      } else if (!checkLight) {
         return true;
      } else {
         if (pos.getY() >= 0 && pos.getY() < 256 && this.getLight(LightType.BLOCK, pos) < 10) {
            Block var5 = this.getBlockState(pos).getBlock();
            if (var5.getMaterial() == Material.AIR && Blocks.SNOW_LAYER.canSurvive(this, pos)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean checkLight(BlockPos pos) {
      boolean var2 = false;
      if (!this.dimension.isDark()) {
         var2 |= this.checkLight(LightType.SKY, pos);
      }

      return var2 | this.checkLight(LightType.BLOCK, pos);
   }

   private int getRawLight(BlockPos pos, LightType type) {
      if (type == LightType.SKY && this.hasSkyAccess(pos)) {
         return 15;
      } else {
         Block var3 = this.getBlockState(pos).getBlock();
         int var4 = type == LightType.SKY ? 0 : var3.getLightLevel();
         int var5 = var3.getOpacity();
         if (var5 >= 15 && var3.getLightLevel() > 0) {
            var5 = 1;
         }

         if (var5 < 1) {
            var5 = 1;
         }

         if (var5 >= 15) {
            return 0;
         } else if (var4 >= 14) {
            return var4;
         } else {
            for(Direction var9 : Direction.values()) {
               BlockPos var10 = pos.offset(var9);
               int var11 = this.getLight(type, var10) - var5;
               if (var11 > var4) {
                  var4 = var11;
               }

               if (var4 >= 14) {
                  return var4;
               }
            }

            return var4;
         }
      }
   }

   public boolean checkLight(LightType type, BlockPos pos) {
      if (!this.isRegionLoaded(pos, 17, false)) {
         return false;
      } else {
         int var3 = 0;
         int var4 = 0;
         this.profiler.push("getBrightness");
         int var5 = this.getLight(type, pos);
         int var6 = this.getRawLight(pos, type);
         int var7 = pos.getX();
         int var8 = pos.getY();
         int var9 = pos.getZ();
         if (var6 > var5) {
            this.scheduledLightUpdates[var4++] = 133152;
         } else if (var6 < var5) {
            this.scheduledLightUpdates[var4++] = 133152 | var5 << 18;

            while(var3 < var4) {
               int var10 = this.scheduledLightUpdates[var3++];
               int var11 = (var10 & 63) - 32 + var7;
               int var12 = (var10 >> 6 & 63) - 32 + var8;
               int var13 = (var10 >> 12 & 63) - 32 + var9;
               int var14 = var10 >> 18 & 15;
               BlockPos var15 = new BlockPos(var11, var12, var13);
               int var16 = this.getLight(type, var15);
               if (var16 == var14) {
                  this.setLight(type, var15, 0);
                  if (var14 > 0) {
                     int var17 = MathHelper.abs(var11 - var7);
                     int var18 = MathHelper.abs(var12 - var8);
                     int var19 = MathHelper.abs(var13 - var9);
                     if (var17 + var18 + var19 < 17) {
                        for(Direction var23 : Direction.values()) {
                           int var24 = var11 + var23.getOffsetX();
                           int var25 = var12 + var23.getOffsetY();
                           int var26 = var13 + var23.getOffsetZ();
                           BlockPos var27 = new BlockPos(var24, var25, var26);
                           int var28 = Math.max(1, this.getBlockState(var27).getBlock().getOpacity());
                           var16 = this.getLight(type, var27);
                           if (var16 == var14 - var28 && var4 < this.scheduledLightUpdates.length) {
                              this.scheduledLightUpdates[var4++] = var24 - var7 + 32 | var25 - var8 + 32 << 6 | var26 - var9 + 32 << 12 | var14 - var28 << 18;
                           }
                        }
                     }
                  }
               }
            }

            var3 = 0;
         }

         this.profiler.pop();
         this.profiler.push("checkedPosition < toCheckCount");

         while(var3 < var4) {
            int var29 = this.scheduledLightUpdates[var3++];
            int var30 = (var29 & 63) - 32 + var7;
            int var31 = (var29 >> 6 & 63) - 32 + var8;
            int var32 = (var29 >> 12 & 63) - 32 + var9;
            BlockPos var33 = new BlockPos(var30, var31, var32);
            int var34 = this.getLight(type, var33);
            int var36 = this.getRawLight(var33, type);
            if (var36 != var34) {
               this.setLight(type, var33, var36);
               if (var36 > var34) {
                  int var37 = Math.abs(var30 - var7);
                  int var38 = Math.abs(var31 - var8);
                  int var39 = Math.abs(var32 - var9);
                  boolean var40 = var4 < this.scheduledLightUpdates.length - 6;
                  if (var37 + var38 + var39 < 17 && var40) {
                     if (this.getLight(type, var33.west()) < var36) {
                        this.scheduledLightUpdates[var4++] = var30 - 1 - var7 + 32 + (var31 - var8 + 32 << 6) + (var32 - var9 + 32 << 12);
                     }

                     if (this.getLight(type, var33.east()) < var36) {
                        this.scheduledLightUpdates[var4++] = var30 + 1 - var7 + 32 + (var31 - var8 + 32 << 6) + (var32 - var9 + 32 << 12);
                     }

                     if (this.getLight(type, var33.down()) < var36) {
                        this.scheduledLightUpdates[var4++] = var30 - var7 + 32 + (var31 - 1 - var8 + 32 << 6) + (var32 - var9 + 32 << 12);
                     }

                     if (this.getLight(type, var33.up()) < var36) {
                        this.scheduledLightUpdates[var4++] = var30 - var7 + 32 + (var31 + 1 - var8 + 32 << 6) + (var32 - var9 + 32 << 12);
                     }

                     if (this.getLight(type, var33.north()) < var36) {
                        this.scheduledLightUpdates[var4++] = var30 - var7 + 32 + (var31 - var8 + 32 << 6) + (var32 - 1 - var9 + 32 << 12);
                     }

                     if (this.getLight(type, var33.south()) < var36) {
                        this.scheduledLightUpdates[var4++] = var30 - var7 + 32 + (var31 - var8 + 32 << 6) + (var32 + 1 - var9 + 32 << 12);
                     }
                  }
               }
            }
         }

         this.profiler.pop();
         return true;
      }
   }

   public boolean doScheduledTicks(boolean debug) {
      return false;
   }

   public List getScheduledTicks(WorldChunk chunk, boolean remove) {
      return null;
   }

   public List getScheduledTicks(StructureBox box, boolean remove) {
      return null;
   }

   public List getEntities(Entity exclude, Box box) {
      return this.getEntities(exclude, box, EntityFilter.NOT_SPECTATOR);
   }

   public List getEntities(Entity exclude, Box box, Predicate filter) {
      ArrayList var4 = Lists.newArrayList();
      int var5 = MathHelper.floor((box.minX - 2.0) / 16.0);
      int var6 = MathHelper.floor((box.maxX + 2.0) / 16.0);
      int var7 = MathHelper.floor((box.minZ - 2.0) / 16.0);
      int var8 = MathHelper.floor((box.maxZ + 2.0) / 16.0);

      for(int var9 = var5; var9 <= var6; ++var9) {
         for(int var10 = var7; var10 <= var8; ++var10) {
            if (this.isChunkLoaded(var9, var10, true)) {
               this.getChunkAt(var9, var10).getEntities(exclude, box, var4, filter);
            }
         }
      }

      return var4;
   }

   public List getEntities(Class type, Predicate filter) {
      ArrayList var3 = Lists.newArrayList();

      for(Entity var5 : this.entities) {
         if (type.isAssignableFrom(var5.getClass()) && filter.apply(var5)) {
            var3.add(var5);
         }
      }

      return var3;
   }

   public List getPlayers(Class type, Predicate filter) {
      ArrayList var3 = Lists.newArrayList();

      for(Entity var5 : this.players) {
         if (type.isAssignableFrom(var5.getClass()) && filter.apply(var5)) {
            var3.add(var5);
         }
      }

      return var3;
   }

   public List getEntities(Class type, Box box) {
      return this.getEntities(type, box, EntityFilter.NOT_SPECTATOR);
   }

   public List getEntities(Class type, Box box, Predicate filter) {
      int var4 = MathHelper.floor((box.minX - 2.0) / 16.0);
      int var5 = MathHelper.floor((box.maxX + 2.0) / 16.0);
      int var6 = MathHelper.floor((box.minZ - 2.0) / 16.0);
      int var7 = MathHelper.floor((box.maxZ + 2.0) / 16.0);
      ArrayList var8 = Lists.newArrayList();

      for(int var9 = var4; var9 <= var5; ++var9) {
         for(int var10 = var6; var10 <= var7; ++var10) {
            if (this.isChunkLoaded(var9, var10, true)) {
               this.getChunkAt(var9, var10).getEntities(type, box, var8, filter);
            }
         }
      }

      return var8;
   }

   public Entity getClosestEntity(Class type, Box box, Entity entity) {
      List var4 = this.getEntities(type, box);
      Entity var5 = null;
      double var6 = Double.MAX_VALUE;

      for(int var8 = 0; var8 < var4.size(); ++var8) {
         Entity var9 = (Entity)var4.get(var8);
         if (var9 != entity && EntityFilter.NOT_SPECTATOR.apply(var9)) {
            double var10 = entity.getSquaredDistanceTo(var9);
            if (!(var10 > var6)) {
               var5 = var9;
               var6 = var10;
            }
         }
      }

      return var5;
   }

   public Entity getEntity(int networkId) {
      return (Entity)this.entitiesByNetworkId.get(networkId);
   }

   @Environment(EnvType.CLIENT)
   public List getEntities() {
      return this.entities;
   }

   public void onBlockEntityChanged(BlockPos pos, BlockEntity blockEntity) {
      if (this.isLoaded(pos)) {
         this.getChunk(pos).markDirty();
      }
   }

   public int getEntityCount(Class type) {
      int var2 = 0;

      for(Entity var4 : this.entities) {
         if ((!(var4 instanceof MobEntity) || !((MobEntity)var4).isPersistent()) && type.isAssignableFrom(var4.getClass())) {
            ++var2;
         }
      }

      return var2;
   }

   public void addEntities(Collection entities) {
      this.entities.addAll(entities);

      for(Entity var3 : entities) {
         this.onEntityAdded(var3);
      }
   }

   public void removeEntities(Collection entities) {
      this.entitiesToRemove.addAll(entities);
   }

   public boolean canReplace(Block block, BlockPos pos, boolean isFalling, Direction face, Entity entity, ItemStack stack) {
      Block var7 = this.getBlockState(pos).getBlock();
      Box var8 = isFalling ? null : block.getCollisionShape(this, pos, block.defaultState());
      if (var8 != null && !this.canBuildIn(var8, entity)) {
         return false;
      } else if (var7.getMaterial() == Material.DECORATION && block == Blocks.ANVIL) {
         return true;
      } else {
         return var7.getMaterial().isReplaceable() && block.canPlace(this, pos, face, stack);
      }
   }

   public Path pathFindEntity(Entity c_47ldwddrb, Entity c_47ldwddrb2, float f, PathHelper c_66qwfgvoa) {
      this.profiler.push("pathfind");
      BlockPos var5 = new BlockPos(c_47ldwddrb).up();
      int var6 = (int)(f + 16.0F);
      WorldRegion var7 = new WorldRegion(this, var5.add(-var6, -var6, -var6), var5.add(var6, var6, var6), 0);
      Path var8 = c_66qwfgvoa.getPathToEntity(var7, c_47ldwddrb, c_47ldwddrb2, f);
      this.profiler.pop();
      return var8;
   }

   public Path pathFindEntity(Entity c_47ldwddrb, BlockPos c_76varpwca, float f, PathHelper c_66qwfgvoa) {
      this.profiler.push("pathfind");
      BlockPos var5 = new BlockPos(c_47ldwddrb);
      int var6 = (int)(f + 8.0F);
      WorldRegion var7 = new WorldRegion(this, var5.add(-var6, -var6, -var6), var5.add(var6, var6, var6), 0);
      Path var8 = c_66qwfgvoa.getPathToPos(var7, c_47ldwddrb, c_76varpwca, f);
      this.profiler.pop();
      return var8;
   }

   @Override
   public int getEmittedStrongPower(BlockPos pos, Direction dir) {
      BlockState var3 = this.getBlockState(pos);
      return var3.getBlock().getEmittedStrongPower(this, pos, var3, dir);
   }

   @Override
   public WorldGeneratorType getGeneratorType() {
      return this.data.getGeneratorType();
   }

   public int getReceivedStrongPower(BlockPos pos) {
      int var2 = 0;
      var2 = Math.max(var2, this.getEmittedStrongPower(pos.down(), Direction.DOWN));
      if (var2 >= 15) {
         return var2;
      } else {
         var2 = Math.max(var2, this.getEmittedStrongPower(pos.up(), Direction.UP));
         if (var2 >= 15) {
            return var2;
         } else {
            var2 = Math.max(var2, this.getEmittedStrongPower(pos.north(), Direction.NORTH));
            if (var2 >= 15) {
               return var2;
            } else {
               var2 = Math.max(var2, this.getEmittedStrongPower(pos.south(), Direction.SOUTH));
               if (var2 >= 15) {
                  return var2;
               } else {
                  var2 = Math.max(var2, this.getEmittedStrongPower(pos.west(), Direction.WEST));
                  if (var2 >= 15) {
                     return var2;
                  } else {
                     var2 = Math.max(var2, this.getEmittedStrongPower(pos.east(), Direction.EAST));
                     return var2 >= 15 ? var2 : var2;
                  }
               }
            }
         }
      }
   }

   public boolean isEmittingPower(BlockPos pos, Direction dir) {
      return this.getEmittedPower(pos, dir) > 0;
   }

   public int getEmittedPower(BlockPos pos, Direction dir) {
      BlockState var3 = this.getBlockState(pos);
      Block var4 = var3.getBlock();
      return var4.isConductor() ? this.getReceivedStrongPower(pos) : var4.getEmittedWeakPower(this, pos, var3, dir);
   }

   public boolean isReceivingPower(BlockPos pos) {
      if (this.getEmittedPower(pos.down(), Direction.DOWN) > 0) {
         return true;
      } else if (this.getEmittedPower(pos.up(), Direction.UP) > 0) {
         return true;
      } else if (this.getEmittedPower(pos.north(), Direction.NORTH) > 0) {
         return true;
      } else if (this.getEmittedPower(pos.south(), Direction.SOUTH) > 0) {
         return true;
      } else if (this.getEmittedPower(pos.west(), Direction.WEST) > 0) {
         return true;
      } else {
         return this.getEmittedPower(pos.east(), Direction.EAST) > 0;
      }
   }

   public int getReceivedPower(BlockPos pos) {
      int var2 = 0;

      for(Direction var6 : Direction.values()) {
         int var7 = this.getEmittedPower(pos.offset(var6), var6);
         if (var7 >= 15) {
            return 15;
         }

         if (var7 > var2) {
            var2 = var7;
         }
      }

      return var2;
   }

   public PlayerEntity getClosestPlayer(Entity entity, double range) {
      return this.getClosestPlayer(entity.x, entity.y, entity.z, range);
   }

   public PlayerEntity getClosestPlayer(double x, double y, double z, double range) {
      double var9 = -1.0;
      PlayerEntity var11 = null;

      for(int var12 = 0; var12 < this.players.size(); ++var12) {
         PlayerEntity var13 = (PlayerEntity)this.players.get(var12);
         if (EntityFilter.NOT_SPECTATOR.apply(var13)) {
            double var14 = var13.getSquaredDistanceTo(x, y, z);
            if ((range < 0.0 || var14 < range * range) && (var9 == -1.0 || var14 < var9)) {
               var9 = var14;
               var11 = var13;
            }
         }
      }

      return var11;
   }

   public boolean isPlayerWithinRange(double x, double y, double z, double range) {
      for(int var9 = 0; var9 < this.players.size(); ++var9) {
         PlayerEntity var10 = (PlayerEntity)this.players.get(var9);
         if (EntityFilter.NOT_SPECTATOR.apply(var10)) {
            double var11 = var10.getSquaredDistanceTo(x, y, z);
            if (range < 0.0 || var11 < range * range) {
               return true;
            }
         }
      }

      return false;
   }

   public PlayerEntity getPlayer(String name) {
      for(int var2 = 0; var2 < this.players.size(); ++var2) {
         PlayerEntity var3 = (PlayerEntity)this.players.get(var2);
         if (name.equals(var3.getName())) {
            return var3;
         }
      }

      return null;
   }

   public PlayerEntity getPlayer(UUID uuid) {
      for(int var2 = 0; var2 < this.players.size(); ++var2) {
         PlayerEntity var3 = (PlayerEntity)this.players.get(var2);
         if (uuid.equals(var3.getUuid())) {
            return var3;
         }
      }

      return null;
   }

   @Environment(EnvType.CLIENT)
   public void disconnect() {
   }

   public void checkSessionLock() {
      this.storage.checkSessionLock();
   }

   @Environment(EnvType.CLIENT)
   public void setTime(long time) {
      this.data.setTime(time);
   }

   public long getSeed() {
      return this.data.getSeed();
   }

   public long getTime() {
      return this.data.getTime();
   }

   public long getTimeOfDay() {
      return this.data.getTimeOfDay();
   }

   public void setTimeOfDay(long time) {
      this.data.setTimeOfDay(time);
   }

   public BlockPos getSpawnPoint() {
      BlockPos var1 = new BlockPos(this.data.getSpawnX(), this.data.getSpawnY(), this.data.getSpawnZ());
      if (!this.getWorldBorder().contains(var1)) {
         var1 = this.getHeight(new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
      }

      return var1;
   }

   public void setSpawnPoint(BlockPos pos) {
      this.data.setSpawnPoint(pos);
   }

   @Environment(EnvType.CLIENT)
   public void addEntityAlways(Entity entity) {
      int var2 = MathHelper.floor(entity.x / 16.0);
      int var3 = MathHelper.floor(entity.z / 16.0);
      byte var4 = 2;

      for(int var5 = var2 - var4; var5 <= var2 + var4; ++var5) {
         for(int var6 = var3 - var4; var6 <= var3 + var4; ++var6) {
            this.getChunkAt(var5, var6);
         }
      }

      if (!this.entities.contains(entity)) {
         this.entities.add(entity);
      }
   }

   public boolean canModify(PlayerEntity player, BlockPos pos) {
      return true;
   }

   public void doEntityEvent(Entity entity, byte event) {
   }

   public ChunkSource getChunkSource() {
      return this.chunkSource;
   }

   public void addBlockEvent(BlockPos pos, Block block, int type, int data) {
      block.doEvent(this, pos, this.getBlockState(pos), type, data);
   }

   public WorldStorage getStorage() {
      return this.storage;
   }

   public WorldData getData() {
      return this.data;
   }

   public Gamerules getGameRules() {
      return this.data.getGamerules();
   }

   public void updateSleepingPlayers() {
   }

   public float getThunder(float tickDelta) {
      return (this.prevThunder + (this.thunder - this.prevThunder) * tickDelta) * this.getRain(tickDelta);
   }

   @Environment(EnvType.CLIENT)
   public void setThunder(float thunder) {
      this.prevThunder = thunder;
      this.thunder = thunder;
   }

   public float getRain(float tickDelta) {
      return this.prevRain + (this.rain - this.prevRain) * tickDelta;
   }

   @Environment(EnvType.CLIENT)
   public void setRain(float rain) {
      this.prevRain = rain;
      this.rain = rain;
   }

   public boolean isThundering() {
      return (double)this.getThunder(1.0F) > 0.9;
   }

   public boolean isRaining() {
      return (double)this.getRain(1.0F) > 0.2;
   }

   public boolean isRaining(BlockPos pos) {
      if (!this.isRaining()) {
         return false;
      } else if (!this.hasSkyAccess(pos)) {
         return false;
      } else if (this.getPrecipitationHeight(pos).getY() > pos.getY()) {
         return false;
      } else {
         Biome var2 = this.getBiome(pos);
         if (var2.canSnow()) {
            return false;
         } else {
            return this.canSnowFall(pos, false) ? false : var2.canRain();
         }
      }
   }

   public boolean isHumid(BlockPos pos) {
      Biome var2 = this.getBiome(pos);
      return var2.isHumid();
   }

   public SavedDataStorage getSavedDataStorage() {
      return this.savedDataStorage;
   }

   public void setSavedData(String id, SavedData data) {
      this.savedDataStorage.setData(id, data);
   }

   public SavedData loadSavedData(Class type, String id) {
      return this.savedDataStorage.loadData(type, id);
   }

   public int getSavedDataCount(String id) {
      return this.savedDataStorage.getNextCount(id);
   }

   public void doGlobalEvent(int type, BlockPos pos, int data) {
      for(int var4 = 0; var4 < this.eventListeners.size(); ++var4) {
         ((WorldEventListener)this.eventListeners.get(var4)).doGlobalEvent(type, pos, data);
      }
   }

   public void doEvent(int type, BlockPos pos, int data) {
      this.doEvent(null, type, pos, data);
   }

   public void doEvent(PlayerEntity source, int type, BlockPos pos, int data) {
      try {
         for(int var5 = 0; var5 < this.eventListeners.size(); ++var5) {
            ((WorldEventListener)this.eventListeners.get(var5)).doEvent(source, type, pos, data);
         }
      } catch (Throwable var8) {
         CrashReport var6 = CrashReport.of(var8, "Playing level event");
         CashReportCategory var7 = var6.addCategory("Level event being played");
         var7.add("Block coordinates", CashReportCategory.formatPosition(pos));
         var7.add("Event source", source);
         var7.add("Event type", type);
         var7.add("Event data", data);
         throw new CrashException(var6);
      }
   }

   public int getHeight() {
      return 256;
   }

   public int getDimensionHeight() {
      return this.dimension.isDark() ? 128 : 256;
   }

   public Random setRandomSeed(int x, int z, int seed) {
      long var4 = (long)x * 341873128712L + (long)z * 132897987541L + this.getData().getSeed() + (long)seed;
      this.random.setSeed(var4);
      return this.random;
   }

   public BlockPos findNearestStructure(String type, BlockPos pos) {
      return this.getChunkSource().findNearestStructure(this, type, pos);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean isSaved() {
      return false;
   }

   @Environment(EnvType.CLIENT)
   public double getHorizonHeight() {
      return this.data.getGeneratorType() == WorldGeneratorType.FLAT ? 0.0 : 63.0;
   }

   public CashReportCategory populateCrashReport(CrashReport report) {
      CashReportCategory var2 = report.addCategory("Affected level", 1);
      var2.add("Level name", this.data == null ? "????" : this.data.getName());
      var2.add("All players", new Callable() {
         public String call() {
            return World.this.players.size() + " total; " + World.this.players.toString();
         }
      });
      var2.add("Chunk stats", new Callable() {
         public String call() {
            return World.this.chunkSource.getName();
         }
      });

      try {
         this.data.populateCrashReport(var2);
      } catch (Throwable var4) {
         var2.add("Level Data Unobtainable", var4);
      }

      return var2;
   }

   public void updateBlockMiningProgress(int id, BlockPos pos, int progress) {
      for(int var4 = 0; var4 < this.eventListeners.size(); ++var4) {
         WorldEventListener var5 = (WorldEventListener)this.eventListeners.get(var4);
         var5.updateBlockMiningProgress(id, pos, progress);
      }
   }

   public Calendar getCalendar() {
      if (this.getTime() % 600L == 0L) {
         this.calendar.setTimeInMillis(MinecraftServer.getTimeMillis());
      }

      return this.calendar;
   }

   @Environment(EnvType.CLIENT)
   public void addFireworksParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, NbtCompound nbt) {
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public void updateComparators(BlockPos pos, Block block) {
      for(Direction var4 : Direction.Plane.HORIZONTAL) {
         BlockPos var5 = pos.offset(var4);
         if (this.isLoaded(var5)) {
            BlockState var6 = this.getBlockState(var5);
            if (Blocks.COMPARATOR.isSameDiode(var6.getBlock())) {
               var6.getBlock().update(this, var5, var6, block);
            } else if (var6.getBlock().isConductor()) {
               var5 = var5.offset(var4);
               var6 = this.getBlockState(var5);
               if (Blocks.COMPARATOR.isSameDiode(var6.getBlock())) {
                  var6.getBlock().update(this, var5, var6, block);
               }
            }
         }
      }
   }

   public LocalDifficulty getLocalDifficulty(BlockPos pos) {
      long var2 = 0L;
      float var4 = 0.0F;
      if (this.isLoaded(pos)) {
         var4 = this.getMoonSize();
         var2 = this.getChunk(pos).getInhabitedTime();
      }

      return new LocalDifficulty(this.getDifficulty(), this.getTimeOfDay(), var2, var4);
   }

   public Difficulty getDifficulty() {
      return this.getData().getDifficulty();
   }

   public int getAmbientDarkness() {
      return this.ambientDarkness;
   }

   public void setAmbientDarkness(int ambientDarkness) {
      this.ambientDarkness = ambientDarkness;
   }

   @Environment(EnvType.CLIENT)
   public int getLightningCooldown() {
      return this.lightningCooldown;
   }

   public void setLightningCooldown(int ticks) {
      this.lightningCooldown = ticks;
   }

   public boolean isSearchingSpawnPoint() {
      return this.isSearchingSpawnPoint;
   }

   public SavedVillageData getVillageData() {
      return this.villageData;
   }

   public WorldBorder getWorldBorder() {
      return this.worldBorder;
   }
}
