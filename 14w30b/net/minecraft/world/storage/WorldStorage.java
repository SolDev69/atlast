package net.minecraft.world.storage;

import java.io.File;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.WorldData;
import net.minecraft.world.chunk.storage.ChunkStorage;
import net.minecraft.world.dimension.Dimension;

public interface WorldStorage {
   WorldData loadData();

   void checkSessionLock();

   ChunkStorage getChunkStorage(Dimension dimension);

   void saveData(WorldData data, NbtCompound playerData);

   void saveData(WorldData data);

   PlayerDataStorage getPlayerDataStorage();

   void waitIfSaving();

   File getDir();

   File getDataFile(String name);

   String getName();
}
