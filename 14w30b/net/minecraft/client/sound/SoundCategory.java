package net.minecraft.client.sound;

import com.google.common.collect.Maps;
import java.util.Map;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public enum SoundCategory {
   MASTER("master", 0),
   MUSIC("music", 1),
   RECORDS("record", 2),
   WEATHER("weather", 3),
   BLOCKS("block", 4),
   MOBS("hostile", 5),
   ANIMALS("neutral", 6),
   PLAYERS("player", 7),
   AMBIENT("ambient", 8);

   private static final Map BY_NAME = Maps.newHashMap();
   private static final Map BY_ID = Maps.newHashMap();
   private final String name;
   private final int id;

   private SoundCategory(String name, int id) {
      this.name = name;
      this.id = id;
   }

   public String getName() {
      return this.name;
   }

   public int getId() {
      return this.id;
   }

   public static SoundCategory byName(String name) {
      return (SoundCategory)BY_NAME.get(name);
   }

   static {
      for(SoundCategory var3 : values()) {
         if (BY_NAME.containsKey(var3.getName()) || BY_ID.containsKey(var3.getId())) {
            throw new Error("Clash in Sound Category ID & Name pools! Cannot insert " + var3);
         }

         BY_NAME.put(var3.getName(), var3);
         BY_ID.put(var3.getId(), var3);
      }
   }
}
