package net.minecraft.server.world.chunk;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.living.mob.MobSpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Long2ObjectHashMap;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.storage.ChunkStorage;
import net.minecraft.world.storage.exception.WorldStorageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerChunkCache implements ChunkSource {
   private static final Logger LOGGER = LogManager.getLogger();
   private Set chunksToUnload = Collections.newSetFromMap(new ConcurrentHashMap());
   private WorldChunk empty;
   private ChunkSource generator;
   private ChunkStorage storage;
   public boolean forceLoad = true;
   private Long2ObjectHashMap chunkMap = new Long2ObjectHashMap();
   private List chunks = Lists.newArrayList();
   private ServerWorld world;

   public ServerChunkCache(ServerWorld world, ChunkStorage storage, ChunkSource generator) {
      this.empty = new EmptyChunk(world, 0, 0);
      this.world = world;
      this.storage = storage;
      this.generator = generator;
   }

   @Override
   public boolean isLoaded(int chunkX, int chunkZ) {
      return this.chunkMap.contains(ChunkPos.toLong(chunkX, chunkZ));
   }

   public List getChunks() {
      return this.chunks;
   }

   public void scheduleUnload(int chunkX, int chunkZ) {
      if (this.world.dimension.hasWorldSpawn()) {
         BlockPos var3 = this.world.getSpawnPoint();
         int var4 = chunkX * 16 + 8 - var3.getX();
         int var5 = chunkZ * 16 + 8 - var3.getZ();
         short var6 = 128;
         if (var4 < -var6 || var4 > var6 || var5 < -var6 || var5 > var6) {
            this.chunksToUnload.add(ChunkPos.toLong(chunkX, chunkZ));
         }
      } else {
         this.chunksToUnload.add(ChunkPos.toLong(chunkX, chunkZ));
      }
   }

   public void scheduleUnloadAll() {
      for(WorldChunk var2 : this.chunks) {
         this.scheduleUnload(var2.chunkX, var2.chunkZ);
      }
   }

   public WorldChunk loadChunk(int chunkX, int chunkZ) {
      long var3 = ChunkPos.toLong(chunkX, chunkZ);
      this.chunksToUnload.remove(var3);
      WorldChunk var5 = (WorldChunk)this.chunkMap.get(var3);
      if (var5 == null) {
         var5 = this.loadChunkFromStorage(chunkX, chunkZ);
         if (var5 == null) {
            if (this.generator == null) {
               var5 = this.empty;
            } else {
               try {
                  var5 = this.generator.getChunk(chunkX, chunkZ);
               } catch (Throwable var9) {
                  CrashReport var7 = CrashReport.of(var9, "Exception generating new chunk");
                  CashReportCategory var8 = var7.addCategory("Chunk to be generated");
                  var8.add("Location", String.format("%d,%d", chunkX, chunkZ));
                  var8.add("Position hash", var3);
                  var8.add("Generator", this.generator.getName());
                  throw new CrashException(var7);
               }
            }
         }

         this.chunkMap.put(var3, var5);
         this.chunks.add(var5);
         var5.load();
         var5.populate(this, this, chunkX, chunkZ);
      }

      return var5;
   }

   @Override
   public WorldChunk getChunk(int chunkX, int chunkZ) {
      WorldChunk var3 = (WorldChunk)this.chunkMap.get(ChunkPos.toLong(chunkX, chunkZ));
      if (var3 == null) {
         return !this.world.isSearchingSpawnPoint() && !this.forceLoad ? this.empty : this.loadChunk(chunkX, chunkZ);
      } else {
         return var3;
      }
   }

   private WorldChunk loadChunkFromStorage(int chunkX, int chunkZ) {
      if (this.storage == null) {
         return null;
      } else {
         try {
            WorldChunk var3 = this.storage.loadChunk(this.world, chunkX, chunkZ);
            if (var3 != null) {
               var3.setLastSaveTime(this.world.getTime());
               if (this.generator != null) {
                  this.generator.placeStructures(var3, chunkX, chunkZ);
               }
            }

            return var3;
         } catch (Exception var4) {
            LOGGER.error("Couldn't load chunk", var4);
            return null;
         }
      }
   }

   private void saveEntities(WorldChunk chunk) {
      if (this.storage != null) {
         try {
            this.storage.saveEntities(this.world, chunk);
         } catch (Exception var3) {
            LOGGER.error("Couldn't save entities", var3);
         }
      }
   }

   private void saveChunk(WorldChunk chunk) {
      if (this.storage != null) {
         try {
            chunk.setLastSaveTime(this.world.getTime());
            this.storage.saveChunk(this.world, chunk);
         } catch (IOException var3) {
            LOGGER.error("Couldn't save chunk", var3);
         } catch (WorldStorageException var4) {
            LOGGER.error("Couldn't save chunk; already in use by another instance of Minecraft?", var4);
         }
      }
   }

   @Override
   public void populate(ChunkSource source, int chunkX, int chunkZ) {
      WorldChunk var4 = this.getChunk(chunkX, chunkZ);
      if (!var4.isTerrainPopulated()) {
         var4.populateLight();
         if (this.generator != null) {
            this.generator.populate(source, chunkX, chunkZ);
            var4.markDirty();
         }
      }
   }

   @Override
   public boolean populateSpecial(ChunkSource source, WorldChunk chunk, int chunkX, int chunkZ) {
      if (this.generator != null && this.generator.populateSpecial(source, chunk, chunkX, chunkZ)) {
         WorldChunk var5 = this.getChunk(chunkX, chunkZ);
         var5.markDirty();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean save(boolean saveEntities, ProgressListener listener) {
      int var3 = 0;

      for(int var4 = 0; var4 < this.chunks.size(); ++var4) {
         WorldChunk var5 = (WorldChunk)this.chunks.get(var4);
         if (saveEntities) {
            this.saveEntities(var5);
         }

         if (var5.shouldSave(saveEntities)) {
            this.saveChunk(var5);
            var5.setDirty(false);
            if (++var3 == 24 && !saveEntities) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public void save() {
      if (this.storage != null) {
         this.storage.save();
      }
   }

   @Override
   public boolean tick() {
      if (!this.world.isSaving) {
         for(int var1 = 0; var1 < 100; ++var1) {
            if (!this.chunksToUnload.isEmpty()) {
               Long var2 = (Long)this.chunksToUnload.iterator().next();
               WorldChunk var3 = (WorldChunk)this.chunkMap.get(var2);
               if (var3 != null) {
                  var3.unload();
                  this.saveChunk(var3);
                  this.saveEntities(var3);
                  this.chunkMap.remove(var2);
                  this.chunks.remove(var3);
               }

               this.chunksToUnload.remove(var2);
            }
         }

         if (this.storage != null) {
            this.storage.tick();
         }
      }

      return this.generator.tick();
   }

   @Override
   public boolean canSave() {
      return !this.world.isSaving;
   }

   @Override
   public String getName() {
      return "ServerChunkCache: " + this.chunkMap.getSize() + " Drop: " + this.chunksToUnload.size();
   }

   @Override
   public List getSpawnEntries(MobSpawnGroup spawnGroup, BlockPos pos) {
      return this.generator.getSpawnEntries(spawnGroup, pos);
   }

   @Override
   public BlockPos findNearestStructure(World world, String name, BlockPos pos) {
      return this.generator.findNearestStructure(world, name, pos);
   }

   @Override
   public int getLoadedCount() {
      return this.chunkMap.getSize();
   }

   @Override
   public void placeStructures(WorldChunk chunk, int chunkX, int chunkZ) {
   }

   @Override
   public WorldChunk getChunk(BlockPos pos) {
      return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
   }
}
