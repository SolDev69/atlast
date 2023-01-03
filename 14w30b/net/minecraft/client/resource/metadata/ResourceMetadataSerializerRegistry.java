package net.minecraft.client.resource.metadata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.resource.metadata.serializer.IResourceMetadataSerializer;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.minecraft.util.registry.MappedRegistry;
import net.minecraft.util.registry.Registry;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ResourceMetadataSerializerRegistry {
   private final Registry registry = new MappedRegistry();
   private final GsonBuilder gsonBuilder = new GsonBuilder();
   private Gson gson;

   public ResourceMetadataSerializerRegistry() {
      this.gsonBuilder.registerTypeHierarchyAdapter(Text.class, new Text.Serializer());
      this.gsonBuilder.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
      this.gsonBuilder.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory());
   }

   public void register(IResourceMetadataSerializer serializer, Class metadataType) {
      this.registry.put(serializer.getName(), new ResourceMetadataSerializerRegistry.Entry(serializer, metadataType));
      this.gsonBuilder.registerTypeAdapter(metadataType, serializer);
      this.gson = null;
   }

   public ResourceMetadataSection readMetadata(String name, JsonObject json) {
      if (name == null) {
         throw new IllegalArgumentException("Metadata section name cannot be null");
      } else if (!json.has(name)) {
         return null;
      } else if (!json.get(name).isJsonObject()) {
         throw new IllegalArgumentException("Invalid metadata for '" + name + "' - expected object, found " + json.get(name));
      } else {
         ResourceMetadataSerializerRegistry.Entry var3 = (ResourceMetadataSerializerRegistry.Entry)this.registry.get(name);
         if (var3 == null) {
            throw new IllegalArgumentException("Don't know how to handle metadata section '" + name + "'");
         } else {
            return (ResourceMetadataSection)this.getGson().fromJson(json.getAsJsonObject(name), var3.metadataType);
         }
      }
   }

   private Gson getGson() {
      if (this.gson == null) {
         this.gson = this.gsonBuilder.create();
      }

      return this.gson;
   }

   @Environment(EnvType.CLIENT)
   class Entry {
      final IResourceMetadataSerializer metadataSerializer;
      final Class metadataType;

      private Entry(IResourceMetadataSerializer metadataSerializer, Class metadataType) {
         this.metadataSerializer = metadataSerializer;
         this.metadataType = metadataType;
      }
   }
}
