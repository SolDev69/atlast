package net.minecraft.client.gui.screen;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ConfirmationListener {
   void confirmResult(boolean result, int id);
}
