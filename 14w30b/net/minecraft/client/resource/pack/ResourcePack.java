package net.minecraft.client.resource.pack;

import com.google.common.base.Charsets;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.TextureUtil;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import net.minecraft.client.resource.metadata.ResourceMetadataSection;
import net.minecraft.client.resource.metadata.ResourceMetadataSerializerRegistry;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class ResourcePack implements IResourcePack {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final File file;

   public ResourcePack(File file) {
      this.file = file;
   }

   private static String getPathToResource(Identifier id) {
      return String.format("%s/%s/%s", "assets", id.getNamespace(), id.getPath());
   }

   protected static String relativize(File dir, File file) {
      return dir.toURI().relativize(file.toURI()).getPath();
   }

   @Override
   public InputStream getResource(Identifier id) {
      return this.openResource(getPathToResource(id));
   }

   @Override
   public boolean hasResource(Identifier id) {
      return this.hasResource(getPathToResource(id));
   }

   protected abstract InputStream openResource(String path);

   protected abstract boolean hasResource(String path);

   protected void warnNonLowercaseNamespace(String namespace) {
      LOGGER.warn("ResourcePack: ignored non-lowercase namespace: %s in %s", new Object[]{namespace, this.file});
   }

   @Override
   public ResourceMetadataSection getMetadataSection(ResourceMetadataSerializerRegistry metadataSerializers, String name) {
      return getMetadataSection(metadataSerializers, this.openResource("pack.mcmeta"), name);
   }

   static ResourceMetadataSection getMetadataSection(ResourceMetadataSerializerRegistry metadataSerializers, InputStream file, String name) {
      Object var3 = null;
      BufferedReader var4 = null;

      try {
         var4 = new BufferedReader(new InputStreamReader(file, Charsets.UTF_8));
         var11 = new JsonParser().parse(var4).getAsJsonObject();
      } catch (RuntimeException var9) {
         throw new JsonParseException(var9);
      } finally {
         IOUtils.closeQuietly(var4);
      }

      return metadataSerializers.readMetadata(name, var11);
   }

   @Override
   public BufferedImage getIcon() {
      return TextureUtil.readImage(this.openResource("pack.png"));
   }

   @Override
   public String getName() {
      return this.file.getName();
   }
}
