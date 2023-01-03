package net.minecraft.client.render.model.block;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.vecmath.Vector3f;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelTransformation {
   public static final ModelTransformation NONE = new ModelTransformation(new Vector3f(), new Vector3f(), new Vector3f(1.0F, 1.0F, 1.0F));
   public final Vector3f rotation;
   public final Vector3f translation;
   public final Vector3f scale;

   public ModelTransformation(Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3) {
      this.rotation = new Vector3f(vector3f);
      this.translation = new Vector3f(vector3f2);
      this.scale = new Vector3f(vector3f3);
   }

   @Environment(EnvType.CLIENT)
   static class Serializer implements JsonDeserializer {
      private static final Vector3f DEFAULT_ROTATION = new Vector3f(0.0F, 0.0F, 0.0F);
      private static final Vector3f DEFAULT_TRANSLATION = new Vector3f(0.0F, 0.0F, 0.0F);
      private static final Vector3f DEFAULT_SCALE = new Vector3f(1.0F, 1.0F, 1.0F);

      public ModelTransformation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
         JsonObject var4 = jsonElement.getAsJsonObject();
         Vector3f var5 = this.deserializeVector3f(var4, "rotation", DEFAULT_ROTATION);
         Vector3f var6 = this.deserializeVector3f(var4, "translation", DEFAULT_TRANSLATION);
         var6.scale(0.0625F);
         MathHelper.clamp((double)var6.x, -1.5, 1.5);
         MathHelper.clamp((double)var6.y, -1.5, 1.5);
         MathHelper.clamp((double)var6.z, -1.5, 1.5);
         Vector3f var7 = this.deserializeVector3f(var4, "scale", DEFAULT_SCALE);
         MathHelper.clamp((double)var7.x, -1.5, 1.5);
         MathHelper.clamp((double)var7.y, -1.5, 1.5);
         MathHelper.clamp((double)var7.z, -1.5, 1.5);
         return new ModelTransformation(var5, var6, var7);
      }

      private Vector3f deserializeVector3f(JsonObject jsonObject, String string, Vector3f vector3f) {
         if (!jsonObject.has(string)) {
            return vector3f;
         } else {
            JsonArray var4 = JsonUtils.getJsonArray(jsonObject, string);
            if (var4.size() != 3) {
               throw new JsonParseException("Expected 3 " + string + " values, found: " + var4.size());
            } else {
               float[] var5 = new float[3];

               for(int var6 = 0; var6 < var5.length; ++var6) {
                  var5[var6] = JsonUtils.asFloat(var4.get(var6), string + "[" + var6 + "]");
               }

               return new Vector3f(var5);
            }
         }
      }
   }
}
