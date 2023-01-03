package net.minecraft.client.render.model.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelTransformations {
   public static final ModelTransformations NONE = new ModelTransformations(
      ModelTransformation.NONE, ModelTransformation.NONE, ModelTransformation.NONE, ModelTransformation.NONE
   );
   public final ModelTransformation thirdPerson;
   public final ModelTransformation firstPerson;
   public final ModelTransformation head;
   public final ModelTransformation gui;

   public ModelTransformations(
      ModelTransformation c_85djrxdvl, ModelTransformation c_85djrxdvl2, ModelTransformation c_85djrxdvl3, ModelTransformation c_85djrxdvl4
   ) {
      this.thirdPerson = c_85djrxdvl;
      this.firstPerson = c_85djrxdvl2;
      this.head = c_85djrxdvl3;
      this.gui = c_85djrxdvl4;
   }

   @Environment(EnvType.CLIENT)
   static class Serializer implements JsonDeserializer {
      public ModelTransformations deserialize(JsonElement jsonElement, java.lang.reflect.Type type, JsonDeserializationContext jsonDeserializationContext) {
         JsonObject var4 = jsonElement.getAsJsonObject();
         ModelTransformation var5 = ModelTransformation.NONE;
         ModelTransformation var6 = ModelTransformation.NONE;
         ModelTransformation var7 = ModelTransformation.NONE;
         ModelTransformation var8 = ModelTransformation.NONE;
         if (var4.has("thirdperson")) {
            var5 = (ModelTransformation)jsonDeserializationContext.deserialize(var4.get("thirdperson"), ModelTransformation.class);
         }

         if (var4.has("firstperson")) {
            var6 = (ModelTransformation)jsonDeserializationContext.deserialize(var4.get("firstperson"), ModelTransformation.class);
         }

         if (var4.has("head")) {
            var7 = (ModelTransformation)jsonDeserializationContext.deserialize(var4.get("head"), ModelTransformation.class);
         }

         if (var4.has("gui")) {
            var8 = (ModelTransformation)jsonDeserializationContext.deserialize(var4.get("gui"), ModelTransformation.class);
         }

         return new ModelTransformations(var5, var6, var7, var8);
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum Type {
      NONE,
      THIRD_PERSON,
      FIRST_PERSON,
      HEAD,
      GUI;
   }
}
