package net.minecraft.client.world.chunk;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.living.mob.MobSpawnGroup;
import net.minecraft.util.Long2ObjectHashMap;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientChunkCache implements ChunkSource {
   private static final Logger LOGGER = LogManager.getLogger();
   private WorldChunk empty;
   private Long2ObjectHashMap chunkMap = new Long2ObjectHashMap();
   private List chunks = Lists.newArrayList();
   private World world;

   public ClientChunkCache(World world) {
      this.empty = new EmptyChunk(world, 0, 0);
      this.world = world;
   }

   @Override
   public boolean isLoaded(int chunkX, int chunkZ) {
      return true;
   }

   public void unloadChunk(int chunkX, int chunkZ) {
      WorldChunk var3 = this.getChunk(chunkX, chunkZ);
      if (!var3.isEmpty()) {
         var3.unload();
      }

      this.chunkMap.remove(ChunkPos.toLong(chunkX, chunkZ));
      this.chunks.remove(var3);
   }

   public WorldChunk loadChunk(int chunkX, int chunkZ) {
      WorldChunk var3 = new WorldChunk(this.world, chunkX, chunkZ);
      this.chunkMap.put(ChunkPos.toLong(chunkX, chunkZ), var3);
      this.chunks.add(var3);
      var3.setLoaded(true);
      return var3;
   }

   @Override
   public WorldChunk getChunk(int chunkX, int chunkZ) {
      WorldChunk var3 = (WorldChunk)this.chunkMap.get(ChunkPos.toLong(chunkX, chunkZ));
      return var3 == null ? this.empty : var3;
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
      long var1 = System.currentTimeMillis();

      for(WorldChunk var4 : this.chunks) {
         var4.tick(System.currentTimeMillis() - var1 > 5L);
      }

      if (System.currentTimeMillis() - var1 > 100L) {
         LOGGER.info("Warning: Clientside chunk ticking took {} ms", new Object[]{System.currentTimeMillis() - var1});
      }

      return false;
   }

   @Override
   public boolean canSave() {
      return false;
   }

   @Override
   public void populate(ChunkSource source, int chunkX, int chunkZ) {
   }

   @Override
   public boolean populateSpecial(ChunkSource source, WorldChunk chunk, int chunkX, int chunkZ) {
      return false;
   }

   @Override
   public String getName() {
      return "MultiplayerChunkCache: " + this.chunkMap.getSize() + ", " + this.chunks.size();
   }

   @Override
   public List getSpawnEntries(MobSpawnGroup spawnGroup, BlockPos pos) {
      return null;
   }

   @Override
   public BlockPos findNearestStructure(World world, String name, BlockPos pos) {
      return null;
   }

   @Override
   public int getLoadedCount() {
      return this.chunks.size();
   }

   @Override
   public void placeStructures(WorldChunk chunk, int chunkX, int chunkZ) {
   }

   @Override
   public WorldChunk getChunk(BlockPos pos) {
      return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
   }
}
