package net.minecraft.util.registry;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public interface Registry extends Iterable {
   @Environment(EnvType.CLIENT)
   Object get(Object key);

   @Environment(EnvType.CLIENT)
   void put(Object key, Object value);
}
