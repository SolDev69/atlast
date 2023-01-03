package net.minecraft.client.render;

import java.awt.image.BufferedImage;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface BufferedImageSkinProvider {
   BufferedImage process(BufferedImage image);

   void onTextureDownloaded();
}
