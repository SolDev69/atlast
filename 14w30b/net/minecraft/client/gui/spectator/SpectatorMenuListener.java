package net.minecraft.client.gui.spectator;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface SpectatorMenuListener {
   void onSpectatorMenuClosed(SpectatorMenu menu);
}
