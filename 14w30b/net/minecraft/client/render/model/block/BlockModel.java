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
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.resource.Identifier;
import net.minecraft.util.JsonUtils;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class BlockModel {
   private static final Logger LOGGER = LogManager.getLogger();
   static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(BlockModel.class, new BlockModel.Serializer())
      .registerTypeAdapter(BlockElement.class, new BlockElement.Serializer())
      .registerTypeAdapter(BlockElementFace.class, new BlockElementFace.Serializer())
      .registerTypeAdapter(BlockElementTexture.class, new BlockElementTexture.Serializer())
      .registerTypeAdapter(ModelTransformation.class, new ModelTransformation.Serializer())
      .registerTypeAdapter(ModelTransformations.class, new ModelTransformations.Serializer())
      .create();
   private final List elements;
   private final boolean gui3d;
   private final boolean ambientOcclusion;
   private ModelTransformations transformations;
   public String name = "";
   protected final Map textures;
   protected BlockModel parent;
   protected Identifier parentId;

   public static BlockModel fromJson(Reader reader) {
      return (BlockModel)GSON.fromJson(reader, BlockModel.class);
   }

   public static BlockModel fromJson(String s) {
      return fromJson(new StringReader(s));
   }

   protected BlockModel(List elements, Map textures, boolean ambientOcclusion, boolean gui3d, ModelTransformations transformations) {
      this(null, elements, textures, ambientOcclusion, gui3d, transformations);
   }

   protected BlockModel(Identifier parent, Map textures, boolean ambientOcclusion, boolean gui3d, ModelTransformations transformations) {
      this(parent, Collections.emptyList(), textures, ambientOcclusion, gui3d, transformations);
   }

   private BlockModel(Identifier parentId, List elements, Map textures, boolean ambientOcclusion, boolean gui3d, ModelTransformations transformations) {
      this.elements = elements;
      this.ambientOcclusion = ambientOcclusion;
      this.gui3d = gui3d;
      this.textures = textures;
      this.parentId = parentId;
      this.transformations = transformations;
   }

   public List getElements() {
      return this.hasParent() ? this.parent.getElements() : this.elements;
   }

   private boolean hasParent() {
      return this.parent != null;
   }

   public boolean usesAmbientOcclusion() {
      return this.hasParent() ? this.parent.usesAmbientOcclusion() : this.ambientOcclusion;
   }

   public boolean isGui3d() {
      return this.gui3d;
   }

   public boolean isComplete() {
      return this.parentId == null || this.parent != null && this.parent.isComplete();
   }

   public void findParent(Map models) {
      if (this.parentId != null) {
         this.parent = (BlockModel)models.get(this.parentId);
      }
   }

   public boolean hasTexture(String path) {
      return !"missingno".equals(this.getTexture(path));
   }

   public String getTexture(String path) {
      if (!this.isTextureReference(path)) {
         path = '#' + path;
      }

      return this.getTexture(path, new BlockModel.TextureContext(this));
   }

   private String getTexture(String path, BlockModel.TextureContext context) {
      if (this.isTextureReference(path)) {
         if (this == context.current) {
            LOGGER.warn("Unable to resolve texture due to upward reference: " + path + " in " + this.name);
            return "missingno";
         } else {
            String var3 = (String)this.textures.get(path.substring(1));
            if (var3 == null && this.hasParent()) {
               var3 = this.parent.getTexture(path, context);
            }

            context.current = this;
            if (var3 != null && this.isTextureReference(var3)) {
               var3 = context.root.getTexture(var3, context);
            }

            return var3 != null && !this.isTextureReference(var3) ? var3 : "missingno";
         }
      } else {
         return path;
      }
   }

   private boolean isTextureReference(String path) {
      return path.charAt(0) == '#';
   }

   public Identifier getParentId() {
      return this.parentId;
   }

   public BlockModel getRoot() {
      return this.hasParent() ? this.parent.getRoot() : this;
   }

   public ModelTransformation m_81pqtuasw() {
      return this.parent != null && this.transformations.thirdPerson == ModelTransformation.NONE ? this.parent.m_81pqtuasw() : this.transformations.thirdPerson;
   }

   public ModelTransformation m_10lvezxir() {
      return this.parent != null && this.transformations.firstPerson == ModelTransformation.NONE ? this.parent.m_10lvezxir() : this.transformations.firstPerson;
   }

   public ModelTransformation m_09toxmbvv() {
      return this.parent != null && this.transformations.head == ModelTransformation.NONE ? this.parent.m_09toxmbvv() : this.transformations.head;
   }

   public ModelTransformation m_12mcmbtqy() {
      return this.parent != null && this.transformations.gui == ModelTransformation.NONE ? this.parent.m_12mcmbtqy() : this.transformations.gui;
   }

   public static void checkHierarchy(Map models) {
      for(BlockModel var2 : models.values()) {
         try {
            BlockModel var3 = var2.parent;

            for(BlockModel var4 = var3.parent; var3 != var4; var4 = var4.parent.parent) {
               var3 = var3.parent;
            }

            throw new BlockModel.InvalidHierarchyException();
         } catch (NullPointerException var5) {
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public static class InvalidHierarchyException extends RuntimeException {
   }

   @Environment(EnvType.CLIENT)
   public static class Serializer implements JsonDeserializer {
      public BlockModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
         JsonObject var4 = jsonElement.getAsJsonObject();
         List var5 = this.deserializeElements(jsonDeserializationContext, var4);
         String var6 = this.getParentName(var4);
         boolean var7 = StringUtils.isEmpty(var6);
         boolean var8 = var5.isEmpty();
         if (var8 && var7) {
            throw new JsonParseException("BlockModel requires either elements or parent, found neither");
         } else if (!var7 && !var8) {
            throw new JsonParseException("BlockModel requires either elements or parent, found both");
         } else {
            Map var9 = this.deserializeTextures(var4);
            boolean var10 = this.getAmbientOcclusion(var4);
            ModelTransformations var11 = ModelTransformations.NONE;
            if (var4.has("display")) {
               JsonObject var12 = JsonUtils.getJsonObject(var4, "display");
               var11 = (ModelTransformations)jsonDeserializationContext.deserialize(var12, ModelTransformations.class);
            }

            return var8 ? new BlockModel(new Identifier(var6), var9, var10, true, var11) : new BlockModel(var5, var9, var10, true, var11);
         }
      }

      private Map deserializeTextures(JsonObject json) {
         HashMap var2 = Maps.newHashMap();
         if (json.has("textures")) {
            JsonObject var3 = json.getAsJsonObject("textures");

            for(Entry var5 : var3.entrySet()) {
               var2.put(var5.getKey(), ((JsonElement)var5.getValue()).getAsString());
            }
         }

         return var2;
      }

      private String getParentName(JsonObject json) {
         return JsonUtils.getStringOrDefault(json, "parent", "");
      }

      protected boolean getAmbientOcclusion(JsonObject json) {
         return JsonUtils.getBooleanOrDefault(json, "ambientocclusion", true);
      }

      protected List deserializeElements(JsonDeserializationContext context, JsonObject json) {
         ArrayList var3 = Lists.newArrayList();
         if (json.has("elements")) {
            for(JsonElement var5 : JsonUtils.getJsonArray(json, "elements")) {
               var3.add((BlockElement)context.deserialize(var5, BlockElement.class));
            }
         }

         return var3;
      }
   }

   @Environment(EnvType.CLIENT)
   static final class TextureContext {
      public final BlockModel root;
      public BlockModel current;

      private TextureContext(BlockModel root) {
         this.root = root;
      }
   }
}
