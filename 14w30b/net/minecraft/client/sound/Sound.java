package net.minecraft.client.sound;

import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class Sound {
   private final Identifier id;
   private final boolean stream;
   private double volume;
   private double pitch;

   public Sound(Identifier id, double volume, double pitch, boolean stream) {
      this.id = id;
      this.volume = volume;
      this.pitch = pitch;
      this.stream = stream;
   }

   public Sound(Sound sound) {
      this.id = sound.id;
      this.volume = sound.volume;
      this.pitch = sound.pitch;
      this.stream = sound.stream;
   }

   public Identifier getIdentifier() {
      return this.id;
   }

   public double getVolume() {
      return this.volume;
   }

   public void setVolume(double volume) {
      this.volume = volume;
   }

   public double getPitch() {
      return this.pitch;
   }

   public void setPitch(double pitch) {
      this.pitch = pitch;
   }

   public boolean isStream() {
      return this.stream;
   }
}
