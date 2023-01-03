package net.minecraft.client.sound.event;

import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ISoundEvent {
   Identifier getId();

   boolean isRepeatable();

   int getPeriod();

   float getVolume();

   float getPitch();

   float getX();

   float getY();

   float getZ();

   ISoundEvent.Attenuation getAttenuationType();

   @Environment(EnvType.CLIENT)
   public static enum Attenuation {
      NONE(0),
      LINEAR(2);

      private final int attenuation;

      private Attenuation(int attenuation) {
         this.attenuation = attenuation;
      }

      public int get() {
         return this.attenuation;
      }
   }
}
