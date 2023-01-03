package net.minecraft.client.gui.screen.resourcepack;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class AvailableResourcePackListWidget extends ResourcePackListWidget {
   public AvailableResourcePackListWidget(MinecraftClient c_13piauvdk, int i, int j, List list) {
      super(c_13piauvdk, i, j, list);
   }

   @Override
   protected String getTitle() {
      return I18n.translate("resourcePack.available.title");
   }
}
