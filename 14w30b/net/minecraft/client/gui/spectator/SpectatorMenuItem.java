package net.minecraft.client.gui.spectator;

import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface SpectatorMenuItem {
   void select(SpectatorMenu hud);

   Text getDisplayName();

   void render(float tickDelta, int slot);

   boolean isEnabled();
}
