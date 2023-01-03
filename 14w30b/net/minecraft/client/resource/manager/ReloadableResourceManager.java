package net.minecraft.client.resource.manager;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resource.IResource;
import net.minecraft.client.resource.metadata.ResourceMetadataSerializerRegistry;
import net.minecraft.client.resource.pack.IResourcePack;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ReloadableResourceManager implements IReloadableResourceManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Joiner JOINER = Joiner.on(", ");
   private final Map delegates = Maps.newHashMap();
   private final List listeners = Lists.newArrayList();
   private final Set namespaces = Sets.newLinkedHashSet();
   private final ResourceMetadataSerializerRegistry metadataSerializers;

   public ReloadableResourceManager(ResourceMetadataSerializerRegistry metadataSerializers) {
      this.metadataSerializers = metadataSerializers;
   }

   public void reload(IResourcePack resourcePack) {
      for(String var3 : resourcePack.getNamespaces()) {
         this.namespaces.add(var3);
         ResourceManager var4 = (ResourceManager)this.delegates.get(var3);
         if (var4 == null) {
            var4 = new ResourceManager(this.metadataSerializers);
            this.delegates.put(var3, var4);
         }

         var4.addResourcePack(resourcePack);
      }
   }

   @Override
   public Set getNamespaces() {
      return this.namespaces;
   }

   @Override
   public IResource getResource(Identifier id) {
      IResourceManager var2 = (IResourceManager)this.delegates.get(id.getNamespace());
      if (var2 != null) {
         return var2.getResource(id);
      } else {
         throw new FileNotFoundException(id.toString());
      }
   }

   @Override
   public List getResources(Identifier id) {
      IResourceManager var2 = (IResourceManager)this.delegates.get(id.getNamespace());
      if (var2 != null) {
         return var2.getResources(id);
      } else {
         throw new FileNotFoundException(id.toString());
      }
   }

   private void clear() {
      this.delegates.clear();
      this.namespaces.clear();
   }

   @Override
   public void reload(List resourcePacks) {
      this.clear();
      LOGGER.info("Reloading ResourceManager: " + JOINER.join(Iterables.transform(resourcePacks, new Function() {
         public String apply(IResourcePack c_73hqnrdhw) {
            return c_73hqnrdhw.getName();
         }
      })));

      for(IResourcePack var3 : resourcePacks) {
         this.reload(var3);
      }

      this.reloadListeners();
   }

   @Override
   public void addListener(ResourceReloadListener listener) {
      this.listeners.add(listener);
      listener.reload(this);
   }

   private void reloadListeners() {
      for(ResourceReloadListener var2 : this.listeners) {
         var2.reload(this);
      }
   }
}
