package net.minecraft.client.resource.metadata.serializer;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.util.JsonUtils;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextureMetadataSerializer extends ResourceMetadataSerializer {
   public TextureResourceMetadata deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
      JsonObject var4 = jsonElement.getAsJsonObject();
      boolean var5 = JsonUtils.getBooleanOrDefault(var4, "blur", false);
      boolean var6 = JsonUtils.getBooleanOrDefault(var4, "clamp", false);
      ArrayList var7 = Lists.newArrayList();
      if (var4.has("mipmaps")) {
         try {
            JsonArray var8 = var4.getAsJsonArray("mipmaps");

            for(int var9 = 0; var9 < var8.size(); ++var9) {
               JsonElement var10 = var8.get(var9);
               if (var10.isJsonPrimitive()) {
                  try {
                     var7.add(var10.getAsInt());
                  } catch (NumberFormatException var12) {
                     throw new JsonParseException("Invalid texture->mipmap->" + var9 + ": expected number, was " + var10, var12);
                  }
               } else if (var10.isJsonObject()) {
                  throw new JsonParseException("Invalid texture->mipmap->" + var9 + ": expected number, was " + var10);
               }
            }
         } catch (ClassCastException var13) {
            throw new JsonParseException("Invalid texture->mipmaps: expected array, was " + var4.get("mipmaps"), var13);
         }
      }

      return new TextureResourceMetadata(var5, var6, var7);
   }

   @Override
   public String getName() {
      return "texture";
   }
}
