package net.minecraft.client.sound.system;

import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundContainer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class FileSoundContainer implements SoundContainer {
   private final Sound sound;
   private final int weight;

   FileSoundContainer(Sound sound, int weight) {
      this.sound = sound;
      this.weight = weight;
   }

   @Override
   public int getWeight() {
      return this.weight;
   }

   public Sound get() {
      return new Sound(this.sound);
   }
}
