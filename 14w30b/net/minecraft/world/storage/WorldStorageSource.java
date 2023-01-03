package net.minecraft.world.storage;

import java.util.List;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.WorldData;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public interface WorldStorageSource {
   @Environment(EnvType.CLIENT)
   String getName();

   WorldStorage get(String worldName, boolean createPlayerDataDir);

   @Environment(EnvType.CLIENT)
   List getAll();

   void clearRegionIo();

   @Environment(EnvType.CLIENT)
   WorldData getData(String worldName);

   @Environment(EnvType.CLIENT)
   boolean canCreate(String worldName);

   boolean delete(String worldName);

   @Environment(EnvType.CLIENT)
   void rename(String worldName, String newName);

   @Environment(EnvType.CLIENT)
   boolean isConvertible(String worldName);

   boolean needsConversion(String worldName);

   boolean convert(String worldName, ProgressListener listener);

   @Environment(EnvType.CLIENT)
   boolean exists(String worldName);
}
