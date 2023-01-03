package net.minecraft.client.resource.pack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.metadata.ResourceMetadataSerializerRegistry;
import net.minecraft.client.resource.metadata.ResourcePackMetadata;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Identifier;
import net.minecraft.text.Formatting;
import net.minecraft.util.NetworkUtils;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ResourcePackLoader {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final FileFilter RESOURCE_PACK_FILTER = new FileFilter() {
      @Override
      public boolean accept(File file) {
         boolean var2 = file.isFile() && file.getName().endsWith(".zip");
         boolean var3 = file.isDirectory() && new File(file, "pack.mcmeta").isFile();
         return var2 || var3;
      }
   };
   private final File dir;
   public final IResourcePack defaultResourcePack;
   private final File file;
   public final ResourceMetadataSerializerRegistry metadataSerializerRegistry;
   private IResourcePack serverResourcePack;
   private boolean awaitingServerResourcePack;
   private List availableResourcePacks = Lists.newArrayList();
   private List appliedResourcePacks = Lists.newArrayList();

   public ResourcePackLoader(
      File dir, File file, IResourcePack defaultResourcePack, ResourceMetadataSerializerRegistry meterdataSerializerRegistry, GameOptions options
   ) {
      this.dir = dir;
      this.file = file;
      this.defaultResourcePack = defaultResourcePack;
      this.metadataSerializerRegistry = meterdataSerializerRegistry;
      this.initDirectory();
      this.loadResourcePacks();

      for(String var7 : options.resourcePacks) {
         for(ResourcePackLoader.Entry var9 : this.availableResourcePacks) {
            if (var9.getName().equals(var7)) {
               this.appliedResourcePacks.add(var9);
               break;
            }
         }
      }
   }

   private void initDirectory() {
      if (!this.dir.isDirectory() && (!this.dir.delete() || !this.dir.mkdirs())) {
         LOGGER.debug("Unable to create resourcepack folder: " + this.dir);
      }
   }

   private List getResourcePackFiles() {
      return this.dir.isDirectory() ? Arrays.asList(this.dir.listFiles(RESOURCE_PACK_FILTER)) : Collections.emptyList();
   }

   public void loadResourcePacks() {
      ArrayList var1 = Lists.newArrayList();

      for(File var3 : this.getResourcePackFiles()) {
         ResourcePackLoader.Entry var4 = new ResourcePackLoader.Entry(var3);
         if (!this.availableResourcePacks.contains(var4)) {
            try {
               var4.load();
               var1.add(var4);
            } catch (Exception var6) {
               var1.remove(var4);
            }
         } else {
            int var5 = this.availableResourcePacks.indexOf(var4);
            if (var5 > -1 && var5 < this.availableResourcePacks.size()) {
               var1.add(this.availableResourcePacks.get(var5));
            }
         }
      }

      this.availableResourcePacks.removeAll(var1);

      for(ResourcePackLoader.Entry var8 : this.availableResourcePacks) {
         var8.close();
      }

      this.availableResourcePacks = var1;
   }

   public List getAvailableResourcePacks() {
      return ImmutableList.copyOf(this.availableResourcePacks);
   }

   public List getAppliedResourcePacks() {
      return ImmutableList.copyOf(this.appliedResourcePacks);
   }

   public void applyResourcePacks(List packs) {
      this.appliedResourcePacks.clear();
      this.appliedResourcePacks.addAll(packs);
   }

   public File getDirectory() {
      return this.dir;
   }

   public void downloadServerResourcePack(String url) {
      String var2 = url.substring(url.lastIndexOf("/") + 1);
      if (var2.contains("?")) {
         var2 = var2.substring(0, var2.indexOf("?"));
      }

      if (var2.endsWith(".zip")) {
         File var3 = new File(this.file, var2.replaceAll("\\W", ""));
         this.removeServerResourcePack();
         this.downloadServerResourcePack(url, var3);
      }
   }

   private void downloadServerResourcePack(String url, File dst) {
      HashMap var3 = Maps.newHashMap();
      ProgressScreen var4 = new ProgressScreen();
      var3.put("X-Minecraft-Username", MinecraftClient.getInstance().getSession().getUsername());
      var3.put("X-Minecraft-UUID", MinecraftClient.getInstance().getSession().getUuid());
      var3.put("X-Minecraft-Version", "14w30b");
      this.awaitingServerResourcePack = true;
      MinecraftClient.getInstance().openScreen(var4);
      NetworkUtils.downloadServerPack(dst, url, new ServerResourcePack() {
         @Override
         public void apply(File file) {
            if (ResourcePackLoader.this.awaitingServerResourcePack) {
               ResourcePackLoader.this.awaitingServerResourcePack = false;
               ResourcePackLoader.this.m_56btiyfcu(file, true);
            }
         }
      }, var3, 52428800, var4, MinecraftClient.getInstance().getNetworkProxy());
   }

   public void m_56btiyfcu(File file, boolean bl) {
      this.serverResourcePack = new ZippedResourcePack(file);
      if (bl) {
         MinecraftClient.getInstance().onApplyServerResourcePack();
      } else {
         MinecraftClient.getInstance().reloadResources();
      }
   }

   public IResourcePack getServerResourcePack() {
      return this.serverResourcePack;
   }

   public void removeServerResourcePack() {
      this.serverResourcePack = null;
      this.awaitingServerResourcePack = false;
   }

   @Environment(EnvType.CLIENT)
   public class Entry {
      private final File file;
      private IResourcePack resourcePack;
      private ResourcePackMetadata metadata;
      private BufferedImage icon;
      private Identifier iconId;

      private Entry(File file) {
         this.file = file;
      }

      public void load() {
         this.resourcePack = (IResourcePack)(this.file.isDirectory() ? new DirectoryResourcePack(this.file) : new ZippedResourcePack(this.file));
         this.metadata = (ResourcePackMetadata)this.resourcePack.getMetadataSection(ResourcePackLoader.this.metadataSerializerRegistry, "pack");

         try {
            this.icon = this.resourcePack.getIcon();
         } catch (IOException var2) {
         }

         if (this.icon == null) {
            this.icon = ResourcePackLoader.this.defaultResourcePack.getIcon();
         }

         this.close();
      }

      public void bindIconTexture(TextureManager textureManager) {
         if (this.iconId == null) {
            this.iconId = textureManager.register("texturepackicon", new NativeImageBackedTexture(this.icon));
         }

         textureManager.bind(this.iconId);
      }

      public void close() {
         if (this.resourcePack instanceof Closeable) {
            IOUtils.closeQuietly((Closeable)this.resourcePack);
         }
      }

      public IResourcePack getResourcePack() {
         return this.resourcePack;
      }

      public String getName() {
         return this.resourcePack.getName();
      }

      public String getDescription() {
         return this.metadata == null
            ? Formatting.RED + "Invalid pack.mcmeta (or missing 'pack' section)"
            : this.metadata.getDescription().buildFormattedString();
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else {
            return object instanceof ResourcePackLoader.Entry ? this.toString().equals(object.toString()) : false;
         }
      }

      @Override
      public int hashCode() {
         return this.toString().hashCode();
      }

      @Override
      public String toString() {
         return String.format("%s:%s:%d", this.file.getName(), this.file.isDirectory() ? "folder" : "zip", this.file.lastModified());
      }
   }
}
