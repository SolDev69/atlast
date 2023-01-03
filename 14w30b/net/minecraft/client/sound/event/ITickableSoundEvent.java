package net.minecraft.client.sound.event;

import net.minecraft.util.Tickable;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ITickableSoundEvent extends ISoundEvent, Tickable {
   boolean isDone();
}
