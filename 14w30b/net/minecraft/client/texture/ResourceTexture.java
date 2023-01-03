package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import net.minecraft.client.resource.IResource;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ResourceTexture extends AbstractTexture {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final Identifier id;

   public ResourceTexture(Identifier id) {
      this.id = id;
   }

   @Override
   public void load(IResourceManager resourceManager) {
      this.clearGlId();
      InputStream var2 = null;

      try {
         IResource var3 = resourceManager.getResource(this.id);
         var2 = var3.asStream();
         BufferedImage var4 = TextureUtil.readImage(var2);
         boolean var5 = false;
         boolean var6 = false;
         if (var3.hasMetadata()) {
            try {
               TextureResourceMetadata var7 = (TextureResourceMetadata)var3.getMetadata("texture");
               if (var7 != null) {
                  var5 = var7.hasBlur();
                  var6 = var7.isClamped();
               }
            } catch (RuntimeException var11) {
               LOGGER.warn("Failed reading metadata of: " + this.id, var11);
            }
         }

         TextureUtil.uploadTexture(this.getGlId(), var4, var5, var6);
      } finally {
         if (var2 != null) {
            var2.close();
         }
      }
   }
}
