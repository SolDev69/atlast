package net.minecraft.world.chunk;

import java.util.List;
import net.minecraft.entity.living.mob.MobSpawnGroup;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ChunkSource {
   boolean isLoaded(int chunkX, int chunkZ);

   WorldChunk getChunk(int chunkX, int chunkZ);

   WorldChunk getChunk(BlockPos pos);

   void populate(ChunkSource source, int chunkX, int chunkZ);

   boolean populateSpecial(ChunkSource source, WorldChunk chunk, int chunkX, int chunkZ);

   boolean save(boolean saveEntities, ProgressListener listener);

   boolean tick();

   boolean canSave();

   String getName();

   List getSpawnEntries(MobSpawnGroup spawnGroup, BlockPos pos);

   BlockPos findNearestStructure(World world, String name, BlockPos pos);

   int getLoadedCount();

   void placeStructures(WorldChunk chunk, int chunkX, int chunkZ);

   void save();
}
