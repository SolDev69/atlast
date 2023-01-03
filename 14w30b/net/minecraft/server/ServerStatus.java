package net.minecraft.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;
import net.minecraft.text.Text;
import net.minecraft.util.JsonUtils;

public class ServerStatus {
   private Text description;
   private ServerStatus.Players players;
   private ServerStatus.Version version;
   private String favicon;

   public Text getDescription() {
      return this.description;
   }

   public void setDescription(Text description) {
      this.description = description;
   }

   public ServerStatus.Players getPlayers() {
      return this.players;
   }

   public void setPlayers(ServerStatus.Players players) {
      this.players = players;
   }

   public ServerStatus.Version getVersion() {
      return this.version;
   }

   public void setVersion(ServerStatus.Version version) {
      this.version = version;
   }

   public void setFavicon(String favicon) {
      this.favicon = favicon;
   }

   public String getFavicon() {
      return this.favicon;
   }

   public static class Players {
      private final int max;
      private final int online;
      private GameProfile[] profiles;

      public Players(int max, int online) {
         this.max = max;
         this.online = online;
      }

      public int getMax() {
         return this.max;
      }

      public int getOnline() {
         return this.online;
      }

      public GameProfile[] get() {
         return this.profiles;
      }

      public void set(GameProfile[] profiles) {
         this.profiles = profiles;
      }

      public static class Serializer implements JsonDeserializer, JsonSerializer {
         public ServerStatus.Players deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            JsonObject var4 = JsonUtils.asJsonObject(jsonElement, "players");
            ServerStatus.Players var5 = new ServerStatus.Players(JsonUtils.getInteger(var4, "max"), JsonUtils.getInteger(var4, "online"));
            if (JsonUtils.hasJsonArray(var4, "sample")) {
               JsonArray var6 = JsonUtils.getJsonArray(var4, "sample");
               if (var6.size() > 0) {
                  GameProfile[] var7 = new GameProfile[var6.size()];

                  for(int var8 = 0; var8 < var7.length; ++var8) {
                     JsonObject var9 = JsonUtils.asJsonObject(var6.get(var8), "player[" + var8 + "]");
                     String var10 = JsonUtils.getString(var9, "id");
                     var7[var8] = new GameProfile(UUID.fromString(var10), JsonUtils.getString(var9, "name"));
                  }

                  var5.set(var7);
               }
            }

            return var5;
         }

         public JsonElement serialize(ServerStatus.Players c_14llhpgrs, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject var4 = new JsonObject();
            var4.addProperty("max", c_14llhpgrs.getMax());
            var4.addProperty("online", c_14llhpgrs.getOnline());
            if (c_14llhpgrs.get() != null && c_14llhpgrs.get().length > 0) {
               JsonArray var5 = new JsonArray();

               for(int var6 = 0; var6 < c_14llhpgrs.get().length; ++var6) {
                  JsonObject var7 = new JsonObject();
                  UUID var8 = c_14llhpgrs.get()[var6].getId();
                  var7.addProperty("id", var8 == null ? "" : var8.toString());
                  var7.addProperty("name", c_14llhpgrs.get()[var6].getName());
                  var5.add(var7);
               }

               var4.add("sample", var5);
            }

            return var4;
         }
      }
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public ServerStatus deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
         JsonObject var4 = JsonUtils.asJsonObject(jsonElement, "status");
         ServerStatus var5 = new ServerStatus();
         if (var4.has("description")) {
            var5.setDescription((Text)jsonDeserializationContext.deserialize(var4.get("description"), Text.class));
         }

         if (var4.has("players")) {
            var5.setPlayers((ServerStatus.Players)jsonDeserializationContext.deserialize(var4.get("players"), ServerStatus.Players.class));
         }

         if (var4.has("version")) {
            var5.setVersion((ServerStatus.Version)jsonDeserializationContext.deserialize(var4.get("version"), ServerStatus.Version.class));
         }

         if (var4.has("favicon")) {
            var5.setFavicon(JsonUtils.getString(var4, "favicon"));
         }

         return var5;
      }

      public JsonElement serialize(ServerStatus c_89nlsgadq, Type type, JsonSerializationContext jsonSerializationContext) {
         JsonObject var4 = new JsonObject();
         if (c_89nlsgadq.getDescription() != null) {
            var4.add("description", jsonSerializationContext.serialize(c_89nlsgadq.getDescription()));
         }

         if (c_89nlsgadq.getPlayers() != null) {
            var4.add("players", jsonSerializationContext.serialize(c_89nlsgadq.getPlayers()));
         }

         if (c_89nlsgadq.getVersion() != null) {
            var4.add("version", jsonSerializationContext.serialize(c_89nlsgadq.getVersion()));
         }

         if (c_89nlsgadq.getFavicon() != null) {
            var4.addProperty("favicon", c_89nlsgadq.getFavicon());
         }

         return var4;
      }
   }

   public static class Version {
      private final String name;
      private final int protocol;

      public Version(String name, int protocol) {
         this.name = name;
         this.protocol = protocol;
      }

      public String getName() {
         return this.name;
      }

      public int getProtocol() {
         return this.protocol;
      }

      public static class Serializer implements JsonDeserializer, JsonSerializer {
         public ServerStatus.Version deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            JsonObject var4 = JsonUtils.asJsonObject(jsonElement, "version");
            return new ServerStatus.Version(JsonUtils.getString(var4, "name"), JsonUtils.getInteger(var4, "protocol"));
         }

         public JsonElement serialize(ServerStatus.Version c_70psyrsxi, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject var4 = new JsonObject();
            var4.addProperty("name", c_70psyrsxi.getName());
            var4.addProperty("protocol", c_70psyrsxi.getProtocol());
            return var4;
         }
      }
   }
}
