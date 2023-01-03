package net.minecraft.client.gui.spectator;

import java.util.List;
import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface SpectatorMenuCategory {
   List getItems();

   Text getPrompt();
}
