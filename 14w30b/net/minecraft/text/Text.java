package net.minecraft.text;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public interface Text extends Iterable {
   Text setStyle(Style style);

   Style getStyle();

   Text append(String string);

   Text append(Text text);

   String getString();

   String buildString();

   @Environment(EnvType.CLIENT)
   String buildFormattedString();

   List getSiblings();

   Text copy();

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      private static final Gson GSON;

      public Text deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
         if (jsonElement.isJsonPrimitive()) {
            return new LiteralText(jsonElement.getAsString());
         } else if (!jsonElement.isJsonObject()) {
            if (jsonElement.isJsonArray()) {
               JsonArray var11 = jsonElement.getAsJsonArray();
               Text var12 = null;

               for(JsonElement var17 : var11) {
                  Text var18 = this.deserialize(var17, var17.getClass(), jsonDeserializationContext);
                  if (var12 == null) {
                     var12 = var18;
                  } else {
                     var12.append(var18);
                  }
               }

               return var12;
            } else {
               throw new JsonParseException("Don't know how to turn " + jsonElement.toString() + " into a Component");
            }
         } else {
            JsonObject var4 = jsonElement.getAsJsonObject();
            Object var5;
            if (var4.has("text")) {
               var5 = new LiteralText(var4.get("text").getAsString());
            } else if (var4.has("translate")) {
               String var6 = var4.get("translate").getAsString();
               if (var4.has("with")) {
                  JsonArray var7 = var4.getAsJsonArray("with");
                  Object[] var8 = new Object[var7.size()];

                  for(int var9 = 0; var9 < var8.length; ++var9) {
                     var8[var9] = this.deserialize(var7.get(var9), type, jsonDeserializationContext);
                     if (var8[var9] instanceof LiteralText) {
                        LiteralText var10 = (LiteralText)var8[var9];
                        if (var10.getStyle().isEmpty() && var10.getSiblings().isEmpty()) {
                           var8[var9] = var10.getRawString();
                        }
                     }
                  }

                  var5 = new TranslatableText(var6, var8);
               } else {
                  var5 = new TranslatableText(var6);
               }
            } else if (var4.has("score")) {
               JsonObject var13 = var4.getAsJsonObject("score");
               if (!var13.has("name") || !var13.has("objective")) {
                  throw new JsonParseException("A score component needs a least a name and an objective");
               }

               var5 = new ScoreText(JsonUtils.getString(var13, "name"), JsonUtils.getString(var13, "objective"));
               if (var13.has("value")) {
                  ((ScoreText)var5).setValue(JsonUtils.getString(var13, "value"));
               }
            } else {
               if (!var4.has("selector")) {
                  throw new JsonParseException("Don't know how to turn " + jsonElement.toString() + " into a Component");
               }

               var5 = new SelectorText(JsonUtils.getString(var4, "selector"));
            }

            if (var4.has("extra")) {
               JsonArray var14 = var4.getAsJsonArray("extra");
               if (var14.size() <= 0) {
                  throw new JsonParseException("Unexpected empty array of components");
               }

               for(int var16 = 0; var16 < var14.size(); ++var16) {
                  ((Text)var5).append(this.deserialize(var14.get(var16), type, jsonDeserializationContext));
               }
            }

            ((Text)var5).setStyle((Style)jsonDeserializationContext.deserialize(jsonElement, Style.class));
            return (Text)var5;
         }
      }

      private void addStyle(Style style, JsonObject json, JsonSerializationContext context) {
         JsonElement var4 = context.serialize(style);
         if (var4.isJsonObject()) {
            JsonObject var5 = (JsonObject)var4;

            for(Entry var7 : var5.entrySet()) {
               json.add((String)var7.getKey(), (JsonElement)var7.getValue());
            }
         }
      }

      public JsonElement serialize(Text c_21uoltggz, Type type, JsonSerializationContext jsonSerializationContext) {
         if (c_21uoltggz instanceof LiteralText && c_21uoltggz.getStyle().isEmpty() && c_21uoltggz.getSiblings().isEmpty()) {
            return new JsonPrimitive(((LiteralText)c_21uoltggz).getRawString());
         } else {
            JsonObject var4 = new JsonObject();
            if (!c_21uoltggz.getStyle().isEmpty()) {
               this.addStyle(c_21uoltggz.getStyle(), var4, jsonSerializationContext);
            }

            if (!c_21uoltggz.getSiblings().isEmpty()) {
               JsonArray var5 = new JsonArray();

               for(Text var7 : c_21uoltggz.getSiblings()) {
                  var5.add(this.serialize(var7, var7.getClass(), jsonSerializationContext));
               }

               var4.add("extra", var5);
            }

            if (c_21uoltggz instanceof LiteralText) {
               var4.addProperty("text", ((LiteralText)c_21uoltggz).getRawString());
            } else if (c_21uoltggz instanceof TranslatableText) {
               TranslatableText var11 = (TranslatableText)c_21uoltggz;
               var4.addProperty("translate", var11.getKey());
               if (var11.getArgs() != null && var11.getArgs().length > 0) {
                  JsonArray var14 = new JsonArray();

                  for(Object var10 : var11.getArgs()) {
                     if (var10 instanceof Text) {
                        var14.add(this.serialize((Text)var10, var10.getClass(), jsonSerializationContext));
                     } else {
                        var14.add(new JsonPrimitive(String.valueOf(var10)));
                     }
                  }

                  var4.add("with", var14);
               }
            } else if (c_21uoltggz instanceof ScoreText) {
               ScoreText var12 = (ScoreText)c_21uoltggz;
               JsonObject var15 = new JsonObject();
               var15.addProperty("name", var12.getOwner());
               var15.addProperty("objective", var12.getObjective());
               var15.addProperty("value", var12.getString());
               var4.add("score", var15);
            } else {
               if (!(c_21uoltggz instanceof SelectorText)) {
                  throw new IllegalArgumentException("Don't know how to serialize " + c_21uoltggz + " as a Component");
               }

               SelectorText var13 = (SelectorText)c_21uoltggz;
               var4.addProperty("selector", var13.getPattern());
            }

            return var4;
         }
      }

      public static String toJson(Text text) {
         return GSON.toJson(text);
      }

      public static Text fromJson(String s) {
         return (Text)GSON.fromJson(s, Text.class);
      }

      static {
         GsonBuilder var0 = new GsonBuilder();
         var0.registerTypeHierarchyAdapter(Text.class, new Text.Serializer());
         var0.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
         var0.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory());
         GSON = var0.create();
      }
   }
}
