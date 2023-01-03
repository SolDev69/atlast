package net.minecraft.client.resource;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import net.minecraft.client.resource.metadata.ResourceMetadataSection;
import net.minecraft.client.resource.metadata.ResourceMetadataSerializerRegistry;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.IOUtils;

@Environment(EnvType.CLIENT)
public class Resource implements IResource {
   private final Map metadataProviders = Maps.newHashMap();
   private final String sourceName;
   private final Identifier id;
   private final InputStream inputStream;
   private final InputStream metadataInputStream;
   private final ResourceMetadataSerializerRegistry metadataSerializers;
   private boolean hasReadMetadata;
   private JsonObject metadata;

   public Resource(
      String sourceName, Identifier id, InputStream inputStream, InputStream metadataInputStream, ResourceMetadataSerializerRegistry metadataSerializers
   ) {
      this.sourceName = sourceName;
      this.id = id;
      this.inputStream = inputStream;
      this.metadataInputStream = metadataInputStream;
      this.metadataSerializers = metadataSerializers;
   }

   @Override
   public Identifier getId() {
      return this.id;
   }

   @Override
   public InputStream asStream() {
      return this.inputStream;
   }

   @Override
   public boolean hasMetadata() {
      return this.metadataInputStream != null;
   }

   @Override
   public ResourceMetadataSection getMetadata(String name) {
      if (!this.hasMetadata()) {
         return null;
      } else {
         if (this.metadata == null && !this.hasReadMetadata) {
            this.hasReadMetadata = true;
            BufferedReader var2 = null;

            try {
               var2 = new BufferedReader(new InputStreamReader(this.metadataInputStream));
               this.metadata = new JsonParser().parse(var2).getAsJsonObject();
            } finally {
               IOUtils.closeQuietly(var2);
            }
         }

         ResourceMetadataSection var6 = (ResourceMetadataSection)this.metadataProviders.get(name);
         if (var6 == null) {
            var6 = this.metadataSerializers.readMetadata(name, this.metadata);
         }

         return var6;
      }
   }

   @Override
   public String getSourceName() {
      return this.sourceName;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof Resource)) {
         return false;
      } else {
         Resource var2 = (Resource)obj;
         if (this.id != null ? this.id.equals(var2.id) : var2.id == null) {
            return this.sourceName != null ? this.sourceName.equals(var2.sourceName) : var2.sourceName == null;
         } else {
            return false;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.sourceName != null ? this.sourceName.hashCode() : 0;
      return 31 * var1 + (this.id != null ? this.id.hashCode() : 0);
   }
}
