package net.minecraft.client.resource.metadata.serializer;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map.Entry;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.metadata.LanguageMetadata;
import net.minecraft.util.JsonUtils;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class LanguageMetadataSerializer extends ResourceMetadataSerializer {
   public LanguageMetadata deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
      JsonObject var4 = jsonElement.getAsJsonObject();
      HashSet var5 = Sets.newHashSet();

      for(Entry var7 : var4.entrySet()) {
         String var8 = (String)var7.getKey();
         JsonObject var9 = JsonUtils.asJsonObject((JsonElement)var7.getValue(), "language");
         String var10 = JsonUtils.getString(var9, "region");
         String var11 = JsonUtils.getString(var9, "name");
         boolean var12 = JsonUtils.getBooleanOrDefault(var9, "bidirectional", false);
         if (var10.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + var8 + "'->region: empty value");
         }

         if (var11.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + var8 + "'->name: empty value");
         }

         if (!var5.add(new LanguageDefinition(var8, var10, var11, var12))) {
            throw new JsonParseException("Duplicate language->'" + var8 + "' defined");
         }
      }

      return new LanguageMetadata(var5);
   }

   @Override
   public String getName() {
      return "language";
   }
}
