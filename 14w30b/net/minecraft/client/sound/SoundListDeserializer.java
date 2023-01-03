package net.minecraft.client.sound;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.lang3.Validate;

@Environment(EnvType.CLIENT)
public class SoundListDeserializer implements JsonDeserializer {
   public SoundList deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
      JsonObject var4 = JsonUtils.asJsonObject(jsonElement, "entry");
      SoundList var5 = new SoundList();
      var5.setReplacable(JsonUtils.getBooleanOrDefault(var4, "replace", false));
      SoundCategory var6 = SoundCategory.byName(JsonUtils.getStringOrDefault(var4, "category", SoundCategory.MASTER.getName()));
      var5.setCategory(var6);
      Validate.notNull(var6, "Invalid category", new Object[0]);
      if (var4.has("sounds")) {
         JsonArray var7 = JsonUtils.getJsonArray(var4, "sounds");

         for(int var8 = 0; var8 < var7.size(); ++var8) {
            JsonElement var9 = var7.get(var8);
            SoundList.Entry var10 = new SoundList.Entry();
            if (JsonUtils.isString(var9)) {
               var10.setName(JsonUtils.asString(var9, "sound"));
            } else {
               JsonObject var11 = JsonUtils.asJsonObject(var9, "sound");
               var10.setName(JsonUtils.getString(var11, "name"));
               if (var11.has("type")) {
                  SoundList.Entry.Type var12 = SoundList.Entry.Type.byName(JsonUtils.getString(var11, "type"));
                  Validate.notNull(var12, "Invalid type", new Object[0]);
                  var10.setType(var12);
               }

               if (var11.has("volume")) {
                  float var13 = JsonUtils.getFloat(var11, "volume");
                  Validate.isTrue(var13 > 0.0F, "Invalid volume", new Object[0]);
                  var10.setVolume(var13);
               }

               if (var11.has("pitch")) {
                  float var14 = JsonUtils.getFloat(var11, "pitch");
                  Validate.isTrue(var14 > 0.0F, "Invalid pitch", new Object[0]);
                  var10.setPitch(var14);
               }

               if (var11.has("weight")) {
                  int var15 = JsonUtils.getInteger(var11, "weight");
                  Validate.isTrue(var15 > 0, "Invalid weight", new Object[0]);
                  var10.setWeight(var15);
               }

               if (var11.has("stream")) {
                  var10.setStream(JsonUtils.getBoolean(var11, "stream"));
               }
            }

            var5.getSounds().add(var10);
         }
      }

      return var5;
   }
}
