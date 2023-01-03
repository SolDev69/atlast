package net.minecraft.client.resource.metadata.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.client.resource.metadata.ResourcePackMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.JsonUtils;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ResourcePackMetadataSerializer extends ResourceMetadataSerializer implements JsonSerializer {
   public ResourcePackMetadata deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
      JsonObject var4 = jsonElement.getAsJsonObject();
      Text var5 = (Text)jsonDeserializationContext.deserialize(var4.get("description"), Text.class);
      if (var5 == null) {
         throw new JsonParseException("Invalid/missing description!");
      } else {
         int var6 = JsonUtils.getInteger(var4, "pack_format");
         return new ResourcePackMetadata(var5, var6);
      }
   }

   public JsonElement serialize(ResourcePackMetadata c_57uxdicji, Type type, JsonSerializationContext jsonSerializationContext) {
      JsonObject var4 = new JsonObject();
      var4.addProperty("pack_format", c_57uxdicji.getFormat());
      var4.add("description", jsonSerializationContext.serialize(c_57uxdicji.getDescription()));
      return var4;
   }

   @Override
   public String getName() {
      return "pack";
   }
}
