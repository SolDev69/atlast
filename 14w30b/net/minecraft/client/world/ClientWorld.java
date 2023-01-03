package net.minecraft.client.world;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.entity.particle.FireworksParticles;
import net.minecraft.client.network.handler.ClientPlayNetworkHandler;
import net.minecraft.client.sound.event.EmptyMinecartSoundEvent;
import net.minecraft.client.sound.event.SimpleSoundEvent;
import net.minecraft.client.world.chunk.ClientChunkCache;
import net.minecraft.client.world.storage.ClientSavedDataStorage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.Identifier;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.text.LiteralText;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.EmptyWorldStorage;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientWorld extends World {
   private ClientPlayNetworkHandler networkHandler;
   private ClientChunkCache chunkCache;
   private final Set forcedEntities = Sets.newHashSet();
   private final Set pendingEntities = Sets.newHashSet();
   private final MinecraftClient client = MinecraftClient.getInstance();
   private final Set removedChunks = Sets.newHashSet();

   public ClientWorld(ClientPlayNetworkHandler networkHandler, WorldSettings info, int dimensionId, Difficulty difficulty, Profiler profiler) {
      super(new EmptyWorldStorage(), new WorldData(info, "MpServer"), Dimension.fromId(dimensionId), profiler, true);
      this.networkHandler = networkHandler;
      this.getData().setDifficulty(difficulty);
      this.setSpawnPoint(new BlockPos(8, 64, 8));
      this.dimension.init(this);
      this.chunkSource = this.createChunkCache();
      this.savedDataStorage = new ClientSavedDataStorage();
      this.initAmbientDarkness();
      this.initWeather();
   }

   @Override
   public void tick() {
      super.tick();
      this.setTime(this.getTime() + 1L);
      if (this.getGameRules().getBoolean("doDaylightCycle")) {
         this.setTimeOfDay(this.getTimeOfDay() + 1L);
      }

      this.profiler.push("reEntryProcessing");

      for(int var1 = 0; var1 < 10 && !this.pendingEntities.isEmpty(); ++var1) {
         Entity var2 = (Entity)this.pendingEntities.iterator().next();
         this.pendingEntities.remove(var2);
         if (!this.entities.contains(var2)) {
            this.addEntity(var2);
         }
      }

      this.profiler.swap("chunkCache");
      this.chunkCache.tick();
      this.profiler.swap("blocks");
      this.tickChunks();
      this.profiler.pop();
   }

   public void regionChanged(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
   }

   @Override
   protected ChunkSource createChunkCache() {
      this.chunkCache = new ClientChunkCache(this);
      return this.chunkCache;
   }

   @Override
   protected void tickChunks() {
      super.tickChunks();
      this.removedChunks.retainAll(this.tickingChunks);
      if (this.removedChunks.size() == this.tickingChunks.size()) {
         this.removedChunks.clear();
      }

      int var1 = 0;

      for(ChunkPos var3 : this.tickingChunks) {
         if (!this.removedChunks.contains(var3)) {
            int var4 = var3.x * 16;
            int var5 = var3.z * 16;
            this.profiler.push("getChunk");
            WorldChunk var6 = this.getChunkAt(var3.x, var3.z);
            this.tickAmbienceAndLight(var4, var5, var6);
            this.profiler.pop();
            this.removedChunks.add(var3);
            if (++var1 >= 10) {
               return;
            }
         }
      }
   }

   public void updateChunk(int chunkX, int chunkZ, boolean load) {
      if (load) {
         this.chunkCache.loadChunk(chunkX, chunkZ);
      } else {
         this.chunkCache.unloadChunk(chunkX, chunkZ);
      }

      if (!load) {
         this.onRegionChanged(chunkX * 16, 0, chunkZ * 16, chunkX * 16 + 15, 256, chunkZ * 16 + 15);
      }
   }

   @Override
   public boolean addEntity(Entity entity) {
      boolean var2 = super.addEntity(entity);
      this.forcedEntities.add(entity);
      if (!var2) {
         this.pendingEntities.add(entity);
      } else if (entity instanceof MinecartEntity) {
         this.client.getSoundManager().play(new EmptyMinecartSoundEvent((MinecartEntity)entity));
      }

      return var2;
   }

   @Override
   public void removeEntity(Entity entity) {
      super.removeEntity(entity);
      this.forcedEntities.remove(entity);
   }

   @Override
   protected void onEntityAdded(Entity entity) {
      super.onEntityAdded(entity);
      if (this.pendingEntities.contains(entity)) {
         this.pendingEntities.remove(entity);
      }
   }

   @Override
   protected void onEntityRemoved(Entity entity) {
      super.onEntityRemoved(entity);
      boolean var2 = false;
      if (this.forcedEntities.contains(entity)) {
         if (entity.isAlive()) {
            this.pendingEntities.add(entity);
            var2 = true;
         } else {
            this.forcedEntities.remove(entity);
         }
      }
   }

   public void addEntity(int networkId, Entity entity) {
      Entity var3 = this.getEntity(networkId);
      if (var3 != null) {
         this.removeEntity(var3);
      }

      this.forcedEntities.add(entity);
      entity.setNetworkId(networkId);
      if (!this.addEntity(entity)) {
         this.pendingEntities.add(entity);
      }

      this.entitiesByNetworkId.put(networkId, entity);
   }

   @Override
   public Entity getEntity(int networkId) {
      return (Entity)(networkId == this.client.player.getNetworkId() ? this.client.player : super.getEntity(networkId));
   }

   public Entity removeEntity(int networkId) {
      Entity var2 = (Entity)this.entitiesByNetworkId.remove(networkId);
      if (var2 != null) {
         this.forcedEntities.remove(var2);
         this.removeEntity(var2);
      }

      return var2;
   }

   public boolean setBlockStateFromPacket(BlockPos pos, BlockState state) {
      int var3 = pos.getX();
      int var4 = pos.getY();
      int var5 = pos.getZ();
      this.regionChanged(var3, var4, var5, var3, var4, var5);
      return super.setBlockState(pos, state, 3);
   }

   @Override
   public void disconnect() {
      this.networkHandler.getConnection().disconnect(new LiteralText("Quitting"));
   }

   @Override
   protected void tickWeather() {
   }

   @Override
   protected int getChunkViewDistance() {
      return this.client.options.viewDistance;
   }

   public void doRandomDisplayTicks(int x, int y, int z) {
      byte var4 = 16;
      Random var5 = new Random();
      ItemStack var6 = this.client.player.getStackInHand();
      boolean var7 = this.client.interactionManager.getGameMode() == WorldSettings.GameMode.CREATIVE
         && var6 != null
         && Block.byItem(var6.getItem()) == Blocks.BARRIER;

      for(int var8 = 0; var8 < 1000; ++var8) {
         int var9 = x + this.random.nextInt(var4) - this.random.nextInt(var4);
         int var10 = y + this.random.nextInt(var4) - this.random.nextInt(var4);
         int var11 = z + this.random.nextInt(var4) - this.random.nextInt(var4);
         BlockPos var12 = new BlockPos(var9, var10, var11);
         BlockState var13 = this.getBlockState(var12);
         var13.getBlock().randomDisplayTick(this, var12, var13, var5);
         if (var7 && var13.getBlock() == Blocks.BARRIER) {
            this.addParticle(
               ParticleType.BARRIER, (double)((float)var9 + 0.5F), (double)((float)var10 + 0.5F), (double)((float)var11 + 0.5F), 0.0, 0.0, 0.0, new int[0]
            );
         }
      }
   }

   public void unloadEntities() {
      this.entities.removeAll(this.entitiesToRemove);

      for(int var1 = 0; var1 < this.entitiesToRemove.size(); ++var1) {
         Entity var2 = (Entity)this.entitiesToRemove.get(var1);
         int var3 = var2.chunkX;
         int var4 = var2.chunkZ;
         if (var2.isLoaded && this.isChunkLoaded(var3, var4, true)) {
            this.getChunkAt(var3, var4).removeEntity(var2);
         }
      }

      for(int var5 = 0; var5 < this.entitiesToRemove.size(); ++var5) {
         this.onEntityRemoved((Entity)this.entitiesToRemove.get(var5));
      }

      this.entitiesToRemove.clear();

      for(int var6 = 0; var6 < this.entities.size(); ++var6) {
         Entity var7 = (Entity)this.entities.get(var6);
         if (var7.vehicle != null) {
            if (!var7.vehicle.removed && var7.vehicle.rider == var7) {
               continue;
            }

            var7.vehicle.rider = null;
            var7.vehicle = null;
         }

         if (var7.removed) {
            int var8 = var7.chunkX;
            int var9 = var7.chunkZ;
            if (var7.isLoaded && this.isChunkLoaded(var8, var9, true)) {
               this.getChunkAt(var8, var9).removeEntity(var7);
            }

            this.entities.remove(var6--);
            this.onEntityRemoved(var7);
         }
      }
   }

   @Override
   public CashReportCategory populateCrashReport(CrashReport report) {
      CashReportCategory var2 = super.populateCrashReport(report);
      var2.add("Forced entities", new Callable() {
         public String call() {
            return ClientWorld.this.forcedEntities.size() + " total; " + ClientWorld.this.forcedEntities.toString();
         }
      });
      var2.add("Retry entities", new Callable() {
         public String call() {
            return ClientWorld.this.pendingEntities.size() + " total; " + ClientWorld.this.pendingEntities.toString();
         }
      });
      var2.add("Server brand", new Callable() {
         public String call() {
            return ClientWorld.this.client.player.getServerBrand();
         }
      });
      var2.add("Server type", new Callable() {
         public String call() {
            return ClientWorld.this.client.getServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
         }
      });
      return var2;
   }

   public void playSound(BlockPos pos, String sound, float volume, float pitch, boolean ignoreDistance) {
      this.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, sound, volume, pitch, ignoreDistance);
   }

   @Override
   public void playSound(double x, double y, double z, String sound, float volume, float pitch, boolean ignoreDistance) {
      double var11 = this.client.getCamera().getSquaredDistanceTo(x, y, z);
      SimpleSoundEvent var13 = new SimpleSoundEvent(new Identifier(sound), volume, pitch, (float)x, (float)y, (float)z);
      if (ignoreDistance && var11 > 100.0) {
         double var14 = Math.sqrt(var11) / 40.0;
         this.client.getSoundManager().play(var13, (int)(var14 * 20.0));
      } else {
         this.client.getSoundManager().play(var13);
      }
   }

   @Override
   public void addFireworksParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, NbtCompound nbt) {
      this.client.particleManager.addParticle(new FireworksParticles.Starter(this, x, y, z, velocityX, velocityY, velocityZ, this.client.particleManager, nbt));
   }

   public void setScoreboard(Scoreboard scoreboard) {
      this.scoreboard = scoreboard;
   }

   @Override
   public void setTimeOfDay(long time) {
      if (time < 0L) {
         time = -time;
         this.getGameRules().set("doDaylightCycle", "false");
      } else {
         this.getGameRules().set("doDaylightCycle", "true");
      }

      super.setTimeOfDay(time);
   }
}
