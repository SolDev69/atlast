package net.minecraft.client.render.model.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.Direction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockElementFace {
   public static final Direction f_89wmfpliz = null;
   public final Direction cullFace;
   public final int tintIndex;
   public final String texture;
   public final BlockElementTexture textureCoords;

   public BlockElementFace(Direction cullFace, int tintIndex, String texture, BlockElementTexture textureCoords) {
      this.cullFace = cullFace;
      this.tintIndex = tintIndex;
      this.texture = texture;
      this.textureCoords = textureCoords;
   }

   @Environment(EnvType.CLIENT)
   static class Serializer implements JsonDeserializer {
      public BlockElementFace deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
         JsonObject var4 = jsonElement.getAsJsonObject();
         Direction var5 = this.deserializeCullFace(var4);
         int var6 = this.deserializeTintIndex(var4);
         String var7 = this.deserializeTexture(var4);
         BlockElementTexture var8 = (BlockElementTexture)jsonDeserializationContext.deserialize(var4, BlockElementTexture.class);
         return new BlockElementFace(var5, var6, var7, var8);
      }

      protected int deserializeTintIndex(JsonObject json) {
         return JsonUtils.getIntegerOrDefault(json, "tintindex", -1);
      }

      private String deserializeTexture(JsonObject json) {
         return JsonUtils.getString(json, "texture");
      }

      private Direction deserializeCullFace(JsonObject json) {
         String var2 = JsonUtils.getStringOrDefault(json, "cullface", "");
         return Direction.byName(var2);
      }
   }
}
