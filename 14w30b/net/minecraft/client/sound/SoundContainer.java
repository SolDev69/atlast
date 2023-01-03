package net.minecraft.client.sound;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface SoundContainer {
   int getWeight();

   Object get();
}
