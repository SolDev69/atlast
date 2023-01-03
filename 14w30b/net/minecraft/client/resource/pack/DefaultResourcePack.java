package net.minecraft.client.resource.pack;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.platform.TextureUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resource.metadata.ResourceMetadataSection;
import net.minecraft.client.resource.metadata.ResourceMetadataSerializerRegistry;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class DefaultResourcePack implements IResourcePack {
   public static final Set NAMESPACES = ImmutableSet.of("minecraft", "realms");
   private final Map resourceCache;

   public DefaultResourcePack(Map file) {
      this.resourceCache = file;
   }

   @Override
   public InputStream getResource(Identifier id) {
      InputStream var2 = this.openResource(id);
      if (var2 != null) {
         return var2;
      } else {
         InputStream var3 = this.getCachedResource(id);
         if (var3 != null) {
            return var3;
         } else {
            throw new FileNotFoundException(id.getPath());
         }
      }
   }

   public InputStream getCachedResource(Identifier id) {
      File var2 = (File)this.resourceCache.get(id.toString());
      return var2 != null && var2.isFile() ? new FileInputStream(var2) : null;
   }

   private InputStream openResource(Identifier identifier) {
      return DefaultResourcePack.class.getResourceAsStream("/assets/" + identifier.getNamespace() + "/" + identifier.getPath());
   }

   @Override
   public boolean hasResource(Identifier id) {
      return this.openResource(id) != null || this.resourceCache.containsKey(id.toString());
   }

   @Override
   public Set getNamespaces() {
      return NAMESPACES;
   }

   @Override
   public ResourceMetadataSection getMetadataSection(ResourceMetadataSerializerRegistry metadataSerializers, String name) {
      try {
         FileInputStream var3 = new FileInputStream((File)this.resourceCache.get("pack.mcmeta"));
         return ResourcePack.getMetadataSection(metadataSerializers, var3, name);
      } catch (RuntimeException var4) {
         return null;
      } catch (FileNotFoundException var5) {
         return null;
      }
   }

   @Override
   public BufferedImage getIcon() {
      return TextureUtil.readImage(DefaultResourcePack.class.getResourceAsStream("/" + new Identifier("pack.png").getPath()));
   }

   @Override
   public String getName() {
      return "Default";
   }
}
