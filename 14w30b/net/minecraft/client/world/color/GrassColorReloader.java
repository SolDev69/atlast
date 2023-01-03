package net.minecraft.client.world.color;

import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.client.resource.manager.ResourceReloadListener;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class GrassColorReloader implements ResourceReloadListener {
   private static final Identifier GRASS_COLOR_TEXTURE = new Identifier("textures/colormap/grass.png");

   @Override
   public void reload(IResourceManager resourceManager) {
      try {
         GrassColors.setColorMap(TextureUtil.getPixels(resourceManager, GRASS_COLOR_TEXTURE));
      } catch (IOException var3) {
      }
   }
}
