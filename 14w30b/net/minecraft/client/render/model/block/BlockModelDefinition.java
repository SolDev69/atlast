package net.minecraft.client.render.model.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.resource.model.ModelRotation;
import net.minecraft.resource.Identifier;
import net.minecraft.util.JsonUtils;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockModelDefinition {
   static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(BlockModelDefinition.class, new BlockModelDefinition.Serializer())
      .registerTypeAdapter(BlockModelDefinition.Variant.class, new BlockModelDefinition.Variant.Serializer())
      .create();
   private final Map variants = Maps.newHashMap();

   public static BlockModelDefinition fromJson(Reader reader) {
      return (BlockModelDefinition)GSON.fromJson(reader, BlockModelDefinition.class);
   }

   public BlockModelDefinition(Collection variants) {
      for(BlockModelDefinition.MultiVariant var3 : variants) {
         this.variants.put(var3.name, var3);
      }
   }

   public BlockModelDefinition(List models) {
      for(BlockModelDefinition var3 : models) {
         this.variants.putAll(var3.variants);
      }
   }

   public BlockModelDefinition.MultiVariant getVariant(String name) {
      BlockModelDefinition.MultiVariant var2 = (BlockModelDefinition.MultiVariant)this.variants.get(name);
      if (var2 == null) {
         throw new BlockModelDefinition.NoSuchVariantException();
      } else {
         return var2;
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj instanceof BlockModelDefinition) {
         BlockModelDefinition var2 = (BlockModelDefinition)obj;
         return this.variants.equals(var2.variants);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.variants.hashCode();
   }

   @Environment(EnvType.CLIENT)
   public static class MultiVariant {
      private final String name;
      private final List variants;

      public MultiVariant(String name, List variants) {
         this.name = name;
         this.variants = variants;
      }

      public List getVariants() {
         return this.variants;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (!(obj instanceof BlockModelDefinition.MultiVariant)) {
            return false;
         } else {
            BlockModelDefinition.MultiVariant var2 = (BlockModelDefinition.MultiVariant)obj;
            if (!this.name.equals(var2.name)) {
               return false;
            } else {
               return this.variants.equals(var2.variants);
            }
         }
      }

      @Override
      public int hashCode() {
         int var1 = this.name.hashCode();
         return 31 * var1 + this.variants.hashCode();
      }
   }

   @Environment(EnvType.CLIENT)
   public class NoSuchVariantException extends RuntimeException {
      protected NoSuchVariantException() {
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Serializer implements JsonDeserializer {
      public BlockModelDefinition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
         JsonObject var4 = jsonElement.getAsJsonObject();
         List var5 = this.deserializeVariants(jsonDeserializationContext, var4);
         return new BlockModelDefinition((Collection)var5);
      }

      protected List deserializeVariants(JsonDeserializationContext context, JsonObject json) {
         JsonObject var3 = JsonUtils.getJsonObject(json, "variants");
         ArrayList var4 = Lists.newArrayList();

         for(Entry var6 : var3.entrySet()) {
            var4.add(this.deserializeVariant(context, var6));
         }

         return var4;
      }

      protected BlockModelDefinition.MultiVariant deserializeVariant(JsonDeserializationContext context, Entry entry) {
         String var3 = (String)entry.getKey();
         ArrayList var4 = Lists.newArrayList();
         JsonElement var5 = (JsonElement)entry.getValue();
         if (var5.isJsonArray()) {
            for(JsonElement var7 : var5.getAsJsonArray()) {
               var4.add((BlockModelDefinition.Variant)context.deserialize(var7, BlockModelDefinition.Variant.class));
            }
         } else {
            var4.add((BlockModelDefinition.Variant)context.deserialize(var5, BlockModelDefinition.Variant.class));
         }

         return new BlockModelDefinition.MultiVariant(var3, var4);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Variant {
      private final Identifier id;
      private final ModelRotation rotation;
      private final boolean uvLock;
      private final int weight;

      public Variant(Identifier id, ModelRotation rotation, boolean uvLock, int weight) {
         this.id = id;
         this.rotation = rotation;
         this.uvLock = uvLock;
         this.weight = weight;
      }

      public Identifier getId() {
         return this.id;
      }

      public ModelRotation gerRotation() {
         return this.rotation;
      }

      public boolean isUvLocked() {
         return this.uvLock;
      }

      public int getWeight() {
         return this.weight;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (!(obj instanceof BlockModelDefinition.Variant)) {
            return false;
         } else {
            BlockModelDefinition.Variant var2 = (BlockModelDefinition.Variant)obj;
            return this.id.equals(var2.id) && this.rotation == var2.rotation && this.uvLock == var2.uvLock;
         }
      }

      @Override
      public int hashCode() {
         int var1 = this.id.hashCode();
         var1 = 31 * var1 + (this.rotation != null ? this.rotation.hashCode() : 0);
         return 31 * var1 + (this.uvLock ? 1 : 0);
      }

      @Environment(EnvType.CLIENT)
      public static class Serializer implements JsonDeserializer {
         public BlockModelDefinition.Variant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            JsonObject var4 = jsonElement.getAsJsonObject();
            String var5 = this.deserializePath(var4);
            ModelRotation var6 = this.deserializeRotation(var4);
            boolean var7 = this.deserializeUvLock(var4);
            int var8 = this.deserializeWeight(var4);
            return new BlockModelDefinition.Variant(this.getId(var5), var6, var7, var8);
         }

         private Identifier getId(String path) {
            Identifier var2 = new Identifier(path);
            return new Identifier(var2.getNamespace(), "block/" + var2.getPath());
         }

         private boolean deserializeUvLock(JsonObject json) {
            return JsonUtils.getBooleanOrDefault(json, "uvlock", false);
         }

         protected ModelRotation deserializeRotation(JsonObject json) {
            int var2 = JsonUtils.getIntegerOrDefault(json, "x", 0);
            int var3 = JsonUtils.getIntegerOrDefault(json, "y", 0);
            ModelRotation var4 = ModelRotation.by(var2, var3);
            if (var4 == null) {
               throw new JsonParseException("Invalid BlockModelRotation x: " + var2 + ", y: " + var3);
            } else {
               return var4;
            }
         }

         protected String deserializePath(JsonObject json) {
            return JsonUtils.getString(json, "model");
         }

         protected int deserializeWeight(JsonObject json) {
            return JsonUtils.getIntegerOrDefault(json, "weight", 1);
         }
      }
   }
}
