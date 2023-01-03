package net.minecraft.world.chunk.storage;

import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

public interface ChunkStorage {
   WorldChunk loadChunk(World world, int chunkX, int chunkZ);

   void saveChunk(World world, WorldChunk chunk);

   void saveEntities(World world, WorldChunk chunk);

   void tick();

   void save();
}
