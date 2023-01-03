package net.minecraft.client.render.model.block;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockElementTexture {
   public float[] coordinates;
   public final int rotation;

   public BlockElementTexture(float[] coordinates, int rotation) {
      this.coordinates = coordinates;
      this.rotation = rotation;
   }

   public float getU(int vertex) {
      if (this.coordinates == null) {
         throw new NullPointerException("uvs");
      } else {
         int var2 = this.index(vertex);
         return var2 != 0 && var2 != 1 ? this.coordinates[2] : this.coordinates[0];
      }
   }

   public float getV(int vertex) {
      if (this.coordinates == null) {
         throw new NullPointerException("uvs");
      } else {
         int var2 = this.index(vertex);
         return var2 != 0 && var2 != 3 ? this.coordinates[3] : this.coordinates[1];
      }
   }

   private int index(int vertex) {
      return (vertex + this.rotation / 90) % 4;
   }

   public int reverseIndex(int vertex) {
      return (vertex + (4 - this.rotation / 90)) % 4;
   }

   public void setCoordinates(float[] coordinates) {
      if (this.coordinates == null) {
         this.coordinates = coordinates;
      }
   }

   @Environment(EnvType.CLIENT)
   static class Serializer implements JsonDeserializer {
      public BlockElementTexture deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
         JsonObject var4 = jsonElement.getAsJsonObject();
         float[] var5 = this.deserializeCoordinates(var4);
         int var6 = this.deserializeRotation(var4);
         return new BlockElementTexture(var5, var6);
      }

      protected int deserializeRotation(JsonObject json) {
         int var2 = JsonUtils.getIntegerOrDefault(json, "rotation", 0);
         if (var2 >= 0 && var2 % 90 == 0 && var2 / 90 <= 3) {
            return var2;
         } else {
            throw new JsonParseException("Invalid rotation " + var2 + " found, only 0/90/180/270 allowed");
         }
      }

      private float[] deserializeCoordinates(JsonObject json) {
         if (!json.has("uv")) {
            return null;
         } else {
            JsonArray var2 = JsonUtils.getJsonArray(json, "uv");
            if (var2.size() != 4) {
               throw new JsonParseException("Expected 4 uv values, found: " + var2.size());
            } else {
               float[] var3 = new float[4];

               for(int var4 = 0; var4 < var3.length; ++var4) {
                  var3[var4] = JsonUtils.asFloat(var2.get(var4), "uv[" + var4 + "]");
               }

               return var3;
            }
         }
      }
   }
}
