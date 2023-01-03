package net.minecraft.world.storage;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class WorldStorageException extends Exception {
   public WorldStorageException(String cause) {
      super(cause);
   }
}
