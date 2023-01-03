package net.minecraft.client.sound.system;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.sound.SoundPool;
import net.minecraft.util.registry.MappedRegistry;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SoundRegistry extends MappedRegistry {
   private Map sounds;

   @Override
   protected Map createMap() {
      this.sounds = Maps.newHashMap();
      return this.sounds;
   }

   public void add(SoundPool pool) {
      this.put(pool.getId(), pool);
   }

   public void clear() {
      this.sounds.clear();
   }
}
