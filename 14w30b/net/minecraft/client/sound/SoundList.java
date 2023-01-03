package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import java.util.List;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SoundList {
   private final List sounds = Lists.newArrayList();
   private boolean replacable;
   private SoundCategory category;

   public List getSounds() {
      return this.sounds;
   }

   public boolean isReplacable() {
      return this.replacable;
   }

   public void setReplacable(boolean replacable) {
      this.replacable = replacable;
   }

   public SoundCategory getCategory() {
      return this.category;
   }

   public void setCategory(SoundCategory category) {
      this.category = category;
   }

   @Environment(EnvType.CLIENT)
   public static class Entry {
      private String name;
      private float volume = 1.0F;
      private float pitch = 1.0F;
      private int weight = 1;
      private SoundList.Entry.Type type = SoundList.Entry.Type.FILE;
      private boolean stream = false;

      public String getName() {
         return this.name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public float getVolume() {
         return this.volume;
      }

      public void setVolume(float volume) {
         this.volume = volume;
      }

      public float getPitch() {
         return this.pitch;
      }

      public void setPitch(float pitch) {
         this.pitch = pitch;
      }

      public int getWeight() {
         return this.weight;
      }

      public void setWeight(int weight) {
         this.weight = weight;
      }

      public SoundList.Entry.Type getType() {
         return this.type;
      }

      public void setType(SoundList.Entry.Type type) {
         this.type = type;
      }

      public boolean isStream() {
         return this.stream;
      }

      public void setStream(boolean stream) {
         this.stream = stream;
      }

      @Environment(EnvType.CLIENT)
      public static enum Type {
         FILE("file"),
         EVENT("event");

         private final String name;

         private Type(String name) {
            this.name = name;
         }

         public static SoundList.Entry.Type byName(String name) {
            for(SoundList.Entry.Type var4 : values()) {
               if (var4.name.equals(name)) {
                  return var4;
               }
            }

            return null;
         }
      }
   }
}
