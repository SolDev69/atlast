package net.minecraft.server.world;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.MobSpawnGroup;
import net.minecraft.entity.living.mob.MobSpawnerHelper;
import net.minecraft.entity.living.mob.passive.Tradable;
import net.minecraft.entity.living.mob.passive.animal.AnimalEntity;
import net.minecraft.entity.living.mob.water.WaterMobEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.entity.weather.LightningBoltEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.AddGlobalEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEventS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameEventS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.scoreboard.SavedScoreboardData;
import net.minecraft.server.ChunkMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.EntityTracker;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.scoreboard.ServerScoreboard;
import net.minecraft.server.world.chunk.ServerChunkCache;
import net.minecraft.util.BlockableEventLoop;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeSource;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.WorldChunkSection;
import net.minecraft.world.chunk.storage.ChunkStorage;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.WorldGeneratorType;
import net.minecraft.world.gen.feature.BonusChestFeature;
import net.minecraft.world.gen.structure.LootEntry;
import net.minecraft.world.gen.structure.StructureBox;
import net.minecraft.world.storage.SavedDataStorage;
import net.minecraft.world.storage.WorldStorage;
import net.minecraft.world.village.SavedVillageData;
import net.minecraft.world.village.VillageSiege;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerWorld extends World implements BlockableEventLoop {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MinecraftServer server;
   private final EntityTracker entityTracker;
   private final ChunkMap chunkMap;
   private final Set scheduledTicks = Sets.newHashSet();
   private final TreeSet scheduledTicksInOrder = new TreeSet();
   private final Map entitiesById = Maps.newHashMap();
   public ServerChunkCache chunkCache;
   public boolean isSaving;
   private boolean allPlayersSleeping;
   private int idleTimeout;
   private final PortalForcer portalForcer;
   private final MobSpawnerHelper spawnHelper = new MobSpawnerHelper();
   protected final VillageSiege villageSiege = new VillageSiege(this);
   private ServerWorld.BlockEventQueue[] blockEvents = new ServerWorld.BlockEventQueue[]{new ServerWorld.BlockEventQueue(), new ServerWorld.BlockEventQueue()};
   private int nextBlockEventQueueIndex;
   private static final List BONUS_CHEST_LOOT_ENTRIES = Lists.newArrayList(
      new LootEntry[]{
         new LootEntry(Items.STICK, 0, 1, 3, 10),
         new LootEntry(Item.byBlock(Blocks.PLANKS), 0, 1, 3, 10),
         new LootEntry(Item.byBlock(Blocks.LOG), 0, 1, 3, 10),
         new LootEntry(Items.STONE_AXE, 0, 1, 1, 3),
         new LootEntry(Items.WOODEN_AXE, 0, 1, 1, 5),
         new LootEntry(Items.STONE_PICKAXE, 0, 1, 1, 3),
         new LootEntry(Items.WOODEN_PICKAXE, 0, 1, 1, 5),
         new LootEntry(Items.APPLE, 0, 2, 3, 5),
         new LootEntry(Items.BREAD, 0, 2, 3, 3),
         new LootEntry(Item.byBlock(Blocks.LOG2), 0, 1, 3, 10)
      }
   );
   private List currentScheduledTicks = Lists.newArrayList();

   public ServerWorld(MinecraftServer server, WorldStorage storage, WorldData data, int dimensionId, Profiler profiler) {
      super(storage, data, Dimension.fromId(dimensionId), profiler, false);
      this.server = server;
      this.entityTracker = new EntityTracker(this);
      this.chunkMap = new ChunkMap(this);
      this.dimension.init(this);
      this.chunkSource = this.createChunkCache();
      this.portalForcer = new PortalForcer(this);
      this.initAmbientDarkness();
      this.initWeather();
      this.getWorldBorder().setMaxSize(server.getMaxWorldSize());
   }

   @Override
   public World init() {
      this.savedDataStorage = new SavedDataStorage(this.storage);
      String var1 = SavedVillageData.getId(this.dimension);
      SavedVillageData var2 = (SavedVillageData)this.savedDataStorage.loadData(SavedVillageData.class, var1);
      if (var2 == null) {
         this.villageData = new SavedVillageData(this);
         this.savedDataStorage.setData(var1, this.villageData);
      } else {
         this.villageData = var2;
         this.villageData.setWorld(this);
      }

      this.scoreboard = new ServerScoreboard(this.server);
      SavedScoreboardData var3 = (SavedScoreboardData)this.savedDataStorage.loadData(SavedScoreboardData.class, "scoreboard");
      if (var3 == null) {
         var3 = new SavedScoreboardData();
         this.savedDataStorage.setData("scoreboard", var3);
      }

      var3.setScoreboard(this.scoreboard);
      ((ServerScoreboard)this.scoreboard).setSavedData(var3);
      this.getWorldBorder().setCenter(this.data.getBorderCenterX(), this.data.getBorderCenterZ());
      this.getWorldBorder().setDamagePerBlock(this.data.getBorderDamagePerBlock());
      this.getWorldBorder().setSafeZone(this.data.getBorderSafeZone());
      this.getWorldBorder().setWarningBlocks(this.data.getBorderWarningBlocks());
      this.getWorldBorder().setWarningTime(this.data.getBorderWarningTime());
      if (this.data.getBorderSizeLerpTime() > 0) {
         this.getWorldBorder().setSize(this.data.getBorderSize(), this.data.getBorderSizeLerpTarget(), this.data.getBorderSizeLerpTime());
      } else {
         this.getWorldBorder().setSize(this.data.getBorderSize());
      }

      return this;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getData().isHardcore() && this.getDifficulty() != Difficulty.HARD) {
         this.getData().setDifficulty(Difficulty.HARD);
      }

      this.dimension.getBiomeSource().cleanCache();
      if (this.canSkipNight()) {
         if (this.getGameRules().getBoolean("doDaylightCycle")) {
            long var1 = this.data.getTimeOfDay() + 24000L;
            this.data.setTimeOfDay(var1 - var1 % 24000L);
         }

         this.wakeSleepingPlayers();
      }

      this.profiler.push("mobSpawner");
      if (this.getGameRules().getBoolean("doMobSpawning") && this.data.getGeneratorType() != WorldGeneratorType.DEBUG_ALL_BLOCK_STATES) {
         this.spawnHelper.spawnEntities(this, this.allowAnimals, this.allowMonsters, this.data.getTime() % 400L == 0L);
      }

      this.profiler.swap("chunkSource");
      this.chunkSource.tick();
      int var3 = this.calculateAmbientDarkness(1.0F);
      if (var3 != this.getAmbientDarkness()) {
         this.setAmbientDarkness(var3);
      }

      this.data.setTime(this.data.getTime() + 1L);
      if (this.getGameRules().getBoolean("doDaylightCycle")) {
         this.data.setTimeOfDay(this.data.getTimeOfDay() + 1L);
      }

      this.profiler.swap("tickPending");
      this.doScheduledTicks(false);
      this.profiler.swap("tickBlocks");
      this.tickChunks();
      this.profiler.swap("chunkMap");
      this.chunkMap.tick();
      this.profiler.swap("village");
      this.villageData.tick();
      this.villageSiege.tick();
      this.profiler.swap("portalForcer");
      this.portalForcer.tick(this.getTime());
      this.profiler.pop();
      this.doBlockEvents();
   }

   public Biome.SpawnEntry pickSpawnEntry(MobSpawnGroup group, BlockPos pos) {
      List var3 = this.getChunkSource().getSpawnEntries(group, pos);
      return var3 != null && !var3.isEmpty() ? (Biome.SpawnEntry)WeightedPicker.pick(this.random, var3) : null;
   }

   @Override
   public void updateSleepingPlayers() {
      this.allPlayersSleeping = false;
      if (!this.players.isEmpty()) {
         int var1 = 0;
         int var2 = 0;

         for(PlayerEntity var4 : this.players) {
            if (var4.isSpectator()) {
               ++var1;
            } else if (var4.isSleeping()) {
               ++var2;
            }
         }

         this.allPlayersSleeping = var2 > 0 && var2 >= this.players.size() - var1;
      }
   }

   protected void wakeSleepingPlayers() {
      this.allPlayersSleeping = false;

      for(PlayerEntity var2 : this.players) {
         if (var2.isSleeping()) {
            var2.wakeUp(false, false, true);
         }
      }

      this.clearWeather();
   }

   private void clearWeather() {
      this.data.setRainTime(0);
      this.data.setRaining(false);
      this.data.setThunderTime(0);
      this.data.setThundering(false);
   }

   public boolean canSkipNight() {
      if (this.allPlayersSleeping && !this.isClient) {
         for(PlayerEntity var2 : this.players) {
            if (var2.isSpectator() || !var2.isSleptEnough()) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void resetSpawnPoint() {
      if (this.data.getSpawnY() <= 0) {
         this.data.setSpawnY(64);
      }

      int var1 = this.data.getSpawnX();
      int var2 = this.data.getSpawnZ();
      int var3 = 0;

      while(this.getSurfaceBlock(new BlockPos(var1, 0, var2)).getMaterial() == Material.AIR) {
         var1 += this.random.nextInt(8) - this.random.nextInt(8);
         var2 += this.random.nextInt(8) - this.random.nextInt(8);
         if (++var3 == 10000) {
            break;
         }
      }

      this.data.setSpawnX(var1);
      this.data.setSpawnZ(var2);
   }

   @Override
   protected void tickChunks() {
      super.tickChunks();
      if (this.data.getGeneratorType() == WorldGeneratorType.DEBUG_ALL_BLOCK_STATES) {
         for(ChunkPos var22 : this.tickingChunks) {
            this.getChunkAt(var22.x, var22.z).tick(false);
         }
      } else {
         int var1 = 0;
         int var2 = 0;

         for(ChunkPos var4 : this.tickingChunks) {
            int var5 = var4.x * 16;
            int var6 = var4.z * 16;
            this.profiler.push("getChunk");
            WorldChunk var7 = this.getChunkAt(var4.x, var4.z);
            this.tickAmbienceAndLight(var5, var6, var7);
            this.profiler.swap("tickChunk");
            var7.tick(false);
            this.profiler.swap("thunder");
            if (this.random.nextInt(100000) == 0 && this.isRaining() && this.isThundering()) {
               this.randomTickLCG = this.randomTickLCG * 3 + 1013904223;
               int var8 = this.randomTickLCG >> 2;
               BlockPos var9 = this.findLightningTarget(new BlockPos(var5 + (var8 & 15), 0, var6 + (var8 >> 8 & 15)));
               if (this.isRaining(var9)) {
                  this.addGlobalEntity(new LightningBoltEntity(this, (double)var9.getX(), (double)var9.getY(), (double)var9.getZ()));
               }
            }

            this.profiler.swap("iceandsnow");
            if (this.random.nextInt(16) == 0) {
               this.randomTickLCG = this.randomTickLCG * 3 + 1013904223;
               int var23 = this.randomTickLCG >> 2;
               BlockPos var25 = this.getPrecipitationHeight(new BlockPos(var5 + (var23 & 15), 0, var6 + (var23 >> 8 & 15)));
               BlockPos var10 = var25.down();
               if (this.canFreezeNaturally(var10)) {
                  this.setBlockState(var10, Blocks.ICE.defaultState());
               }

               if (this.isRaining() && this.canSnowFall(var25, true)) {
                  this.setBlockState(var25, Blocks.SNOW_LAYER.defaultState());
               }

               if (this.isRaining() && this.getBiome(var10).canRain()) {
                  this.getBlockState(var10).getBlock().randomPrecipitationTick(this, var10);
               }
            }

            this.profiler.swap("tickBlocks");
            int var24 = this.getGameRules().getInt("randomTickSpeed");
            if (var24 > 0) {
               for(WorldChunkSection var12 : var7.getSections()) {
                  if (var12 != null && var12.hasRandomTickingBlocks()) {
                     for(int var13 = 0; var13 < var24; ++var13) {
                        this.randomTickLCG = this.randomTickLCG * 3 + 1013904223;
                        int var14 = this.randomTickLCG >> 2;
                        int var15 = var14 & 15;
                        int var16 = var14 >> 8 & 15;
                        int var17 = var14 >> 16 & 15;
                        ++var2;
                        BlockPos var18 = new BlockPos(var15 + var5, var17 + var12.getOffsetY(), var16 + var6);
                        BlockState var19 = var12.getBlockState(var15, var17, var16);
                        Block var20 = var19.getBlock();
                        if (var20.ticksRandomly()) {
                           ++var1;
                           var20.randomTick(this, var18, var19, this.random);
                        }
                     }
                  }
               }
            }

            this.profiler.pop();
         }
      }
   }

   protected BlockPos findLightningTarget(BlockPos pos) {
      BlockPos var2 = this.getPrecipitationHeight(pos);
      Box var3 = new Box(var2, new BlockPos(var2.getX(), this.getHeight(), var2.getZ())).expand(3.0, 3.0, 3.0);
      List var4 = this.getEntities(LivingEntity.class, var3, new Predicate() {
         public boolean apply(LivingEntity c_97zulxhng) {
            return c_97zulxhng != null && c_97zulxhng.isAlive() && ServerWorld.this.hasSkyAccess(c_97zulxhng.getSourceBlockPos());
         }
      });
      return !var4.isEmpty() ? ((LivingEntity)var4.get(this.random.nextInt(var4.size()))).getSourceBlockPos() : var2;
   }

   @Override
   public boolean willTickThisTick(BlockPos pos, Block block) {
      ScheduledTick var3 = new ScheduledTick(pos, block);
      return this.currentScheduledTicks.contains(var3);
   }

   @Override
   public void scheduleTick(BlockPos pos, Block block, int delay) {
      this.scheduleTick(pos, block, delay, 0);
   }

   @Override
   public void scheduleTick(BlockPos pos, Block block, int delay, int priority) {
      ScheduledTick var5 = new ScheduledTick(pos, block);
      byte var6 = 0;
      if (this.doTicksImmediately && block.getMaterial() != Material.AIR) {
         if (block.acceptsImmediateTicks()) {
            var6 = 8;
            if (this.isRegionLoaded(var5.pos.add(-var6, -var6, -var6), var5.pos.add(var6, var6, var6))) {
               BlockState var7 = this.getBlockState(var5.pos);
               if (var7.getBlock().getMaterial() != Material.AIR && var7.getBlock() == var5.getBlock()) {
                  var7.getBlock().tick(this, var5.pos, var7, this.random);
               }
            }

            return;
         }

         delay = 1;
      }

      if (this.isRegionLoaded(pos.add(-var6, -var6, -var6), pos.add(var6, var6, var6))) {
         if (block.getMaterial() != Material.AIR) {
            var5.setTime((long)delay + this.data.getTime());
            var5.setPriority(priority);
         }

         if (!this.scheduledTicks.contains(var5)) {
            this.scheduledTicks.add(var5);
            this.scheduledTicksInOrder.add(var5);
         }
      }
   }

   @Override
   public void loadScheduledTick(BlockPos pos, Block block, int delay, int priority) {
      ScheduledTick var5 = new ScheduledTick(pos, block);
      var5.setPriority(priority);
      if (block.getMaterial() != Material.AIR) {
         var5.setTime((long)delay + this.data.getTime());
      }

      if (!this.scheduledTicks.contains(var5)) {
         this.scheduledTicks.add(var5);
         this.scheduledTicksInOrder.add(var5);
      }
   }

   @Override
   public void tickEntities() {
      if (this.players.isEmpty()) {
         if (this.idleTimeout++ >= 1200) {
            return;
         }
      } else {
         this.resetIdleTimeout();
      }

      super.tickEntities();
   }

   public void resetIdleTimeout() {
      this.idleTimeout = 0;
   }

   @Override
   public boolean doScheduledTicks(boolean debug) {
      if (this.data.getGeneratorType() == WorldGeneratorType.DEBUG_ALL_BLOCK_STATES) {
         return false;
      } else {
         int var2 = this.scheduledTicksInOrder.size();
         if (var2 != this.scheduledTicks.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
         } else {
            if (var2 > 1000) {
               var2 = 1000;
            }

            this.profiler.push("cleaning");

            for(int var3 = 0; var3 < var2; ++var3) {
               ScheduledTick var4 = (ScheduledTick)this.scheduledTicksInOrder.first();
               if (!debug && var4.time > this.data.getTime()) {
                  break;
               }

               this.scheduledTicksInOrder.remove(var4);
               this.scheduledTicks.remove(var4);
               this.currentScheduledTicks.add(var4);
            }

            this.profiler.pop();
            this.profiler.push("ticking");
            Iterator var11 = this.currentScheduledTicks.iterator();

            while(var11.hasNext()) {
               ScheduledTick var12 = (ScheduledTick)var11.next();
               var11.remove();
               byte var5 = 0;
               if (this.isRegionLoaded(var12.pos.add(-var5, -var5, -var5), var12.pos.add(var5, var5, var5))) {
                  BlockState var6 = this.getBlockState(var12.pos);
                  if (var6.getBlock().getMaterial() != Material.AIR && Block.areEqual(var6.getBlock(), var12.getBlock())) {
                     try {
                        var6.getBlock().tick(this, var12.pos, var6, this.random);
                     } catch (Throwable var10) {
                        CrashReport var8 = CrashReport.of(var10, "Exception while ticking a block");
                        CashReportCategory var9 = var8.addCategory("Block being ticked");
                        CashReportCategory.addBlockDetails(var9, var12.pos, var6);
                        throw new CrashException(var8);
                     }
                  }
               } else {
                  this.scheduleTick(var12.pos, var12.getBlock(), 0);
               }
            }

            this.profiler.pop();
            this.currentScheduledTicks.clear();
            return !this.scheduledTicksInOrder.isEmpty();
         }
      }
   }

   @Override
   public List getScheduledTicks(WorldChunk chunk, boolean remove) {
      ChunkPos var3 = chunk.getPos();
      int var4 = (var3.x << 4) - 2;
      int var5 = var4 + 16 + 2;
      int var6 = (var3.z << 4) - 2;
      int var7 = var6 + 16 + 2;
      return this.getScheduledTicks(new StructureBox(var4, 0, var6, var5, 256, var7), remove);
   }

   @Override
   public List getScheduledTicks(StructureBox box, boolean remove) {
      ArrayList var3 = null;

      for(int var4 = 0; var4 < 2; ++var4) {
         Iterator var5;
         if (var4 == 0) {
            var5 = this.scheduledTicksInOrder.iterator();
         } else {
            var5 = this.currentScheduledTicks.iterator();
            if (!this.currentScheduledTicks.isEmpty()) {
               LOGGER.debug("toBeTicked = " + this.currentScheduledTicks.size());
            }
         }

         while(var5.hasNext()) {
            ScheduledTick var6 = (ScheduledTick)var5.next();
            BlockPos var7 = var6.pos;
            if (var7.getX() >= box.minX && var7.getX() < box.maxX && var7.getZ() >= box.minZ && var7.getZ() < box.maxZ) {
               if (remove) {
                  this.scheduledTicks.remove(var6);
                  var5.remove();
               }

               if (var3 == null) {
                  var3 = Lists.newArrayList();
               }

               var3.add(var6);
            }
         }
      }

      return var3;
   }

   @Override
   public void tickEntity(Entity entity, boolean requireLoaded) {
      if (!this.shouldSpawnAnimals() && (entity instanceof AnimalEntity || entity instanceof WaterMobEntity)) {
         entity.remove();
      }

      if (!this.shouldSpawnNpcs() && entity instanceof Tradable) {
         entity.remove();
      }

      super.tickEntity(entity, requireLoaded);
   }

   private boolean shouldSpawnNpcs() {
      return this.server.shouldSpawnNpcs();
   }

   private boolean shouldSpawnAnimals() {
      return this.server.shouldSpawnAnimals();
   }

   @Override
   protected ChunkSource createChunkCache() {
      ChunkStorage var1 = this.storage.getChunkStorage(this.dimension);
      this.chunkCache = new ServerChunkCache(this, var1, this.dimension.createChunkGenerator());
      return this.chunkCache;
   }

   public List getBlockEntities(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      ArrayList var7 = Lists.newArrayList();

      for(int var8 = 0; var8 < this.blockEntities.size(); ++var8) {
         BlockEntity var9 = (BlockEntity)this.blockEntities.get(var8);
         BlockPos var10 = var9.getPos();
         if (var10.getX() >= minX && var10.getY() >= minY && var10.getZ() >= minZ && var10.getX() < maxX && var10.getY() < maxY && var10.getZ() < maxZ) {
            var7.add(var9);
         }
      }

      return var7;
   }

   @Override
   public boolean canModify(PlayerEntity player, BlockPos pos) {
      return !this.server.isSpawnProtected(this, pos, player) && this.getWorldBorder().contains(pos);
   }

   @Override
   public void init(WorldSettings settings) {
      if (!this.data.isInitialized()) {
         try {
            this.initSpawnPoint(settings);
            if (this.data.getGeneratorType() == WorldGeneratorType.DEBUG_ALL_BLOCK_STATES) {
               this.initDebugWorld();
            }

            super.init(settings);
         } catch (Throwable var6) {
            CrashReport var3 = CrashReport.of(var6, "Exception initializing level");

            try {
               this.populateCrashReport(var3);
            } catch (Throwable var5) {
            }

            throw new CrashException(var3);
         }

         this.data.setInitialized(true);
      }
   }

   private void initDebugWorld() {
      this.data.setAllowStructures(false);
      this.data.setAllowCommands(true);
      this.data.setRaining(false);
      this.data.setThundering(false);
      this.data.setClearWeatherTime(1000000000);
      this.data.setTimeOfDay(6000L);
      this.data.setDefaultGamemode(WorldSettings.GameMode.SPECTATOR);
      this.data.setDifficulty(Difficulty.PEACEFUL);
      this.data.setDifficultyLocked(true);
      this.getGameRules().set("doDaylightCycle", "false");
   }

   private void initSpawnPoint(WorldSettings settings) {
      if (!this.dimension.hasWorldSpawn()) {
         this.data.setSpawnPoint(BlockPos.ORIGIN.up(this.dimension.getMinSpawnY()));
      } else if (this.data.getGeneratorType() == WorldGeneratorType.DEBUG_ALL_BLOCK_STATES) {
         this.data.setSpawnPoint(BlockPos.ORIGIN.up());
      } else {
         this.isSearchingSpawnPoint = true;
         BiomeSource var2 = this.dimension.getBiomeSource();
         List var3 = var2.getBiomesForSpawnpoint();
         Random var4 = new Random(this.getSeed());
         BlockPos var5 = var2.getBiomePos(0, 0, 256, var3, var4);
         int var6 = 0;
         int var7 = this.dimension.getMinSpawnY();
         int var8 = 0;
         if (var5 != null) {
            var6 = var5.getX();
            var8 = var5.getZ();
         } else {
            LOGGER.warn("Unable to find spawn biome");
         }

         int var9 = 0;

         while(!this.dimension.isValidSpawnPos(var6, var8)) {
            var6 += var4.nextInt(64) - var4.nextInt(64);
            var8 += var4.nextInt(64) - var4.nextInt(64);
            if (++var9 == 1000) {
               break;
            }
         }

         this.data.setSpawnPoint(new BlockPos(var6, var7, var8));
         this.isSearchingSpawnPoint = false;
         if (settings.hasBonusChest()) {
            this.placeBonusChest();
         }
      }
   }

   protected void placeBonusChest() {
      BonusChestFeature var1 = new BonusChestFeature(BONUS_CHEST_LOOT_ENTRIES, 10);

      for(int var2 = 0; var2 < 10; ++var2) {
         int var3 = this.data.getSpawnX() + this.random.nextInt(6) - this.random.nextInt(6);
         int var4 = this.data.getSpawnZ() + this.random.nextInt(6) - this.random.nextInt(6);
         BlockPos var5 = this.getSurfaceHeight(new BlockPos(var3, 0, var4)).up();
         if (var1.place(this, this.random, var5)) {
            break;
         }
      }
   }

   public BlockPos getForcedSpawnPoint() {
      return this.dimension.getForcedSpawnPoint();
   }

   public void save(boolean saveEntities, ProgressListener progressListener) {
      if (this.chunkSource.canSave()) {
         if (progressListener != null) {
            progressListener.updateProgress("Saving level");
         }

         this.saveData();
         if (progressListener != null) {
            progressListener.setTask("Saving chunks");
         }

         this.chunkSource.save(saveEntities, progressListener);

         for(WorldChunk var5 : this.chunkCache.getChunks()) {
            if (!this.chunkMap.isLoaded(var5.chunkX, var5.chunkZ)) {
               this.chunkCache.scheduleUnload(var5.chunkX, var5.chunkZ);
            }
         }
      }
   }

   public void saveChunks() {
      if (this.chunkSource.canSave()) {
         this.chunkSource.save();
      }
   }

   protected void saveData() {
      this.checkSessionLock();
      this.data.setBorderSize(this.getWorldBorder().getLerpSize());
      this.data.setBorderCenterX(this.getWorldBorder().getCenterX());
      this.data.setBorderCenterZ(this.getWorldBorder().getCenterZ());
      this.data.setBorderSafeZone(this.getWorldBorder().getSafeZone());
      this.data.setBorderDamagePerBlock(this.getWorldBorder().getDamagePerBlock());
      this.data.setBorderWarningBlocks(this.getWorldBorder().getWarningBlocks());
      this.data.setBorderWarningTime(this.getWorldBorder().getWarningTime());
      this.data.setBorderSizeLerpTarget(this.getWorldBorder().getSizeLerpTarget());
      this.data.m_91bztiyhs(this.getWorldBorder().getLerpTime());
      this.storage.saveData(this.data, this.server.getPlayerManager().getSinglePlayerData());
      this.savedDataStorage.save();
   }

   @Override
   protected void onEntityAdded(Entity entity) {
      super.onEntityAdded(entity);
      this.entitiesByNetworkId.put(entity.getNetworkId(), entity);
      this.entitiesById.put(entity.getUuid(), entity);
      Entity[] var2 = entity.getParts();
      if (var2 != null) {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            this.entitiesByNetworkId.put(var2[var3].getNetworkId(), var2[var3]);
         }
      }
   }

   @Override
   protected void onEntityRemoved(Entity entity) {
      super.onEntityRemoved(entity);
      this.entitiesByNetworkId.remove(entity.getNetworkId());
      this.entitiesById.remove(entity.getUuid());
      Entity[] var2 = entity.getParts();
      if (var2 != null) {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            this.entitiesByNetworkId.remove(var2[var3].getNetworkId());
         }
      }
   }

   @Override
   public boolean addGlobalEntity(Entity entity) {
      if (super.addGlobalEntity(entity)) {
         this.server.getPlayerManager().sendToAround(entity.x, entity.y, entity.z, 512.0, this.dimension.getId(), new AddGlobalEntityS2CPacket(entity));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void doEntityEvent(Entity entity, byte event) {
      this.getEntityTracker().sendToListenersAndTrackedEntityIfPlayer(entity, new EntityEventS2CPacket(entity, event));
   }

   @Override
   public Explosion explode(Entity source, double x, double y, double z, float power, boolean createFire, boolean destructive) {
      Explosion var11 = new Explosion(this, source, x, y, z, power, createFire, destructive);
      var11.damageEntities();
      var11.damageBlocks(false);
      if (!destructive) {
         var11.clearDamagedBlocks();
      }

      for(PlayerEntity var13 : this.players) {
         if (var13.getSquaredDistanceTo(x, y, z) < 4096.0) {
            ((ServerPlayerEntity)var13)
               .networkHandler
               .sendPacket(new ExplosionS2CPacket(x, y, z, power, var11.getDamagedBlocks(), (Vec3d)var11.getDamagedPlayers().get(var13)));
         }
      }

      return var11;
   }

   @Override
   public void addBlockEvent(BlockPos pos, Block block, int type, int data) {
      BlockEvent var5 = new BlockEvent(pos, block, type, data);

      for(BlockEvent var7 : this.blockEvents[this.nextBlockEventQueueIndex]) {
         if (var7.equals(var5)) {
            return;
         }
      }

      this.blockEvents[this.nextBlockEventQueueIndex].add(var5);
   }

   private void doBlockEvents() {
      while(!this.blockEvents[this.nextBlockEventQueueIndex].isEmpty()) {
         int var1 = this.nextBlockEventQueueIndex;
         this.nextBlockEventQueueIndex ^= 1;

         for(BlockEvent var3 : this.blockEvents[var1]) {
            if (this.doBlockEvent(var3)) {
               this.server
                  .getPlayerManager()
                  .sendToAround(
                     (double)var3.getPos().getX(),
                     (double)var3.getPos().getY(),
                     (double)var3.getPos().getZ(),
                     64.0,
                     this.dimension.getId(),
                     new BlockEventS2CPacket(var3.getPos(), var3.getBlock(), var3.getType(), var3.getData())
                  );
            }
         }

         this.blockEvents[var1].clear();
      }
   }

   private boolean doBlockEvent(BlockEvent blockEvent) {
      BlockState var2 = this.getBlockState(blockEvent.getPos());
      return var2.getBlock() == blockEvent.getBlock()
         ? var2.getBlock().doEvent(this, blockEvent.getPos(), var2, blockEvent.getType(), blockEvent.getData())
         : false;
   }

   public void waitIfSaving() {
      this.storage.waitIfSaving();
   }

   @Override
   protected void tickWeather() {
      boolean var1 = this.isRaining();
      super.tickWeather();
      if (this.prevRain != this.rain) {
         this.server.getPlayerManager().sendToDimension(new GameEventS2CPacket(7, this.rain), this.dimension.getId());
      }

      if (this.prevThunder != this.thunder) {
         this.server.getPlayerManager().sendToDimension(new GameEventS2CPacket(8, this.thunder), this.dimension.getId());
      }

      if (var1 != this.isRaining()) {
         if (var1) {
            this.server.getPlayerManager().sendToAll(new GameEventS2CPacket(2, 0.0F));
         } else {
            this.server.getPlayerManager().sendToAll(new GameEventS2CPacket(1, 0.0F));
         }

         this.server.getPlayerManager().sendToAll(new GameEventS2CPacket(7, this.rain));
         this.server.getPlayerManager().sendToAll(new GameEventS2CPacket(8, this.thunder));
      }
   }

   @Override
   protected int getChunkViewDistance() {
      return this.server.getPlayerManager().getChunkViewDistance();
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public EntityTracker getEntityTracker() {
      return this.entityTracker;
   }

   public ChunkMap getChunkMap() {
      return this.chunkMap;
   }

   public PortalForcer getPortalForcer() {
      return this.portalForcer;
   }

   public void addParticle(
      ParticleType type, double x, double y, double z, int count, double velocityX, double velocityY, double velocityZ, double velocityScale, int... parameters
   ) {
      this.addParticle(type, false, x, y, z, count, velocityX, velocityY, velocityZ, velocityScale, parameters);
   }

   public void addParticle(
      ParticleType type,
      boolean ignoreDistance,
      double x,
      double y,
      double z,
      int count,
      double velocityX,
      double velocityY,
      double velocityZ,
      double velocityScale,
      int... parameters
   ) {
      ParticleS2CPacket var19 = new ParticleS2CPacket(
         type, ignoreDistance, (float)x, (float)y, (float)z, (float)velocityX, (float)velocityY, (float)velocityZ, (float)velocityScale, count, parameters
      );

      for(int var20 = 0; var20 < this.players.size(); ++var20) {
         ServerPlayerEntity var21 = (ServerPlayerEntity)this.players.get(var20);
         BlockPos var22 = var21.getSourceBlockPos();
         double var23 = var22.squaredDistanceTo(x, y, z);
         if (var23 <= 256.0 || ignoreDistance && var23 <= 65536.0) {
            var21.networkHandler.sendPacket(var19);
         }
      }
   }

   public Entity getEntity(UUID uuid) {
      return (Entity)this.entitiesById.get(uuid);
   }

   @Override
   public ListenableFuture submit(Runnable event) {
      return this.server.submit(event);
   }

   @Override
   public boolean isOnSameThread() {
      return this.server.isOnSameThread();
   }

   static class BlockEventQueue extends ArrayList {
      private BlockEventQueue() {
      }
   }
}
