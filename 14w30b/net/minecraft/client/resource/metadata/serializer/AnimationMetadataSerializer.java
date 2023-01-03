package net.minecraft.client.resource.metadata.serializer;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import net.minecraft.client.resource.metadata.AnimationFrame;
import net.minecraft.client.resource.metadata.AnimationMetadata;
import net.minecraft.util.JsonUtils;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.lang3.Validate;

@Environment(EnvType.CLIENT)
public class AnimationMetadataSerializer extends ResourceMetadataSerializer implements JsonSerializer {
   public AnimationMetadata deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
      ArrayList var4 = Lists.newArrayList();
      JsonObject var5 = JsonUtils.asJsonObject(jsonElement, "metadata section");
      int var6 = JsonUtils.getIntegerOrDefault(var5, "frametime", 1);
      if (var6 != 1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)var6, "Invalid default frame time");
      }

      if (var5.has("frames")) {
         try {
            JsonArray var7 = JsonUtils.getJsonArray(var5, "frames");

            for(int var8 = 0; var8 < var7.size(); ++var8) {
               JsonElement var9 = var7.get(var8);
               AnimationFrame var10 = this.deserializeAnimationFrame(var8, var9);
               if (var10 != null) {
                  var4.add(var10);
               }
            }
         } catch (ClassCastException var11) {
            throw new JsonParseException("Invalid animation->frames: expected array, was " + var5.get("frames"), var11);
         }
      }

      int var12 = JsonUtils.getIntegerOrDefault(var5, "width", -1);
      int var13 = JsonUtils.getIntegerOrDefault(var5, "height", -1);
      if (var12 != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)var12, "Invalid width");
      }

      if (var13 != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)var13, "Invalid height");
      }

      boolean var14 = JsonUtils.getBooleanOrDefault(var5, "interpolate", false);
      return new AnimationMetadata(var4, var12, var13, var6, var14);
   }

   private AnimationFrame deserializeAnimationFrame(int frameIndex, JsonElement jsonElement) {
      if (jsonElement.isJsonPrimitive()) {
         return new AnimationFrame(JsonUtils.asInteger(jsonElement, "frames[" + frameIndex + "]"));
      } else if (jsonElement.isJsonObject()) {
         JsonObject var3 = JsonUtils.asJsonObject(jsonElement, "frames[" + frameIndex + "]");
         int var4 = JsonUtils.getIntegerOrDefault(var3, "time", -1);
         if (var3.has("time")) {
            Validate.inclusiveBetween(1L, 2147483647L, (long)var4, "Invalid frame time");
         }

         int var5 = JsonUtils.getInteger(var3, "index");
         Validate.inclusiveBetween(0L, 2147483647L, (long)var5, "Invalid frame index");
         return new AnimationFrame(var5, var4);
      } else {
         return null;
      }
   }

   public JsonElement serialize(AnimationMetadata c_18orfsnvl, Type type, JsonSerializationContext jsonSerializationContext) {
      JsonObject var4 = new JsonObject();
      var4.addProperty("frametime", c_18orfsnvl.getTime());
      if (c_18orfsnvl.getWidth() != -1) {
         var4.addProperty("width", c_18orfsnvl.getWidth());
      }

      if (c_18orfsnvl.getHeight() != -1) {
         var4.addProperty("height", c_18orfsnvl.getHeight());
      }

      if (c_18orfsnvl.getFrameCount() > 0) {
         JsonArray var5 = new JsonArray();

         for(int var6 = 0; var6 < c_18orfsnvl.getFrameCount(); ++var6) {
            if (c_18orfsnvl.usesNonDefaultFrameTime(var6)) {
               JsonObject var7 = new JsonObject();
               var7.addProperty("index", c_18orfsnvl.getIndex(var6));
               var7.addProperty("time", c_18orfsnvl.getTime(var6));
               var5.add(var7);
            } else {
               var5.add(new JsonPrimitive(c_18orfsnvl.getIndex(var6)));
            }
         }

         var4.add("frames", var5);
      }

      return var4;
   }

   @Override
   public String getName() {
      return "animation";
   }
}
