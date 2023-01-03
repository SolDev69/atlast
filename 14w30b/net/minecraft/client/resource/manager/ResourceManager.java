package net.minecraft.client.resource.manager;

import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resource.IResource;
import net.minecraft.client.resource.Resource;
import net.minecraft.client.resource.metadata.ResourceMetadataSerializerRegistry;
import net.minecraft.client.resource.pack.IResourcePack;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ResourceManager implements IResourceManager {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final List resourcePacks = Lists.newArrayList();
   private final ResourceMetadataSerializerRegistry metadataSerializers;

   public ResourceManager(ResourceMetadataSerializerRegistry metadataSerializers) {
      this.metadataSerializers = metadataSerializers;
   }

   public void addResourcePack(IResourcePack pack) {
      this.resourcePacks.add(pack);
   }

   @Override
   public Set getNamespaces() {
      return null;
   }

   @Override
   public IResource getResource(Identifier id) {
      IResourcePack var2 = null;
      Identifier var3 = getMetadataId(id);

      for(int var4 = this.resourcePacks.size() - 1; var4 >= 0; --var4) {
         IResourcePack var5 = (IResourcePack)this.resourcePacks.get(var4);
         if (var2 == null && var5.hasResource(var3)) {
            var2 = var5;
         }

         if (var5.hasResource(id)) {
            InputStream var6 = null;
            if (var2 != null) {
               var6 = this.wrapResource(var3, var2);
            }

            return new Resource(var5.getName(), id, this.wrapResource(id, var5), var6, this.metadataSerializers);
         }
      }

      throw new FileNotFoundException(id.toString());
   }

   protected InputStream wrapResource(Identifier id, IResourcePack pack) {
      InputStream var3 = pack.getResource(id);
      return (InputStream)(LOGGER.isDebugEnabled() ? new ResourceManager.ResourceWrapper(var3, id, pack.getName()) : var3);
   }

   @Override
   public List getResources(Identifier id) {
      ArrayList var2 = Lists.newArrayList();
      Identifier var3 = getMetadataId(id);

      for(IResourcePack var5 : this.resourcePacks) {
         if (var5.hasResource(id)) {
            InputStream var6 = var5.hasResource(var3) ? this.wrapResource(var3, var5) : null;
            var2.add(new Resource(var5.getName(), id, this.wrapResource(id, var5), var6, this.metadataSerializers));
         }
      }

      if (var2.isEmpty()) {
         throw new FileNotFoundException(id.toString());
      } else {
         return var2;
      }
   }

   static Identifier getMetadataId(Identifier id) {
      return new Identifier(id.getNamespace(), id.getPath() + ".mcmeta");
   }

   @Environment(EnvType.CLIENT)
   static class ResourceWrapper extends InputStream {
      private final InputStream resource;
      private final String message;
      private boolean closed = false;

      public ResourceWrapper(InputStream resource, Identifier id, String packName) {
         this.resource = resource;
         ByteArrayOutputStream var4 = new ByteArrayOutputStream();
         new Exception().printStackTrace(new PrintStream(var4));
         this.message = "Leaked resource: '" + id + "' loaded from pack: '" + packName + "'\n" + var4.toString();
      }

      @Override
      public void close() {
         this.resource.close();
         this.closed = true;
      }

      @Override
      protected void finalize() {
         if (!this.closed) {
            ResourceManager.LOGGER.warn(this.message);
         }

         super.finalize();
      }

      @Override
      public int read() {
         return this.resource.read();
      }
   }
}
