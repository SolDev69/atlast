package net.minecraft.client.render.model.block;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.vecmath.Vector3f;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockElement {
   public final Vector3f from;
   public final Vector3f to;
   public final Map faces;
   public final BlockElementRotation rotation;
   public final boolean shade;

   public BlockElement(Vector3f vector3f, Vector3f vector3f2, Map map, BlockElementRotation c_16uwxdqhy, boolean bl) {
      this.from = vector3f;
      this.to = vector3f2;
      this.faces = map;
      this.rotation = c_16uwxdqhy;
      this.shade = bl;
      this.init();
   }

   private void init() {
      for(Entry var2 : this.faces.entrySet()) {
         float[] var3 = this.getTextureCoords((Direction)var2.getKey());
         ((BlockElementFace)var2.getValue()).textureCoords.setCoordinates(var3);
      }
   }

   private float[] getTextureCoords(Direction face) {
      float[] var2;
      switch(face) {
         case DOWN:
         case UP:
            var2 = new float[]{this.from.x, this.from.z, this.to.x, this.to.z};
            break;
         case NORTH:
         case SOUTH:
            var2 = new float[]{this.from.x, 16.0F - this.to.y, this.to.x, 16.0F - this.from.y};
            break;
         case WEST:
         case EAST:
            var2 = new float[]{this.from.z, 16.0F - this.to.y, this.to.z, 16.0F - this.from.y};
            break;
         default:
            throw new NullPointerException();
      }

      return var2;
   }

   @Environment(EnvType.CLIENT)
   static class Serializer implements JsonDeserializer {
      public BlockElement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
         JsonObject var4 = jsonElement.getAsJsonObject();
         Vector3f var5 = this.deserializeFrom(var4);
         Vector3f var6 = this.deserializeTo(var4);
         BlockElementRotation var7 = this.deserializeRotation(var4);
         Map var8 = this.deserializeFaces(jsonDeserializationContext, var4);
         if (var4.has("shade") && !JsonUtils.hasBoolean(var4, "shade")) {
            throw new JsonParseException("Expected shade to be a Boolean");
         } else {
            boolean var9 = JsonUtils.getBooleanOrDefault(var4, "shade", true);
            return new BlockElement(var5, var6, var8, var7, var9);
         }
      }

      private BlockElementRotation deserializeRotation(JsonObject json) {
         BlockElementRotation var2 = null;
         if (json.has("rotation")) {
            JsonObject var3 = JsonUtils.getJsonObject(json, "rotation");
            Vector3f var4 = this.deserializeVector3f(var3, "origin");
            var4.scale(0.0625F);
            Direction.Axis var5 = this.deserializeAxis(var3);
            float var6 = this.deserializeAngle(var3);
            boolean var7 = JsonUtils.getBooleanOrDefault(var3, "rescale", false);
            var2 = new BlockElementRotation(var4, var5, var6, var7);
         }

         return var2;
      }

      private float deserializeAngle(JsonObject json) {
         float var2 = JsonUtils.getFloat(json, "angle");
         if (var2 != 0.0F && MathHelper.abs(var2) != 22.5F && MathHelper.abs(var2) != 45.0F) {
            throw new JsonParseException("Invalid rotation " + var2 + " found, only -45/-22.5/0/22.5/45 allowed");
         } else {
            return var2;
         }
      }

      private Direction.Axis deserializeAxis(JsonObject json) {
         String var2 = JsonUtils.getString(json, "axis");
         Direction.Axis var3 = Direction.Axis.byName(var2.toLowerCase());
         if (var3 == null) {
            throw new JsonParseException("Invalid rotation axis: " + var2);
         } else {
            return var3;
         }
      }

      private Map deserializeFaces(JsonDeserializationContext context, JsonObject json) {
         Map var3 = this.deserializeFacesFilterNull(context, json);
         if (var3.isEmpty()) {
            throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
         } else {
            return var3;
         }
      }

      private Map deserializeFacesFilterNull(JsonDeserializationContext context, JsonObject json) {
         EnumMap var3 = Maps.newEnumMap(Direction.class);
         JsonObject var4 = JsonUtils.getJsonObject(json, "faces");

         for(Entry var6 : var4.entrySet()) {
            Direction var7 = this.deserializeFacing((String)var6.getKey());
            var3.put(var7, (BlockElementFace)context.deserialize((JsonElement)var6.getValue(), BlockElementFace.class));
         }

         return var3;
      }

      private Direction deserializeFacing(String name) {
         Direction var2 = Direction.byName(name);
         if (var2 == null) {
            throw new JsonParseException("Unknown facing: " + name);
         } else {
            return var2;
         }
      }

      private Vector3f deserializeTo(JsonObject jsonObject) {
         Vector3f var2 = this.deserializeVector3f(jsonObject, "to");
         if (!(var2.x < -16.0F) && !(var2.y < -16.0F) && !(var2.z < -16.0F) && !(var2.x > 32.0F) && !(var2.y > 32.0F) && !(var2.z > 32.0F)) {
            return var2;
         } else {
            throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + var2);
         }
      }

      private Vector3f deserializeFrom(JsonObject jsonObject) {
         Vector3f var2 = this.deserializeVector3f(jsonObject, "from");
         if (!(var2.x < -16.0F) && !(var2.y < -16.0F) && !(var2.z < -16.0F) && !(var2.x > 32.0F) && !(var2.y > 32.0F) && !(var2.z > 32.0F)) {
            return var2;
         } else {
            throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + var2);
         }
      }

      private Vector3f deserializeVector3f(JsonObject jsonObject, String string) {
         JsonArray var3 = JsonUtils.getJsonArray(jsonObject, string);
         if (var3.size() != 3) {
            throw new JsonParseException("Expected 3 " + string + " values, found: " + var3.size());
         } else {
            float[] var4 = new float[3];

            for(int var5 = 0; var5 < var4.length; ++var5) {
               var4[var5] = JsonUtils.asFloat(var3.get(var5), string + "[" + var5 + "]");
            }

            return new Vector3f(var4);
         }
      }
   }
}
