package net.minecraft.client.sound.event;

import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class SoundEvent implements ISoundEvent {
   protected final Identifier id;
   protected float volume = 1.0F;
   protected float pitch = 1.0F;
   protected float x;
   protected float y;
   protected float z;
   protected boolean repeat = false;
   protected int period = 0;
   protected ISoundEvent.Attenuation attenuation = ISoundEvent.Attenuation.LINEAR;

   protected SoundEvent(Identifier id) {
      this.id = id;
   }

   @Override
   public Identifier getId() {
      return this.id;
   }

   @Override
   public boolean isRepeatable() {
      return this.repeat;
   }

   @Override
   public int getPeriod() {
      return this.period;
   }

   @Override
   public float getVolume() {
      return this.volume;
   }

   @Override
   public float getPitch() {
      return this.pitch;
   }

   @Override
   public float getX() {
      return this.x;
   }

   @Override
   public float getY() {
      return this.y;
   }

   @Override
   public float getZ() {
      return this.z;
   }

   @Override
   public ISoundEvent.Attenuation getAttenuationType() {
      return this.attenuation;
   }
}
