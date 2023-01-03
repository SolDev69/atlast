package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.TextureUtil;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class LayeredTexture extends AbstractTexture {
   private static final Logger LOGGER = LogManager.getLogger();
   public final List locations;

   public LayeredTexture(String... strings) {
      this.locations = Lists.newArrayList(strings);
   }

   @Override
   public void load(IResourceManager resourceManager) {
      this.clearGlId();
      BufferedImage var2 = null;

      try {
         for(String var4 : this.locations) {
            if (var4 != null) {
               InputStream var5 = resourceManager.getResource(new Identifier(var4)).asStream();
               BufferedImage var6 = TextureUtil.readImage(var5);
               if (var2 == null) {
                  var2 = new BufferedImage(var6.getWidth(), var6.getHeight(), 2);
               }

               var2.getGraphics().drawImage(var6, 0, 0, null);
            }
         }
      } catch (IOException var7) {
         LOGGER.error("Couldn't load layered image", var7);
         return;
      }

      TextureUtil.uploadTexture(this.getGlId(), var2);
   }
}
