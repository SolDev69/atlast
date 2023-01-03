package net.minecraft.world.storage;

import java.io.File;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.WorldData;
import net.minecraft.world.chunk.storage.AnvilChunkStorage;
import net.minecraft.world.chunk.storage.ChunkStorage;
import net.minecraft.world.chunk.storage.RegionIo;
import net.minecraft.world.chunk.storage.io.FileIoThread;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.NetherDimension;
import net.minecraft.world.dimension.TheEndDimension;

public class AnvilWorldStorage extends RegionWorldStorage {
   public AnvilWorldStorage(File file, String string, boolean bl) {
      super(file, string, bl);
   }

   @Override
   public ChunkStorage getChunkStorage(Dimension dimension) {
      File var2 = this.getDir();
      if (dimension instanceof NetherDimension) {
         File var4 = new File(var2, "DIM-1");
         var4.mkdirs();
         return new AnvilChunkStorage(var4);
      } else if (dimension instanceof TheEndDimension) {
         File var3 = new File(var2, "DIM1");
         var3.mkdirs();
         return new AnvilChunkStorage(var3);
      } else {
         return new AnvilChunkStorage(var2);
      }
   }

   @Override
   public void saveData(WorldData data, NbtCompound playerData) {
      data.setVersion(19133);
      super.saveData(data, playerData);
   }

   @Override
   public void waitIfSaving() {
      try {
         FileIoThread.getInstance().waitUntilFinished();
      } catch (InterruptedException var2) {
         var2.printStackTrace();
      }

      RegionIo.clear();
   }
}
