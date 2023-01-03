package net.minecraft.client.world.color;

import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.client.resource.manager.ResourceReloadListener;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class FoliageColorReloader implements ResourceReloadListener {
   private static final Identifier FOLIAGE_TEXTURE = new Identifier("textures/colormap/foliage.png");

   @Override
   public void reload(IResourceManager resourceManager) {
      try {
         FoliageColors.set(TextureUtil.getPixels(resourceManager, FOLIAGE_TEXTURE));
      } catch (IOException var3) {
      }
   }
}
