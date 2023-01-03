package net.minecraft.client.texture;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Tickable {
   void tick();
}
