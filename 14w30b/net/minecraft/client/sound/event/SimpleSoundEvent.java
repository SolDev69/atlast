package net.minecraft.client.sound.event;

import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SimpleSoundEvent extends SoundEvent {
   public static SimpleSoundEvent of(Identifier id, float pitch) {
      return new SimpleSoundEvent(id, 0.25F, pitch, false, 0, ISoundEvent.Attenuation.NONE, 0.0F, 0.0F, 0.0F);
   }

   public static SimpleSoundEvent of(Identifier id) {
      return new SimpleSoundEvent(id, 1.0F, 1.0F, false, 0, ISoundEvent.Attenuation.NONE, 0.0F, 0.0F, 0.0F);
   }

   public static SimpleSoundEvent of(Identifier id, float x, float y, float z) {
      return new SimpleSoundEvent(id, 4.0F, 1.0F, false, 0, ISoundEvent.Attenuation.LINEAR, x, y, z);
   }

   public SimpleSoundEvent(Identifier id, float volume, float pitch, float x, float y, float z) {
      this(id, volume, pitch, false, 0, ISoundEvent.Attenuation.LINEAR, x, y, z);
   }

   private SimpleSoundEvent(
      Identifier id, float volume, float pitch, boolean repeat, int period, ISoundEvent.Attenuation attenuation, float x, float y, float z
   ) {
      super(id);
      this.volume = volume;
      this.pitch = pitch;
      this.x = x;
      this.y = y;
      this.z = z;
      this.repeat = repeat;
      this.period = period;
      this.attenuation = attenuation;
   }
}
