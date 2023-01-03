package net.minecraft.client.sound.event;

import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class TickableSoundEvent extends SoundEvent implements ITickableSoundEvent {
   protected boolean done = false;

   protected TickableSoundEvent(Identifier c_07ipdbewr) {
      super(c_07ipdbewr);
   }

   @Override
   public boolean isDone() {
      return this.done;
   }
}
